cat data.json | jq '.servers[1].name'
cat data.json | jq '.servers | length'