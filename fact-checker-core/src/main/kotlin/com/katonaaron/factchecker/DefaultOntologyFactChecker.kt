package com.katonaaron.factchecker

import com.katonaaron.commons.logger
import com.katonaaron.onto.*
import com.katonaaron.onto.OntologyFactCheckerResult.*
import com.katonaaron.provenance.PROVENANCE_IRI_INPUT
import com.katonaaron.provenance.annotateProvenance
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import java.io.FileOutputStream


class DefaultOntologyFactChecker(
    private val knowledgeBase: OWLOntology,
    private val aligner: OntologyAligner,
    private val conflictDetector: ConflictDetector,
    private val entailmentDetector: EntailmentDetector,
    private val merger: OntologyMerger,
) : OntologyFactChecker {
    private val mergedIri = IRI.create("http://katonaaron.com/merged#")

    override fun factCheck(onto: OWLOntology): OntologyFactCheckerResult {
        // 1. Annotate the axioms of the input ontology with the source IRI
        val ontoAnnotated = annotateProvenance(onto, PROVENANCE_IRI_INPUT)

        // 2. Align the two ontologies
        val (o, kb) = aligner.alignOntologies(ontoAnnotated, knowledgeBase)
        // DEBUG: save the aligned ontologies
        o.saveOntology(FileOutputStream("o-aligned.owl"))
        kb.saveOntology(FileOutputStream("kb-aligned.owl"))

        // 3. Detect entailment
        val entailmentResult = entailmentDetector.detectEntailment(o, kb)
        val entailment = entailmentResult.entailment

        logger.trace("entailmentResult result: $entailmentResult")

        if (entailmentResult.isTotalEntailment) {
            return True(entailment)
        }

        // 4. Merge the ontologies
        val merged = merger.merge(mergedIri, o, kb)
        merged.saveOntology(FileOutputStream("merged.owl"))

        // 5. Detect the conflict in the merged ontology
        val result = conflictDetector.detectConflict(merged)

        logger.trace("conflictDetector result: $result")

        if (result !is NoConflict) {
            return False(result, entailment)
        }

        return Unknown(entailment)
    }
}
