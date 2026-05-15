---
name: caveman
description: Token reduction tool achieving ~75% token savings by compressing verbose LLM outputs into minimal representations.
source: https://github.com/JuliusBrussee/caveman
trigger: token reduction, token savings, compress output, reduce cost
auto-update: true
update-interval: weekly
commands:
  compress: npx caveman --input "${INPUT_FILE}" --output "${OUTPUT_FILE}"
  compress-stdin: cat "${FILE}" | npx caveman
  estimate: npx caveman --estimate "${FILE}"
metrics:
  avg_token_reduction: 75%
  use_cases:
    - LLM response compression
    - Prompt history compaction
    - Long-context optimization
