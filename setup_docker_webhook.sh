#!/bin/bash

# Load API Key from .env
if [ -f ./shell-evolution-api/.env ]; then
    export $(grep -v '^#' ./shell-evolution-api/.env | xargs)
fi

API_KEY=${AUTHENTICATION_API_KEY:-api_key}
INSTANCE="diplos"
# Internal Docker URL (using service name)
WEBHOOK_URL="http://igsm_backend:8081/api/webhook/evolution"

echo "Configuring Webhook for Docker environment..."
echo "API Key: $API_KEY"
echo "Webhook URL: $WEBHOOK_URL"

curl -X POST "http://localhost:8080/webhook/set/$INSTANCE" \
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
