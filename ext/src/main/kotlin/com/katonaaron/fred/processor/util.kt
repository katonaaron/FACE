package com.katonaaron.fred.processor

import com.katonaaron.sparqldsl.Var
import org.semanticweb.owlapi.model.IRI

internal fun formatResults(results: List<Map<Var, IRI>>): String {
    return buildString {
        append("[\n")
        results.forEach { resultMap ->
            append("{\n")
            append(resultMap.map { "\t" + it.key.name + "=" + it.value }.joinToString(",\n"))
            append("\n}\n")
        }
        append("]")
    }
}
