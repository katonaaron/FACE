package com.katonaaron.learner

import com.katonaaron.commons.logger
import com.katonaaron.onto.OntologyLearner
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLOntology
import java.io.File

class DummyOntologyLearner(private val path: String) : OntologyLearner {
    override fun learnOntologyFromText(text: String): OWLOntology {
        logger.info("Using dummy ontology from: $path")
        val man = OWLManager.createOWLOntologyManager()
        return man.loadOntologyFromOntologyDocument(File(path))
    }
}
