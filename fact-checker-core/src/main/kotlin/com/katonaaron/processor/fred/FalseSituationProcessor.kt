package com.katonaaron.processor.fred

import com.katonaaron.commons.*
import com.katonaaron.onto.OntologyProcessor
import de.derivo.sparqldlapi.QueryEngine
import de.derivo.sparqldlapi.Var
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
    private val iriFredQuant = "http://www.ontologydesignpatterns.org/ont/fred/quantifiers.owl#"

    override fun processOntology(onto: OWLOntology) {
        val man = onto.owlOntologyManager
        val df = man.owlDataFactory
        val r = reasonerFactory.createReasoner(onto)

        val situation = df.getOWLClass("${namespace}Situation")

        if (!onto.classesInSignature.contains(situation)) {
            println("not found: $situation")
            return
        }

        val engine = QueryEngine.create(man, r, true)

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

        println("situations = $situations")

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

        println("type($iriIndividual, $iriClass) = $isType")

        if (!isType)
            return false

        val isMultiple = sparqlAsk(engine) {
            prefixes {
                prefix("quant:", iriFredQuant)
            }
            ask {
                propertyValue(iriIndividual, "quant:hasQuantifier", "quant:multiple")
            }
        }

        println("isMultiple = $isMultiple")

        val man = onto.owlOntologyManager
        val df = man.owlDataFactory

        val clazz = df.getOWLClass(iriClass)
        val indiv = df.getOWLNamedIndividual(iriIndividual)

        if (isMultiple) {
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
                println("Individual converted to class: <$iriIndividual>")
                onto.add(
                    df.getOWLSubClassOfAxiom(df.getOWLClass(iriIndividual), df.getOWLObjectComplementOf(clazz))
                )
            } else {
                println("Classes updated of instance: <$iriIndividual>")
                // The classes of the instance
                types.forEach { type ->
                    onto.add(
                        df.getOWLSubClassOfAxiom(type, df.getOWLObjectComplementOf(clazz))
                    )
                }
            }
        } else {
            onto.remove(
                df.getOWLClassAssertionAxiom(clazz, indiv)
            )

            onto.add(
                df.getOWLClassAssertionAxiom(df.getOWLObjectComplementOf(clazz), indiv)
            )
        }

        return true
    }

    private fun printResults(results: List<Map<Var, IRI>>) {
        print("sparql results = [")
        results.forEach { resultMap ->
            print("{\n")
            print(resultMap.map { "\t" + it.key.name + "=" + it.value }.joinToString(",\n"))
            println("\n}")
        }
        println("]")
    }
}
