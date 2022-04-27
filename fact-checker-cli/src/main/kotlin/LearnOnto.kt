import com.katonaaron.fred.FredOntologyLearner
import com.katonaaron.learner.DummyOntologyLearner
import com.katonaaron.processor.fred.FredOntologyProcessor
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalCli::class)
class LearnOnto : Subcommand("learn-onto", "Performs ontology learning") {
    private val inputFile by option(
        ArgType.String,
        fullName = "input",
        shortName = "i",
        description = "The input text. Otherwise STDIN will be used."
    )

    private val outputFile by option(
        ArgType.String,
        fullName = "output",
        shortName = "o",
        description = "The filename of the output ontology. Otherwise STDOUT will be used."
    )

    private val process by option(
        ArgType.Boolean,
        description = "Defines whether the processor class should be called after learning"
    ).default(false)

    private val dummy by option(
        ArgType.String,
        description = "Ontology file to be used instead of calling the ontology learner. If given, input is ignored."
    )

    override fun execute() {
        val learner = dummy?.let { DummyOntologyLearner(it) } ?: FredOntologyLearner()
        val processor = FredOntologyProcessor(ReasonerFactory())

        val text = inputFile?.let {
            File(it).readText()
        } ?: System.`in`.bufferedReader().readText()

        println("Input text: $text")

        val o = learner.learnOntologyFromText(text)

        if (process) {
            processor.processOntology(o)
        }

        o.saveOntology(
            OWLXMLDocumentFormat(),
            outputFile?.let {
                FileOutputStream(it)
            } ?: System.out
        )
    }
}
