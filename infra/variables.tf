variable "location" {
  description = "Azure region to deploy resources"
  default     = "northeurope"
}

variable "project_name" {
  description = "global name of the project"
  type        = string
}

variable "subscription_id" {
  description = "Azure subscription ID"
  type        = string
}