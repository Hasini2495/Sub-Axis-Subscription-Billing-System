@echo off
echo ========================================
echo Compiling Subscription Billing System
echo ========================================
echo.

cd src

echo Compiling Model classes...
javac model\*.java

echo Compiling DAO classes...
javac -cp .;..\mysql-connector-j-9.6.0.jar dao\*.java

echo Compiling Service classes...
javac -cp .;..\mysql-connector-j-9.6.0.jar service\*.java

echo Compiling UI Utilities...
javac ui\utils\*.java

echo Compiling Table Models...
javac ui\tablemodels\*.java

echo Compiling UI Components...
javac -cp .;..\mysql-connector-j-9.6.0.jar ui\*.java

cd ..

echo.
echo ========================================
echo Compilation Complete!
echo ========================================
echo.
echo To run the application, execute: run.bat
echo.

pause
