package com.katonaaron.matcher

import com.katonaaron.onto.Hypernym
import com.katonaaron.onto.MatchingResult
import com.katonaaron.onto.OntologyMatcher
import com.katonaaron.onto.Synonym
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology

abstract class BaseOntologyMatcher : OntologyMatcher {

    override fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult {
        val iriToSynonymSet = mutableMapOf<IRI, MutableSet<IRI>>()
        val hypernyms = mutableSetOf<Hypernym>()

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
            .forEach { (entities1, entities2) -> matchEntities(iriToSynonymSet, hypernyms, entities1, entities2) }


        val synonyms = iriToSynonymSet.values
            .distinct()
            .map { Synonym(it) }
            .toSet()
        return MatchingResult(synonyms, hypernyms)
    }

    abstract fun matchEntities(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        hypernyms: MutableSet<Hypernym>,
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

        /*  if (entity1.entityType != entity2.entityType) {
              // Entity matching with different types
              when {
                  entity1.isOWLClass && entity2.isOWLNamedIndividual ->
                      hypernyms.add(Hypernym(iri1, iri2, matcherIRI))
                  entity1.isOWLNamedIndividual && entity2.isOWLClass ->
                      hypernyms.add(Hypernym(iri2, iri1, matcherIRI))
                  else -> {
                      logger.error("Unsupported entity types for synonyms: $iri1 $iri2")
                      return
                  }

              }
              logger.warn("Synonym for different entity types: $iri1 $iri2. Hypernym created")
              return
          }*/

        val synset: MutableSet<IRI> = iriToSynonymSet[iri1] ?: iriToSynonymSet[iri2] ?: mutableSetOf()

        iriToSynonymSet[iri1] = synset
        iriToSynonymSet[iri2] = synset

        synset.add(iri1)
        synset.add(iri2)
    }
}
