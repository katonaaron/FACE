package com.katonaaron.onto

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology

data class MatchingResult(val synonyms: Set<Synonym>, val hypernyms: Set<Hypernym>)

data class Synonym(val iris: Set<IRI>)
data class Hypernym(val parent: IRI, val child: IRI, val informationSource: IRI) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hypernym) return false

        if (parent != other.parent) return false
        if (child != other.child) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parent.hashCode()
        result = 31 * result + child.hashCode()
        return result
    }
}

interface OntologyMatcher {

    fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult
}

