#!/bin/sh

set -e

OUTPUT="src/main/resources/updates.json"

echo "[" > "$OUTPUT"

git log \
  --pretty=format:'%cs|%s' \
  -100 |
grep -E '\|(feat|fix):' |
while IFS="|" read -r date message
do

  type=$(echo "$message" | cut -d':' -f1)
  title=$(echo "$message" | cut -d':' -f2- | sed 's/^ //')

  cat <<EOF >> "$OUTPUT"
  {
    "date": "$date",
    "type": "$type",
    "title": "$title",
    "changes": [
      "$title"
    ]
  },
EOF

done

# Remove trailing comma
sed -i '$ s/,$//' "$OUTPUT"

echo "]" >> "$OUTPUT"