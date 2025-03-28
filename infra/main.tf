provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "project_rg" {
  name     = var.project_name
  location = var.location
}

data "azurerm_client_config" "current" {}


# az ad sp create-for-rbac --name "architecture-workshops-sikor-github" --role Contributor --scopes /subscriptions/ba90069d-9e46-4163-9570-1b2bd4db55d4 --sdk-auth