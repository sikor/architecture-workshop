resource "azurerm_service_plan" "aggregator_plan" {
  name                = "aggregator-app-service-plan"
  location            = azurerm_resource_group.project_rg.location
  resource_group_name = azurerm_resource_group.project_rg.name
  os_type             = "Linux"
  sku_name            = "B1"
}

resource "azurerm_linux_web_app" "aggregator_app" {
  name                = local.aggregator_app_name
  location            = azurerm_resource_group.project_rg.location
  resource_group_name = azurerm_resource_group.project_rg.name
  service_plan_id     = azurerm_service_plan.aggregator_plan.id

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
    "DATABASE_NAME" = azurerm_postgresql_flexible_server_database.aggregator_db_instance.name
    "POSTGRES_PASSWORD" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.pg_password.id})"
    "POSTGRES_USERNAME" = azurerm_postgresql_flexible_server.events_db.administrator_login
    "SPRING_PROFILES_ACTIVE" = "cloud"
    "AD_CLIENT_ID" = azuread_application.aggregator_app_ad.client_id
    "AD_CLIENT_SECRET" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.aggregator_app_ad_client_secret.id})"
    "EVENTS_API_BASE_URL" = azurerm_linux_web_app.events_app.default_hostname
    "TENANT_ID" = data.azurerm_client_config.current.tenant_id
    "EVENTS_APP_IDENTIFIER_URI" = local.events_app_identifier_uri
  }
}

resource "azurerm_key_vault_access_policy" "aggregator_app_service_policy" {
  key_vault_id = module.key_vault.key_vault_id

  tenant_id = data.azurerm_client_config.current.tenant_id
  object_id = azurerm_linux_web_app.aggregator_app.identity[0].principal_id

  secret_permissions = ["Get"]
}

# ---------------------------------------------
# API App Registration (resource server)
# ---------------------------------------------

# Azure Entra ID Application for Aggregator
resource "azuread_application" "aggregator_app_ad" {
  display_name = local.aggregator_ad_name

  required_resource_access {
    resource_app_id = azuread_application.events_app_ad.client_id

    resource_access {
      id   = random_uuid.access_as_user_scope_id.result
      type = "Scope"
    }
  }
}

# Service Principal for Aggregator
resource "azuread_service_principal" "aggregator_app_sp" {
  client_id = azuread_application.aggregator_app_ad.client_id
}

# Client Secret for Aggregator
resource "azuread_application_password" "aggregator_app_ad_secret" {
  application_id = azuread_application.aggregator_app_ad.id
  display_name          = "${local.aggregator_ad_name}-secret"
}

resource "azurerm_key_vault_secret" "aggregator_app_ad_client_secret" {
  name         = "${local.aggregator_ad_name}-secret"
  value        = azuread_application_password.aggregator_app_ad_secret.value
  key_vault_id = module.key_vault.key_vault_id
}

resource "azurerm_monitor_diagnostic_setting" "aggregator_app_logs" {
  name               = "${local.aggregator_app_name}-logs"
  target_resource_id = azurerm_linux_web_app.aggregator_app.id
  log_analytics_workspace_id = azurerm_log_analytics_workspace.log_analytics.id

  enabled_log {
    category = "AppServiceConsoleLogs"
  }

  metric {
    category = "AllMetrics"
    enabled  = true
  }
}