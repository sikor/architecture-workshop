resource "azurerm_log_analytics_workspace" "log_analytics" {
  name                = "${local.short_name}-log"
  location            = azurerm_resource_group.project_rg.location
  resource_group_name = azurerm_resource_group.project_rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}