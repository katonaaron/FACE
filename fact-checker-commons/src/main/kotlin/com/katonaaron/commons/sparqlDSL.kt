package com.katonaaron.commons

import de.derivo.sparqldlapi.Query
import de.derivo.sparqldlapi.QueryEngine
import de.derivo.sparqldlapi.Var
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLLiteral
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString

private fun IRI.format(): String = "<$this>"
private fun Var.format(): String = "?$name"
private fun OWLLiteral.format(): String = "\"${literal}\""

val VAR_ALL = Var("*")

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

    fun prefix(prefix: String, uriString: String) {
        prefix(prefix, IRI.create(uriString))
    }
}

sealed class QueryBuilder {
    private var prefixes: List<Prefix> = listOf()

    fun prefixes(prefixesList: Prefixes.() -> Unit) {
        prefixes = ArrayList(Prefixes().apply(prefixesList))
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

class SelectQueryBuilder(
    private val variables: List<Var>
) : QueryBuilder() {
    private val whereList = mutableListOf<String>()
    var distinct = false

    init {
        if (variables.isEmpty()) {
            throw RuntimeException("Must provide at least one variable")
        }
    }

    fun where(atoms: Atoms.() -> Unit) {
        whereList.add(Atoms().apply(atoms).build())
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

fun select(vararg variables: Var, selectBuilder: SelectQueryBuilder.() -> Unit): SelectQueryBuilder {
    return SelectQueryBuilder(variables.toList()).apply(selectBuilder)
}

fun select(selectBuilder: SelectQueryBuilder.() -> Unit): SelectQueryBuilder {
    return SelectQueryBuilder(listOf(VAR_ALL)).apply(selectBuilder)
}

fun queryBuilder(builderProducer: () -> QueryBuilder): String {
    val builder = builderProducer()
    return builder.build()
}

fun sparql(engine: QueryEngine, builderProducer: () -> SelectQueryBuilder): List<Map<Var, IRI>> {
    val queryString = queryBuilder(builderProducer)
    println("queryString = $queryString")
//    return query(engine, queryString)

    val result = engine.execute(Query.create(queryString))

    return if (result.ask()) {
        result.map { queryBinding ->
            queryBinding.boundArgs.associate { arg ->
                arg.valueAsVar to queryBinding[arg].valueAsIRI
            }
        }
    } else {
        emptyList()
    }
}

fun sparqlAsk(engine: QueryEngine, builderProducer: AskQueryBuilder.() -> Unit): Boolean {
    val queryString = queryBuilder { AskQueryBuilder().apply(builderProducer) }
    println("queryString = $queryString")

    val result = engine.execute(Query.create(queryString))

    return result.ask()
}

// test
fun main() {
    val x = Var("x")

    val query = queryBuilder {
        select(x) {
            distinct = false
            prefixes {
                prefix("fred:", IRI.create("http://www.ontologydesignpatterns.org/ont/fred/domain.owl#"))
                prefix("boxing:", IRI.create("http://www.ontologydesignpatterns.org/ont/boxer/boxing.owl#"))
            }
            where {
                clazz(x)
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
    }

    println("query = $query")
}
