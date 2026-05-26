$ok = 0
$fail = 0
$SkillsDir = "test"
$AgentsSkillsDir = "test2"

function Do-Clone {
    param($Name, $Url, $Dir)
    Write-Host "cloning $Name..."
    return $true
}

$gitSkills = @(@("graphify", "url1"), @("ua", "url2"))
foreach ($s in $gitSkills) {
    $name = $s[0]
    if (Do-Clone $name $s[1] "dir") { $ok++ } else { $fail++ }
}

foreach ($name in @("smol", "caveman")) {
    $ver = "1.0.0"
    if ($ver) { $ok++ } else { $fail++ }
}

$lockFile = "lock.json"
if (Test-Path -LiteralPath $lockFile) {
    Write-Host "ok"
}

Write-Host "done ok=$ok fail=$fail"
