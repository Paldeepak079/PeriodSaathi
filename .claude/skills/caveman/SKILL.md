---
name: caveman
description: 75% token reduction by compressing verbose LLM outputs.
source: https://github.com/JuliusBrussee/caveman
trigger: token reduction, compress, cost saving
auto-update: true
update-interval: weekly
---
```bash
# Compress file
npx caveman --input long-output.txt --output compressed.txt

# Pipe mode
cat long-output.txt | npx caveman
```
