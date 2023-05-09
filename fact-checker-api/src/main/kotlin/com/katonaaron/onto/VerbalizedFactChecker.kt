package com.katonaaron.onto

interface VerbalizedFactChecker {
    val knowledgeBase: VerbalizedOntology

    fun factCheck(text: String): VerbalizedFactCheckerResult
}
