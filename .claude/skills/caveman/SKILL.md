---
name: caveman
description: 75% token reduction by compressing verbose LLM outputs. Auto-applied to every response.
trigger: /caveman, compress, token reduction, cost saving
auto-update: true
update-interval: weekly
---

# Caveman — Auto Token Compression

Caveman compresses LLM responses by ~75% using aggressive but lossy summarization. Every response in this session will be automatically piped through caveman to save tokens — no manual invocation needed.

## Auto-Apply (Enabled)

All assistant responses are automatically compressed using caveman:
- Code blocks are preserved verbatim
- Explanations are condensed to essentials
- Bullet lists are deduplicated and shortened
- Filler words and repetition are removed

To temporarily disable for a single response, say: `--no-caveman`

## Manual Usage

```bash
# Compress a file
npx caveman --input long-output.txt --output compressed.txt

# Pipe mode
cat long-output.txt | npx caveman

# Direct string compression
echo "Your verbose text here" | npx caveman
```

## What Gets Compressed

| Content | Treatment |
|---------|-----------|
| Code snippets | Pass through unchanged |
| File paths / line numbers | Preserved |
| Technical explanations | Condensed 60-75% |
| Multiple examples | Keep first, drop rest |
| Bullet lists | Shorten to 1-2 words per item |
| Repeated context | Removed after first mention |
| Greetings / closings | Stripped |
