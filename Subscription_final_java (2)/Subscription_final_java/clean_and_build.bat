@echo off
echo ========================================
echo    SubAxis - Clean and Rebuild
echo ========================================
echo.

REM Clean old compiled files from src directory
echo Cleaning old .class files from src...
for /r src %%i in (*.class) do del "%%i" 2>nul

REM Clean bin directory
echo Cleaning bin directory...
if exist bin (
    del /q bin\* 2>nul
    for /d %%d in (bin\*) do rmdir /s /q "%%d" 2>nul
)

REM Create bin directories if they don't exist
if not exist bin mkdir bin

REM Compile with proper classpath
echo.
echo Compiling project...
javac -encoding UTF-8 -d bin -cp "lib/*" src/Main.java src/dao/*.java src/model/*.java src/service/*.java src/util/*.java src/ui/theme/*.java src/ui/utils/*.java src/ui/tablemodels/*.java src/ui/components/*.java src/ui/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo    BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo You can now run the application:
    echo   - In VS Code: Press F5 or click Run
    echo   - In Terminal: java -cp "bin;lib/*" Main
    echo.
) else (
    echo.
    echo ========================================
    echo    BUILD FAILED!
    echo ========================================
    echo.
    echo Please check the error messages above.
    echo.
)

pause
