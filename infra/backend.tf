terraform {
  backend "azurerm" {
    resource_group_name  = "terraform-state"
    storage_account_name = "tfstateaccount"
    container_name       = "tfstate"
    key                  = local.tf_state_key
  }
}