variable "subscription_id" {
  description = "Azure subscription ID"
  type        = string
}

variable "client_id" {
  description = "Azure client ID"
  type        = string
}

variable "client_secret" {
  description = "Azure client secret"
  type        = string
}

variable "tenant_id" {
  description = "Azure tenant ID"
  type        = string
}

variable "location" {
  description = "Azure region"
  type        = string
  default     = "polandcentral"
}

variable "resource_group_name" {
  description = "Azure resource group name"
  type        = string
  default     = "test"
}

variable "vnet_name" {
  description = "Virtual Network name"
  type        = string
  default     = "myVNet"
}

variable "vnet_address_space" {
  description = "Virtual Network address space"
  type        = string
  default     = "10.30.0.0/16"
}

variable "subnet_name" {
  description = "Subnet name"
  type        = string
  default     = "mySubnet"
}

variable "subnet_address_prefix" {
  description = "Subnet address prefix"
  type        = string
  default     = "10.30.1.0/24"
}

variable "acr_name" {
  description = "Azure Container Registry name"
  type        = string
  default     = "myACR"
}

variable "aks_cluster_name" {
  description = "AKS cluster name"
  type        = string
  default     = "myAKSCluster"
}

variable "aks_node_count" {
  description = "Number of nodes in the AKS cluster"
  type        = number
  default     = 1
}

variable "aks_node_vm_size" {
  description = "VM size for the AKS nodes"
  type        = string
  default     = "Standard_DS2_v2"
}

variable "db_username" {
  description = "SQL Server admin login"
  type        = string
}

variable "db_password" {
  description = "SQL Server admin password"
  type        = string
}