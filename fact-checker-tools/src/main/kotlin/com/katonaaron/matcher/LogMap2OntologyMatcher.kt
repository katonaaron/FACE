package com.katonaaron.matcher

import com.katonaaron.onto.MatchingResult
import com.katonaaron.onto.OntologyMatcher
import com.katonaaron.onto.Synonym
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import uk.ac.ox.krr.logmap2.LogMap2_Matcher

class LogMap2OntologyMatcher : OntologyMatcher {

    override fun matchOntologiesToPairs(
        resultIri: IRI,
        onto1: OWLOntology,
        onto2: OWLOntology
    ): Collection<Pair<IRI, IRI>> {
        val matcher = LogMap2_Matcher(onto1, onto2)

        val pairs = matcher.logmap2_Mappings.map { Pair(IRI.create(it.iriStrEnt1), IRI.create(it.iriStrEnt2)) }

        println("matching: $pairs")

//        println("matcher.logmap2_ConflictiveMappings = ${matcher.logmap2_ConflictiveMappings}")
//        println("matcher.logmap2_DiscardedMappings = ${matcher.logmap2_DiscardedMappings}")

        return pairs
    }

    override fun matchOntologies(onto1: OWLOntology, onto2: OWLOntology): MatchingResult {
        val iriToSynonymSet = mutableMapOf<IRI, MutableSet<IRI>>()

        matchOntologiesToPairs(IRI.generateDocumentIRI(), onto1, onto2).forEach { (iri1, iri2) ->
            val synset: MutableSet<IRI> = iriToSynonymSet[iri1] ?: iriToSynonymSet[iri2] ?: mutableSetOf()

            if (synset.isEmpty()) {
                iriToSynonymSet[iri1] = synset
            }

            synset.add(iri1)
            synset.add(iri2)
        }

        val synonyms = iriToSynonymSet.values
            .map { Synonym(it) }

        return MatchingResult(synonyms, emptyList())
    }

    override fun matchOntologies(resultIri: IRI, onto1: OWLOntology, onto2: OWLOntology): OWLOntology {
        val man = OWLManager.createOWLOntologyManager()
        val df = man.owlDataFactory

        val matcher = LogMap2_Matcher(onto1, onto2)

        val axioms = matcher.logmap2_Mappings.map {
            val class1 = df.getOWLClass(IRI.create(it.iriStrEnt1))
            val class2 = df.getOWLClass(IRI.create(it.iriStrEnt2))
            df.getOWLEquivalentClassesAxiom(class1, class2)
        }.toSet()

        println("matching: ${matcher.logmap2_Mappings.map { Pair(it.iriStrEnt1, it.iriStrEnt2) }}")

        return man.createOntology(axioms, resultIri)
    }
}
