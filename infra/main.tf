provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "events_rg" {
  name     = "architecture-workshops"
  location = var.location
}

data "azurerm_client_config" "current" {}


