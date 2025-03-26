resource "random_id" "kv_suffix" {
  byte_length = 4
}

resource "azurerm_key_vault" "events_kv" {
  name                        = "eventskeyvault${random_id.kv_suffix.hex}"
  location                    = azurerm_resource_group.events_rg.location
  resource_group_name         = azurerm_resource_group.events_rg.name
  tenant_id                   = data.azurerm_client_config.current.tenant_id
  sku_name                    = "standard"
  soft_delete_retention_days = 7

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    secret_permissions = [
      "get",
      "set",
      "list",
    ]
  }
}

resource "random_password" "pg_password" {
  length  = 20
  special = true
}

resource "azurerm_key_vault_secret" "pg_password" {
  name         = "postgres-password"
  value        = random_password.pg_password.result
  key_vault_id = azurerm_key_vault.events_kv.id
}

resource "azurerm_key_vault_access_policy" "app_service_policy" {
  key_vault_id = azurerm_key_vault.events_kv.id

  tenant_id = data.azurerm_client_config.current.tenant_id
  object_id = azurerm_app_service.events_app.identity.principal_id

  secret_permissions = ["get"]
}