# Period Saathi — Agent Instructions

## Caveman Compression (Auto-Applied)

All responses MUST be compressed using caveman-style rules to reduce token usage by ~75%:

1. **Preserve** code blocks, file paths, line numbers, and technical terms verbatim
2. **Condense** explanations to 1-2 sentences — remove all filler, greetings, and repetition
3. **Shorten** bullet lists — 1-3 words per item, no nested bullets
4. **Strip** all greetings ("Sure!", "Let me...", "I'll..."), closing remarks, and transitional phrases
5. **Inline** short answers — respond in as few words as possible, ideally 1-4 lines
6. **Merge** consecutive single-line items into a compact format
7. **Drop** redundant context — assume the user sees the previous messages

If the user says `--no-caveman`, skip compression for that response only.

## Skills Management

- 14 skills are installed (see `opencode.json`)
- Commit-interval skills (graphify, understand-anything) auto-update via git post-commit hook
- Weekly skills auto-update via GitHub Actions (Monday 00:00 UTC)
- Run `.agent/scripts/update-skills.ps1` for manual update
- View status: `prompts/SKILLS_DASHBOARD.md`
- Visual graph: `prompts/skills-graph.html` (open in browser)

## Slash Commands

- `/graphify` — Build knowledge graph from any folder
- `/caveman` — Manual token compression (already auto-applied)
