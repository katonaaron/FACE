package com.katonaaron.onto

interface EntailmentDetectorFactory {
    fun createEntailmentDetector(explanationGenerator: OntologyExplanationGenerator): EntailmentDetector
}
