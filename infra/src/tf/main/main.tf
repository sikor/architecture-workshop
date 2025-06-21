provider "azurerm" {
  features {}
  use_cli = true
  subscription_id = var.subscription_id
}

provider "azuread" {
  # No extra config needed if you're logged in via `az login`
}

resource "azurerm_resource_group" "project_rg" {
  name     = local.rg_name
  location = var.location
}

data "azurerm_client_config" "current" {}

module key_vault {
  source = "./modules/key_vault"

  key_vault_name       = local.kv_name
  location             = azurerm_resource_group.project_rg.location
  resource_group_name  = azurerm_resource_group.project_rg.name
  tenant_id            = data.azurerm_client_config.current.tenant_id
  admin_object_id      = data.azurerm_client_config.current.object_id
  portal_access_object_id = "f066046b-9848-4fe9-9f91-ff0382ec45ac"
}

module events_app {
  source = "./modules/app_service"

  app_base_name = "archiwork-events"
  location = azurerm_resource_group.project_rg.location
  resource_group_name  = azurerm_resource_group.project_rg.name
  tenant_id            = data.azurerm_client_config.current.tenant_id
  key_vault_id = module.key_vault.key_vault_id
  log_analytics_workspace_id = azurerm_log_analytics_workspace.log_analytics.id
  app_settings = {
    "DATABASE_URL" = azurerm_postgresql_flexible_server.events_db.fqdn
    "DATABASE_NAME" = azurerm_postgresql_flexible_server_database.events_db_instance.name
    "POSTGRES_PASSWORD" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.pg_password.id})"
    "POSTGRES_USERNAME" = azurerm_postgresql_flexible_server.events_db.administrator_login
  }

}

module aggregator_app {
  source = "./modules/app_service"

  app_base_name = "archiwork-aggregator"
  location = azurerm_resource_group.project_rg.location
  resource_group_name  = azurerm_resource_group.project_rg.name
  tenant_id            = data.azurerm_client_config.current.tenant_id
  key_vault_id = module.key_vault.key_vault_id
  log_analytics_workspace_id = azurerm_log_analytics_workspace.log_analytics.id
  app_settings = {
    "DATABASE_URL" = azurerm_postgresql_flexible_server.events_db.fqdn
    "DATABASE_NAME" = azurerm_postgresql_flexible_server_database.aggregator_db_instance.name
    "POSTGRES_PASSWORD" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.pg_password.id})"
    "POSTGRES_USERNAME" = azurerm_postgresql_flexible_server.events_db.administrator_login
    "EVENTS_APP_SCOPE" = module.events_app.ad_client_credentials_scope
    "EVENTS_API_BASE_URL" = "https://${module.events_app.app_service_hostname}"
  }

}

module e2e_client {
  source = "./modules/client_application"
  client_name_prefix = "e2e"
  events_app_client_id = module.events_app.app_client_id
  events_app_scope_id = module.events_app.app_scope_id
  aggregator_app_client_id = module.aggregator_app.app_client_id
  aggregator_app_scope_id = module.aggregator_app.app_scope_id
  key_vault_id = module.key_vault.key_vault_id
}

module perf_client {
  source = "./modules/client_application"
  client_name_prefix = "perf"
  events_app_client_id = module.events_app.app_client_id
  events_app_scope_id = module.events_app.app_scope_id
  aggregator_app_client_id = module.aggregator_app.app_client_id
  aggregator_app_scope_id = module.aggregator_app.app_scope_id
  key_vault_id = module.key_vault.key_vault_id
}

# export TF_VAR_subscription_id=$(az account show --query id --output tsv) ba90069d-9e46-4163-9570-1b2bd4db55d4
# az ad sp create-for-rbac --name "architecture-workshops-sikor-github" --role Contributor --scopes /subscriptions/ba90069d-9e46-4163-9570-1b2bd4db55d4 --sdk-auth