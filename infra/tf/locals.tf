resource "random_id" "kv_suffix" {
  byte_length = 4
}

locals {
  # Base input (e.g., from var.project_name = "my-events_app")
  base_project_name = lower(replace(var.project_name, "[^a-z0-9-]", "-"))

  # Truncated safe name for resources with short limits (like Key Vault or App Service)
  short_name = substr(local.base_project_name, 0, 10)

  # Suffixes for different resource types - project scope
  kv_name = "${local.short_name}-kv-${random_id.kv_suffix.hex}"
  rg_name = "${local.short_name}-rg"

  # events service scope
  events_postgres_server_name = "${local.short_name}-events-pgs"
  events_postgres_db_name     = "${local.short_name}-events-pgdb"

  # aggregator service
  aggregator_postgres_db_name = "${local.short_name}-aggregator-pgdb"
}