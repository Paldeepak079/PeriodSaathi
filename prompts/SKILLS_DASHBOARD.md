# Period Saathi — Skills Dashboard

> Auto-generated: view current status of all agent skills in the project.
> Last generated: May 2026

---

## Legend

| Icon | Meaning |
|------|---------|
| ✅ | Up to date, auto-update enabled |
| ⏰ | Not auto-updated (manual only) |
| ❌ | Missing version file |
| 🔄 | Update available (run script) |

---

## All Skills

### Project Skills (`.claude/skills/`)

| Status | Skill | Version | Auto-Update | Interval | Source |
|--------|-------|---------|-------------|----------|--------|
| ✅ | **agent-almanac** | 1.0.0 | ✅ yes | weekly | `BehiSecc/awesome-claude-skills` |
| ✅ | **ai-code-review** | 1.0.0 | ✅ yes | weekly | `nuekkis/AI-Code-Review` |
| ✅ | **ai-review** | 1.0.0 | ✅ yes | weekly | `Nikita-Filonov/ai-review` |
| ✅ | **awesome-ai-agents** | 1.0.0 | ✅ yes | weekly | `ARUNAGIRINATHAN-K/awesome-ai-agents-2026` |
| ✅ | **caveman** | 1.0.0 | ✅ yes | weekly | `npm:caveman` |
| ✅ | **graphify** | 0.4.21 | ✅ yes | **commit** | `ALJAZEERAPLUS/graphify` |
| ✅ | **openspec-apply-change** | 1.0 | ⏰ no | — | local |
| ✅ | **openspec-archive-change** | 1.0 | ⏰ no | — | local |
| ✅ | **openspec-explore** | 1.0 | ⏰ no | — | local |
| ✅ | **openspec-propose** | 1.0 | ⏰ no | — | local |
| ✅ | **smol-developer** | 1.0.0 | ✅ yes | weekly | `npm:smol-developer` |
| ✅ | **understand-anything** | 1.0.0 | ✅ yes | **commit** | `Lum1104/Understand-Anything` |

### Agent Skills (`.agents/skills/`)

| Status | Skill | Version | Auto-Update | Interval | Source |
|--------|-------|---------|-------------|----------|--------|
| ✅ | **supabase** | 0.1.2 | ✅ yes | weekly | `supabase/agent-skills` |
| ✅ | **supabase-postgres-best-practices** | 1.1.1 | ✅ yes | weekly | `supabase/agent-skills` |

### Global Skills (user-level installs)

| Status | Skill | Version | Location |
|--------|-------|---------|----------|
| ✅ | **graphify** | 0.4.21 | `~/.config/opencode/skills/graphify/` |
| ✅ | **graphify** | 0.4.21 | `~/.claude/skills/graphify/` |

---

## Update Schedule

| Trigger | Interval | Skills Affected | Mechanism |
|---------|----------|----------------|-----------|
| **Git post-commit** | Every commit | graphify, understand-anything | `.git/hooks/post-commit` |
| **GitHub Actions** | Weekly (Mon 00:00) | All auto-update skills | `.github/workflows/update-skills.yml` |
| **Manual** | On demand | All skills | `.agent/scripts/update-skills.ps1` |

---

## Quick Commands

```powershell
# Update all skills manually
.\.agent\scripts\update-skills.ps1

# Check a specific skill version
Get-Content .claude\skills\graphify\.version

# View skill SKILL.md
notepad .claude\skills\graphify\SKILL.md
```

---

## Directory Structure

```
PeriodSaathi/
├── opencode.json                        ← Central skill registry
├── .claude/
│   ├── skills/
│   │   ├── agent-almanac/       [SKILL.md, .version]
│   │   ├── ai-code-review/      [SKILL.md, .version]
│   │   ├── ai-review/           [SKILL.md, .version]
│   │   ├── awesome-ai-agents/   [SKILL.md, .version]
│   │   ├── caveman/             [SKILL.md, .version]
│   │   ├── graphify/            [SKILL.md, .version]  ← 1238 lines (synced)
│   │   ├── openspec-apply-change/  [SKILL.md, .version]
│   │   ├── openspec-archive-change/ [SKILL.md, .version]
│   │   ├── openspec-explore/    [SKILL.md, .version]
│   │   ├── openspec-propose/    [SKILL.md, .version]
│   │   ├── smol-developer/      [SKILL.md, .version]
│   │   └── understand-anything/ [SKILL.md, .version]
│   └── commands/opsx/           ← Slash-command definitions
├── .agents/
│   └── skills/
│       ├── supabase/            [SKILL.md, .version]
│       └── supabase-postgres-best-practices/ [SKILL.md, .version]
├── .agent/
│   ├── scripts/
│   │   ├── update-skills.ps1    ← PowerShell updater
│   │   └── update-all-skills.sh ← Bash updater (original)
│   └── logs/
│       └── update.log
├── .github/
│   └── workflows/
│       └── update-skills.yml    ← Weekly auto-update
└── prompts/
    ├── SKILLS_DASHBOARD.md       ← You are here
    ├── ImplementationPlan.md
    ├── masterprompts/
    └── fixallMasterPrompt/
```

---

## Troubleshooting

**Skill not triggering?**
- Check the `trigger` field in the skill's `SKILL.md` YAML front-matter
- Ensure the skill directory exists at `.claude/skills/<name>/SKILL.md`
- The system prompt lists available skills — if not listed, check `opencode.json`

**Update script failing?**
- Run `powershell -ExecutionPolicy Bypass -File .agent/scripts/update-skills.ps1`
- Check `.agent/logs/update.log` for error details
- Ensure Git is installed and accessible
