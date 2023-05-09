package com.katonaaron.fred.processor

import com.katonaaron.commons.getOWLClass
import com.katonaaron.commons.isCoherent
import com.katonaaron.onto.OntologyProcessor
import com.katonaaron.processor.FilterProcessor
import com.katonaaron.processor.RemoveClassAndItsInstancesProcessor
import com.katonaaron.processor.RemoveWithSameIriAsAClassProcessor
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class FredOntologyProcessor(
    private val reasonerFactory: OWLReasonerFactory
) : OntologyProcessor {
    private val iriFred = "http://www.ontologydesignpatterns.org/ont/fred/domain.owl#"

    private val classesToRemove = listOf<String>(
//        "http://www.essepuntato.it/2008/12/earmark#PointerRange",
//        "http://www.essepuntato.it/2008/12/earmark#StringDocuverse"
    )

    override fun processOntology(onto: OWLOntology) {
        val df = onto.owlOntologyManager.owlDataFactory

        RemoveClassAndItsInstancesProcessor(
            reasonerFactory,
            *classesToRemove.map { df.getOWLClass(it) }.toTypedArray()
        )
            // START Process Situations
            .next(NegationProcessor(reasonerFactory, iriFred))
            // END Process Situations
            .next(
                RemoveClassAndItsInstancesProcessor(
                    reasonerFactory,
                    df.getOWLClass("${iriFred}Situation")
                )
            )
            .next(TypeDisjunctionProcessor(reasonerFactory, iriFred))
            .next(RemoveWithSameIriAsAClassProcessor(iriFred)) // TODO: Sure?
            .next(VerbNetRoleProcessor(reasonerFactory, iriFred))
            .next(
                FilterProcessor {
                    when {
                        it.isBuiltIn -> true
                        it.isOWLClass && it.iri.namespace != iriFred -> false
                        it.isOWLNamedIndividual && it.iri.namespace != iriFred -> false
                        it.isOWLObjectProperty && it.iri.namespace != iriFred -> false
                        else -> true
                    }
                }
            )
            .processOntology(onto)

        val reasoner = reasonerFactory.createReasoner(onto)
        assert(reasoner.isConsistent)
        assert(reasoner.isCoherent)
    }
}
