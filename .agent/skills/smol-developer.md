---
name: smol-developer
description: Auto-scaffold code using smol-ai/developer. Generates project boilerplate from natural language prompts.
source: https://github.com/smol-ai/developer
trigger: scaffold code, generate boilerplate, create project from prompt
auto-update: true
update-interval: weekly
env:
  OPENAI_API_KEY: ${OPENAI_API_KEY}
  ANTHROPIC_API_KEY: ${ANTHROPIC_API_KEY}
commands:
  scaffold: npx smol-developer --prompt "${PROMPT}" --output "${OUTPUT_DIR}"
  list-models: npx smol-developer --list-models
