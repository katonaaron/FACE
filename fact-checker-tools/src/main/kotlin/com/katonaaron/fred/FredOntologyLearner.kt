package com.katonaaron.fred

import com.katonaaron.onto.OntologyLearner
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLOntology

class FredOntologyLearner : OntologyLearner {

    companion object {
        private const val URL = "http://wit.istc.cnr.it/stlab-tools/fred"
        private const val AUTH_KEY = "5ed19e5f-8692-389d-a1f3-cd2096c54e73"
    }

    override fun learnOntologyFromText(text: String): OWLOntology {
        val man = OWLManager.createOWLOntologyManager()

        val client = ApacheClient()
        val request = Request(Method.GET, URL)
            .header("Authorization", "Bearer $AUTH_KEY")
            .header("Accept", "application/rdf+xml")
            .query("text", text)
            .query("wsd", "true")

        println("Sending \"${text.trim()}\" to FRED services")
        val response = client(request)

        return man.loadOntologyFromOntologyDocument(response.body.stream)
    }
}
