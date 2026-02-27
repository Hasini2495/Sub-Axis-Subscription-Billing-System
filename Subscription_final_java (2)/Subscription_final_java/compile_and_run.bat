@echo off
echo ========================================
echo Compile and Run - Subscription System
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

echo.
echo ========================================
echo Compilation Complete!
echo ========================================
echo.
echo Starting application...
echo.

java -cp .;..\mysql-connector-j-9.6.0.jar ui.AdminDashboard
