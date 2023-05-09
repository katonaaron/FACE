package com.katonaaron.factcheckerservice

import com.katonaaron.config.getFaceModuleFromConfig
import org.koin.core.context.GlobalContext.startKoin
import org.koin.logger.slf4jLogger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FactCheckerServiceApplication : CommandLineRunner {
    override fun run(vararg args: String?) {
        startKoin {
            // use Koin logger
            slf4jLogger()
            // declare modules
            modules(getFaceModuleFromConfig())
        }
    }

}

fun main(args: Array<String>) {
    runApplication<FactCheckerServiceApplication>(*args)
}
