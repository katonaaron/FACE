package com.katonaaron.factcheckercli

import com.katonaaron.onto.OntologyVerbalizer
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.semanticweb.owlapi.apibinding.OWLManager
import java.io.File

@OptIn(ExperimentalCli::class)
class Verbalize : Subcommand("verbalize", "Verbalize an ontology"), KoinComponent {
    private val verbalizer by inject<OntologyVerbalizer>()

    private val onto by argument(ArgType.String, description = "The ontology to be verbalized")

    override fun execute() {
        val man = OWLManager.createOWLOntologyManager()
        val o = man.loadOntologyFromOntologyDocument(File(onto))

        verbalizer.verbalizeOntology(o).printOntologySentences()
    }
}
