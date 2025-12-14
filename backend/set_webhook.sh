#!/bin/bash

# Configuration
API_URL="http://localhost:8080"
INSTANCE="diplos"
API_KEY="api_key" # Default from .env
# WEBHOOK_URL="https://YOUR_NGROK_URL.ngrok-free.app/api/webhook/evolution"
# OR if using local network:
# WEBHOOK_URL="http://host.docker.internal:8081/api/webhook/evolution"
CURRENT_IP=$(hostname -I | awk '{print $1}')
WEBHOOK_URL="http://$CURRENT_IP:8081/api/webhook/evolution"

echo "Configuring Webhook for instance: $INSTANCE"
echo "Target URL: $WEBHOOK_URL"

curl -X POST "$API_URL/webhook/set/$INSTANCE" \
-H "apikey: $API_KEY" \
-H "Content-Type: application/json" \
-d "{
    \"webhook\": {
        \"enabled\": true,
        \"url\": \"$WEBHOOK_URL\",
        \"webhookByEvents\": false,
        \"events\": [\"MESSAGES_UPSERT\"]
    }
}"

echo -e "\nDone."
