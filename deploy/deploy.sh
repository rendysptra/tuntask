#!/usr/bin/env bash
set -euo pipefail

ENV_NAME="${1:?Argumen 1 (environment) wajib diisi}"
IMAGE_TAG="${2:?Argumen 2 (image_tag) wajib diisi}"
IMAGE_PREFIX="${3:?Argumen 3 (image_prefix) wajib diisi}"

# Baca token Artifact Registry satu baris dari stdin
IFS= read -r AR_TOKEN
if [ -z "$AR_TOKEN" ]; then
  echo "ERROR: Token Artifact Registry dari stdin kosong" >&2
  exit 1
fi

REGISTRY_HOST="${IMAGE_PREFIX%%/*}"
PREV_TAG_FILE=".previous_tag"
PREV_TAG=""
if [ -f "$PREV_TAG_FILE" ]; then
  PREV_TAG="$(cat "$PREV_TAG_FILE")"
fi

# Login sementara ke Artifact Registry dan pastikan logout saat script selesai
cleanup() {
  docker logout "$REGISTRY_HOST" >/dev/null 2>&1 || true
}
trap cleanup EXIT

printf '%s\n' "$AR_TOKEN" | docker login -u oauth2accesstoken --password-stdin "$REGISTRY_HOST" >/dev/null

export ENV_NAME IMAGE_TAG IMAGE_PREFIX
COMPOSE_CMD="docker compose -p tuntask-${ENV_NAME}"

echo "=== [1/3] Pulling image tag ${IMAGE_TAG} untuk ${ENV_NAME} ==="
$COMPOSE_CMD pull backend frontend

echo "=== [2/3] Menjalankan container ${ENV_NAME} ==="
$COMPOSE_CMD up -d --remove-orphans

echo "=== [3/3] Memverifikasi health check backend ==="
HEALTHY=false
for i in $(seq 1 18); do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' "backend-${ENV_NAME}" 2>/dev/null || echo "starting")
  echo "Percobaan $i/18 - Status backend-${ENV_NAME}: $STATUS"
  if [ "$STATUS" = "healthy" ]; then
    HEALTHY=true
    break
  fi
  sleep 5
done

if [ "$HEALTHY" != "true" ]; then
  echo "ERROR: Health check gagal setelah deploy tag ${IMAGE_TAG}!" >&2
  docker logs --tail 40 "backend-${ENV_NAME}" >&2 || true

  if [ -n "$PREV_TAG" ] && [ "$PREV_TAG" != "$IMAGE_TAG" ]; then
    echo "=== Melakukan rollback ke tag sebelumnya: ${PREV_TAG} ===" >&2
    export IMAGE_TAG="$PREV_TAG"
    $COMPOSE_CMD up -d --remove-orphans
  fi
  exit 1
fi

# Simpan tag sukses untuk referensi rollback berikutnya & bersihkan image dangling
printf '%s\n' "$IMAGE_TAG" > "$PREV_TAG_FILE"
docker image prune -f >/dev/null 2>&1 || true
echo "=== Deploy ${ENV_NAME} (tag: ${IMAGE_TAG}) BERHASIL ==="