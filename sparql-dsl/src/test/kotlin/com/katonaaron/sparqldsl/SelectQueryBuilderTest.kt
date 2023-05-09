package com.katonaaron.sparqldsl

import org.semanticweb.HermiT.Reasoner
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SelectQueryBuilderTest {

    @Test
    fun `test multiple variables in select`() {
        val man = OWLManager.createOWLOntologyManager()
        val o = man.createOntology()
        val reasoner = ReasonerFactory().createReasoner(o)
        val engine = QueryEngine(man, reasoner, strict = false)

        val x = Var("x")
        val y = Var("y")

        val results = sparqlSelect(engine) {
            prefix("fred:", "http://www.ontologydesignpatterns.org/ont/fred/domain.owl#")
            prefix("boxing:", IRI.create("http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"))

            select(x, y, distinct = true)

            where {
                clazz(x)
                clazz(y)
                equivalentClass(x, "fred:frog")
            }
            where {
                clazz("fred:haha")
            }
        }
    }

//    @Test
//    fun `test all variables in select`() {
//        val query = selectQueryBuilder {
//            prefix("fred:", IRI.create("http://www.ontologydesignpatterns.org/ont/fred/domain.owl#"))
//            prefix("boxing:", IRI.create("http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"))
//
//            select()
//
//            where {
//                clazz(x)
//                clazz(y)
//            }
//        }
//
//        assertEquals("""
//            PREFIX fred: <http://www.ontologydesignpatterns.org/ont/fred/domain.owl#>
//            PREFIX boxing: <http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#>
//            SELECT DISTINCT ?x ?y
//            WHERE {
//            	Class(?x),
//            	Class(?y),
//            	EquivalentClass(?x, fred:frog)
//            }
//            OR
//            WHERE {
//            	Class(fred:haha)
//            }
//            OR
//            WHERE {
//            	Class("Joe")
//            }
//            OR
//            WHERE {
//            	Class(<asdas>)
//            }
//
//        """.trimIndent(),
//            query)
//    }

    @Test
    fun `test multiple functions`() {
        val x = Var("x")
        val y = Var("y")

        val query = selectQueryBuilder {
            prefix("fred:", IRI.create("http://www.ontologydesignpatterns.org/ont/fred/domain.owl#"))
            prefix("boxing:", IRI.create("http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"))

            select(x, y, distinct = true)

            where {
                clazz(x)
                clazz(y)
                equivalentClass(x, "fred:frog")
            }
            where {
                clazz("fred:haha")
            }
            where {
                clazz(OWLLiteralImplString("Joe"))
            }
            where {
                clazz(IRI.create("asdas"))
            }
        }
        assertEquals(
            """
            PREFIX fred: <http://www.ontologydesignpatterns.org/ont/fred/domain.owl#> 
            PREFIX boxing: <http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#> 
            SELECT DISTINCT ?x ?y 
            WHERE {
            	Class(?x),
            	Class(?y),
            	EquivalentClass(?x, fred:frog)
            }
            OR
            WHERE {
            	Class(fred:haha)
            }
            OR
            WHERE {
            	Class("Joe")
            }
            OR
            WHERE {
            	Class(<asdas>)
            }
            
        """.trimIndent(),
            query
        )
    }
}
