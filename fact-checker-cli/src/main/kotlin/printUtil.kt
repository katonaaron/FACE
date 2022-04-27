import com.katonaaron.commons.axiomsToOntology
import com.katonaaron.onto.*
import com.katonaaron.provenance.PROVENANCE_IRI_ANNOTATION
import org.semanticweb.owlapi.model.OWLAxiom

fun printFactCheckerResult(verbalizer: OntologyVerbalizer, result: FactCheckerResult) {
    print("The given ontology is: ")
    when (result) {
        is False -> {
            println("False")

            val reason = result.reason
            print("Reason: ")
            when (reason) {
                is Incoherence -> {
                    printIncoherence(verbalizer, reason)
                }
                is Inconsistency -> {
                    printInconsistency(verbalizer, reason)
                }
                NoConflict -> throw RuntimeException("Should not be false if there is no conflict")
            }
        }
        is True -> {
            println("True")
        }
        is Unknown -> println("Unknown")
    }

    printEntailment(verbalizer, result.entailment)
}

fun printEntailment(verbalizer: OntologyVerbalizer, entailment: Entailment) {
    if (entailment.isEmpty) {
        println("Entailed axioms: none")
        return
    }

    println("Entailed axioms:")
    printAxiomExplanations(verbalizer, entailment.entailedAxioms)
}

fun printIncoherence(verbalizer: OntologyVerbalizer, incoherence: Incoherence) {
    println("Incoherence")
    printUnsatisfiableClasses(verbalizer, incoherence.explanations)
}

fun printInconsistency(verbalizer: OntologyVerbalizer, inconsistency: Inconsistency) {
    println("Inconsistency")
    printExplanations(verbalizer, inconsistency.explanations)
}

fun printUnsatisfiableClasses(verbalizer: OntologyVerbalizer, explanations: Collection<ClassExplanation>) {
    explanations.forEachIndexed { idx, explanation ->
        println("Unsatisfiable class #$idx: ${explanation.clazz}")
        printExplanations(verbalizer, explanation.justifications)
        println()
    }
}

fun OWLAxiom.verbalize(verbalizer: OntologyVerbalizer): String {
    val o = axiomsToOntology(listOf(this))
    val df = o.owlOntologyManager.owlDataFactory
    val text = verbalizer.verbalizeOntology(o)
    val source = this.getAnnotations(df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION)).first()!!.value

    return "$text <$source>"
}

fun printAxiomExplanations(verbalizer: OntologyVerbalizer, explanations: Collection<AxiomExplanation>) {
    explanations.forEachIndexed { idx, explanation ->
        println("Axiom #$idx: " + explanation.axiom.verbalize(verbalizer))
        printExplanations(verbalizer, explanation.justifications)
        println()
    }
}

fun printExplanations(verbalizer: OntologyVerbalizer, explanations: Collection<Explanation>) {
    explanations.forEachIndexed { index, expl ->
        println("Explanation #$index:")
        printExplanation(verbalizer, expl)
    }
}

fun printExplanation(verbalizer: OntologyVerbalizer, explanation: Explanation) {
    val axioms = explanation.axioms

    axioms.forEach { println(it.verbalize(verbalizer)) }

/*    val explOnto = axiomsToOntology(axioms)
    println(verbalizer.verbalizeOntology(explOnto))*/
//    println("axioms: $axioms")

    // Debug
//    explOnto.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("explanation.owl"))
}
