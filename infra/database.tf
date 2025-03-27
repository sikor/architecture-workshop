resource "azurerm_postgresql_flexible_server" "events_db" {
  name                   = "events-postgres-db"
  resource_group_name    = azurerm_resource_group.events_rg.name
  location               = azurerm_resource_group.events_rg.location
  administrator_login    = "pgadmin"
  administrator_password = azurerm_key_vault_secret.pg_password.value

  sku_name   = "B_Standard_B1ms"
  version    = "13"
  storage_mb = 32768

  authentication {
    active_directory_auth_enabled = false
    password_auth_enabled         = true
  }
}

resource "azurerm_postgresql_flexible_server_database" "events_db_instance" {
  name      = "eventsdb"
  server_id = azurerm_postgresql_flexible_server.events_db.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}
