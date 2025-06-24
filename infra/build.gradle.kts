plugins {
    id("org.ysb33r.terraform")
}

configurations {
    create("testArtifacts")
}

val writeTerraformOutputs by tasks.registering {
    group = "infrastructure"
    description = "Exports Terraform outputs to a JSON file"

    val outputFile = layout.buildDirectory.file("terraform/outputs.json")

    inputs.files(tasks.named("tfOutput")) // depends on terraform output
    outputs.file(outputFile)

    doLast {
        val terraformExt = extensions.getByType<org.ysb33r.gradle.terraform.extensions.TerraformExtension>()
        val outputs = terraformExt.sourceSets.getByName("main").rawOutputVariables()

        val json = groovy.json.JsonOutput.toJson(outputs)
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(json)
        }
    }
}

artifacts {
    add("testArtifacts", writeTerraformOutputs.map { it.outputs.files.singleFile })
}
