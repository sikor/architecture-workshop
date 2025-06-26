
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
  enable_rbac_authorization   = true
}

resource "azurerm_role_assignment" "keyvault_admin" {
  scope                = azurerm_key_vault.project_kv.id
  role_definition_name = "Key Vault Administrator" # 👈 use a built-in role
  principal_id         = var.admin_object_id       # 👈 pass in the objectId
}

resource "azurerm_role_assignment" "keyvault_personal_admin" {
  scope                = azurerm_key_vault.project_kv.id
  role_definition_name = "Key Vault Administrator" # 👈 use a built-in role
  principal_id         = var.portal_access_object_id       # 👈 pass in the objectId
}

output "key_vault_id" {
  value = azurerm_key_vault.project_kv.id
}