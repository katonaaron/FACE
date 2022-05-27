package com.katonaaron.matcher

import com.katonaaron.onto.Hypernym
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity

class CombinedOntologyMatcher(
    private val matchers: List<BaseOntologyMatcher>
) : BaseOntologyMatcher() {

    override fun matchEntities(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        hypernyms: MutableSet<Hypernym>,
        entities1: Collection<OWLEntity>,
        entities2: Collection<OWLEntity>
    ) {
        matchers.forEach { it.matchEntities(iriToSynonymSet, hypernyms, entities1, entities2) }
    }
}


