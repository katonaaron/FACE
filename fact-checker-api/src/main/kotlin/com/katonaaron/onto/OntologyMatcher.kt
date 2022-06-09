package com.katonaaron.onto

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology

data class MatchingResult(val synonyms: Set<Synonym>, val hypernyms: Set<Hypernym>, val disjoints: Set<Disjoint>)

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

data class Disjoint(val iri1: IRI, val iri2: IRI, val informationSource: IRI) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Disjoint) return false

        if (iri1 != other.iri1) return false
        if (iri2 != other.iri2) return false
        if (informationSource != other.informationSource) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iri1.hashCode()
        result = 31 * result + iri2.hashCode()
        result = 31 * result + informationSource.hashCode()
        return result
    }
}

interface OntologyMatcher {

    fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult
}

