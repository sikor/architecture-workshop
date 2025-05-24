resource "azuread_application" "e2e_client" {
  display_name     = "e2e-client"
  sign_in_audience = "AzureADMyOrg"

  required_resource_access {
    resource_app_id = module.events_app.app_client_id

    resource_access {
      id   = module.events_app.app_scope_id
      type = "Scope"
    }
  }

  required_resource_access {
    resource_app_id = module.aggregator_app.app_client_id

    resource_access {
      id   = module.aggregator_app.app_scope_id
      type = "Scope"
    }
  }
}

resource "azuread_service_principal" "e2e_sp" {
  client_id = azuread_application.e2e_client.client_id
}

resource "azuread_application_password" "e2e_ad_secret" {
  application_id = azuread_application.e2e_client.id
  display_name          = "e2e-ad-secret"
}

resource "azurerm_key_vault_secret" "e2e_client_secret" {
  name         = "e2e-ad-secret"
  value        = azuread_application_password.e2e_ad_secret.value
  key_vault_id = module.key_vault.key_vault_id
}