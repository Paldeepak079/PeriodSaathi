---
name: ai-review
description: Automated PR review powered by AI. Reviews code diffs, suggests improvements, catches bugs.
source: https://github.com/Nikita-Filonov/ai-review
trigger: PR review, code review, pull request
auto-update: true
update-interval: weekly
github-action: true
events: [opened, synchronize, reopened]
env:
  OPENAI_API_KEY: ${OPENAI_API_KEY}
  GITHUB_TOKEN: ${GITHUB_TOKEN}
config:
  review_depth: full
  inline_comments: true
  summary_comment: true
  label_on_review: ai-reviewed
