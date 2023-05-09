package com.katonaaron.matcher

import com.katonaaron.onto.Disjoint
import com.katonaaron.onto.Hypernym
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology

class CombinedOntologyMatcher(
    private val matchers: List<BaseOntologyMatcher>
) : BaseOntologyMatcher() {

    constructor(vararg matchers: BaseOntologyMatcher) : this(matchers.toList())

    override fun matchEntities(
        iriToSynonymSet: MutableMap<IRI, MutableSet<IRI>>,
        hypernyms: MutableSet<Hypernym>,
        disjoints: MutableSet<Disjoint>,
        entities1: Collection<OWLEntity>,
        entities2: Collection<OWLEntity>,
        onto1: OWLOntology,
        onto2: OWLOntology
    ) {
        matchers.forEach { it.matchEntities(iriToSynonymSet, hypernyms, disjoints, entities1, entities2, onto1, onto2) }
    }
}


