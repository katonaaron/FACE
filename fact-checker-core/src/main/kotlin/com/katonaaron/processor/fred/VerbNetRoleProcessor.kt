package com.katonaaron.processor.fred

import com.katonaaron.commons.add
import com.katonaaron.commons.select
import com.katonaaron.commons.sparql
import com.katonaaron.onto.OntologyProcessor
import com.katonaaron.processor.RemoveClassAndItsInstancesProcessor
import de.derivo.sparqldlapi.QueryEngine
import de.derivo.sparqldlapi.Var
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class VerbNetRoleProcessor(
    private val reasonerFactory: OWLReasonerFactory,
    private val newNamespace: String,
    private val namespace: String
) : OntologyProcessor {
    private val iriVnData = "http://www.ontologydesignpatterns.org/ont/vn/data/"
    private val iriVnRole = "http://www.ontologydesignpatterns.org/ont/vn/abox/role/"

    override fun processOntology(onto: OWLOntology) {
        val man = onto.owlOntologyManager
        val df = man.owlDataFactory
        val r = reasonerFactory.createReasoner(onto)
        val engine = QueryEngine.create(man, r, true)

        val verbs = onto.signature.filter {
            it.iri.namespace == iriVnData
        }

        val toRemove = mutableListOf<OWLClass>()

        verbs
            .filter { it.isOWLClass }
            .map { it.asOWLClass() }
            .forEach { verb ->

                val vRole = Var("role")
                val vRoleClass = Var("roleClass")
                val vAgent = Var("agent")
                val vPatient = Var("patient")

                val results = sparql(engine) {
                    select {
                        prefixes {
                            prefix("vn.data:", iriVnData)
                            prefix("vn.role:", iriVnRole)
                            prefix("fred:", namespace)
                        }
                        where {
                            equivalentClass(vRoleClass, verb.iri)
                            type(vRole, vRoleClass)
                            propertyValue(vRole, "vn.role:Agent", vAgent)
                            propertyValue(vRole, "vn.role:Patient", vPatient)
                        }
                    }
                }

                print("sparql results = [")
                results.forEach { resultMap ->
                    print("{\n")
                    print(resultMap.map { "\t" + it.key.name + "=" + it.value }.joinToString(",\n"))
                    println("\n}")
                }
                println("]")

                results
                    .forEach { binding ->
                        val roleClass = df.getOWLClass(binding[vRoleClass]!!)

                        if (roleClass.iri.namespace == namespace) {
                            val role = df.getOWLObjectProperty(IRI.create(newNamespace, roleClass.iri.remainder.get()))
                            val agent = df.getOWLNamedIndividual(binding[vAgent]!!)
                            val patient = df.getOWLNamedIndividual(binding[vPatient]!!)
                            onto.add(df.getOWLObjectPropertyAssertionAxiom(role, agent, patient))
                        }

                        toRemove.add(roleClass)
                    }

                toRemove.add(verb)
            }

        RemoveClassAndItsInstancesProcessor(
            reasonerFactory,
            *toRemove.toTypedArray()
        ).processOntology(onto)
    }
}
