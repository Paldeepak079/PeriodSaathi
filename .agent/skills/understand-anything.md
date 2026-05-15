---
name: understand-anything
description: Builds knowledge graphs from any codebase, document set, or dataset. Auto-refreshes after commits.
source: https://github.com/Lum1104/Understand-Anything
trigger: knowledge graph, codebase understanding, code analysis, visualize dependencies
auto-update: true
update-interval: weekly (or on every commit via hook)
commands:
  build: npx understand-anything --input "${PROJECT_ROOT}" --output ".agent/knowledge-graph/"
  watch: npx understand-anything --input "${PROJECT_ROOT}" --output ".agent/knowledge-graph/" --watch
  incremental: npx understand-anything --input "${PROJECT_ROOT}" --output ".agent/knowledge-graph/" --incremental
output: .agent/knowledge-graph/
formats:
  - json
  - html
  - mermaid
  - graphml
