resource "random_id" "kv_suffix" {
  byte_length = 4
}

locals {
  # Base input (e.g., from var.project_name = "my-events_app")
  base_project_name = lower(replace(var.project_name, "[^a-z0-9-]", "-"))

  # Truncated safe name for resources with short limits (like Key Vault or App Service)
  short_name = substr(local.base_project_name, 0, 10)

  # Suffixes for different resource types - project scope
  kv_name        = "${local.short_name}-kv-${random_id.kv_suffix.hex}"
  rg_name        = "${local.short_name}-rg"
  tf_state_key = "${local.short_name}.tfstate"

  # events service scope
  events_app_name       = "${local.short_name}-events-wa"
  events_postgres_server_name  = "${local.short_name}-events-pgs"
  events_postgres_db_name  = "${local.short_name}-events-pgdb"
  events_ad_name = "${local.short_name}-events-ad"

  events_app_service_hostname = "${local.events_app_name}.azurewebsites.net"
  events_app_redirect_uri         = "https://${local.events_app_service_hostname}/swagger-ui/oauth2-redirect.html"
  events_app_identifier_uri = "api://${local.events_app_name}"
}