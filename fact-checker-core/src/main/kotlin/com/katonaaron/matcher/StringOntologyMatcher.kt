package com.katonaaron.matcher

import com.katonaaron.onto.MatchingResult
import com.katonaaron.onto.OntologyMatcher
import com.katonaaron.onto.Synonym
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology

class StringOntologyMatcher : OntologyMatcher {
    override fun matchOntologies(resultIri: IRI, onto1: OWLOntology, onto2: OWLOntology): OWLOntology {
        val man = OWLManager.createOWLOntologyManager()
        val df = man.owlDataFactory

        val axioms = matchOntologiesToPairs(resultIri, onto1, onto2).map { pair ->
            df.getOWLEquivalentClassesAxiom(df.getOWLClass(pair.first), df.getOWLClass(pair.second))
        }.toSet()

        return man.createOntology(axioms, resultIri)
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

    override fun matchOntologiesToPairs(
        resultIri: IRI,
        onto1: OWLOntology,
        onto2: OWLOntology
    ): Collection<Pair<IRI, IRI>> {
        val entityMap: Map<String, OWLEntity> = onto1.signature
            .filter { !it.isBuiltIn }
            .associateBy { entityToString(it) }

        val matching = onto2.signature
            .filter { !it.isBuiltIn }
            .mapNotNull { thisEntity ->
                entityMap[entityToString(thisEntity)]
                    ?.let { otherEntity ->
                        Pair(
                            otherEntity.iri,
                            thisEntity.iri
                        )
                    }
            }

        println("matching = $matching")

        return matching
    }

    private fun entityToString(entity: OWLEntity): String = entity.iri.remainder.get()!!.lowercase()
}
