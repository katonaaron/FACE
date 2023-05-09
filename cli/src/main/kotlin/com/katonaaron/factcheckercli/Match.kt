package com.katonaaron.factcheckercli

import com.katonaaron.onto.OntologyMatcher
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.semanticweb.owlapi.apibinding.OWLManager
import java.io.File

@OptIn(ExperimentalCli::class)
class Match : Subcommand("match", "Perform matching on ontologies"), KoinComponent {
    private val matcher by inject<OntologyMatcher>()

    private val onto1 by argument(ArgType.String, description = "First ontology")
    private val onto2 by argument(ArgType.String, description = "Second ontology")

    override fun execute() {
        val man = OWLManager.createOWLOntologyManager()
        val o1 = man.loadOntologyFromOntologyDocument(File(onto1))
        val o2 = man.loadOntologyFromOntologyDocument(File(onto2))

        val res = matcher.matchOntologies(o1, o2)

        println("res = $res")
    }
}
