resource "random_password" "pg_password" {
  length  = 20
  special = true
}

resource "azurerm_key_vault_secret" "pg_password" {
  name         = "postgres-password"
  value        = random_password.pg_password.result
  key_vault_id = module.key_vault.key_vault_id
}

resource "azurerm_postgresql_flexible_server" "events_db" {
  name                   = local.events_postgres_server_name
  resource_group_name    = azurerm_resource_group.project_rg.name
  location               = azurerm_resource_group.project_rg.location
  administrator_login    = "pgadmin"
  administrator_password = azurerm_key_vault_secret.pg_password.value

  sku_name   = "B_Standard_B1ms"
  version    = "16"
  storage_mb = 32768

  zone = "1"

  authentication {
    active_directory_auth_enabled = false
    password_auth_enabled         = true
  }
}

resource "azurerm_postgresql_flexible_server_database" "events_db_instance" {
  name      = local.events_postgres_db_name
  server_id = azurerm_postgresql_flexible_server.events_db.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

resource "azurerm_postgresql_flexible_server_database" "aggregator_db_instance" {
  name      = local.aggregator_postgres_db_name
  server_id = azurerm_postgresql_flexible_server.events_db.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_azure_services" {
  name             = "AllowAzureServices"
  server_id        = azurerm_postgresql_flexible_server.events_db.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}