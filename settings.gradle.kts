rootProject.name = "fact-checker"
include("fact-checker-core")
include("fact-checker-api")
include("fact-checker-cli")
include("owl-verbalizer")
project(":owl-verbalizer").projectDir = File("owl-verbalizer", "java")
include("fact-checker-tools")
include("fact-checker-commons")
