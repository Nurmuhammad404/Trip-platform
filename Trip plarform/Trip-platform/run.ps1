# PowerShell script to build and run Trip Platform with Maven

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Trip Platform - Maven Build and Run" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven is installed
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if (!$mvnCmd) {
    Write-Host "ERROR: Maven is not installed or not in PATH!" -ForegroundColor Red
    Write-Host "Please install Maven from https://maven.apache.org/" -ForegroundColor Yellow
    pause
    exit 1
}

Write-Host "Maven version:" -ForegroundColor Green
mvn --version
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building with Maven..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "BUILD FAILED!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    pause
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Running Trip Platform..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

mvn exec:java

Write-Host ""
pause
