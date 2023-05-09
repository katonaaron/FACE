package com.katonaaron.matcher

import com.katonaaron.onto.MatchingResult
import com.katonaaron.onto.OntologyMatcher
import org.semanticweb.owlapi.model.OWLOntology

class DefaultOntologyMatcher : OntologyMatcher {
    val matcher = CombinedOntologyMatcher(
        listOf(
            WordnetOntologyMatcher(),
            StringOntologyMatcher()
        )
    )

    override fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult {
        return matcher.matchOntologies(onto1, onto2)
    }
}
