package com.katonaaron.matcher

import com.katonaaron.commons.logger
import com.katonaaron.onto.Hypernym
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity

class StringOntologyMatcher : BaseOntologyMatcher() {
    private val iri = IRI.create("http://katonaaron.com/matcher#StringOntologyMatcher")

    override fun matchEntities(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        hypernyms: MutableSet<Hypernym>,
        entities1: Collection<OWLEntity>,
        entities2: Collection<OWLEntity>
    ) {
        val entityMap: Map<String, OWLEntity> = entities1.associateBy { processIri(it.iri) }

        entities2
            .forEach { entity1 ->
                entityMap[processIri(entity1.iri)]
                    ?.let { entity2 ->
                        logger.trace("Synonym found: ${entity1.iri} ${entity2.iri}")
                        addSynonym(iriToSynonymSet, hypernyms, entity1, entity2, iri)
                    }
            }
    }

    private fun processIri(iri: IRI): String = iri.remainder.get()!!.lowercase().trim()
}
