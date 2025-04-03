resource "azurerm_key_vault" "project_kv" {
  name                        = local.kv_name
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