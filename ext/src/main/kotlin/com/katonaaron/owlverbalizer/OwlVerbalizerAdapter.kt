package com.katonaaron.owlverbalizer

import ch.uzh.ifi.attempto.owl.OutputType
import ch.uzh.ifi.attempto.owl.VerbalizerWebservice
import com.katonaaron.commons.axiomsToOntology
import com.katonaaron.commons.toFormattedString
import com.katonaaron.onto.OntologyVerbalizer
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLOntology

class OwlVerbalizerAdapter(url: String) : OntologyVerbalizer {
    private val verbalizer = VerbalizerWebservice(url)

    override fun verbalizeOntology(ontology: OWLOntology): List<String> {
        val newAxioms = ontology.logicalAxioms.map {
            it.axiomWithoutAnnotations
        }
        val o = axiomsToOntology(newAxioms)

        return verbalizer.call(o.toFormattedString(OWLXMLDocumentFormat()), OutputType.ACE)
            .replace("\\n+".toRegex(), "\n")
            .replace("\\n+$".toRegex(), "")
            .split("\n")
    }

    override fun verbalizeAxiom(axiom: OWLAxiom): String {
        return verbalizeOntology(axiomsToOntology(axiom)).first()
    }
}
