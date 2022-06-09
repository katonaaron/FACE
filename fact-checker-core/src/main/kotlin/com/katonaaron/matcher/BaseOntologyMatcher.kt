package com.katonaaron.matcher

import com.katonaaron.onto.*
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology

abstract class BaseOntologyMatcher : OntologyMatcher {

    override fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult {
        val iriToSynonymSet = mutableMapOf<IRI, MutableSet<IRI>>()
        val hypernyms = mutableSetOf<Hypernym>()
        val disjoints = mutableSetOf<Disjoint>()

        listOf(
            onto1.classesInSignature + onto1.individualsInSignature to onto2.classesInSignature + onto2.individualsInSignature,
            onto1.objectPropertiesInSignature to onto2.objectPropertiesInSignature,
            // TODO: Entity matching for other types skipped for now
//            onto1.dataPropertiesInSignature to onto2.dataPropertiesInSignature,
//            onto1.datatypesInSignature to onto2.datatypesInSignature,
//            onto1.annotationPropertiesInSignature to onto2.annotationPropertiesInSignature
        )
            .map { (entities1, entities2) ->
                entities1.filter { !it.isBuiltIn } to
                        entities2.filter { !it.isBuiltIn }
            }
            .forEach { (entities1, entities2) ->
                matchEntities(
                    iriToSynonymSet,
                    hypernyms,
                    disjoints,
                    entities1,
                    entities2
                )
            }


        val synonyms = iriToSynonymSet.values
            .distinct()
            .map { Synonym(it) }
            .toSet()
        return MatchingResult(synonyms, hypernyms, disjoints)
    }

    abstract fun matchEntities(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        hypernyms: MutableSet<Hypernym>,
        disjoints: MutableSet<Disjoint>,
        entities1: Collection<OWLEntity>,
        entities2: Collection<OWLEntity>
    )

    protected fun addSynonym(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        entity1: OWLEntity,
        entity2: OWLEntity
    ) {
        val iri1 = entity1.iri
        val iri2 = entity2.iri

        val synset: MutableSet<IRI> = iriToSynonymSet[iri1] ?: iriToSynonymSet[iri2] ?: mutableSetOf()

        iriToSynonymSet[iri1] = synset
        iriToSynonymSet[iri2] = synset

        synset.add(iri1)
        synset.add(iri2)
    }
}
