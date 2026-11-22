param(
    [ValidateSet('bootRun', 'test', 'bootJar', 'clean')]
    [string]$Task = 'bootRun'
)
$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$gradleVersion = '8.14.3'

function Find-Jdk21 {
    $candidates = New-Object 'System.Collections.Generic.List[string]'
    if ($env:JAVA_HOME) { $candidates.Add((Join-Path $env:JAVA_HOME 'bin\java.exe')) }
    $pathJava = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($pathJava) { $candidates.Add($pathJava.Source) }
    $bases = @(
        (Join-Path $env:ProgramFiles 'Eclipse Adoptium'),
        (Join-Path $env:ProgramFiles 'Java'),
        (Join-Path $env:ProgramFiles 'Microsoft'),
        (Join-Path $env:ProgramFiles 'Amazon Corretto'),
        (Join-Path $env:ProgramFiles 'Zulu'),
        (Join-Path $env:ProgramFiles 'BellSoft'),
        (Join-Path $env:LOCALAPPDATA 'Programs\Eclipse Adoptium'),
        (Join-Path $env:USERPROFILE '.jdks')
    )
    foreach ($base in $bases) {
        if (-not $base -or -not (Test-Path -LiteralPath $base -PathType Container)) { continue }
        foreach ($folder in (Get-ChildItem -LiteralPath $base -Directory -ErrorAction SilentlyContinue)) {
            $candidates.Add((Join-Path $folder.FullName 'bin\java.exe'))
        }
    }
    foreach ($exe in ($candidates | Select-Object -Unique)) {
        if (-not (Test-Path -LiteralPath $exe -PathType Leaf)) { continue }
        $jdkHomeDir = Split-Path (Split-Path $exe -Parent) -Parent
        if (-not (Test-Path -LiteralPath (Join-Path $jdkHomeDir 'bin\javac.exe') -PathType Leaf)) { continue }
        try {
            $psi = New-Object System.Diagnostics.ProcessStartInfo
            $psi.FileName = $exe
            $psi.Arguments = '-version'
            $psi.UseShellExecute = $false
            $psi.CreateNoWindow = $true
            $psi.RedirectStandardError = $true
            $psi.RedirectStandardOutput = $true
            $process = [System.Diagnostics.Process]::Start($psi)
            $output = $process.StandardError.ReadToEnd() + $process.StandardOutput.ReadToEnd()
            $process.WaitForExit()
            $match = [regex]::Match($output, '(?i)\b(?:openjdk|java)\s+(?:version\s+)?"?(?<major>\d+)')
            if ($process.ExitCode -eq 0 -and $match.Success -and [int]$match.Groups['major'].Value -eq 21) {
                return $jdkHomeDir
            }
        } catch { continue }
    }
    return $null
}

try {
    Push-Location $root
    $jdk = Find-Jdk21
    if (-not $jdk) {
        throw 'JDK 21 was not found. Install Temurin 21 from https://adoptium.net/temurin/releases/?version=21 and enable PATH and JAVA_HOME.'
    }
    $env:JAVA_HOME = $jdk
    $env:Path = (Join-Path $jdk 'bin') + ';' + $env:Path
    Write-Host "Java 21 found: $jdk" -ForegroundColor Green

    # Check local Gradle and the earlier Student Exam Results project in Downloads.
    $gradleLocations = @(
        (Join-Path $root ".tools\gradle-$gradleVersion\bin\gradle.bat"),
        (Join-Path $env:USERPROFILE "Downloads\Student_Exam_Results\.tools\gradle-$gradleVersion\bin\gradle.bat"),
        (Join-Path (Split-Path $root -Parent) "Student_Exam_Results\.tools\gradle-$gradleVersion\bin\gradle.bat")
    )
    $gradleExe = $null
    foreach ($location in $gradleLocations) {
        if (Test-Path -LiteralPath $location -PathType Leaf) { $gradleExe = $location; break }
    }
    if (-not $gradleExe) {
        $systemGradle = Get-Command gradle.bat -ErrorAction SilentlyContinue
        if ($systemGradle) { $gradleExe = $systemGradle.Source }
    }
    if (-not $gradleExe) {
        Write-Host "Downloading Gradle $gradleVersion (first run, internet required)..." -ForegroundColor Cyan
        $toolDir = Join-Path $root '.tools'
        New-Item -ItemType Directory -Path $toolDir -Force | Out-Null
        $archive = Join-Path $toolDir "gradle-$gradleVersion-bin.zip"
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip" -OutFile $archive -UseBasicParsing
        Expand-Archive -LiteralPath $archive -DestinationPath $toolDir -Force
        Remove-Item -LiteralPath $archive -Force
        $gradleExe = Join-Path $toolDir "gradle-$gradleVersion\bin\gradle.bat"
    }
    New-Item -ItemType Directory -Path (Join-Path $root '.data') -Force | Out-Null
    Write-Host "Running Gradle: $Task" -ForegroundColor Cyan
    & $gradleExe --no-daemon $Task
    if ($LASTEXITCODE -ne 0) { throw "Gradle failed with exit code $LASTEXITCODE." }
} catch {
    Write-Host ("ERROR: " + $_.Exception.Message) -ForegroundColor Red
    exit 1
} finally {
    Pop-Location
}
