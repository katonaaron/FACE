package com.katonaaron.factcheckerservice

import com.katonaaron.factchecker.FactCheckerService
import com.katonaaron.factchecker.FactCheckerServiceResult
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "fact-checker", description = "fact checking service")
@RestController
class FactCheckerController : KoinComponent {
    val factCheckerService by inject<FactCheckerService>()

    @Operation(
        summary = "Performs fact checking",
        responses = [
            ApiResponse(
                description = "Fact checking result",
                responseCode = "200"
            )
        ]
    )
    @GetMapping("/check", produces = ["application/json"])
    fun performFactChecking(
        @Parameter(example = "Antibiotics kill viruses.")
        @RequestParam text: String
    ): ResponseEntity<FactCheckerServiceResult> {
        return factCheckerService.performFactChecking(text)
            .let { ResponseEntity.ok(it) }
    }

    @Operation(
        summary = "Retrieves the knowledge base of the fact checker",
        responses = [
            ApiResponse(
                description = "Sentences in the knowledge base",
                responseCode = "200"
            )
        ]
    )
    @GetMapping("/kb", produces = ["application/json"])
    fun retrieveKnowledgeBase(
    ): ResponseEntity<List<String>> {
        return factCheckerService.knowledgeBase
            .let { ResponseEntity.ok(it) }
    }
}
