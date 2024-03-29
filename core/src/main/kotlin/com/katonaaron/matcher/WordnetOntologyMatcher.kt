package com.katonaaron.matcher

import com.katonaaron.commons.logger
import com.katonaaron.onto.Disjoint
import com.katonaaron.onto.Hypernym
import com.katonaaron.provenance.PROVENANCE_IRI_WORDNET
import net.sf.extjwnl.data.IndexWord
import net.sf.extjwnl.data.POS
import net.sf.extjwnl.data.PointerType
import net.sf.extjwnl.data.Synset
import net.sf.extjwnl.data.relationship.AsymmetricRelationship
import net.sf.extjwnl.data.relationship.RelationshipFinder
import net.sf.extjwnl.dictionary.Dictionary
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.search.EntitySearcher

class WordnetOntologyMatcher : BaseOntologyMatcher() {
    private val dictionary = Dictionary.getDefaultResourceInstance()
    private val iri = PROVENANCE_IRI_WORDNET
    private val senseAnnotationProperty: OWLAnnotationProperty

    init {
        val annotationIri = IRI.create("http://katonaaron.com/onto#hasSense")
        val df = OWLManager.createOWLOntologyManager().owlDataFactory
        senseAnnotationProperty = df.getOWLAnnotationProperty(annotationIri)
    }

    override fun matchEntities(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        hypernyms: MutableSet<Hypernym>,
        disjoints: MutableSet<Disjoint>,
        entities1: Collection<OWLEntity>,
        entities2: Collection<OWLEntity>,
        onto1: OWLOntology,
        onto2: OWLOntology
    ) {
        entities1.forEach entity1ForEach@{ entity1 ->
            val iri1 = entity1.iri
            val rem1 = iri1.remainder.get()

            val pos1 = entityTypeToPos(entity1.entityType) ?: return@entity1ForEach

            val word1 = dictionary.lookupIndexWord(pos1, rem1) ?: return@entity1ForEach

            // Must have the same number of hyphens, otherwise wrongly matched
            if (word1.lemma.count { it == '-' } != rem1.count { it == '-' }) {
                return@entity1ForEach
            }

            val senses1 = findAnnotatedSenses(onto1, entity1, word1).ifEmpty { word1.senses }

            entities2.forEach entity2ForEach@{ entity2 ->
                val iri2 = entity2.iri
                val rem2 = iri2.remainder.get()

                val pos2 = entityTypeToPos(entity2.entityType) ?: return@entity2ForEach

                if (pos1.id != pos2.id) {
                    // Entity matching with different POS is not supported
                    return@entity2ForEach
                }

                val word2 = dictionary.lookupIndexWord(pos2, rem2) ?: return@entity2ForEach

                // Must have the same number of hyphens, otherwise wrongly matched
                if (word2.lemma.count { it == '-' } != rem2.count { it == '-' }) {
                    return@entity2ForEach
                }

                val senses2 = findAnnotatedSenses(onto2, entity2, word2).ifEmpty { word2.senses }

                // todo search in senses1 and senses2
                val immediateRelationship = RelationshipFinder.getImmediateRelationship(word1, word2)

                if (immediateRelationship != -1) { // Synonyms
                    logger.trace("Synonym found: $iri1 $iri2 ${word1.senses[immediateRelationship - 1]}")
                    addSynonym(iriToSynonymSet, entity1, entity2)
                    return@entity2ForEach
                }

                senses1.forEach { sense1 ->
                    senses2.forEach { sense2 ->

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

                // There is no synonym or hypernym relationship between the two entities
                disjoints.add(Disjoint(iri1, iri2, iri))
            }
        }
    }

    private fun findAnnotatedSenses(
        onto: OWLOntology,
        entity: OWLEntity,
        word: IndexWord
    ): List<Synset> {
        return EntitySearcher.getAnnotations(entity, onto, senseAnnotationProperty)
            .mapNotNull { it.value.asLiteral().orNull() }
            .filter { it.isInteger }
            .mapNotNull {
                try {
                    val sense = it.parseInteger()
                    if (sense < word.senses.size) {
                        word.senses[sense]
                    } else {
                        null
                    }
                } catch (e: NumberFormatException) {
                    null
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
