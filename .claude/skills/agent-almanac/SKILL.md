---
name: agent-almanac
description: 69,000+ Claude skills for instant capability extension.
source: https://github.com/BehiSecc/awesome-claude-skills
trigger: find skill, capability extension
auto-update: true
update-interval: weekly
---
```bash
# Search all 69k+ skills
grep -ri "${QUERY}" .agent/skills/agent-almanac/ --include="*.md"

# Install a skill
cp .agent/skills/agent-almanac/${PATH} .agent/skills/
```
