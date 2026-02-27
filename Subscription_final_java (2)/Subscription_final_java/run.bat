@echo off
echo ========================================
echo Running Subscription Billing System
echo ========================================
echo.

cd src
java -cp .;..\mysql-connector-j-9.6.0.jar ui.AdminDashboard

pause
