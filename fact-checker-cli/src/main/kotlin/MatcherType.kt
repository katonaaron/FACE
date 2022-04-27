import com.katonaaron.matcher.LogMap2OntologyMatcher
import com.katonaaron.matcher.StringOntologyMatcher
import com.katonaaron.matcher.WordnetOntologyMatcher
import com.katonaaron.onto.OntologyMatcher

enum class MatcherType {
    LOGMAP2,
    STRING,
    WN
}

fun getMatcher(matcherType: MatcherType): OntologyMatcher = when (matcherType) {
    MatcherType.LOGMAP2 -> LogMap2OntologyMatcher()
    MatcherType.STRING -> StringOntologyMatcher()
    MatcherType.WN -> WordnetOntologyMatcher()
}

val DEFAULT_MATCHER = MatcherType.WN
