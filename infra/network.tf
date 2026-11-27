resource "google_compute_address" "tuntask_ip" {
  name         = "tuntask-ip"
  region       = var.region
  address_type = "EXTERNAL"
  network_tier = "STANDARD" # Lebih hemat biaya, cocok untuk proyek UAS

  depends_on = [google_project_service.apis]
}

resource "google_compute_firewall" "tuntask_web_ssh" {
  name    = "allow-tuntask-web-ssh"
  network = "default"

  allow {
    protocol = "tcp"
    ports    = ["22", "80", "443"]
  }

  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["tuntask-server"]

  depends_on = [google_project_service.apis]
}