package com.katonaaron.fred.learner

import com.katonaaron.commons.logger
import com.katonaaron.onto.OntologyLearner
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLOntology

class FredOntologyLearner(
    private val fredURL: String,
    private val fredKey: String,
) : OntologyLearner {

    override fun learnOntologyFromText(text: String): OWLOntology {
        val man = OWLManager.createOWLOntologyManager()

        val client = ApacheClient()
        val request = Request(Method.GET, fredURL)
            .header("Authorization", "Bearer $fredKey")
            .header("Accept", "application/rdf+xml")
            .query("text", text)
            .query("wsd", "true")
//            .query("semantic-subgraph", "true")

        logger.info("Sending \"${text.trim()}\" to FRED services")
        val response = client(request)

        return man.loadOntologyFromOntologyDocument(response.body.stream)
    }
}
