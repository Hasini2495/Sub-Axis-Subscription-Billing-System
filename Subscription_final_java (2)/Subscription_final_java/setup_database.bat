@echo off
echo ========================================
echo    SubAxis Database Setup
echo ========================================
echo.
echo This will create the subscription_db database
echo with all required tables and sample data.
echo.
echo Please ensure MySQL is running on localhost:3306
echo.
pause

echo.
echo Setting up database...
mysql -u root -pkanna < complete_database_setup.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo    Database setup completed successfully!
    echo ========================================
    echo.
    echo You can now run the application using run.bat
    echo.
) else (
    echo.
    echo ========================================
    echo    Database setup failed!
    echo ========================================
    echo.
    echo Please check:
    echo 1. MySQL is running
    echo 2. Username and password are correct
    echo 3. MySQL is accessible on localhost:3306
    echo.
)

pause
