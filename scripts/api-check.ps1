$ErrorActionPreference = 'Stop'
$root = 'http://127.0.0.1:8081'
try {
    foreach ($path in @('/tasks', '/board', '/api/tasks', '/api/tasks/stats', '/tasks/export.csv')) {
        $response = Invoke-WebRequest -Uri ($root + $path) -UseBasicParsing -TimeoutSec 12
        if ($response.StatusCode -ne 200) { throw "Unexpected status: $($response.StatusCode) for $path" }
        Write-Host "OK 200: $path" -ForegroundColor Green
    }
    Write-Host 'All HTTP smoke checks passed.' -ForegroundColor Green
} catch {
    Write-Host ("ERROR: " + $_.Exception.Message) -ForegroundColor Red
    Write-Host 'Start the server first with START_WINDOWS.bat and keep it running.'
    exit 1
}
