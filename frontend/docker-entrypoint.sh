#!/bin/sh
set -e

ENV_FILE=/usr/share/nginx/html/env-config.js

if [ -f "$ENV_FILE" ]; then
  API_URL=${VITE_API_URL:-http://localhost:8080/api/v1}
  
  sed -i "s|__VITE_API_URL__|${API_URL}|g" "$ENV_FILE"
  
  echo "Injected VITE_API_URL: ${API_URL}"
else
  echo "Warning: $ENV_FILE not found"
fi

exec nginx -g 'daemon off;'
