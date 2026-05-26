#!/usr/bin/env pwsh
$RepoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$SkillsDir = Join-Path $RepoRoot ".claude\skills"
$AgentsDir = Join-Path $RepoRoot ".agents\skills"
$LogDir = Join-Path $RepoRoot ".agent\logs"
New-Item -ItemType Directory -Path $LogDir -Force | Out-Null
$ts = Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ"
Write-Host "skills updater | $ts"
$ok = 0; $fail = 0
$tmpDir = Join-Path $RepoRoot ".agent\tmp"

Function Do-Clone($Name, $Url, $Dir) {
    if (Test-Path $Dir) { Remove-Item -Recurse -Force $Dir -ErrorAction SilentlyContinue }
    Write-Host "  cloning $Name..."
    git clone --depth 1 $Url $Dir 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) { Write-Host "  ok $Name" -ForegroundColor Green; return $true }
    Write-Host "  fail $Name" -ForegroundColor Red; return $false
}
Function Do-Version($Path, $Ver) { Set-Content -Path (Join-Path $Path ".version") -Value $Ver -NoNewline }

$gitSkills = @( @("graphify","https://github.com/ALJAZEERAPLUS/graphify.git"), @("understand-anything","https://github.com/Lum1104/Understand-Anything.git"), @("agent-almanac","https://github.com/BehiSecc/awesome-claude-skills.git"), @("awesome-ai-agents","https://github.com/ARUNAGIRINATHAN-K/awesome-ai-agents-2026.git") )
foreach ($s in $gitSkills) {
    $d = Join-Path $SkillsDir $s[0]
    $r = Do-Clone $s[0] $s[1] $d
    if ($r) { $v = & git -C $d rev-parse --short HEAD 2>$null; if ($v) { Do-Version $d $v }; $ok++ } else { $fail++ }
}

$npmSkills = @("smol-developer","caveman")
foreach ($name in $npmSkills) {
    Write-Host "  checking npm $name..."
    $v = npm view $name version 2>$null
    if ($v) { Write-Host "  ok $name $v" -ForegroundColor Green; Do-Version (Join-Path $SkillsDir $name) $v; $ok++ }
    else { Write-Host "  fail npm $name" -ForegroundColor Red; $fail++ }
}

Write-Host "  cloning supabase..."
$st = Join-Path $tmpDir "sa"
if (Test-Path $st) { Remove-Item -Recurse -Force $st -ErrorAction SilentlyContinue }
git clone --depth 1 "https://github.com/supabase/agent-skills.git" $st 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Copy-Item (Join-Path (Join-Path $st "skills") "supabase\SKILL.md") (Join-Path $AgentsDir "supabase\SKILL.md") -Force
    Do-Version (Join-Path $AgentsDir "supabase") "0.1.2"
    Write-Host "  ok supabase" -ForegroundColor Green; $ok++
    Copy-Item (Join-Path (Join-Path $st "skills") "supabase-postgres-best-practices\SKILL.md") (Join-Path $AgentsDir "supabase-postgres-best-practices\SKILL.md") -Force
    Do-Version (Join-Path $AgentsDir "supabase-postgres-best-practices") "1.1.1"
    Write-Host "  ok postgres" -ForegroundColor Green; $ok++
    Remove-Item -Recurse -Force $st -ErrorAction SilentlyContinue
} else { Write-Host "  fail supabase" -ForegroundColor Red; $fail++; $fail++ }

$lockFile = Join-Path $RepoRoot "skills-lock.json"
if (Test-Path $lockFile) {
    $h1 = (Get-FileHash (Join-Path $AgentsDir "supabase\SKILL.md") -Algorithm SHA256).Hash.ToLower()
    $h2 = (Get-FileHash (Join-Path $AgentsDir "supabase-postgres-best-practices\SKILL.md") -Algorithm SHA256).Hash.ToLower()
    $j = '{"version":1,"skills":{"supabase":{"source":"supabase/agent-skills","sourceType":"github","skillPath":"skills/supabase/SKILL.md","computedHash":"__H1__"},"supabase-postgres-best-practices":{"source":"supabase/agent-skills","sourceType":"github","skillPath":"skills/supabase-postgres-best-practices/SKILL.md","computedHash":"__H2__"}}}'
    $j = $j.Replace("__H1__",$h1).Replace("__H2__",$h2)
    Set-Content -Path $lockFile -Value $j
    Write-Host "  ok skills-lock.json" -ForegroundColor Green
}

Write-Host "done ok=$ok fail=$fail"
"$ts | update-skills | ok=$ok fail=$fail" | Out-File (Join-Path $LogDir "update.log") -Append
Remove-Item -Recurse -Force $tmpDir -ErrorAction SilentlyContinue


