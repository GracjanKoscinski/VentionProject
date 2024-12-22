#!/bin/bash

# Function to log messages with a timestamp
log_message() {
  echo "$(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# Start Kibana with root permissions
log_message "Starting Kibana..."
kibana --allow-root &

# Wait for Kibana to initialize
log_message "Waiting for Kibana to start..."

# Define the maximum number of retries and the interval between checks
MAX_RETRIES=100
RETRY_INTERVAL=2
RETRIES=0

# Check if Kibana is ready by checking the status endpoint
until curl -s "http://localhost:5601/api/status" | grep -q '"state":"green"'; do
  RETRIES=$((RETRIES+1))
  if [ "$RETRIES" -ge "$MAX_RETRIES" ]; then
    log_message "Kibana failed to start after $((MAX_RETRIES*RETRY_INTERVAL)) seconds. Exiting..."
    exit 1
  fi
  log_message "Kibana is not ready yet. Waiting $RETRY_INTERVAL seconds..."
  sleep $RETRY_INTERVAL
done

log_message "Kibana has started successfully."

# Set up the index pattern
log_message "Setting up the index pattern from index.json..."
curl -X POST "http://localhost:5601/api/index_patterns/index_pattern" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d @/usr/share/kibana/config/index.json

# Wait before proceeding to the next step
log_message "Waiting for the index pattern to be fully registered (5 seconds)..."
sleep 5

# Import the dashboard
log_message "Importing the dashboard from dashboard.json..."
curl -X POST "http://localhost:5601/api/kibana/dashboards/import" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d @/usr/share/kibana/config/dashboard.json

# Wait for any background processes to complete
log_message "Waiting for background processes to complete..."
wait

log_message "Script execution completed successfully."
