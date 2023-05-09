import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.npm.task.NpxTask

plugins {
  java
  id("com.github.node-gradle.node")
  id("org.openapi.generator")
}

project.buildDir = File("dist")

val openapiSpecPath = project.rootProject.layout.projectDirectory
  .dir("web-service")
  .dir("build")
  .file("openapi.json")

val openapiGeneratedPath = project.project.layout.projectDirectory
  .dir("src")
  .dir("openapi")

val nodeModulesPath = project.project.layout.projectDirectory
  .dir("node_modules")

dependencies {
}

//val generateTask = tasks.register<NpmTask>("generateProto") {
//  dependsOn(tasks.npmInstall) //, lintTask)
//  args.set(listOf("run", "proto:generate"))
//  rootProject.childProjects["protos"]!!.sourceSets["main"].resources.srcDirs.forEach {
//    if (it.name == "proto") {
//      inputs.dir(it)
//    }
//  }
//  outputs.dir(protoDir)
//}

openApiGenerate {
  generatorName.set("typescript-angular")
  inputSpec.set(openapiSpecPath.toString())
  outputDir.set(openapiGeneratedPath.toString())
  configOptions.set(
    mapOf(
      "modelFileSuffix" to ".model"
    )
  )
  typeMappings.set(
    mapOf(
      "Date" to "Date",
      "date" to "Date",
      "DateTime" to "Date",
    )
  )
}

tasks.openApiGenerate {
  dependsOn(":web-service:generateOpenApiDocs")
}

val buildTask = tasks.register<NpxTask>("ngBuild") {
  command.set("ng")
  args.set(listOf("build"))
  dependsOn(tasks.npmInstall, tasks.openApiGenerate)
  inputs.dir(project.fileTree("src").exclude("**/*.spec.ts"))
  inputs.dir("node_modules")
  inputs.files("angular.json", ".browserslistrc", "tsconfig.json", "tsconfig.app.json")
  outputs.dir("${project.buildDir}/${project.name}")
}

//val cleanTask = tasks.register<Delete>

tasks.clean {
  delete(openapiGeneratedPath)
  delete(nodeModulesPath)
}

tasks.assemble {
  dependsOn(buildTask)
}
