variable "app_base_name" {
  description = "App Name prefix"
  type        = string
}

variable "location" {
  description = "Azure region"
  type        = string
}

variable "resource_group_name" {
  description = "Name of the resource group"
  type        = string
}

variable "tenant_id" {
  description = "Azure AD tenant ID"
  type        = string
}

variable "key_vault_id" {
  description = "Key Vault Id"
  type        = string
}

variable "log_analytics_workspace_id" {
  description = "log_analytics_workspace_id"
  type        = string
}

variable "app_settings" {
  description = "Optional app settings override"
  type        = map(string)
  default     = {} # caller doesn't have to provide it
}

locals {
  app_name = "${var.app_base_name}-app"
  app_identifier_uri =  "api://${local.app_name}"
  ad_scope = "access"
  ad_identifier_scope = "${local.app_identifier_uri}/${local.ad_scope}"
  app_service_hostname = "${local.app_name}.azurewebsites.net"
  swagger_redirect_uri = "https://${local.app_service_hostname}/swagger-ui/oauth2-redirect.html"

  default_app_settings = {
    "SPRING_PROFILES_ACTIVE" = "cloud"
    "SWAGGER_AD_CLIENT_ID" = azuread_application.swagger_ui_client.client_id
    "TENANT_ID" = var.tenant_id
    "AD_CLIENT_ID" = azuread_application.app_ad.client_id
    "AD_CLIENT_SECRET" = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.app_ad_client_secret.id})"
    "AD_SCOPE" = local.ad_identifier_scope
  }

  merged_app_settings = merge(local.default_app_settings, var.app_settings)
}

resource "azurerm_service_plan" "app_plan" {
  name                = "${local.app_name}-service-plan"
  location            = var.location
  resource_group_name = var.resource_group_name
  os_type             = "Linux"
  sku_name            = "B1"
}

resource "azurerm_linux_web_app" "app" {
  name                = local.app_name
  location            = var.location
  resource_group_name = var.resource_group_name
  service_plan_id     = azurerm_service_plan.app_plan.id

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

  app_settings = local.merged_app_settings
}

resource "azurerm_key_vault_access_policy" "app_service_policy" {
  key_vault_id = var.key_vault_id

  tenant_id = var.tenant_id
  object_id = azurerm_linux_web_app.app.identity[0].principal_id

  secret_permissions = ["Get"]
}

# ---------------------------------------------
# API App Registration (resource server)
# ---------------------------------------------

resource "random_uuid" "access_scope_id" {}

resource "azuread_application" "app_ad" {
  display_name     = local.app_name
  sign_in_audience = "AzureADMyOrg"

  identifier_uris = [local.app_identifier_uri]

  api {
    requested_access_token_version = 2

    oauth2_permission_scope {
      admin_consent_description  = "Allow access to the ${local.app_name} API"
      admin_consent_display_name = "Access ${local.app_name} API"
      id                         = random_uuid.access_scope_id.result
      type                       = "User"
      value                      = local.ad_scope
    }
  }
}

resource "azuread_service_principal" "app_sp" {
  client_id = azuread_application.app_ad.client_id
}

# ---------------------------------------------
# Swagger UI Client App Registration (public client)
# ---------------------------------------------
resource "azuread_application" "swagger_ui_client" {
  display_name     = "${local.app_name}-swagger-client"
  sign_in_audience = "AzureADMyOrg"

  single_page_application {
    redirect_uris = [
      local.swagger_redirect_uri
    ]
  }

  required_resource_access {
    resource_app_id = azuread_application.app_ad.client_id

    resource_access {
      id   = random_uuid.access_scope_id.result
      type = "Scope"
    }
  }
}

resource "azuread_service_principal" "swagger_ui_sp" {
  client_id = azuread_application.swagger_ui_client.client_id
}

resource "azuread_application_password" "app_ad_secret" {
  application_id = azuread_application.app_ad.id
  display_name          = "${local.app_name}-ad-secret"
}

resource "azurerm_key_vault_secret" "app_ad_client_secret" {
  name         = "${local.app_name}-ad-secret"
  value        = azuread_application_password.app_ad_secret.value
  key_vault_id = var.key_vault_id
}

resource "azurerm_monitor_diagnostic_setting" "app_logs" {
  name               = "${local.app_name}-logs"
  target_resource_id = azurerm_linux_web_app.app.id
  log_analytics_workspace_id = var.log_analytics_workspace_id

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

output "app_service_hostname" {
  value = local.app_service_hostname
}

output "ad_scope" {
  value = local.ad_identifier_scope
}

output "app_name" {
  value = azurerm_linux_web_app.app.name
}