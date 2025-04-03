resource "random_id" "kv_suffix" {
  byte_length = 4
}

resource "azurerm_key_vault" "project_kv" {
  name                        = "${var.project_name}-keyvault-${random_id.kv_suffix.hex}"
  location                    = azurerm_resource_group.project_rg.location
  resource_group_name         = azurerm_resource_group.project_rg.name
  tenant_id                   = data.azurerm_client_config.current.tenant_id
  sku_name                    = "standard"
  soft_delete_retention_days = 7

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    secret_permissions = [
      "Get",
      "Set",
      "List",
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
  key_vault_id = azurerm_key_vault.project_kv.id
}

resource "azurerm_key_vault_access_policy" "app_service_policy" {
  key_vault_id = azurerm_key_vault.project_kv.id

  tenant_id = data.azurerm_client_config.current.tenant_id
  object_id = azurerm_linux_web_app.events_app.identity[0].principal_id

  secret_permissions = ["Get"]
}