resource "google_artifact_registry_repository" "tuntask_repo" {
  location      = var.region
  repository_id = "tuntask"
  description   = "Docker images untuk aplikasi Tuntask (backend & frontend)"
  format        = "DOCKER"

  cleanup_policies {
    id     = "keep-recent-images"
    action = "KEEP"
    most_recent_versions {
      keep_count = 5
    }
  }

  cleanup_policies {
    id     = "delete-old-untagged"
    action = "DELETE"
    condition {
      tag_state  = "ANY"
      older_than = "604800s" # 7 hari
    }
  }

  depends_on = [google_project_service.apis]
}