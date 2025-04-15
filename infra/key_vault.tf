resource "azurerm_key_vault" "project_kv" {
  name                        = local.kv_name
  location                    = azurerm_resource_group.project_rg.location
  resource_group_name         = azurerm_resource_group.project_rg.name
  tenant_id                   = data.azurerm_client_config.current.tenant_id
  sku_name                    = "standard"
  soft_delete_retention_days = 7
}


resource "azurerm_key_vault_access_policy" "admin_access" {
  key_vault_id = azurerm_key_vault.project_kv.id

  tenant_id = data.azurerm_client_config.current.tenant_id
  object_id = data.azurerm_client_config.current.object_id

  secret_permissions = [
    "Get",
    "List",
    "Set",
  ]
}