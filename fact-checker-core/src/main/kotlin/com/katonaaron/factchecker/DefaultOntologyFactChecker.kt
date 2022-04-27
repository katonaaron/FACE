package com.katonaaron.factchecker

import com.katonaaron.commons.add
import com.katonaaron.commons.getEntityType
import com.katonaaron.onto.*
import com.katonaaron.provenance.PROVENANCE_IRI_ANNOTATION
import com.katonaaron.provenance.PROVENANCE_IRI_WORDNET
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import java.io.FileOutputStream

class DefaultOntologyFactChecker(
    private val knowledgeBase: OWLOntology,
    private val matcher: OntologyMatcher,
    private val conflictDetector: ConflictDetector,
    private val entailmentDetector: EntailmentDetector,
    private val merger: OntologyMerger,
    private val replacer: IRIReplacer,
) : OntologyFactChecker {
    private val mergedIri = IRI.create("http://katonaaron.com/merged#")

    override fun factCheck(onto: OWLOntology): FactCheckerResult {
        /*      // 1. Align the two ontologies
              val matching = matcher.matchOntologiesToPairs(matchedIri, onto, knowledgeBase)

              val toReplaceInput = matching.associate { pair ->
                  pair.copy(
                      second = IRI.create(mergedIri.namespace, pair.first.remainder.orNull())
                  )
              }

              val toReplaceKB = matching.associate { pair ->
                  Pair(
                      pair.second,
                      IRI.create(mergedIri.namespace, pair.first.remainder.orNull())
                  )
              }

              println("toReplaceInput = ${toReplaceInput}")
              println("toReplaceInput = ${toReplaceKB}")

              val o = replacer.replaceIRI(onto, toReplaceInput)
              val kb = replacer.replaceIRI(knowledgeBase, toReplaceKB)

              o.saveOntology(FileOutputStream("o-replaced.owl"))
              kb.saveOntology(FileOutputStream("kb-replaced.owl"))*/

        // 1. Align the two ontologies
        val (o, kb) = alignOntologies(onto, knowledgeBase)

        // 2. Detect entailment
        val entailmentResult = entailmentDetector.detectEntailment(o, kb)
        val entailment = entailmentResult.entailment

        println("entailmentResult result: $entailmentResult")

        if (entailmentResult.isTotalEntailment) {
            return True(entailment)
        }

        // 3. Merge the ontologies
        val merged = merger.merge(mergedIri, o, kb)
        merged.saveOntology(FileOutputStream("merged.owl"))

        // 4. Detect the conflict in the merged ontology
        val result = conflictDetector.detectConflict(merged)

        println("conflictDetector result: $result")

        if (result !is NoConflict) {
            return False(result, entailment)
        }

        return Unknown(entailment)
    }

    private fun alignOntologies(onto: OWLOntology, knowledgeBase: OWLOntology): Pair<OWLOntology, OWLOntology> {
        val result = matcher.matchOntologies(onto, knowledgeBase)

        // Replace synonyms with the first IRI
        val oldToNewIri = result.synonyms.flatMap { synonym ->
            val newIri = IRI.create(mergedIri.namespace, synonym.iris.first().remainder.get())
            synonym.iris.map { oldIri -> oldIri to newIri }
        }.toMap()

        println("oldToNewIri = $oldToNewIri")

        val o = replacer.replaceIRI(onto, oldToNewIri)
        val kb = replacer.replaceIRI(knowledgeBase, oldToNewIri)

        // Replace synonyms in the hypernyms
        val hypernyms = result.hypernyms.map { (parent, child) ->
            Hypernym(
                oldToNewIri[parent] ?: parent,
                oldToNewIri[child] ?: child
            )
        }

        // Add subclass axioms to the knowledge base
        val df = kb.owlOntologyManager.owlDataFactory
        val axioms = hypernyms.mapNotNull { (parent, child) ->
            val typeParent = getEntityType(kb, parent) ?: getEntityType(o, parent)!!
            val typeChild = getEntityType(kb, child) ?: getEntityType(o, child)!!

            when {
                typeParent.isOWLClass && typeChild.isOWLClass ->
                    df.getOWLSubClassOfAxiom(df.getOWLClass(child), df.getOWLClass(parent))
                typeParent.isOWLClass && typeChild.isOWLNamedIndividual ->
                    df.getOWLClassAssertionAxiom(df.getOWLClass(parent), df.getOWLNamedIndividual(child))
                else -> {
                    System.err.println("Wrong entity type in hypernym: parent <$parent>: $typeParent child <$child>: $typeChild")
                    null
                }
            }?.getAnnotatedAxiom(
                setOf(
                    df.getOWLAnnotation(
                        df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION),
                        PROVENANCE_IRI_WORDNET
                    )
                )
            )
        }.toSet()
        kb.add(axioms)

        // DEBUG: save the replaced ontologies
        o.saveOntology(FileOutputStream("o-replaced.owl"))
        kb.saveOntology(FileOutputStream("kb-replaced.owl"))

        return Pair(o, kb)
    }
}
