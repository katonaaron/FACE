package com.katonaaron.matcher

//class StemOntologyMatcher : BaseOntologyMatcher() {
//    override fun matchEntities(entities1: Collection<OWLEntity>, entities2: Collection<OWLEntity>): MatchingResult {
//        val props = Properties()
//        props["annotators"] = "tokenize, ssplit, pos, lemma"
//
//        val pipeline = StanfordCoreNLP(props, false)
//        val text = "COVID-19"
//        val document = pipeline.process(text)
//
//        for (sentence in document.get(SentencesAnnotation::class.java)) {
//            println("sentence: $sentence.")
//            for (token in sentence.get(TokensAnnotation::class.java)) {
//                val word = token.get(TextAnnotation::class.java)
//                val lemma = token.get(LemmaAnnotation::class.java)
//                println("lemmatized version of $word is  : $lemma")
//            }
//        }
//
//        return MatchingResult(emptySet(), emptySet())
//    }
//}
