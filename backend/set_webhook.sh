#!/bin/bash

# Configuration
API_URL="http://localhost:8080"
INSTANCE="diplos"
API_KEY="api_key" # Default from .env
# WEBHOOK_URL="https://YOUR_NGROK_URL.ngrok-free.app/api/webhook/evolution"
# OR if using local network:
# WEBHOOK_URL="http://host.docker.internal:8081/api/webhook/evolution"
WEBHOOK_URL="http://192.168.18.110:8081/api/webhook/evolution"

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
