output "vm_external_ip" {
  description = "Static External IP VM (untuk Hostinger DNS & GitHub Secret GCP_HOST)"
  value       = google_compute_address.tuntask_ip.address
}

output "vm_user" {
  description = "Username SSH (untuk GitHub Secret GCP_USERNAME)"
  value       = var.vm_user
}

output "wif_provider" {
  description = "Resource name lengkap WIF Provider (untuk GitHub Variable WIF_PROVIDER)"
  value       = google_iam_workload_identity_pool_provider.github_provider.name
}

output "wif_service_account" {
  description = "Email Service Account (untuk GitHub Variable WIF_SERVICE_ACCOUNT)"
  value       = google_service_account.github_actions.email
}

output "artifact_registry_url" {
  description = "URL prefix Artifact Registry"
  value       = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.tuntask_repo.repository_id}"
}