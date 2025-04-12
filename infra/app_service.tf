resource "azurerm_service_plan" "events_plan" {
  name                = "events-app-service-plan"
  location            = azurerm_resource_group.project_rg.location
  resource_group_name = azurerm_resource_group.project_rg.name
  os_type             = "Linux"
  sku_name            = "B1"
}

resource "azurerm_linux_web_app" "events_app" {
  name                = local.events_app_name
  location            = azurerm_resource_group.project_rg.location
  resource_group_name = azurerm_resource_group.project_rg.name
  service_plan_id     = azurerm_service_plan.events_plan.id

  identity {
    type = "SystemAssigned"
  }

  site_config {
    always_on = true

    container_registry_use_managed_identity = true

    application_stack {
      java_server = "JAVA"
      java_server_version = 21
      java_version = 21
    }
  }

  app_settings = {
    "DATABASE_URL" = azurerm_postgresql_flexible_server.events_db.fqdn
    "DATABASE_NAME" = azurerm_postgresql_flexible_server_database.events_db_instance.name
    "POSTGRES_PASSWORD" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.pg_password.id})"
    "SPRING_PROFILES_ACTIVE" = "cloud"
    "AD_CLIENT_ID" = azuread_application.events_app_ad.client_id
    "TENANT_ID" = data.azurerm_client_config.current.tenant_id
    "AD_CLIENT_SECRET" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.event_app_ad_client_secret.id})"
  }
}

resource "azurerm_key_vault_access_policy" "app_service_policy" {
  key_vault_id = azurerm_key_vault.project_kv.id

  tenant_id = data.azurerm_client_config.current.tenant_id
  object_id = azurerm_linux_web_app.events_app.identity[0].principal_id

  secret_permissions = ["Get"]
}

resource "azuread_application" "events_app_ad" {
  display_name = local.events_ad_name
  sign_in_audience = "AzureADMyOrg"

  web {
    redirect_uris = [
      "https://${azurerm_linux_web_app.events_app.default_hostname}/swagger-ui/oauth2-redirect.html"
    ]
  }
}

resource "azuread_application_password" "events_app_ad_secret" {
  application_id = azuread_application.events_app_ad.id
  display_name          = "${local.events_ad_name}-secret"
}

resource "azurerm_key_vault_secret" "event_app_ad_client_secret" {
  name         = "${local.events_ad_name}-secret"
  value        = azuread_application_password.events_app_ad_secret.value
  key_vault_id = azurerm_key_vault.project_kv.id
}

resource "azurerm_monitor_diagnostic_setting" "event_app_logs" {
  name               = "${local.events_app_name}-logs"
  target_resource_id = azurerm_linux_web_app.events_app.id
  log_analytics_workspace_id = azurerm_log_analytics_workspace.log_analytics.id

  enabled_log {
    category = "AppServiceConsoleLogs"
  }

  enabled_log {
    category = "AppServiceHTTPLogs"
  }

  metric {
    category = "AllMetrics"
    enabled  = true
  }
}