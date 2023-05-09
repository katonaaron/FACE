package com.katonaaron.factcheckercli

import com.katonaaron.commons.axiomsToOntology
import com.katonaaron.factchecker.Decision
import com.katonaaron.factchecker.FactCheckerServiceResult
import com.katonaaron.onto.*
import com.katonaaron.onto.OntologyFactCheckerResult.*

fun Collection<String>.printOntologySentences() {
    forEachIndexed { idx, sentence -> println("${idx + 1}.$sentence") }
}

fun printOntologyFactCheckerResult(verbalizer: OntologyVerbalizer, result: OntologyFactCheckerResult) {
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
    printConflictExplanations(verbalizer, inconsistency.explanations)
}

fun printUnsatisfiableClasses(verbalizer: OntologyVerbalizer, explanations: Collection<ClassConflictExplanation>) {
    explanations.forEachIndexed { idx, explanation ->
        println("Unsatisfiable class #${idx + 1}: ${explanation.clazz}")
        printConflictExplanations(verbalizer, explanation.justifications)
        println()
    }
}

fun Axiom.verbalize(verbalizer: OntologyVerbalizer): String {
    val o = axiomsToOntology(listOf(axiom))
    val text = verbalizer.verbalizeOntology(o)

    return "$text (${sources.joinToString(", ")})"
}

fun printAxiomExplanations(verbalizer: OntologyVerbalizer, explanations: Collection<AxiomExplanation>) {
    explanations.forEachIndexed { idx, explanation ->
        println("Axiom #${idx + 1}: " + explanation.axiom.verbalize(verbalizer))
        printExplanations(verbalizer, explanation.justifications)
        println()
    }
}

fun printExplanations(verbalizer: OntologyVerbalizer, explanations: Collection<Explanation>) {
    explanations.forEachIndexed { index, expl ->
        println("Explanation #${index + 1}:")
        printExplanation(verbalizer, expl)
    }
}

fun printConflictExplanations(verbalizer: OntologyVerbalizer, explanations: Collection<ConflictExplanation>) {
    explanations.forEachIndexed { index, expl ->
        println("Explanation #${index + 1}:")
        printConflictExplanation(verbalizer, expl)
    }
}

fun printExplanation(verbalizer: OntologyVerbalizer, explanation: Explanation) {
    val axioms = explanation.axioms

    axioms.print(verbalizer)

    /*    val explOnto = axiomsToOntology(axioms)
        println(verbalizer.verbalizeOntology(explOnto))*/
//    println("axioms: $axioms")

    // Debug
//    explOnto.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("explanation.owl"))
}

fun printConflictExplanation(verbalizer: OntologyVerbalizer, conflictExplanation: ConflictExplanation) {
    println("Input claims:")
    conflictExplanation.inputAxioms.print(verbalizer)
    println("Trusted facts:")
    conflictExplanation.trustedAxioms.print(verbalizer)
}

fun Set<Axiom>.print(verbalizer: OntologyVerbalizer) {
    forEachIndexed { idx, axiom -> println("${idx + 1}.${axiom.verbalize(verbalizer)}") }
}


fun printFactCheckerServiceResult(result: FactCheckerServiceResult) {
    val decision = when (result.decision) {
        Decision.FALSE -> "False"
        Decision.TRUE -> "True"
        Decision.UNKNOWN -> "Unknown"
    }
    println("The given text is: $decision\n")

    if (result.inconsistency.isNotEmpty()) {
        println("Inconsistency:")
        printFactCheckerServiceConflictExplanations(result.inconsistency)
        println()
    }

    if (result.unsatisfiableClasses.isNotEmpty()) {
        println("Incoherence:")
        printFactCheckerServiceClassExplanations(result.unsatisfiableClasses)
        println()
    }

    printFactCheckerServiceEntailment(result.entailedAxioms)
}

fun printFactCheckerServiceEntailment(entailedAxioms: Set<com.katonaaron.factchecker.AxiomExplanation>) {
    if (entailedAxioms.isEmpty()) {
        println("Entailed axioms: none")
        return
    }

    println("Entailed axioms:")
    printFactCheckerServiceAxiomExplanations(entailedAxioms)
}

fun printFactCheckerServiceAxiomExplanations(explanations: Collection<com.katonaaron.factchecker.AxiomExplanation>) {
    explanations.forEachIndexed { idx, explanation ->
        println("Axiom #${idx + 1}: " + explanation.axiom.formatNoSource())
        printFactCheckerServiceExplanations(explanation.justifications)
        println()
    }
}

fun printFactCheckerServiceClassExplanations(explanations: Collection<com.katonaaron.factchecker.ClassConflictExplanation>) {
    explanations.forEachIndexed { idx, explanation ->
        println("Unsatisfiable class #${idx + 1}: ${explanation.clazz}")
        printFactCheckerServiceConflictExplanations(explanation.justifications)
        println()
    }
}

fun printFactCheckerServiceExplanations(explanations: Collection<com.katonaaron.factchecker.Explanation>) {
    explanations.forEachIndexed { idx, expl ->
        println("Explanation #${idx + 1}:")
        expl.axioms.printFormatted()
        println()
    }
}

fun printFactCheckerServiceConflictExplanations(explanations: Collection<com.katonaaron.factchecker.ConflictExplanation>) {
    explanations.forEachIndexed { idx, expl ->
        println("Explanation #${idx + 1}:")
        println("\tknowledge from input text:")
        expl.inputAxioms.printFormatted(false)
        println("\tknowledge from trusted sources (counterspeech):")
        expl.trustedAxioms.printFormatted()
        println()
    }
}

fun Set<com.katonaaron.factchecker.Axiom>.printFormatted(withSource: Boolean = true) {
    forEachIndexed { idx, axiom -> println("${idx + 1}.${if (withSource) axiom.format() else axiom.formatNoSource()}") }
}


fun com.katonaaron.factchecker.Axiom.format(): String = "$sentence (${sources.joinToString(", ")})"
fun com.katonaaron.factchecker.Axiom.formatNoSource(): String = sentence
