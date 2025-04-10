provider "azurerm" {
  features {}
  use_cli = true
  subscription_id = var.subscription_id
}

provider "azuread" {
  # No extra config needed if you're logged in via `az login`
}

resource "azurerm_resource_group" "project_rg" {
  name     = local.rg_name
  location = var.location
}

data "azurerm_client_config" "current" {}


# export TF_VAR_subscription_id=$(az account show --query id --output tsv) ba90069d-9e46-4163-9570-1b2bd4db55d4
# az ad sp create-for-rbac --name "architecture-workshops-sikor-github" --role Contributor --scopes /subscriptions/ba90069d-9e46-4163-9570-1b2bd4db55d4 --sdk-auth