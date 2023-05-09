package com.katonaaron.commons

import de.derivo.sparqldlapi.Query
import de.derivo.sparqldlapi.QueryEngine

fun query(engine: QueryEngine, queryString: String): List<Map<String, String>> {
    val q = Query.create(queryString)
    val result = engine.execute(q)

    return if (result.ask()) {
        result.map { queryBinding ->
            queryBinding.boundArgs.associate { arg ->
                arg.valueAsString to queryBinding[arg].valueAsString
            }
        }
    } else {
        emptyList()
    }
}
