package com.katonaaron.aligner

import com.katonaaron.commons.*
import com.katonaaron.onto.Hypernym
import com.katonaaron.onto.OntologyAligner
import com.katonaaron.onto.OntologyMatcher
import com.katonaaron.onto.Synonym
import com.katonaaron.provenance.PROVENANCE_IRI_ANNOTATION
import com.katonaaron.provenance.PROVENANCE_IRI_INPUT
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.model.parameters.OntologyCopy
import org.semanticweb.owlapi.search.EntitySearcher
import java.io.FileOutputStream

class DefaultOntologyAligner(
    private val matcher: OntologyMatcher,
) : OntologyAligner {
    private val regex = """^(.+)_[0-9]+${'$'}""".toRegex() // e.g. virus_1 matches virus

    private val iriFred = "http://www.ontologydesignpatterns.org/ont/fred/domain.owl#"


    override fun alignOntologies(onto: OWLOntology, base: OWLOntology): Pair<OWLOntology, OWLOntology> {
        val man = OWLManager.createOWLOntologyManager()
        val o = man.copyOntology(onto, OntologyCopy.DEEP)
        val b = man.copyOntology(base, OntologyCopy.DEEP)

        val result = matcher.matchOntologies(o, b)

        val matching = handleSynonyms(o, b, result.synonyms)

        logger.trace("matching = $matching")

        // Replace synonyms in the hypernyms and remove the removed entities
        val hypernyms = result.hypernyms.mapNotNull { hypernym ->
            val newHypernym = hypernym.run {
                copy(
                    parent = matching[parent] ?: parent,
                    child = matching[child] ?: child
                )
            }
            newHypernym.run {
                if ((!o.containsEntityInSignature(parent) && !b.containsEntityInSignature(parent))
                    || (!o.containsEntityInSignature(child) && !b.containsEntityInSignature(child))
                ) {
                    println("not contained: $parent $child")
                    null
                } else {
                    this
                }
            }
        }

        handleHypernyms(o, b, hypernyms)

        return Pair(o, b)
    }

    private fun handleHypernyms(
        onto: OWLOntology,
        base: OWLOntology,
        hypernyms: Collection<Hypernym>,
    ) {
        val df = base.owlOntologyManager.owlDataFactory

        // Add subclass axioms to the knowledge base
        val axioms = hypernyms.mapNotNull { (parent, child, matcherIri) ->
            val typeParent = getEntityType(base, parent) ?: getEntityType(onto, parent)!!
            val typeChild = getEntityType(base, child) ?: getEntityType(onto, child)!!

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
        base.add(axioms)
    }

    private fun handleSynonyms(onto: OWLOntology, base: OWLOntology, synonyms: Collection<Synonym>): Map<IRI, IRI> {
        val toAdd = mutableSetOf<OWLAxiom>()
        val toReplace = mutableMapOf<IRI, IRI>()
        val toRemove = mutableSetOf<OWLEntity>()
        val alias = mutableMapOf<IRI, IRI>()

        val indToConcept = mutableMapOf<OWLIndividual, OWLClass>()


        val baseIris = base.signature.map { it.iri }.toSet()
        val matching = synonyms.flatMap { synonym ->
            //TODO
//            if(synonym.iris.size != 2) {
//                throw UnsupportedOperationException("Synonym of 2 entities is only supported")
//            }

            val newIri = synonym.iris.find { baseIris.contains(it) }!! // Must be present
            synonym.iris
                .filter { it != newIri }
                .map { oldIri -> oldIri to newIri }
        }.toMap().toMutableMap()

        logger.trace("initial matching: $matching")


        // Save all roles
        val roles: List<Triple<OWLNamedIndividual, OWLObjectPropertyExpression, OWLIndividual>> =
            onto.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION).map { axiom ->
                Triple(axiom.subject.asOWLNamedIndividual(), axiom.property, axiom.`object`.asOWLNamedIndividual())
            }


        val df = onto.owlOntologyManager.owlDataFactory

        // FRED specific: Handle cases when an instance is the same as an individual: e.g. virus_1:Virus
        val conceptMap = onto.classesInSignature
            .filter { it.iri.namespace == iriFred }
            .associateBy { it.iri.remainder.get().lowercase().trim() }
        onto.individualsInSignature
            .filter { it.iri.namespace == iriFred }
            .forEach { ind ->
                val iriInd = ind.iri
                val nameInd = iriInd.remainder.get()
                val nameNew = regex.find(nameInd)?.run { groups[1] }?.value ?: return@forEach

                val concept: OWLClass = conceptMap[nameNew] ?: return@forEach

                logger.trace("Found instance and class correspondence: $ind $concept")

                val kbEntity = matching[iriInd] ?: matching[concept.iri]
                assert(kbEntity == matching[concept.iri])

                if (kbEntity == null) {
                    logger.trace("Knowledge base entity is not found for $ind $concept: $kbEntity")
                    // The knowledge base entity comes from a different source, than b (e.g. Wordnet)
                    // Thus any of the two cases below can be chosen.
                    // Choose to convert to a concept
                    convertIndToConcept(onto, ind, concept, toAdd, toRemove, indToConcept)

                    /* // Choose to convert to an instance

                     // NOTE: Assumed that there are no role constraints
                     convertConceptToInd(onto, concept, ind, toAdd, toRemove)

                     val iriNew = IRI.create(iriInd.namespace, nameNew)
                     toReplace[iriInd] = iriNew
                     alias[iriInd] = iriNew*/


                    return@forEach
                }

                val kbEntityType = getEntityType(base, kbEntity)!!
                when {
                    kbEntityType.isOWLClass -> {
                        logger.trace("Knowledge base entity is concept for $ind $concept: $kbEntity")
                        // The knowledge base entity is a concept. Convert instance to concept.

                        convertIndToConcept(onto, ind, concept, toAdd, toRemove, indToConcept)

                    }
                    kbEntityType.isOWLNamedIndividual -> {
                        logger.trace("Knowledge base entity is individual for $ind $concept: $kbEntity")
                        // The knowledge base entity is an instance. Convert concept to instance
                        // Add superclasses and equivalent classes of the concept as types for the
                        // individual

                        // NOTE: Assumed that there are no role constraints
                        convertConceptToInd(onto, concept, ind, toAdd, toRemove)

                        val iriNew = IRI.create(iriInd.namespace, nameNew)
                        toReplace[iriInd] = iriNew
                    }
                    else -> throw IllegalStateException("Wrong kb entity type: $kbEntityType")
                }


            }

        onto.add(toAdd.map {
            it.getAnnotatedAxiom(
                setOf(
                    df.getOWLAnnotation(
                        df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION),
                        PROVENANCE_IRI_INPUT
                    )
                )
            )
        })
//        replaceIRI(onto, toReplace)
//        removeEntity(onto, toRemove)
//
//        toReplace.forEach { (oldIri, newIri) ->
//            matching[newIri] = matching[oldIri]!!
//            matching.remove(oldIri)
//        }
        toRemove.forEach { matching.remove(it.iri) }

        logger.trace("matching after FRED fix: $matching")


        // Perform changes based on the synonym pairs
//        toAdd.clear()
//        toReplace.clear()
//        toRemove.clear()

        matching.forEach { (oIri, kbIri) ->
            val oType = getEntityType(onto, oIri)!!
            val kbType = getEntityType(base, kbIri)!!

            when {
                // TODO: Support other types as well
                (oType.isOWLClass && kbType.isOWLClass)
                        || (oType.isOWLNamedIndividual && kbType.isOWLNamedIndividual)
                        || (oType.isOWLObjectProperty && kbType.isOWLObjectProperty) -> {
                    toReplace[oIri] = kbIri
                }
                oType.isOWLClass && kbType.isOWLNamedIndividual -> {
                    convertConceptToInd(onto, df.getOWLClass(oIri), df.getOWLNamedIndividual(kbIri), toAdd, toRemove)
                }
                oType.isOWLNamedIndividual && kbType.isOWLClass -> {
                    convertIndToConcept(
                        onto,
                        df.getOWLNamedIndividual(oIri),
                        df.getOWLClass(kbIri),
                        toAdd,
                        toRemove,
                        indToConcept
                    )
                }
                else -> throw IllegalStateException("Unsupported synonym between entities: $oIri ($oType) and $kbIri ($kbType)")
            }
        }

        // Fix roles
        // NOTE: Assumed that there are initially no role constraints on concepts
        roles.forEach { (source, role, target) ->
            val conceptSource = indToConcept[source]
            val conceptTarget = indToConcept[target]

            when {
                conceptSource == null && conceptTarget == null -> {
                    // Both remained individuals
                    // Do nothing
                }
                conceptSource != null && conceptTarget != null -> {
                    // Both are concepts
                    // Create existential restriction to the target concept

//                    toAdd.add(
//                        df.getOWLSubClassOfAxiom(
//                            conceptSource,
//                            df.getOWLObjectSomeValuesFrom(role, conceptTarget)
//                        )
//                    )

                    toAdd.add(
                        df.getOWLSubClassOfAxiom(
                            conceptTarget,
                            df.getOWLObjectMinCardinality(
                                1,
                                df.getOWLObjectInverseOf(role),
                                conceptSource
                            )
                        )
                    )

//                    toAdd.add(
//                        df.getOWLSubClassOfAxiom(
//                            df.getOWLObjectSomeValuesFrom(role, conceptTarget),
//                            conceptSource
//                        )
//                    )

//                    toAdd.add(
//                        df.getOWLSubClassOfAxiom(
//                            df.getOWLObjectIntersectionOf(
//                                conceptSource,
//                                df.getOWLObjectSomeValuesFrom(role, conceptTarget)
//                            ),
//                            df.owlThing
//                        )
//                    )

                }

                conceptSource != null && conceptTarget == null -> {
                    // The source is a concept, the target is an instance
                    // Create existential restriction to a nominal formed by the target

//                    toAdd.add(
//                        df.getOWLSubClassOfAxiom(
//                            conceptSource,
//                            df.getOWLObjectSomeValuesFrom(
//                                role,
//                                df.getOWLObjectOneOf(target)
//                            )
//                        )
//                    )

//                    toAdd.add(
//                        df.getOWLSubClassOfAxiom(
//                            df.getOWLObjectSomeValuesFrom(
//                                role,
//                                df.getOWLObjectOneOf(target)
//                            ),
//                            conceptSource
//                        )
//                    )

//                    toAdd.add(
//                        df.getOWLSubClassOfAxiom(
//                            df.getOWLObjectIntersectionOf(
//                                conceptSource,
//                                df.getOWLObjectSomeValuesFrom(
//                                    role,
//                                    df.getOWLObjectOneOf(target)
//                                )
//                            ),
//                            df.owlThing
//                        )
//                    )

                    toAdd.add(
                        df.getOWLSubClassOfAxiom(
                            df.getOWLObjectOneOf(target),
                            df.getOWLObjectMinCardinality(
                                1,
                                df.getOWLObjectInverseOf(role),
                                conceptSource
                            )
                        )
                    )
                }

                conceptSource == null && conceptTarget != null -> {
                    // The source is an instance, the target is a concept
                    // Create existential restriction from a nominal formed by the source, to the target

//                    toAdd.add(
//                        df.getOWLSubClassOfAxiom(
//                            df.getOWLObjectOneOf(source),
//                            df.getOWLObjectSomeValuesFrom(
//                                role,
//                                conceptTarget
//                            )
//                        )
//                    )
                    toAdd.add(
                        df.getOWLSubClassOfAxiom(
                            conceptTarget,
                            df.getOWLObjectMinCardinality(
                                1,
                                df.getOWLObjectInverseOf(role),
                                df.getOWLObjectOneOf(source)
                            )
                        )
                    )
                }
            }

        }

        logger.trace("toAdd = ${toAdd}")
        logger.trace("toReplace = ${toReplace}")
        logger.trace("toRemove = ${toRemove}")


        onto.add(toAdd.map {
            it.getAnnotatedAxiom(
                setOf(
                    df.getOWLAnnotation(
                        df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION),
                        PROVENANCE_IRI_INPUT
                    )
                )
            )
        })
        replaceIRI(onto, toReplace)
//        toReplace.forEach { (oldIri, newIri) ->
//            matching[newIri] = matching[oldIri]!!
//            matching.remove(oldIri)
//        }
        removeEntity(onto, toRemove)
        toRemove.forEach { matching.remove(it.iri) }
        matching.putAll(alias)

        logger.trace("matching after synonym handling: $matching")
        //DEBUG
        onto.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("align-synonym.owl"))

        return matching
    }

    private fun convertIndToConcept(
        onto: OWLOntology,
        ind: OWLNamedIndividual,
        concept: OWLClass,
        toAdd: MutableSet<OWLAxiom>,
        toRemove: MutableSet<OWLEntity>,
        indToConcept: MutableMap<OWLIndividual, OWLClass>
    ) {
        val df = onto.owlOntologyManager.owlDataFactory

        val types = EntitySearcher.getTypes(ind, onto).filter { it != concept }
        val roles = EntitySearcher.getObjectPropertyValues(ind, onto)

        logger.trace("Found types of $ind: $types")
        logger.trace("Found roles of $ind: $roles")

        // Convert all types as inclusions
        toAdd.addAll(
            types.map { type ->
                df.getOWLSubClassOfAxiom(concept, type)
            }
        )

        indToConcept[ind] = concept
        toRemove.add(ind)
    }

    private fun convertConceptToInd(
        onto: OWLOntology,
        concept: OWLClass,
        ind: OWLNamedIndividual,
        toAdd: MutableSet<OWLAxiom>,
        toRemove: MutableSet<OWLEntity>
    ) {
        val df = onto.owlOntologyManager.owlDataFactory

        val types = EntitySearcher.getSuperClasses(concept, onto) +
                EntitySearcher.getEquivalentClasses(concept, onto)

        logger.trace("Found types for $ind: $types")

        toAdd.addAll(
            types.map { superClass ->
                df.getOWLClassAssertionAxiom(superClass, ind)
            }
        )

        toRemove.add(concept)
    }
}
