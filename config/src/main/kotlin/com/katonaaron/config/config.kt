package com.katonaaron.config

import com.sksamuel.hoplite.*
import org.slf4j.LoggerFactory
import java.nio.file.Paths

private val logger = LoggerFactory.getLogger("com.katonaaron.config.ConfigKt")

enum class MatcherType {
        STRING,
        WN,
        DEFAULT
}

data class Config(val face: Face, val fred: Fred, val owlVerbalizer: OwlVerbalizer)

data class Face(val knowledgebase: String, val matcher: MatcherType)

data class Fred(val url: String, val key: String, val dummy: String?)

data class OwlVerbalizer(val url: String)


fun loadConfiguration(): Config = ConfigLoaderBuilder.default()
        .addPathSource(Paths.get("face.conf"), true)
        .addPathSource(Paths.get("config/face.conf"), true)
        .addResourceOrFileSource("/face.conf")
        .build()
        .loadConfigOrThrow<Config>()
        .also {
                logger.info(
                        """Configuration loaded. 
                        knowledge base: ${it.face.knowledgebase}
                        fred dummy: ${it.fred.dummy}
                """.trimIndent()
                )
        }
