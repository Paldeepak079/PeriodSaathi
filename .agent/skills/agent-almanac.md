---
name: agent-almanac
description: Repository of 69,000+ Claude skills and agent configurations for instant capability extension.
source: https://github.com/BehiSecc/awesome-claude-skills
trigger: find skill, Claude skill, agent skill, capability extension
auto-update: true
update-interval: weekly
sync:
  method: git-clone
  target: .agent/skills/agent-almanac/
  branch: main
  shallow: true
categories:
  - coding
  - writing
  - research
  - data-analysis
  - design
  - devops
  - security
  - testing
count: 69000+
usage:
  search: grep -ri "${QUERY}" .agent/skills/agent-almanac/ --include="*.md"
  install: cp .agent/skills/agent-almanac/${SKILL_PATH} .agent/skills/
