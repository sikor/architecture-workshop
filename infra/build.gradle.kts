import org.ysb33r.gradle.terraform.backends.GenericBackend
import org.ysb33r.gradle.terraform.internal.plugins.TerraformGlobalConfigForMultiProjectSubProjectPlugin
import org.ysb33r.gradle.terraform.tasks.TerraformApply
import org.ysb33r.gradle.terraform.tasks.TerraformImport
import org.ysb33r.gradle.terraform.tasks.TerraformInit
import org.ysb33r.gradle.terraform.tasks.TerraformOutputJson
import org.ysb33r.gradle.terraform.tasks.TerraformPlan

plugins {
    id("org.ysb33r.terraform")
}

terraform {
    toolchains {
        getByName("standard") {
            executableBySearchPath("terraform")
        }
    }
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
            setSrcDir(file("tf"))
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

tasks.named<TerraformImport>("tfImport") {
    dependsOn(TerraformGlobalConfigForMultiProjectSubProjectPlugin.SYNC_TASK)
}

val tfPlan = tasks.named<TerraformPlan>("tfPlan") {
    doLast {
        val f = File(dataDir.get(), ".applied-plan.sha256")
        f.writeText("ignored")
    }
}

val tfApply = tasks.named<TerraformApply>("tfApply") {
    doLast {
        val f = File(dataDir.get(), ".applied-plan.sha256")
        f.writeText("ignored")
    }
}

val tfCacheOutputsVariables = tasks.named<TerraformOutputJson>("tfCacheOutputVariables") {
    inputs.files(tfApply)
    outputs.upToDateWhen { true }
}

artifacts {
    add("terraformOutputs", tfCacheOutputsVariables.map { it.statusReportOutputFile })
}


//plan run -> tfplan1
//apply run -> tracker = old, later = tfplan1
//apply run -> tracker = tfplan1 != old

//plan run -> tfplan1
//apply run -> tracker = tfplan1 (after cleanup), later = tfplan1
//apply up to date -> tracker = tfplan1 == tfplan1