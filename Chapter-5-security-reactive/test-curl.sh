#!/bin/bash
# A simple script to create load by sending multiple concurrent requests to the server.

# Define the number of requests
REQUESTS=300

# The endpoint to test
#URL="http://localhost:8080/users/stream"
URL="http://localhost:8080/users"
for i in $(seq 1 $REQUESTS)
do
   curl "$URL" &  # The ampersand at the end sends the request in the background, allowing for concurrency
done

wait # Wait for all background jobs to finish
echo "All requests sent."
