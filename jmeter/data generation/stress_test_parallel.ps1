# stress_test_extreme.ps1
param(
    [int]$NumRuns = 1
)

Write-Host "🌪️  EXTREME STRESS TEST - $NumRuns PROCESSES" -ForegroundColor Red

# Execute the script with no controll
$jobs = @()
for ($i = 1; $i -le $NumRuns; $i++) {
    Write-Host "💥 Starting process number $i" -ForegroundColor Red

    $job = Start-Job -ScriptBlock {
        & python .\main.py
        return $LASTEXITCODE
    }
    $jobs += $job
}

Write-Host "`n☢️  $NumRuns processes executing simultaneously!" -ForegroundColor Red
Write-Host "⚠️  The system may start failing..." -ForegroundColor Yellow

$completed = 0
foreach ($job in $jobs) {
    $result = Receive-Job -Job $job -Wait
    if ($result -eq 0) { $completed++ }
    Remove-Job -Job $job
}

Write-Host "`n🎉 Completed processes: $completed/$NumRuns" -ForegroundColor Green