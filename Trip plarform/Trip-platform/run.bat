@echo off
echo ========================================
echo Trip Platform - Maven Build and Run
echo ========================================
echo.

cd /d "%~dp0"

echo Checking for Maven...
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH!
    echo Please install Maven from https://maven.apache.org/
    pause
    exit /b 1
)

echo.
echo ========================================
echo Building with Maven...
echo ========================================
echo.

mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo BUILD FAILED!
    echo ========================================
    pause
    exit /b 1
)

echo.
echo ========================================
echo Running Trip Platform...
echo ========================================
echo.

mvn exec:java

pause
