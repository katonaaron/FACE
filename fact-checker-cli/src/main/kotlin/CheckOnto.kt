import com.katonaaron.conflict.DefaultConflictDetectorFactory
import com.katonaaron.entailment.OwlApiEntailmentDetector
import com.katonaaron.explanation.OwlExplanationGenerator
import com.katonaaron.factchecker.DefaultOntologyFactCheckerFactory
import com.katonaaron.merge.OwlApiOntologyMerger
import com.katonaaron.verbalizer.OwlVerbalizerProxy
import kotlinx.cli.*
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import java.io.File

@OptIn(ExperimentalCli::class)
class CheckOnto : Subcommand("check-onto", "Performs fact checking on an ontology") {
    private val onto by option(
        ArgType.String,
        fullName = "input",
        shortName = "i",
        description = "The ontology to be checked against he knowledge base"
    ).required()
    private val knowledgeBase by option(
        ArgType.String,
        shortName = "kb",
        description = "The knowledge base"
    ).required()
    private val verbalizer by option(ArgType.String, description = "The URL of the verbalizer service")
        .default("http://localhost:5123")
    private val matcherType by option(
        ArgType.Choice<MatcherType>(),
        fullName = "matcher",
        shortName = "m",
        description = "The matcher implementation to be used"
    )
        .default(DEFAULT_MATCHER)

    override fun execute() {
        val man = OWLManager.createOWLOntologyManager()
        val o = man.loadOntologyFromOntologyDocument(File(onto))
        val kb = man.loadOntologyFromOntologyDocument(File(knowledgeBase))

        // Dependency injection
        val verbalizer = OwlVerbalizerProxy(verbalizer)

        val matcher = getMatcher(matcherType)

        val merger = OwlApiOntologyMerger()

        val rf = ReasonerFactory()

        val eg = OwlExplanationGenerator(rf)

        val cdFactory = DefaultConflictDetectorFactory()
        val cd = cdFactory.createConflictDetector(rf, eg)

        val ed = OwlApiEntailmentDetector(eg)

        val fcFactory = DefaultOntologyFactCheckerFactory(matcher, merger, cd, ed)
        val fc = fcFactory.createOntologyFactChecker(kb)

        // Procedure
        println("kb:\n" + verbalizer.verbalizeOntology(kb))
        println("o:\n" + verbalizer.verbalizeOntology(o))

        val result = fc.factCheck(o)
        printFactCheckerResult(verbalizer, result)
    }
}
