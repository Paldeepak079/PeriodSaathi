#!/usr/bin/env bash
# ============================================================
# update-all-skills.sh
# Fetches latest versions of all 8 AI tools/skills.
# Run manually or via weekly GitHub Action.
# ============================================================

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SKILLS_DIR="$REPO_ROOT/.agent/skills"
LOG_DIR="$REPO_ROOT/.agent/logs"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

mkdir -p "$SKILLS_DIR" "$LOG_DIR"

echo "========================================"
echo " Update All Skills"
echo " Timestamp: $TIMESTAMP"
echo "========================================"

update_git_repo() {
  local name="$1"
  local url="$2"
  local target="$SKILLS_DIR/$name"

  echo ""
  echo "[$name] $url"

  if [ -d "$target/.git" ]; then
    echo "  → Pulling latest..."
    (cd "$target" && git pull --ff-only) && echo "  ✓ Updated" || echo "  ⚠ Pull failed"
  else
    echo "  → Cloning..."
    rm -rf "$target"
    git clone --depth 1 "$url" "$target" && echo "  ✓ Cloned" || echo "  ⚠ Clone failed"
  fi
}

# 1. Smol Developer — npx-based, no git repo
echo ""
echo "[smol-developer] https://github.com/smol-ai/developer"
echo "  → npx-checking latest version..."
npx smol-developer --version 2>/dev/null && echo "  ✓ smol-developer available" || echo "  ⚠ Run: npx smol-developer to pull latest"

# 2. awesome-ai-agents-2026
update_git_repo "awesome-ai-agents-2026" "https://github.com/ARUNAGIRINATHAN-K/awesome-ai-agents-2026.git"

# 3. AI Review — GitHub Action, version tracked in workflow
echo ""
echo "[ai-review] https://github.com/Nikita-Filonov/ai-review"
echo "  → Action version: Nikita-Filonov/ai-review@v1 (update .github/workflows/pr-review.yml for newer)"

# 4. Caveman — npm check
echo ""
echo "[caveman] https://github.com/JuliusBrussee/caveman"
echo "  → Checking npm..."
npm view caveman version 2>/dev/null && echo "  ✓ npm latest available" || echo "  ⚠ npm check failed"

# 5. AI-Code-Review — GitHub Action
echo ""
echo "[ai-code-review] https://github.com/nuekkis/AI-Code-Review"
echo "  → Action version: nuekkis/AI-Code-Review@v1 (update .github/workflows/pr-review.yml for newer)"

# 6. Understand-Anything
update_git_repo "understand-anything" "https://github.com/Lum1104/Understand-Anything.git"

# 7. Graphify
update_git_repo "graphify" "https://github.com/ALJAZEERAPLUS/graphify.git"

# 8. Agent Almanac (69k+ skills)
update_git_repo "agent-almanac" "https://github.com/BehiSecc/awesome-claude-skills.git"

# ---- Generate Index ----
echo ""
echo "========================================"
echo " Regenerating Skill Index..."
echo "========================================"

cat > "$SKILLS_DIR/INDEX.md" << EOINDEX
# Agent Skills Index

Auto-generated: $TIMESTAMP

| # | Skill | Source | Type |
|---|-------|--------|------|
$(i=0; for f in "$SKILLS_DIR"/*.md; do
  [ "$(basename "$f")" = "INDEX.md" ] && continue
  i=$((i+1))
  name=$(grep -m1 '^name:' "$f" 2>/dev/null | sed 's/.*name: *//')
  desc=$(grep -m1 '^description:' "$f" 2>/dev/null | sed 's/.*description: *//')
  [ -z "$name" ] && name="$(basename "$f" .md)"
  echo "| $i | \`$name\` | $desc | Skill |"
done)
EOINDEX

echo "  ✓ INDEX.md regenerated"

# ---- Summary ----
SKILL_COUNT=$(find "$SKILLS_DIR" -maxdepth 1 -name '*.md' -not -name 'INDEX.md' | wc -l)
ALMANAC_COUNT=0
[ -d "$SKILLS_DIR/agent-almanac" ] && ALMANAC_COUNT=$(find "$SKILLS_DIR/agent-almanac" -name '*.md' 2>/dev/null | wc -l)

echo ""
echo "========================================"
echo " Update Complete"
echo "========================================"
echo " Local skills:   $SKILL_COUNT"
echo " Almanac skills: $ALMANAC_COUNT"
echo " Total:          $((SKILL_COUNT + ALMANAC_COUNT))"
echo ""

echo "$TIMESTAMP | update-all-skills.sh | complete" >> "$LOG_DIR/update.log"
