package com.katonaaron.factchecker

import com.katonaaron.commons.clone
import com.katonaaron.commons.logger
import com.katonaaron.commons.remove
import com.katonaaron.onto.*
import com.katonaaron.onto.OntologyFactCheckerResult.*
import com.katonaaron.provenance.PROVENANCE_IRI_INPUT
import com.katonaaron.provenance.annotateProvenance
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import java.io.FileOutputStream


class DefaultOntologyFactChecker(
    knowledgeBase: OWLOntology,
    private val aligner: OntologyAligner,
    private val conflictDetector: ConflictDetector,
    private val entailmentDetector: EntailmentDetector,
    private val merger: OntologyMerger,
) : OntologyFactChecker {
    private val _knowledgeBase = knowledgeBase
    override val knowledgeBase: OWLOntology
        get() = _knowledgeBase.clone()

    private val mergedIri = IRI.create("http://katonaaron.com/merged#")

    override fun factCheck(onto: OWLOntology): OntologyFactCheckerResult {
        // 1. Annotate the axioms of the input ontology with the source IRI
        val ontoAnnotated = annotateProvenance(onto, PROVENANCE_IRI_INPUT)

        // 2. Align the two ontologies
        val (o, kb) = aligner.alignOntologies(ontoAnnotated, _knowledgeBase)
        // DEBUG: save the aligned ontologies
//        o.saveOntology(FileOutputStream("o-aligned.owl"))
//        kb.saveOntology(FileOutputStream("kb-aligned.owl"))
        logger.debug("Alignment finished")

        // 3. Detect entailment
        val entailmentResult = entailmentDetector.detectEntailment(o, kb)
        val entailment = entailmentResult.entailment

        logger.trace("entailmentResult result: $entailmentResult")

        if (entailmentResult.isTotalEntailment) {
            return True(entailment, o)
        }

        // 4. Merge the ontologies
        val merged = merger.merge(mergedIri, o, kb)
//        merged.saveOntology(FileOutputStream("merged.owl"))

        merged.remove(entailment.entailedAxioms.map { it.axiom.axiom }.toSet())

        // 5. Detect the conflict in the merged ontology
        val conflict = conflictDetector.detectConflict(merged)

        logger.trace("conflictDetector result: $conflict")

        if (conflict != null) {
            return False(conflict, entailment, o)
        }

        return Unknown(entailment, o)
    }

}
