package com.katonaaron.config

import com.katonaaron.factchecker.DefaultFactCheckerFactory
import com.katonaaron.factchecker.DefaultOntologyFactCheckerFactory
import com.katonaaron.factchecker.FactCheckerService
import com.katonaaron.fred.learner.ProcessedFredOntologyLearner
import com.katonaaron.fred.processor.FredOntologyProcessor
import com.katonaaron.learner.DummyOntologyLearner
import com.katonaaron.learner.ProcessedOntologyLearner
import com.katonaaron.matcher.DefaultOntologyMatcher
import com.katonaaron.matcher.StringOntologyMatcher
import com.katonaaron.matcher.WordnetOntologyMatcher
import com.katonaaron.onto.*
import com.katonaaron.owlverbalizer.OwlVerbalizerAdapter
import org.koin.dsl.module
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory
import java.io.File


private val defaultOntologyMatcher = DefaultOntologyMatcher()

fun getMatcher(matcherType: MatcherType?): OntologyMatcher = when (matcherType) {
    null -> defaultOntologyMatcher
    MatcherType.DEFAULT -> defaultOntologyMatcher
    MatcherType.STRING -> StringOntologyMatcher()
    MatcherType.WN -> WordnetOntologyMatcher()
}

//fun getCombinedMatcher(matcherTypes: List<MatcherType>): OntologyMatcher {
//    if(matcherTypes.isEmpty())
//        return getMatcher(MatcherType.DEFAULT)
//    return CombinedOntologyMatcher(matcherTypes.)
//}

fun getFaceModule(
    factCheckerKnowledgebase: String,
    factCheckerMatcher: MatcherType?,
    owlVerbalizerUrl: String,
    fredUrl: String,
    fredKey: String,
    fredDummy: String?
): org.koin.core.module.Module = module {
    single<OWLReasonerFactory> { ReasonerFactory() }
    single<OntologyVerbalizer> { OwlVerbalizerAdapter(owlVerbalizerUrl) }
    single<OntologyLearner> {
        fredDummy?.let {
            ProcessedOntologyLearner(
                DummyOntologyLearner(it),
                FredOntologyProcessor(get())
            )
        } ?: ProcessedFredOntologyLearner(fredUrl, fredKey, get())
    }
    single<OntologyMatcher> { getMatcher(factCheckerMatcher) }
    single<OntologyFactCheckerFactory> {
        DefaultOntologyFactCheckerFactory(get(), get())
    }
    single<OntologyFactChecker> {
        get<OntologyFactCheckerFactory>().createOntologyFactChecker(loadKnowledgeBase(factCheckerKnowledgebase))
    }
    single<FactCheckerFactory> {
        DefaultFactCheckerFactory(get(), get(), get())
    }
    single<FactChecker> {
        get<FactCheckerFactory>().createFactChecker(loadKnowledgeBase(factCheckerKnowledgebase))
    }
    single {
        FactCheckerService(
            factCheckerKnowledgebase,
            get(),
            get()
        )
    }
}


private fun loadKnowledgeBase(path: String): OWLOntology =
    OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(File(path))


fun getFaceModuleFromConfig(): org.koin.core.module.Module = getFaceModuleFromConfig(loadConfiguration())
fun getFaceModuleFromConfig(config: Config): org.koin.core.module.Module = config.run {
    getFaceModule(
        face.knowledgebase,
        face.matcher,
        owlVerbalizer.url,
        fred.url,
        fred.key,
        fred.dummy
    )
}
