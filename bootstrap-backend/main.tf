provider "azurerm" {
  features {}
  use_cli         = true
  subscription_id = var.subscription_id
}

variable "subscription_id" {
  description = "Azure subscription ID"
  type        = string
  default     = "ba90069d-9e46-4163-9570-1b2bd4db55d4"
}

variable "prefix" {
  description = "A short prefix for naming resources (e.g., project or team name)"
  type        = string
  default     = "archiwork"
}

locals {
  storage_account_name = lower(substr("${var.prefix}0backendsa", 0, 24))
}


resource "azurerm_resource_group" "tfstate" {
  name     = "terraform-state"
  location = "northeurope"
}

resource "azurerm_storage_account" "tfstate" {
  name                     = local.storage_account_name
  resource_group_name      = azurerm_resource_group.tfstate.name
  location                 = azurerm_resource_group.tfstate.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_storage_container" "tfstate" {
  name                  = "tfstate"
  storage_account_id    = azurerm_storage_account.tfstate.id
  container_access_type = "private"
}