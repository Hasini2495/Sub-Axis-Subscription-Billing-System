@echo off
REM Clean and compile SubAxis application
echo Cleaning old class files...
if exist bin rmdir /s /q bin
if exist out rmdir /s /q out
del /s /q src\*.class 2>nul

echo Creating bin directory...
mkdir bin 2>nul

echo Compiling project...
javac -encoding UTF-8 -d bin -cp "lib/*;." src/util/*.java src/dao/*.java src/model/*.java src/service/*.java src/ui/utils/*.java src/ui/theme/*.java src/ui/components/*.java src/ui/tablemodels/*.java src/ui/*.java src/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Starting application...
    java -cp "bin;lib/*" Main
) else (
    echo Compilation failed!
    pause
)
