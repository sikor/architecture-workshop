
variable "key_vault_name" {
  description = "Key Vault Name"
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

variable "admin_object_id" {
  description = "Object ID of the identity used to apply Terraform (e.g., GitHub OIDC identity)"
  type        = string
}

variable "portal_access_object_id" {
  description = "Object ID of the user that will access via UI"
  type        = string
}


resource "azurerm_key_vault" "project_kv" {
  name                        = var.key_vault_name
  location                    = var.location
  resource_group_name         = var.resource_group_name
  tenant_id                   = var.tenant_id
  sku_name                    = "standard"
  soft_delete_retention_days = 7
}


resource "azurerm_key_vault_access_policy" "admin_access" {
  key_vault_id = azurerm_key_vault.project_kv.id

  tenant_id = var.tenant_id
  object_id = var.admin_object_id

  secret_permissions = [
    "Get",
    "List",
    "Set",
  ]
}

resource "azurerm_key_vault_access_policy" "portal_access" {
  key_vault_id = azurerm_key_vault.project_kv.id
  tenant_id    = var.tenant_id
  object_id    = var.portal_access_object_id

  secret_permissions = ["Get", "List", "Set"]
  key_permissions    = ["Get", "List", "Set"]
  certificate_permissions = ["Get", "List", "Set"]
}

output "key_vault_id" {
  value = azurerm_key_vault.project_kv.id
}