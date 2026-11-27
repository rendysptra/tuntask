variable "project_id" {
  description = "GCP Project ID untuk Tuntask"
  type        = string
}

variable "region" {
  description = "GCP Region"
  type        = string
  default     = "asia-southeast2"
}

variable "zone" {
  description = "GCP Zone"
  type        = string
  default     = "asia-southeast2-a"
}

variable "github_repo" {
  description = "Repositori GitHub format owner/repo"
  type        = string
  default     = "rendysptra/tuntask"
}

variable "vm_user" {
  description = "Username SSH di VM untuk deployment"
  type        = string
  default     = "deploy"
}

variable "ssh_public_key" {
  description = "Isi file public key (~/.ssh/tuntask_deploy_key.pub)"
  type        = string
}