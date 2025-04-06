output "events_app_name" {
  description = "The name of the Events Azure App Service"
  value       = azurerm_linux_web_app.events_app.name
}

output "events_app_url" {
  description = "The default hostname (URL) of the Events Azure App Service"
  value       = azurerm_linux_web_app.events_app.default_hostname
}