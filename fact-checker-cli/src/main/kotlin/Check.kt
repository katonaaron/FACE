import com.katonaaron.conflict.DefaultConflictDetectorFactory
import com.katonaaron.entailment.OwlApiEntailmentDetector
import com.katonaaron.explanation.OwlExplanationGenerator
import com.katonaaron.factchecker.DefaultOntologyFactCheckerFactory
import com.katonaaron.fred.FredOntologyLearner
import com.katonaaron.learner.DummyOntologyLearner
import com.katonaaron.merge.OwlApiOntologyMerger
import com.katonaaron.processor.fred.FredOntologyProcessor
import com.katonaaron.provenance.PROVENANCE_IRI_INPUT
import com.katonaaron.provenance.annotateProvenance
import com.katonaaron.verbalizer.OwlVerbalizerProxy
import kotlinx.cli.*
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalCli::class)
class Check : Subcommand("check", "Performs fact checking") {
    private val inputFile by option(
        ArgType.String,
        fullName = "input",
        shortName = "i",
        description = "The input text. Otherwise STDIN will be used."
    )

    private val dummy by option(
        ArgType.String,
        description = "Ontology file to be used instead of calling the ontology learner. If given, input is ignored."
    )

    private val onto by option(
        ArgType.String,
        fullName = "onto",
        description = "If given, the learned ontology is saved to this location."
    )

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
        // Instantiate classes
        val man = OWLManager.createOWLOntologyManager()
        val learner = dummy?.let { DummyOntologyLearner(it) } ?: FredOntologyLearner()
        val processor = FredOntologyProcessor(ReasonerFactory())

        val verbalizer = OwlVerbalizerProxy(verbalizer)

        val matcher = getMatcher(matcherType)

        val merger = OwlApiOntologyMerger()

        val rf = ReasonerFactory()

        val eg = OwlExplanationGenerator(rf)

        val cdFactory = DefaultConflictDetectorFactory()
        val cd = cdFactory.createConflictDetector(rf, eg)

        val ed = OwlApiEntailmentDetector(eg)

        val fcFactory = DefaultOntologyFactCheckerFactory(matcher, merger, cd, ed)

        // Process Input
        val text = inputFile?.let {
            File(it).readText()
        } ?: System.`in`.bufferedReader().readText()

        println("Input text: $text")

        val kb = man.loadOntologyFromOntologyDocument(File(knowledgeBase))
        println("kb:\n" + verbalizer.verbalizeOntology(kb))

        // Perform fact checking

        var o = learner.learnOntologyFromText(text)
        o.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("unprocessed.owl"))
        processor.processOntology(o)
        println("learned ontology:\n" + verbalizer.verbalizeOntology(o))

        o = annotateProvenance(o, PROVENANCE_IRI_INPUT)

        val fc = fcFactory.createOntologyFactChecker(kb)
        val result = fc.factCheck(o)

        printFactCheckerResult(verbalizer, result)

        // Save learned ontology
        onto?.let {
            o.saveOntology(OWLXMLDocumentFormat(), FileOutputStream(it))
        }
    }
}
