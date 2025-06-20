plugins {
    id("org.ysb33r.terraform")
}

terraform {

//    backend {
//        type.set("azurerm")
//        // Reuse your existing config file
//        backendConfigFile.set(file("src/tf/main/sikor-backend.config"))
//    }

    backends {
//        sikor(GenericBackend) {
//            type("azurerm")
//            // Reuse your existing config file
//            backendConfigFile("src/tf/main/sikor-backend.config")
//        }
    }
//
//    variables {
//        files("src/tf/main/sikor.tfvars")
//    }

//    varsFile.set(file("src/tf/main/sikor.tfvars"))

//    sourceSets {
//        main {
//            useBackend('sikor')
//        }
//    }
}