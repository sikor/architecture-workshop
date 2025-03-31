resource "azurerm_container_registry" "project_acr" {
  name                = var.project_name
  resource_group_name = azurerm_resource_group.project_rg.name
  location            = azurerm_resource_group.project_rg.location
  sku                 = "Basic"
  admin_enabled       = false
}

resource "azurerm_role_assignment" "acr_pull" {
  principal_id   = azurerm_linux_web_app.events_app.identity[0].principal_id
  role_definition_name = "AcrPull"
  scope          = azurerm_container_registry.project_acr.id
}