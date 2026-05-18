# Agent Skills Index

Auto-generated weekly. Last updated: $(date -u)

| # | Skill | Source | Description |
|---|-------|--------|-------------|
$(for f in .agent/skills/*.md; do
  name=$(grep -m1 'name:' "$f" 2>/dev/null | sed 's/.*name: *//')
  desc=$(grep -m1 'description:' "$f" 2>/dev/null | sed 's/.*description: *//')
  echo "| | \`$name\` | | $desc |"
done)
