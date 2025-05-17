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
  value       = module.events_app.app_service_hostname
}

output "aggregator_app_url" {
  description = "The default hostname (URL) of the Aggregator Azure App Service"
  value       = module.aggregator_app.app_service_hostname
}


output "current_terraform_oid" {
  value = data.azurerm_client_config.current.object_id
}