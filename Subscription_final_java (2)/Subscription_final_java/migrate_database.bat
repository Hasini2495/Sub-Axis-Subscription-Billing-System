@echo off
echo ========================================
echo Database Migration: Add Password Field
echo ========================================
echo.
echo This will add password and phone columns to the users table
echo.
echo Connecting to MySQL...
echo.

mysql -u root -pkanna < add_password_field.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Migration completed successfully!
    echo ========================================
    echo.
    echo The users table now has:
    echo - password VARCHAR(255)
    echo - phone VARCHAR(20)
    echo.
    echo You can now register new users with passwords!
    echo.
) else (
    echo.
    echo ========================================
    echo Migration failed!
    echo ========================================
    echo.
    echo Possible issues:
    echo 1. MySQL is not running
    echo 2. Database credentials are incorrect
    echo 3. Database 'subscription_db' does not exist
    echo.
    echo Run setup_database.bat to create the database from scratch
    echo.
)

pause
