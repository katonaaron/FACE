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

        if (!response.status.successful) {
            if (response.status.code == 429) {
                throw FredException("FRED: too many request: headers = ${response.headers}")
            } else {
                throw FredException("FRED returned an unsuccessful status: ${response.status.code}")
            }
        }

        val o = man.loadOntologyFromOntologyDocument(response.body.stream)

        if (o.logicalAxioms.size == 0) {
            throw FredException("FRED did not learn any axiom")
        }

        return o
    }
}
