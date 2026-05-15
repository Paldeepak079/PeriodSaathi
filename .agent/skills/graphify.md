---
name: graphify
description: Converts any input (code, docs, papers) into knowledge graphs with clustered communities. 71.5x token savings.
source: https://github.com/ALJAZEERAPLUS/graphify
trigger: knowledge graph, token saving, code to graph, document to graph
auto-update: true
update-interval: weekly (or on every commit via hook)
commands:
  build: npx graphify --input "${PROJECT_ROOT}" --output ".agent/graphify-out/"
  incremental: npx graphify --input "${PROJECT_ROOT}" --output ".agent/graphify-out/" --incremental
  quick: npx graphify --input "${PROJECT_ROOT}" --output ".agent/graphify-out/" --fast
output: .agent/graphify-out/
metrics:
  token_savings: 71.5x
  output_formats:
    - html
    - json
    - audit-report
clustering:
  algorithm: leiden
  communities: auto-detect
