resource "azurerm_app_service_plan" "events_plan" {
  name                = "events-app-service-plan"
  location            = azurerm_resource_group.events_rg.location
  resource_group_name = azurerm_resource_group.events_rg.name
  sku {
    tier = "Basic"
    size = "B1"
  }
}

resource "azurerm_app_service" "events_app" {
  name                = "events-app-service"
  location            = azurerm_resource_group.events_rg.location
  resource_group_name = azurerm_resource_group.events_rg.name
  app_service_plan_id = azurerm_app_service_plan.events_plan.id

  site_config {
    java_version = "17"
    linux_fx_version = "JAVA|17"
  }

  app_settings = {
    "DATABASE_URL" = azurerm_postgresql_flexible_server.events_db.fqdn
    "POSTGRES_PASSWORD" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.pg_password.id})"
    "SPRING_PROFILES_ACTIVE" = "cloud"
  }

  identity {
    type = "SystemAssigned"
  }
}
