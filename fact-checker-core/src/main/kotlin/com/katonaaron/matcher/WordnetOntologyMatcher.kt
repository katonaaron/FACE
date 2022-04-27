package com.katonaaron.matcher

import com.katonaaron.onto.Hypernym
import com.katonaaron.onto.MatchingResult
import com.katonaaron.onto.OntologyMatcher
import com.katonaaron.onto.Synonym
import net.sf.extjwnl.data.POS
import net.sf.extjwnl.data.PointerType
import net.sf.extjwnl.data.relationship.AsymmetricRelationship
import net.sf.extjwnl.data.relationship.RelationshipFinder
import net.sf.extjwnl.dictionary.Dictionary
import org.semanticweb.owlapi.model.EntityType
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology

class WordnetOntologyMatcher : OntologyMatcher {
    private val dictionary = Dictionary.getDefaultResourceInstance()

    override fun matchOntologies(resultIri: IRI, onto1: OWLOntology, onto2: OWLOntology): OWLOntology {
        TODO("Not yet implemented")
    }

    override fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult {
        val sig1 = onto1.signature
            .filter { !it.isBuiltIn }

        val sig2 = onto2.signature
            .filter { !it.isBuiltIn }

        val iriToSynonymSet = mutableMapOf<IRI, MutableSet<IRI>>()
        val hypernyms = mutableListOf<Hypernym>()

        sig1.forEach entity1ForEach@{ entity1 ->
            val iri1 = entity1.iri
            val rem1 = iri1.remainder.get()

            val pos1 = entityTypeToPos(entity1.entityType) ?: return@entity1ForEach

            val word1 = dictionary.lookupIndexWord(pos1, rem1) ?: return@entity1ForEach

            sig2.forEach entity2ForEach@{ entity2 ->
                val iri2 = entity2.iri
                val rem2 = iri2.remainder.get()

                val pos2 = entityTypeToPos(entity2.entityType) ?: return@entity2ForEach

                if (pos1.id != pos2.id) {
                    // Entity matching with different POS is not supported
                    return@entity2ForEach
                }

                val word2 = dictionary.lookupIndexWord(pos2, rem2) ?: return@entity2ForEach

                val immediateRelationship = RelationshipFinder.getImmediateRelationship(word1, word2)

                if (immediateRelationship != -1) { // Synonyms
                    if (entity1.entityType != entity2.entityType) {
                        // TODO: Entity matching with different types skipped for now
                        println("IGNORED: Synonym for different entity types: $iri1 $iri2 ${word1.senses[immediateRelationship - 1]}")
                        return@entity2ForEach
                    }

                    println("Synonym found: $iri1 $iri2 ${word1.senses[immediateRelationship - 1]}")

                    val synset: MutableSet<IRI> = iriToSynonymSet[iri1] ?: iriToSynonymSet[iri2] ?: mutableSetOf()

                    if (synset.isEmpty()) {
                        iriToSynonymSet[iri1] = synset
                    }

                    synset.add(iri1)
                    synset.add(iri2)
                    return@entity2ForEach
                }

                word1.senses.forEach { sense1 ->
                    word2.senses.forEach { sense2 ->
                        val relationships = RelationshipFinder.findRelationships(
                            sense1, sense2, PointerType.HYPERNYM
                        )

                        relationships
                            .filterIsInstance<AsymmetricRelationship>()
                            .forEach { rel ->
                                val commonParent = rel.nodeList[rel.commonParentIndex].synset

                                if (commonParent.containsWord(word1.lemma)) {
                                    println("Hypernym found: $iri1 $iri2 $rel")
                                    hypernyms.add(Hypernym(iri1, iri2))
                                    return@entity2ForEach
                                } else if (commonParent.containsWord(word2.lemma)) {
                                    println("Hypernym found: $iri2 $iri1 $rel")
                                    hypernyms.add(Hypernym(iri2, iri1))
                                    return@entity2ForEach
                                }
                            }
                    }
                }
            }
        }

        val synonyms = iriToSynonymSet.values
            .map { Synonym(it) }
        return MatchingResult(synonyms, hypernyms)
    }

    private fun entityTypeToPos(entityType: EntityType<*>) = when (entityType) {
        EntityType.CLASS -> POS.NOUN
        EntityType.NAMED_INDIVIDUAL -> POS.NOUN
        EntityType.OBJECT_PROPERTY -> POS.VERB
        else -> null // Entity matching for other types skipped for now
    }

    override fun matchOntologiesToPairs(
        resultIri: IRI,
        onto1: OWLOntology,
        onto2: OWLOntology
    ): Collection<Pair<IRI, IRI>> {
        TODO("Not yet implemented")
    }
}
