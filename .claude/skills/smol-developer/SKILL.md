---
name: smol-developer
description: Auto-scaffold code using smol-ai/developer.
source: https://github.com/smol-ai/developer
trigger: scaffold code, generate boilerplate
auto-update: true
update-interval: weekly
---
Use `npx smol-developer` to generate project code from prompts.

```bash
# Scaffold a new component
npx smol-developer --prompt "Create a Kotlin Compose BottomSheet for period tracking" --output app/src/main/java/com/periodsaathi/ui/

# List available models
npx smol-developer --list-models
```
