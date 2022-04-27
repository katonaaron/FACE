package com.katonaaron.onto

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology

data class MatchingResult(val synonyms: List<Synonym>, val hypernyms: List<Hypernym>)

data class Synonym(val iris: Set<IRI>)
data class Hypernym(val parent: IRI, val child: IRI)

interface OntologyMatcher {

    /**
     * Matches onto1 ontology with onto2. Returns a new ontology containing the equality axioms
     * between the concepts in the two different ontologies.
     */
    fun matchOntologies(resultIri: IRI, onto1: OWLOntology, onto2: OWLOntology): OWLOntology

    fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult

    fun matchOntologiesToPairs(resultIri: IRI, onto1: OWLOntology, onto2: OWLOntology): Collection<Pair<IRI, IRI>>
}
