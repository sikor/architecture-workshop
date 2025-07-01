import org.ysb33r.gradle.terraform.backends.GenericBackend
import org.ysb33r.gradle.terraform.tasks.TerraformInit
import org.ysb33r.gradle.terraform.tasks.TerraformOutputJson
import org.ysb33r.gradle.terraform.tasks.TerraformPlan

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
            variables {
                files("sikor.tfvars")
            }
        }
    }
}


configurations {
    create("terraformOutputs") {
        isCanBeConsumed = true
        isCanBeResolved = false
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        }
    }
}

tasks.named<TerraformInit>("tfInit") {
    val property = objects.property<File>()
    property.set(project.file("sikor-backend.config"))
    backendConfigFile = property
}

val tfOutputsFile: Provider<RegularFile> =
    tasks.named<TerraformOutputJson>("tfCacheOutputVariables").flatMap { it.statusReportOutputFile }

tasks.named<TerraformPlan>("tfPlan").configure {
    outputs.upToDateWhen { false }
//    inputs.file(tfOutputsFile)
}

val writeTerraformOutputs = tasks.register("writeTerraformOutputs") {
    group = "infrastructure"
    description = "Exports Terraform outputs to a JSON file"
    dependsOn("tfCacheOutputVariables")
    outputs.file(tfOutputsFile)
}

artifacts {
    add("terraformOutputs", writeTerraformOutputs.map { it.outputs.files.singleFile })
}
