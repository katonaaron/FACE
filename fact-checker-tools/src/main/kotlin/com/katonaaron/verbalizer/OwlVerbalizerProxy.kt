package com.katonaaron.verbalizer

import ch.uzh.ifi.attempto.owl.OutputType
import ch.uzh.ifi.attempto.owl.VerbalizerWebservice
import com.katonaaron.commons.axiomsToOntology
import com.katonaaron.commons.toFormattedString
import com.katonaaron.onto.OntologyVerbalizer
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model.OWLOntology

class OwlVerbalizerProxy(url: String) : OntologyVerbalizer {
    private val verbalizer = VerbalizerWebservice(url)

    override fun verbalizeOntology(ontology: OWLOntology): String {
        val newAxioms = ontology.axioms.map { it.axiomWithoutAnnotations }
        val o = axiomsToOntology(newAxioms)

        return verbalizer.call(o.toFormattedString(OWLXMLDocumentFormat()), OutputType.ACE)
            .replace("\\n+".toRegex(), "\n")
            .replace("\\n+$".toRegex(), "")
    }
}
