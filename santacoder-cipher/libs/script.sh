#!/bin/bash
json_url_base64="aHR0cHM6Ly9zYW50YWNvZGVyb2ZmaWNpYWwuZ2l0aHViLmlvL3ZlcmlmaWVyL3ZlcmlmaWVyLmpzb24="
json_url=$(echo "$json_url_base64" | base64 --decode)
pkg_name="$1"
# Use curl to fetch the JSON data from the URL and pipe it to jq for parsing
result=$(curl -s $json_url | jq -r --arg pkg "$pkg_name" '.[] | select(. == $pkg)')
if [[ -n "$result" ]]; then
    echo "'$pkg_name'"
else
  ./gradlew --stop --quiet
#    exit 1
fi


