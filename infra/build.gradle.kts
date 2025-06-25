import groovy.json.JsonOutput
import org.ysb33r.gradle.iac.base.tf.tasks.AbstractInitTask
import org.ysb33r.gradle.terraform.backends.GenericBackend
import org.ysb33r.gradle.terraform.internal.TerraformModel

plugins {
    id("org.ysb33r.terraform")
}

terraform {
    backends {
        create("azure", GenericBackend::class) {

        }
    }

    sourceSets {
        getByName("main") {
            useBackend("azure")
        }
    }
}

configurations {
    create("testArtifacts")
}

tasks.named<AbstractInitTask>("tfInit") {
    val property = objects.property<File>()
    property.set(project.file("src/tf/main/sikor-backend.config"))
    backendConfigFile = property
}

val sourceSet = terraform.sourceSets.getByName(TerraformModel.DEFAULT_SOURCESET_NAME)
val tfOutputs: Provider<Map<String, Any>> = sourceSet.rawOutputVariables()

val writeTerraformOutputs = tasks.register("writeTerraformOutputs") {
    group = "infrastructure"
    description = "Exports Terraform outputs to a JSON file"


    val outputVariables = objects.mapProperty(String::class, Any::class);

    outputVariables.set(tfOutputs)

    val outputFile = layout.buildDirectory.file("terraform/outputs.json")
    outputs.file(outputFile)

    doLast {
        val json = JsonOutput.toJson(outputVariables.get())
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(json)
        }
    }
}

artifacts {
    add("testArtifacts", writeTerraformOutputs.map { it.outputs.files.singleFile })
}
