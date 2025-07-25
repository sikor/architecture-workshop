output "resource_group_name" {
  value = azurerm_resource_group.project_rg.name
}

output "events_app_name" {
  description = "The name of the Events Azure App Service"
  value       = module.events_app.app_name
}

output "aggregator_app_name" {
  description = "The name of the Aggregator Azure App Service"
  value       = module.aggregator_app.app_name
}

output "events_app_url" {
  description = "The default hostname (URL) of the Events Azure App Service"
  value       = module.events_app.app_service_url
}

output "aggregator_app_url" {
  description = "The default hostname (URL) of the Aggregator Azure App Service"
  value       = module.aggregator_app.app_service_url
}

output "e2e_client_id" {
  value = module.e2e_client.client_id
}

output "perf_client_id" {
  value = module.perf_client.client_id
}

output "token_uri" {
  value = "https://login.microsoftonline.com/${data.azurerm_client_config.current.tenant_id}/oauth2/v2.0/token"
}

output "events_app_client_credentials_scope" {
  value = module.events_app.ad_client_credentials_scope
}

output "aggregator_app_client_credentials_scope" {
  value = module.aggregator_app.ad_client_credentials_scope
}

output "key_vault_url" {
  value = module.key_vault.key_vault_url
}