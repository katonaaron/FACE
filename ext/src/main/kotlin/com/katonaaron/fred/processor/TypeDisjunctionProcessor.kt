package com.katonaaron.fred.processor

import com.katonaaron.commons.add
import com.katonaaron.commons.getOWLClass
import com.katonaaron.commons.logger
import com.katonaaron.commons.remove
import com.katonaaron.onto.OntologyProcessor
import com.katonaaron.processor.RemoveClassAndItsInstancesProcessor
import com.katonaaron.sparqldsl.QueryEngine
import com.katonaaron.sparqldsl.Var
import com.katonaaron.sparqldsl.sparqlSelect
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLNamedIndividual
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory
import org.semanticweb.owlapi.search.EntitySearcher

class TypeDisjunctionProcessor(
    private val reasonerFactory: OWLReasonerFactory,
    private val namespace: String
) : OntologyProcessor {
    private val iriBoxing = "http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"

    override fun processOntology(onto: OWLOntology) {
        val man = onto.owlOntologyManager
        val df = man.owlDataFactory
        val r = reasonerFactory.createReasoner(onto)
        val engine = QueryEngine(man, r, true)

        val disjunctClass = df.getOWLClass("${namespace}Disjunct")

        if (!onto.classesInSignature.contains(disjunctClass)) {
            logger.trace("not found: $disjunctClass")
            return
        }

        val disjointToIndividualToTypes: MutableMap<OWLNamedIndividual, MutableMap<OWLNamedIndividual, MutableSet<OWLClass>>> =
            mutableMapOf()
        val toAdd: MutableSet<OWLAxiom> = mutableSetOf()
        val toRemove: MutableSet<OWLAxiom> = mutableSetOf()


        val vTerm = Var("term")
        val vIndividual = Var("individual")
        val vConcept = Var("concept")

        val results1 = sparqlSelect(engine) {
            prefix("boxing:", iriBoxing)
            prefix("fred:", namespace)

            select()

            where {
                type(vTerm, "fred:Disjunct")
                propertyValue(vTerm, "boxing:involves", vIndividual)
                propertyValue(vTerm, "boxing:involves", vConcept)
            }
        }

        logger.trace("sparql results1 = ${formatResults(results1)}")

        results1.forEach { binding ->
            val iriTerm = binding[vTerm]!!
            val iriIndividual = binding[vIndividual]!!
            val iriConcept = binding[vConcept]!!

            if (!onto.containsClassInSignature(iriConcept)) {
                return@forEach
            }

            val term = df.getOWLNamedIndividual(iriTerm)
            val individual = df.getOWLNamedIndividual(iriIndividual)
            val concept = df.getOWLClass(iriConcept)

            if (!EntitySearcher.getTypes(individual, onto).contains(concept)) {
                return@forEach
            }

            logger.trace("disjunction term found: $iriTerm $iriIndividual $iriConcept")

            val map =
                disjointToIndividualToTypes[term] ?: mutableMapOf<OWLNamedIndividual, MutableSet<OWLClass>>().also {
                    disjointToIndividualToTypes[term] = it
                }

            val set = map[individual] ?: mutableSetOf<OWLClass>().also {
                map[individual] = it
            }

            set.add(concept)
        }

        /*   val d1 = df.getSWRLVariable(IRI.create(namespace, "d1"))
           val d2 = df.getSWRLVariable(IRI.create(namespace, "d2"))
           val d3 = df.getSWRLVariable(IRI.create(namespace, "d3"))
           val x = df.getSWRLVariable(IRI.create(namespace, "x"))
           val involves = df.getOWLObjectProperty(IRI.create(iriBoxing, "involves"))
           val union = df.getOWLObjectProperty(IRI.create(namespace, "union"))

           val rule1 = df.getSWRLRule(
               setOf(
                   df.getSWRLObjectPropertyAtom(involves, d1, x),
                   df.getSWRLObjectPropertyAtom(involves, d2, x)
               ),
               setOf(
                   df.getSWRLObjectPropertyAtom(union, d1, d2)
               )
           )
           val rule2 = df.getSWRLRule(
               setOf(
                   df.getSWRLObjectPropertyAtom(union, d1, d2),
                   df.getSWRLObjectPropertyAtom(union, d2, d3)
               ),
               setOf(
                   df.getSWRLObjectPropertyAtom(union, d1, d3)
               )
           )
           val rule3 = df.getSWRLRule(
               setOf(
                   df.getSWRLObjectPropertyAtom(union, d1, d2)
               ),
               setOf(
                   df.getSWRLObjectPropertyAtom(union, d2, d1)
               )
           )
           onto.add(rule1, rule2, rule3)

           r.precomputeInferences(InferenceType.OBJECT_PROPERTY_ASSERTIONS)

           onto.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("tmp.owl"))

           r.getInstances(disjunctClass, true).flattened
               .forEach { term1 ->
                   r.getObjectPropertyValues(term1,union).flattened.forEach inner@{ term2 ->
                       val map1 = disjointToIndividualToTypes[term1] ?: return@inner
                       val map2 = disjointToIndividualToTypes[term2] ?: return@inner

                       println("union: $term1 $term2")

                       for ((ind, types) in map2) {
                           map1[ind]?.addAll(types) ?: let {
                               map1[ind] = types
                           }
                       }
                       disjointToIndividualToTypes.remove(term2)
                   }
               }*/


        val vTerm1 = Var("term1")
        val vTerm2 = Var("term2")

        val results2 = sparqlSelect(engine) {
            prefix("boxing:", iriBoxing)
            prefix("fred:", namespace)

            select(vTerm1, vTerm2)

            where {
                type(vTerm1, "fred:Disjunct")
                type(vTerm2, "fred:Disjunct")
                propertyValue(vTerm1, "fred:union", vTerm2)
            }
            where {
                type(vTerm1, "fred:Disjunct")
                type(vTerm2, "fred:Disjunct")
                propertyValue(vTerm1, "boxing:involves", vIndividual)
                propertyValue(vTerm2, "boxing:involves", vIndividual)
            }
        }

        logger.trace("sparql results2 = ${formatResults(results2)}")

        results2.forEach { binding ->
            val term1 = df.getOWLNamedIndividual(binding[vTerm1]!!)
            val term2 = df.getOWLNamedIndividual(binding[vTerm2]!!)

            val map1 = disjointToIndividualToTypes[term1] ?: return@forEach
            val map2 = disjointToIndividualToTypes[term2] ?: return@forEach

            logger.trace("union: $term1 $term2")

            for ((ind, types) in map2) {
                map1[ind]?.addAll(types) ?: let {
                    map1[ind] = types
                }
            }

            disjointToIndividualToTypes[term2] = map1
        }

        disjointToIndividualToTypes.values.forEach { map ->
            map.forEach { (ind, types) ->
                if (types.size > 1) {
                    toRemove.addAll(types.map { df.getOWLClassAssertionAxiom(it, ind) })
                    toAdd.add(
                        df.getOWLClassAssertionAxiom(
                            df.getOWLObjectUnionOf(types),
                            ind
                        )
                    )
                }
            }
        }

        onto.remove(toRemove)
        onto.add(toAdd)

        RemoveClassAndItsInstancesProcessor(reasonerFactory, disjunctClass)
            .processOntology(onto)
    }
}
