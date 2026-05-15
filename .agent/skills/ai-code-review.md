---
name: ai-code-review
description: GitHub Action for automated AI code review on pull requests with inline suggestions.
source: https://github.com/nuekkis/AI-Code-Review
trigger: PR review, GitHub Action review, automated code review
auto-update: true
update-interval: weekly
github-action: true
events: [opened, synchronize, reopened]
env:
  OPENAI_API_KEY: ${OPENAI_API_KEY}
  GITHUB_TOKEN: ${GITHUB_TOKEN}
config:
  model: gpt-4
  review_severity: all
  comment_on_files: true
  request_changes_on_critical: true
