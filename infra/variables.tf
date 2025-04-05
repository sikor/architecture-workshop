variable "location" {
  description = "Azure region to deploy resources"
  default     = "northeurope"
}

variable "project_name" {
  description = "global name of the project"
  default     = "archiwork"
  type        = string
}

variable "subscription_id" {
  description = "Azure subscription ID"
  type        = string
}

variable "tf_backend_storage_account" {
  description = "Terraform backend storage account"
  type        = string
  default     = "archiwork0b318872b"
}