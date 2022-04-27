import com.katonaaron.verbalizer.OwlVerbalizerProxy
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import org.semanticweb.owlapi.apibinding.OWLManager
import java.io.File

@OptIn(ExperimentalCli::class)
class Verbalize : Subcommand("verbalize", "Verbalize an ontologies") {
    private val onto by argument(ArgType.String, description = "The ontology to be verbalized")
    private val url by option(ArgType.String, description = "The URL of the verbalizer service")
        .default("http://localhost:5123")

    override fun execute() {
        val man = OWLManager.createOWLOntologyManager()
        val o1 = man.loadOntologyFromOntologyDocument(File(onto))

        val verbalizer = OwlVerbalizerProxy(url)
        val result = verbalizer.verbalizeOntology(o1)

        println(result)
    }
}
