#!/usr/bin/env bash
# ============================================================
# refresh-knowledge-graph.sh
# Rebuilds graphify + understand-anything knowledge graphs.
# Run after significant code changes or on demand.
# ============================================================

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
GRAPHIFY_OUT="$REPO_ROOT/.agent/graphify-out"
KG_OUT="$REPO_ROOT/.agent/knowledge-graph"
LOG_DIR="$REPO_ROOT/.agent/logs"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

mkdir -p "$GRAPHIFY_OUT" "$KG_OUT" "$LOG_DIR"

echo "========================================"
echo " Refresh Knowledge Graphs"
echo " Timestamp: $TIMESTAMP"
echo "========================================"

# ---- Graphify ----
echo ""
echo "[1/2] Graphify (71.5x token savings)"
if command -v graphify &> /dev/null; then
  graphify --input "$REPO_ROOT" --output "$GRAPHIFY_OUT" --incremental
  echo "$TIMESTAMP" > "$GRAPHIFY_OUT/.last-refresh"
  echo "  Output: $GRAPHIFY_OUT"
else
  echo "  SKIP: graphify not installed"
  echo "  Install: npm install -g graphify"
fi

# ---- Understand-Anything ----
echo ""
echo "[2/2] Understand-Anything (knowledge graph)"
if command -v understand-anything &> /dev/null; then
  understand-anything --input "$REPO_ROOT" --output "$KG_OUT" --incremental
  echo "$TIMESTAMP" > "$KG_OUT/.last-refresh"
  echo "  Output: $KG_OUT"
else
  echo "  SKIP: understand-anything not installed"
  echo "  Install: npm install -g understand-anything"
fi

# ---- Summary ----
echo ""
echo "========================================"
echo " Refresh Complete"
echo "========================================"
echo "graphify:          $([ -f "$GRAPHIFY_OUT/.last-refresh" ] && echo 'OK' || echo 'NOT RUN')"
echo "understand-any:    $([ -f "$KG_OUT/.last-refresh" ] && echo 'OK' || echo 'NOT RUN')"
echo ""

# Append to log
echo "$TIMESTAMP | refresh-knowledge-graph.sh | manual" >> "$LOG_DIR/refresh.log"
