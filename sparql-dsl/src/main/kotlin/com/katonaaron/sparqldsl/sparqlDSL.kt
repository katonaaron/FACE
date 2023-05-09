package com.katonaaron.sparqldsl

import de.derivo.sparqldlapi.Query
import de.derivo.sparqldlapi.QueryResult
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLLiteral
import org.semanticweb.owlapi.model.OWLOntologyManager
import org.semanticweb.owlapi.reasoner.OWLReasoner
import org.slf4j.LoggerFactory
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString

data class Var(val name: String)

private fun de.derivo.sparqldlapi.Var.toExternal(): Var = Var(name)

private fun IRI.format(): String = "<$this>"
private fun Var.format(): String = "?$name"
private fun OWLLiteral.format(): String = "\"${literal}\""

val VAR_ALL = Var("*")
private val logger = LoggerFactory.getLogger("SparqlDLS")

private fun <T> format(primitive: T) = when (primitive) {
    is String -> {
        if (!primitive.contains(':')) {
            throw RuntimeException("Missing ':' character in prefixed iri notation: \"$primitive\"")
        }
        primitive.trim()
    }
    is IRI -> primitive.format()
    is Var -> primitive.format()
    is OWLLiteral -> primitive.format()
    else -> throw RuntimeException("Invalid primitive type")
}

data class Prefix(val prefix: String, val iri: IRI)

class Prefixes : ArrayList<Prefix>() {
    fun prefix(prefix: String, iri: IRI) {
        val trimmed = prefix.trim()
        if (!trimmed.endsWith(':')) {
            throw RuntimeException("prefix must end with ':' character")
        }
        if (trimmed.contains("\\s")) {
            throw RuntimeException("prefix must not contain any whitespace")
        }

        add(Prefix(trimmed, iri))
    }

    fun prefix(prefix: String, iriString: String) {
        prefix(prefix, IRI.create(iriString))
    }
}

sealed class PrefixedQueryBuilder {
    private var prefixes: MutableList<Prefix> = mutableListOf()

    fun prefix(prefix: String, iri: IRI) {
        val trimmed = prefix.trim()
        if (!trimmed.endsWith(':')) {
            throw RuntimeException("prefix must end with ':' character")
        }
        if (trimmed.contains("\\s")) {
            throw RuntimeException("prefix must not contain any whitespace")
        }

        prefixes.add(Prefix(trimmed, iri))
    }

    fun prefix(prefix: String, iriString: String) {
        prefix(prefix, IRI.create(iriString))
    }

    open fun build(): String {
        return buildString {
            prefixes.forEach { (prefix, iri) ->
                append("PREFIX $prefix ${iri.format()} \n")
            }
        }
    }
}

sealed class QueryBuilder {
    private var prefixes: MutableList<Prefix> = mutableListOf()

    fun prefix(prefix: String, iri: IRI) {
        val trimmed = prefix.trim()
        if (!trimmed.endsWith(':')) {
            throw RuntimeException("prefix must end with ':' character")
        }
        if (trimmed.contains("\\s")) {
            throw RuntimeException("prefix must not contain any whitespace")
        }

        prefixes.add(Prefix(trimmed, iri))
    }

    fun prefix(prefix: String, iriString: String) {
        prefix(prefix, IRI.create(iriString))
    }

    open fun build(): String {
        return buildString {
            prefixes.forEach { (prefix, iri) ->
                append("PREFIX $prefix ${iri.format()} \n")
            }
        }
    }
}

class Atoms : ArrayList<String>() {
    fun <T> clazz(a: T) {
        add("Class(${format(a)})")
    }

    fun <T> property(a: T) {
        add("Property(${format(a)})")
    }

    fun <T> individual(a: T) {
        add("Individual(${format(a)})")
    }

    fun <T, U> type(individual: T, clazz: U) {
        add("Type(${format(individual)}, ${format(clazz)})")
    }

    fun <T, U, V> propertyValue(a: T, property: U, b: V) {
        add("PropertyValue(${format(a)}, ${format(property)}, ${format(b)})")
    }

    fun <T, U> equivalentClass(a: T, b: U) {
        add("EquivalentClass(${format(a)}, ${format(b)})")
    }

    fun <T, U> subClassOf(a: T, b: U) {
        add("SubClassOf(${format(a)}, ${format(b)})")
    }

    fun <T, U> equivalentProperty(a: T, b: U) {
        add("EquivalentProperty(${format(a)}, ${format(b)})")
    }

    fun <T, U> subPropertyOf(a: T, b: U) {
        add("SubPropertyOf(${format(a)}, ${format(b)})")
    }

    fun <T, U> inverseOf(a: T, b: U) {
        add("InverseOf(${format(a)}, ${format(b)})")
    }

    fun <T> objectProperty(a: T) {
        add("ObjectProperty(${format(a)})")
    }

    fun <T> dataProperty(a: T) {
        add("DataProperty(${format(a)})")
    }

    fun <T> functional(a: T) {
        add("Functional(${format(a)})")
    }

    fun <T> inverseFunctional(a: T) {
        add("InverseFunctional(${format(a)})")
    }

    fun <T> transitive(a: T) {
        add("Transitive(${format(a)})")
    }

    fun <T> symmetric(a: T) {
        add("Symmetric(${format(a)})")
    }

    fun <T> reflexive(a: T) {
        add("Reflexive(${format(a)})")
    }

    fun <T> irreflexive(a: T) {
        add("Irreflexive(${format(a)})")
    }

    fun <T, U> sameAs(a: T, b: U) {
        add("SameAs(${format(a)}, ${format(b)})")
    }

    fun <T, U> disjointWith(a: T, b: U) {
        add("DisjointWith(${format(a)}, ${format(b)})")
    }

    fun <T, U> differentFrom(a: T, b: U) {
        add("DifferentFrom(${format(a)}, ${format(b)})")
    }

    fun <T, U> complementOf(a: T, b: U) {
        add("ComplementOf(${format(a)}, ${format(b)})")
    }

    fun <T, U, V> annotation(a: T, b: U, c: V) {
        add("Annotation(${format(a)}, ${format(b)}, ${format(c)})")
    }

    fun <T, U> strictSubClassOf(a: T, b: U) {
        add("StrictSubClassOf(${format(a)}, ${format(b)})")
    }

    fun <T, U> directSubClassOf(a: T, b: U) {
        add("DirectSubClassOf(${format(a)}, ${format(b)})")
    }

    fun <T, U> directType(a: T, b: U) {
        add("DirectType(${format(a)}, ${format(b)})")
    }

    fun <T, U> strictSubPropertyOf(a: T, b: U) {
        add("StrictSubPropertyOf(${format(a)}, ${format(b)})")
    }

    fun <T, U> directSubPropertyOf(a: T, b: U) {
        add("DirectSubPropertyOf(${format(a)}, ${format(b)})")
    }

    fun build(): String {
        return buildString {
            append("{\n")
            this@Atoms.forEachIndexed { index, atom ->
                append("\t")
                append(atom)
                if (index != this@Atoms.size - 1) {
                    append(",\n")
                }
            }
            append("\n}\n")
        }
    }
}

class SelectQueryBuilder : QueryBuilder() {
    private val whereList = mutableListOf<String>()
    private var variables = emptyList<Var>()
    private var distinct = false

    fun select(vararg variables: Var, distinct: Boolean = false) {
        if (variables.isEmpty()) {
            this.variables = listOf(VAR_ALL)
        } else {
            this.variables = variables.toList()
        }
        this.distinct = distinct
    }

    fun where(atomsBuilder: Atoms.() -> Unit) {
        whereList.add(Atoms().apply(atomsBuilder).build())
    }

    override fun build(): String {
        if (whereList.isEmpty()) {
            throw RuntimeException("At least one where clause must be added")
        }
        return buildString {
            append(super.build())
            append("SELECT ")
            if (distinct) {
                append("DISTINCT ")
            }
            if (variables.contains(VAR_ALL)) {
                append("*")
            } else {
                variables.forEach {
                    append(it.format())
                    append(" ")
                }
            }
            append("\n")
            whereList.forEachIndexed { index, where ->
                if (index != 0) {
                    append("OR\n")
                }
                append("WHERE ")
                append(where)
            }
        }
    }
}

class AskQueryBuilder : QueryBuilder() {
    private var atoms = ""

    fun ask(atomsBuilder: Atoms.() -> Unit) {
        atoms = Atoms().apply(atomsBuilder).build()
    }

    override fun build(): String {
        if (atoms.isEmpty()) {
            throw RuntimeException("At least one atom must be added")
        }
        return buildString {
            append(super.build())
            append("ASK \n")
            append(atoms)
        }
    }
}


fun queryBuilder(builderProducer: () -> QueryBuilder): String {
    val builder = builderProducer()
    return builder.build()
}

fun selectQueryBuilder(builder: SelectQueryBuilder.() -> Unit): String {
    return SelectQueryBuilder().apply(builder).build()
}

fun askQueryBuilder(builder: AskQueryBuilder.() -> Unit): String {
    return AskQueryBuilder().apply(builder).build()
}

data class QueryEngine(val manager: OWLOntologyManager, val reasoner: OWLReasoner, val strict: Boolean = false) {
    private val engine = de.derivo.sparqldlapi.QueryEngine.create(manager, reasoner, strict)

    fun execute(query: Query): QueryResult = engine.execute(query)
}

fun sparqlSelect(engine: QueryEngine, builder: SelectQueryBuilder.() -> Unit): List<Map<Var, IRI>> {
    val queryString = selectQueryBuilder(builder)
    logger.trace("queryString = $queryString")

    val result = engine.execute(Query.create(queryString))

    return if (result.ask()) {
        result.map { queryBinding ->
            queryBinding.boundArgs.associate { arg ->
                arg.valueAsVar.toExternal() to queryBinding[arg].valueAsIRI
            }
        }
    } else {
        emptyList()
    }
}

fun sparqlAsk(engine: QueryEngine, builder: AskQueryBuilder.() -> Unit): Boolean {
    val queryString = askQueryBuilder(builder)
    logger.trace("queryString = $queryString")

    val result = engine.execute(Query.create(queryString))

    return result.ask()
}

// test
fun main() {
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

    println("query = \n$query")

    val query2 = selectQueryBuilder {
        prefix("fred:", IRI.create("http://www.ontologydesignpatterns.org/ont/fred/domain.owl#"))
        prefix("boxing:", IRI.create("http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"))

        select()

        where {
            clazz(x)
            clazz(y)
        }
    }

    println("query2 = \n$query2")


    val query3 = askQueryBuilder {
        prefix("fred:", IRI.create("http://www.ontologydesignpatterns.org/ont/fred/domain.owl#"))
        prefix("boxing:", IRI.create("http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"))
        ask {
            clazz("fred:haha")
        }
    }

    println("query3 = \n$query3")
}
