resource "google_compute_instance" "tuntask_vm" {
  name         = "tuntask-vm"
  machine_type = "e2-medium"
  zone         = var.zone
  tags         = ["tuntask-server"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2404-lts-amd64"
      size  = 30
      type  = "pd-balanced"
    }
  }

  network_interface {
    network = "default"
    access_config {
      nat_ip       = google_compute_address.tuntask_ip.address
      network_tier = "STANDARD"
    }
  }

  metadata = {
    ssh-keys = "${var.vm_user}:${trimspace(var.ssh_public_key)}"
  }

  metadata_startup_script = <<-EOF
    #!/bin/bash
    set -e

    # Instal Docker Engine & Docker Compose plugin (resmi)
    if ! command -v docker &> /dev/null; then
      apt-get update
      apt-get install -y ca-certificates curl
      install -m 0755 -d /etc/apt/keyrings
      curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
      chmod a+r /etc/apt/keyrings/docker.asc

      echo \
        "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
        $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
        tee /etc/apt/sources.list.d/docker.list > /dev/null

      apt-get update
      apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    fi

    # Pastikan user deploy ada dan tergabung di grup docker
    id -u ${var.vm_user} &>/dev/null || useradd -m -s /bin/bash ${var.vm_user}
    usermod -aG docker ${var.vm_user}

    # Buat struktur direktori aplikasi dan Docker network bersama untuk Caddy
    mkdir -p /home/${var.vm_user}/apps/{proxy,staging,production}
    chown -R ${var.vm_user}:${var.vm_user} /home/${var.vm_user}/apps
    docker network inspect tuntask-net >/dev/null 2>&1 || docker network create tuntask-net
  EOF

  depends_on = [
    google_project_service.apis,
    google_compute_firewall.tuntask_web_ssh
  ]
}