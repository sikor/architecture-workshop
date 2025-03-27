resource "azurerm_service_plan" "events_plan" {
  name                = "events-app-service-plan"
  location            = azurerm_resource_group.events_rg.location
  resource_group_name = azurerm_resource_group.events_rg.name
  os_type             = "Linux"
  sku_name            = "B1"
}

resource "azurerm_linux_web_app" "events_app" {
  name                = "events-app-service"
  location            = azurerm_resource_group.events_rg.location
  resource_group_name = azurerm_resource_group.events_rg.name
  service_plan_id     = azurerm_service_plan.events_plan.id

  identity {
    type = "SystemAssigned"
  }

  site_config {
    always_on = true
  }

  site_config {
    always_on        = true

    container_registry_use_managed_identity = false

    application_stack {
      docker_image_name   = "youracr.azurecr.io/events-app:latest" #TODO ACR
      docker_registry_url = "https://youracr.azurecr.io" #TODO ACR
    }
  }

  app_settings = {
    "DATABASE_URL" = azurerm_postgresql_flexible_server.events_db.fqdn
    "POSTGRES_PASSWORD" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.pg_password.id})"
    "SPRING_PROFILES_ACTIVE" = "cloud"
  }
}
