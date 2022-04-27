import com.katonaaron.processor.fred.FredOntologyProcessor
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalCli::class)
class ProcessFred : Subcommand("process-fred", "Performs some processing on the translated FRED ontology") {
    private val input by argument(ArgType.String, description = "Ontology translated by the FRED tool")
    private val output by argument(ArgType.String, description = "Output ontology")

    override fun execute() {
        val reasonerFactory = ReasonerFactory()
        val processor = FredOntologyProcessor(reasonerFactory)
        val man = OWLManager.createOWLOntologyManager()

        val o = man.loadOntologyFromOntologyDocument(File(input))
        processor.processOntology(o)

        println(o)

        o.saveOntology(OWLXMLDocumentFormat(), FileOutputStream(output))
    }
}
