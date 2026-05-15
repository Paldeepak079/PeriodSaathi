#!/usr/bin/env bash
# ============================================================
# setup-hooks.sh
# Configures git to use .githooks/ as the hooks directory.
# Run once after cloning.
# ============================================================

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
HOOKS_DIR="$REPO_ROOT/.githooks"

echo "Setting up git hooks..."
git config core.hooksPath "$HOOKS_DIR"

echo "Making hooks executable..."
chmod +x "$HOOKS_DIR"/* 2>/dev/null || true

echo "✓ Git hooks configured: $HOOKS_DIR"
echo "  post-commit hook will refresh knowledge graphs after every commit."
echo ""
echo "To test: git commit --allow-empty -m 'test post-commit hook'"
