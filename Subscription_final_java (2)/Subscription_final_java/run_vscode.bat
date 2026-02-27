@echo off
echo ================================================
echo  SubAxis - Clean Build and Run for VS Code
echo ================================================

REM Clean bin directory
echo.
echo [1/4] Cleaning bin directory...
if exist bin rmdir /s /q bin
mkdir bin
echo Bin directory cleaned.

REM Compile all Java files
echo.
echo [2/4] Compiling all Java files...
powershell -Command "$files = Get-ChildItem -Recurse -Path src -Filter '*.java' | Where-Object { $_.Name -notmatch '^Test' } | ForEach-Object { $_.FullName }; javac -encoding UTF-8 -d bin -cp 'lib/mysql-connector-j-9.6.0.jar' $files"

if %ERRORLEVEL% EQU 0 (
    echo Compilation SUCCESSFUL
) else (
    echo Compilation FAILED
    pause
    exit /b 1
)

REM Verify class files exist
echo.
echo [3/4] Verifying compiled files...
if exist bin\Main.class (
    echo Main.class found
) else (
    echo ERROR: Main.class not found!
    pause
    exit /b 1
)

if exist bin\ui\UserDashboard_V2.class (
    echo UserDashboard_V2.class found
) else (
    echo ERROR: UserDashboard_V2.class not found!
    pause
    exit /b 1
)

REM Run the application
echo.
echo [4/4] Launching application...
echo ================================================
echo.
java -cp "bin;lib/mysql-connector-j-9.6.0.jar" Main

echo.
echo ================================================
echo Application closed.
pause
