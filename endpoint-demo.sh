 #!/bin/bash
#in unit test also short/long names cant succeed
OUTPUT=$(curl -w "\n" -X POST -H "Content-Type: application/json" -d "{\"name\":\"Devilman\"}" http://localhost:8080/persons)
echo "$OUTPUT"
read -p "Press any key"
REG_PATTERN='.*"id":([0-9]+).*'
[[ "$OUTPUT" =~ $REG_PATTERN ]]
COMPUTED_ID="${BASH_REMATCH[1]}"
curl -w "\n" "localhost:8080/persons/${COMPUTED_ID}"
read -p "Press any key"
curl -w "\n" -X PUT -H "Content-Type: application/json" -d "{\"name\":\"Devilwoman\"}" "http://localhost:8080/persons/${COMPUTED_ID}"
read -p "Press any key"
curl -w "\n" localhost:8080/persons
read -p "Press any key"
curl -w "\n" -X DELETE "http://localhost:8080/persons/${COMPUTED_ID}"
read -p "Press any key"
curl -w "\n" localhost:8080/persons