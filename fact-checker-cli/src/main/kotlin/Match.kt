import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import org.semanticweb.owlapi.apibinding.OWLManager
import java.io.File

@OptIn(ExperimentalCli::class)
class Match : Subcommand("match", "Perform matching on ontologies") {
    private val onto1 by argument(ArgType.String, description = "First ontology")
    private val onto2 by argument(ArgType.String, description = "Second ontology")

    private val matcherType by option(
        ArgType.Choice<MatcherType>(),
        fullName = "matcher",
        shortName = "m",
        description = "The matcher implementation to be used"
    )
        .default(DEFAULT_MATCHER)

    override fun execute() {
        val man = OWLManager.createOWLOntologyManager()
        val o1 = man.loadOntologyFromOntologyDocument(File(onto1))
        val o2 = man.loadOntologyFromOntologyDocument(File(onto2))

        val matcher = getMatcher(matcherType)

        val res = matcher.matchOntologies(o1, o2)

        println("res = $res")
    }
}
