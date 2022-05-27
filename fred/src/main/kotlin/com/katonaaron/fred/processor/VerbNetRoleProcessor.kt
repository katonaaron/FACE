package com.katonaaron.fred.processor

import com.katonaaron.commons.add
import com.katonaaron.commons.logger
import com.katonaaron.onto.OntologyProcessor
import com.katonaaron.processor.RemoveClassAndItsInstancesProcessor
import com.katonaaron.sparqldsl.QueryEngine
import com.katonaaron.sparqldsl.Var
import com.katonaaron.sparqldsl.select
import com.katonaaron.sparqldsl.sparql
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class VerbNetRoleProcessor(
    private val reasonerFactory: OWLReasonerFactory,
    private val namespace: String
) : OntologyProcessor {
    private val iriVnData = "http://www.ontologydesignpatterns.org/ont/vn/data/"
    private val iriVnRole = "http://www.ontologydesignpatterns.org/ont/vn/abox/role/"

    private val propertyPairs = listOf(
        "Agent" to "Patient",
        "Theme1" to "Theme2"
    )

    override fun processOntology(onto: OWLOntology) {
        val man = onto.owlOntologyManager
        val df = man.owlDataFactory
        val r = reasonerFactory.createReasoner(onto)
        val engine = QueryEngine(man, r, true)

        val verbs = onto.signature.filter {
            it.iri.namespace == iriVnData
        }

        val toRemove = mutableListOf<OWLClass>()
        val toAdd = mutableListOf<OWLAxiom>()

        verbs
            .filter { it.isOWLClass }
            .map { it.asOWLClass() }
            .forEach { verb ->

                val vRole = Var("role")
                val vRoleClass = Var("roleClass")
                val vSource = Var("source")
                val vTarget = Var("target")

                val results = sparql(engine) {
                    select {
                        prefixes {
                            prefix("vn.data:", iriVnData)
                            prefix("vn.role:", iriVnRole)
                            prefix("fred:", namespace)
                        }

                        propertyPairs
                            .filter { (pSource, pTarget) ->
                                onto.containsObjectPropertyInSignature(IRI.create(iriVnRole, pSource))
                                        && onto.containsObjectPropertyInSignature(IRI.create(iriVnRole, pTarget))
                            }
                            .forEach { (pSource, pTarget) ->
                                where {
                                    equivalentClass(vRoleClass, verb.iri)
                                    type(vRole, vRoleClass)
                                    propertyValue(vRole, "vn.role:$pSource", vSource)
                                    propertyValue(vRole, "vn.role:$pTarget", vTarget)
                                }
                            }
                    }
                }


                logger.trace("sparql results = ${formatResults(results)}")


                results
                    .forEach { binding ->
                        val roleClass = df.getOWLClass(binding[vRoleClass]!!)

                        if (roleClass.iri.namespace == namespace) {
                            val role = df.getOWLObjectProperty(IRI.create(namespace, roleClass.iri.remainder.get()))
                            val source = df.getOWLNamedIndividual(binding[vSource]!!)
                            val target = df.getOWLNamedIndividual(binding[vTarget]!!)
                            toAdd.add(df.getOWLObjectPropertyAssertionAxiom(role, source, target))
                        }

                        toRemove.add(roleClass)
                    }

                toRemove.add(verb)
            }

        RemoveClassAndItsInstancesProcessor(
            reasonerFactory,
            *toRemove.toTypedArray()
        ).processOntology(onto)

        onto.add(toAdd)
    }
}
