variable "client_name_prefix" {
  type = string
}

variable "events_app_client_id" {
  type = string
}

variable "events_app_scope_id" {
  type = string
}

variable "aggregator_app_client_id" {
  type = string
}

variable "aggregator_app_scope_id" {
  type = string
}

variable "key_vault_id" {
  type = string
}

resource "azuread_application" "client" {
  display_name     = "${var.client_name_prefix}-client-app"
  sign_in_audience = "AzureADMyOrg"

  required_resource_access {
    resource_app_id = var.events_app_client_id

    resource_access {
      id   = var.events_app_scope_id
      type = "Scope"
    }
  }

  required_resource_access {
    resource_app_id = var.aggregator_app_client_id

    resource_access {
      id   = var.aggregator_app_scope_id
      type = "Scope"
    }
  }
}

resource "azuread_service_principal" "client_sp" {
  client_id = azuread_application.client.client_id
}

resource "azuread_application_password" "client_ad_secret" {
  application_id = azuread_application.client.id
  display_name   = "${var.client_name_prefix}-secret"
}

resource "azurerm_key_vault_secret" "client_secret" {
  name         = "${var.client_name_prefix}-secret"
  value        = azuread_application_password.client_ad_secret.value
  key_vault_id = var.key_vault_id
}

output "client_id" {
  value = azuread_application.client.client_id
}

output "client_secret" {
  value     = azuread_application_password.client_ad_secret.value
  sensitive = true
}