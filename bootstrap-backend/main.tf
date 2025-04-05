provider "azurerm" {
  features {}
  use_cli = true
  subscription_id = var.subscription_id
}

variable "subscription_id" {
  description = "Azure subscription ID"
  type        = string
}

variable "prefix" {
  description = "A short prefix for naming resources (e.g., project or team name)"
  type        = string
  default     = "archiwork"
}

resource "random_id" "sa_suffix" {
  byte_length = 4
}

locals {
  storage_account_name = lower(substr("${var.prefix}0${random_id.sa_suffix.hex}", 0, 24))  # must be ≤ 24 chars, no dashes
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
  storage_account_id   = azurerm_storage_account.tfstate.id
  container_access_type = "private"
}