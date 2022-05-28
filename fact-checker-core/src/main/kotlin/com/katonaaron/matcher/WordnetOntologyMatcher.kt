package com.katonaaron.matcher

import com.katonaaron.commons.logger
import com.katonaaron.onto.Hypernym
import com.katonaaron.provenance.PROVENANCE_IRI_WORDNET
import net.sf.extjwnl.data.POS
import net.sf.extjwnl.data.PointerType
import net.sf.extjwnl.data.relationship.AsymmetricRelationship
import net.sf.extjwnl.data.relationship.RelationshipFinder
import net.sf.extjwnl.dictionary.Dictionary
import org.semanticweb.owlapi.model.EntityType
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity

class WordnetOntologyMatcher : BaseOntologyMatcher() {
    private val dictionary = Dictionary.getDefaultResourceInstance()
    private val iri = PROVENANCE_IRI_WORDNET

    override fun matchEntities(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        hypernyms: MutableSet<Hypernym>,
        entities1: Collection<OWLEntity>,
        entities2: Collection<OWLEntity>
    ) {
        entities1.forEach entity1ForEach@{ entity1 ->
            val iri1 = entity1.iri
            val rem1 = iri1.remainder.get()

            val pos1 = entityTypeToPos(entity1.entityType) ?: return@entity1ForEach

            val word1 = dictionary.lookupIndexWord(pos1, rem1) ?: return@entity1ForEach

            entities2.forEach entity2ForEach@{ entity2 ->
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
                    logger.trace("Synonym found: $iri1 $iri2 ${word1.senses[immediateRelationship - 1]}")
                    addSynonym(iriToSynonymSet, entity1, entity2)
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
                                    logger.trace("Hypernym found: $iri1 $iri2 $rel")
                                    hypernyms.add(Hypernym(iri1, iri2, iri))
                                    return@entity2ForEach
                                } else if (commonParent.containsWord(word2.lemma)) {
                                    logger.trace("Hypernym found: $iri2 $iri1 $rel")
                                    hypernyms.add(Hypernym(iri2, iri1, iri))
                                    return@entity2ForEach
                                }
                            }
                    }
                }
            }
        }
    }

    private fun entityTypeToPos(entityType: EntityType<*>) = when (entityType) {
        EntityType.CLASS -> POS.NOUN
        EntityType.NAMED_INDIVIDUAL -> POS.NOUN
        EntityType.OBJECT_PROPERTY -> POS.VERB
        else -> null // TODO: Entity matching for other types skipped for now
    }
}
