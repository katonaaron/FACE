package com.katonaaron.fred.processor

import com.katonaaron.commons.*
import com.katonaaron.onto.OntologyProcessor
import com.katonaaron.sparqldsl.*
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory
import java.io.FileOutputStream

class FalseSituationProcessor(
    private val reasonerFactory: OWLReasonerFactory,
    private val namespace: String
) : OntologyProcessor {
    private val iriBoxing = "http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"

    override fun processOntology(onto: OWLOntology) {
        val man = onto.owlOntologyManager
        val df = man.owlDataFactory
        val r = reasonerFactory.createReasoner(onto)

        val situation = df.getOWLClass("${namespace}Situation")

        if (!onto.classesInSignature.contains(situation)) {
            logger.trace("not found: $situation")
            return
        }

        val engine = QueryEngine(man, r, true)

        val vSituation = Var("situation")
        val vTarget = Var("target")

        val results = sparql(engine) {
            select {
                prefixes {
                    prefix("boxing:", iriBoxing)
                    prefix("fred:", namespace)
                }
                where {
                    type(vSituation, "fred:Situation")
                    propertyValue(vSituation, "boxing:hasTruthValue", "boxing:False")
                    propertyValue(vSituation, "boxing:involves", vTarget)
                }
            }
        }

        printResults(results)

        val situations = results
            .groupBy({ it[vSituation]!! }, { it[vTarget]!! })
            .mapValues { (_, involvedIris) ->
                involvedIris.filter {
                    it.namespace == namespace
                }
            }

        logger.trace("situations = $situations")

        situations.forEach { (situationIri, involvedIris) ->
            assert(involvedIris.size == 2)

            val iri1 = involvedIris[0]
            val iri2 = involvedIris[1]

            val type1 = getEntityType(onto, iri1)!!
            val type2 = getEntityType(onto, iri2)!!

            val isHandled = when {
                type1.isOWLClass && !type2.isOWLClass && type2.isOWLNamedIndividual ->
                    handleFalseTypeSituation(engine, onto, iri1, iri2)
                type2.isOWLClass && !type1.isOWLClass && type1.isOWLNamedIndividual ->
                    handleFalseTypeSituation(engine, onto, iri2, iri1)
                else -> false // TODO
            }

            if (isHandled) {
                removeEntity(onto, df.getOWLNamedIndividual(situationIri))
            }
        }

        onto.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("result-FalseSituationProcessor.owl"))
    }

    private fun handleFalseTypeSituation(
        engine: QueryEngine,
        onto: OWLOntology,
        iriClass: IRI,
        iriIndividual: IRI
    ): Boolean {
        val isType = sparqlAsk(engine) {
            ask {
                type(iriIndividual, iriClass)
            }
        }

        logger.trace("type($iriIndividual, $iriClass) = $isType")

        if (!isType)
            return false

/*        val isMultiple = onto.objectPropertiesInSignature
            .find { it.iri == IRI.create(iriFredQuant, "hasQuantifier") } // Check if exists
            ?.let {
                sparqlAsk(engine) {
                    prefixes {
                        prefix("quant:", iriFredQuant)
                    }
                    ask {
                        propertyValue(iriIndividual, "quant:hasQuantifier", "quant:multiple")
                    }
                }
            } ?: false


        logger.trace("isMultiple = $isMultiple")*/

        val man = onto.owlOntologyManager
        val df = man.owlDataFactory

        val clazz = df.getOWLClass(iriClass)
        val indiv = df.getOWLNamedIndividual(iriIndividual)

        /*      if (isMultiple) {
                  onto.remove(
                      df.getOWLClassAssertionAxiom(clazz, indiv)
                  )

                  val r = reasonerFactory.createReasoner(onto)
                  val types = r.getTypes(indiv, true)
                      .flatMap { it.entities }
                      .filter { it.iri.namespace == namespace }

                  removeEntity(onto, indiv)

                  if (types.isEmpty()) {
                      // The individual should be a class
                      logger.debug("Individual converted to class: <$iriIndividual>")
                      onto.add(
                          df.getOWLSubClassOfAxiom(df.getOWLClass(iriIndividual), df.getOWLObjectComplementOf(clazz))
                      )
                  } else {
                      logger.debug("Classes updated of instance: <$iriIndividual>")
                      // The classes of the instance
                      types.forEach { type ->
                          onto.add(
                              df.getOWLSubClassOfAxiom(type, df.getOWLObjectComplementOf(clazz))
                          )
                      }
                  }
              } else {*/
            onto.remove(
                df.getOWLClassAssertionAxiom(clazz, indiv)
            )

            onto.add(
                df.getOWLClassAssertionAxiom(df.getOWLObjectComplementOf(clazz), indiv)
            )
        /*}*/

        return true
    }

    private fun printResults(results: List<Map<Var, IRI>>) {
        logger.trace("sparql results = ${formatResults(results)}")
    }
}
