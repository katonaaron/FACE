package com.katonaaron.aligner

import com.katonaaron.commons.add
import com.katonaaron.commons.getEntityType
import com.katonaaron.commons.logger
import com.katonaaron.onto.IRIReplacer
import com.katonaaron.onto.OntologyAligner
import com.katonaaron.onto.OntologyMatcher
import com.katonaaron.provenance.PROVENANCE_IRI_ANNOTATION
import org.semanticweb.owlapi.model.OWLOntology

class DefaultOntologyAligner(
    private val matcher: OntologyMatcher,
    private val replacer: IRIReplacer,
) : OntologyAligner {

    override fun alignOntologies(onto: OWLOntology, base: OWLOntology): Pair<OWLOntology, OWLOntology> {
        val result = matcher.matchOntologies(onto, base)

        // Replace IRIs in synonym sets with an IRI from the base ontology included in the set
        val baseIris = base.signature.map { it.iri }.toSet()
        val oldToNewIri = result.synonyms.flatMap { synonym ->
            val newIri = synonym.iris.find { baseIris.contains(it) }!! // Must be present
            synonym.iris.map { oldIri -> oldIri to newIri }
        }.toMap()

        logger.trace("oldToNewIri = $oldToNewIri")

        val o = replacer.replaceIRI(onto, oldToNewIri)
        val kb = replacer.replaceIRI(base, oldToNewIri)

        // Replace synonyms in the hypernyms
        val hypernyms = result.hypernyms.map { hypernym ->
            hypernym.run {
                copy(
                    parent = oldToNewIri[parent] ?: parent,
                    child = oldToNewIri[child] ?: child
                )
            }
        }

        // Add subclass axioms to the knowledge base
        val df = kb.owlOntologyManager.owlDataFactory
        val axioms = hypernyms.mapNotNull { (parent, child, matcherIri) ->
            val typeParent = getEntityType(kb, parent) ?: getEntityType(o, parent)!!
            val typeChild = getEntityType(kb, child) ?: getEntityType(o, child)!!

            when {
                typeParent.isOWLClass && typeChild.isOWLClass ->
                    df.getOWLSubClassOfAxiom(df.getOWLClass(child), df.getOWLClass(parent))
                typeParent.isOWLClass && typeChild.isOWLNamedIndividual ->
                    df.getOWLClassAssertionAxiom(df.getOWLClass(parent), df.getOWLNamedIndividual(child))
                else -> {
                    logger.error("Wrong entity type in hypernym: parent <$parent>: $typeParent child <$child>: $typeChild")
                    null
                }
            }?.getAnnotatedAxiom(
                setOf(
                    df.getOWLAnnotation(
                        df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION),
                        matcherIri
                    )
                )
            )
        }.toSet()
        kb.add(axioms)

        return Pair(o, kb)
    }
}
