plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    // Project dependencies
    implementation(project(":fact-checker-commons"))
    implementation(project(":fact-checker-api"))

    // Libraries
    implementation(kotlin("stdlib"))
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution:4.1.3")
    implementation("net.sourceforge.owlapi:owlexplanation:2.0.0")
    implementation("com.github.protegeproject:sparql-dl-api:80d430d439e17a691d0111819af2d3613e28d625")
    implementation("net.sf.extjwnl:extjwnl:2.0.5")
    implementation("net.sf.extjwnl:extjwnl-data-wn31:1.2")
    implementation("edu.stanford.nlp:stanford-corenlp:4.4.0")
    implementation("edu.stanford.nlp:stanford-corenlp:4.4.0:models")

    // Test Libraries
    testImplementation(kotlin("test"))
    testImplementation("net.sourceforge.owlapi:org.semanticweb.hermit:1.3.8.413")
}
