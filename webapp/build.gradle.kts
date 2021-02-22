import com.github.gradle.node.npm.task.NpxTask

plugins {
  java
  id("com.github.node-gradle.node") version "3.0.1"
}

val lintTask = tasks.register<NpxTask>("lintWebapp") {
  command.set("ng")
  args.set(listOf("lint"))
  dependsOn(tasks.npmInstall)
  inputs.dir("src")
  inputs.dir("node_modules")
  inputs.files(
    "angular.json", ".browserslistrc", "tsconfig.json", "tsconfig.app.json", "tsconfig.spec.json",
    "tslint.json"
  )
  outputs.upToDateWhen { true }
}

val buildTask = tasks.register<NpxTask>("buildWebapp") {
  command.set("ng")
  args.set(listOf("build", "--prod"))
  dependsOn(tasks.npmInstall, lintTask)
  inputs.dir(project.fileTree("src").exclude("**/*.spec.ts"))
  inputs.dir("node_modules")
  inputs.files("angular.json", ".browserslistrc", "tsconfig.json", "tsconfig.app.json")
  outputs.dir("${project.buildDir}/webapp")
}

val testTask = tasks.register<NpxTask>("testWebapp") {
  command.set("ng")
  args.set(listOf("test"))
  dependsOn(tasks.npmInstall, lintTask)
  inputs.dir("src")
  inputs.dir("node_modules")
  inputs.files("angular.json", ".browserslistrc", "tsconfig.json", "tsconfig.spec.json", "karma.conf.js")
  outputs.upToDateWhen { true }
}

sourceSets {
  java {
    main {
      resources {
        // This makes the processResources task automatically depend on the buildWebapp one
        srcDir(buildTask)
      }
    }
  }
}

tasks.test {
  //dependsOn(lintTask, testTask)
  dependsOn(lintTask)
}
