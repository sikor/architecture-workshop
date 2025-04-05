terraform {
  backend "azurerm" {
    resource_group_name  = "terraform-state"
    storage_account_name = var.tf_backend_storage_account
    container_name       = "tfstate"
    key                  = local.tf_state_key
  }
}