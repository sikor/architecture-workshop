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

module key_vault {
  source = "./modules/key_vault"

  key_vault_name       = local.kv_name
  location             = azurerm_resource_group.project_rg.location
  resource_group_name  = azurerm_resource_group.project_rg.name
  tenant_id            = data.azurerm_client_config.current.tenant_id
  admin_object_id      = data.azurerm_client_config.current.object_id
}


# export TF_VAR_subscription_id=$(az account show --query id --output tsv) ba90069d-9e46-4163-9570-1b2bd4db55d4
# az ad sp create-for-rbac --name "architecture-workshops-sikor-github" --role Contributor --scopes /subscriptions/ba90069d-9e46-4163-9570-1b2bd4db55d4 --sdk-auth