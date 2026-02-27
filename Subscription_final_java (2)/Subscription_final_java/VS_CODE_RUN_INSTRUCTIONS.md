# Running SubAxis in VS Code - Complete Guide

## ✅ IMPORTANT: Fresh Start Required

VS Code may cache old compiled files. Follow these steps to ensure you're running the latest version:

## Method 1: Press F5 (Recommended)

1. **Open the project in VS Code**
   - Ensure you have the project folder open: `c:\Users\91949\OneDrive\Documents\Subscription_final_java`

2. **Press F5** (or click Run → Start Debugging)
   - This will automatically:
     - Clean the `bin` folder
     - Recompile all Java files
     - Launch the application with debug console output

3. **Login**
   - Email: `john.doe@example.com`
   - Password: `user123`

4. **Check the Debug Console**
   - You should see output like:
     ```
     === createPlansPanel() called ===
     Plans loaded: 5
       - Basic Monthly ($9.99)
       - Pro Monthly ($19.99)
       ...
     ```

## Method 2: Run from Terminal (Alternative)

If F5 doesn't work, use the integrated terminal:

```powershell
# Clean and rebuild
Remove-Item -Recurse -Force bin
javac -encoding UTF-8 -d bin -cp "lib/*" src/dao/*.java src/model/*.java src/service/*.java src/util/*.java
javac -encoding UTF-8 -d bin -cp "bin;lib/*" src/ui/utils/*.java src/ui/theme/*.java src/ui/components/*.java src/ui/tablemodels/*.java
javac -encoding UTF-8 -d bin -cp "bin;lib/*" src/ui/*.java src/Main.java

# Run
java -cp "bin;lib/*" Main
```

## Method 3: Use the Batch Script

Double-click `run_vscode.bat` in the project root folder.

## Troubleshooting Empty Panels

### Problem: All panels still show empty

**Solution 1: Force Clean Build**
```powershell
# From project root in terminal
Remove-Item -Recurse -Force bin
Remove-Item -Recurse -Force .vscode\.java
```

Then restart VS Code and press F5.

**Solution 2: Check Database Connection**

Run this test to verify data is in the database:
```powershell
java -cp "bin;lib/*" TestDataDisplay
```

Expected output:
```
=== Testing Data Display (Post-Fix) ===
User: John Doe (ID: 1)
Status: ACTIVE

1. Testing Plans Display:
   Plans loaded: 5
   SUCCESS: Plans will be displayed

2. Testing Subscriptions Display:
   Total subscriptions for user: 3
   Active subscriptions (ACTIVE): 3
   SUCCESS: 3 subscription(s) will be displayed
```

**Solution 3: Verify Compiled Files Have Latest Changes**

Check if UserDashboard_V2.class was recently compiled:
```powershell
Get-Item bin\ui\UserDashboard_V2.class | Select-Object LastWriteTime
```

If the timestamp is old (not from today), force recompile:
```powershell
Remove-Item bin\ui\UserDashboard_V2.class
Remove-Item bin\ui\UserDashboard_V2`$*.class  # Remove inner classes
```

Then press F5 again.

## What Should You See?

### Available Plans Panel
- **5 plans** displayed in a grid:
  1. Basic Monthly - $9.99
  2. Pro Monthly - $19.99
  3. Enterprise Monthly - $49.99
  4. Basic Yearly - $99.99
  5. Pro Yearly - $199.99

### My Subscriptions Panel
- **3 active subscriptions** for John Doe:
  1. Basic Monthly (ACTIVE)
  2. Basic Yearly (ACTIVE) 
  3. Basic Monthly (ACTIVE)
- Each shows start date, end date, next billing date
- Each has a "Cancel" button

### Payments Panel
- **1 payment** record displayed
- Shows payment date, amount, method, status

### Invoices Panel
- **3 invoices** displayed
- Each linked to a subscription
- Shows invoice number, date, amount, status

### Dashboard Panel
- Active subscriptions count: **3**
- Recent activity with timestamps
- Quick action cards
- Metric cards with counts

## Debug Console Output

When you press F5, you should see debug output in the Debug Console:

```
=== createPlansPanel() called ===
Plans loaded: 5
  - Basic Monthly ($9.99)
  - Pro Monthly ($19.99)
  - Enterprise Monthly ($49.99)
  - Basic Yearly ($99.99)
  - Pro Yearly ($199.99)

=== createSubscriptionsPanel() called ===
Current User ID: 1
Subscriptions loaded: 3
  - Sub ID: 1, Status: ACTIVE
  - Sub ID: 2, Status: ACTIVE
  - Sub ID: 3, Status: ACTIVE
```

If you don't see this output, the panels will be empty!

## Common Issues

### Issue: "No plans available" even after clean build

**Cause**: Database connection issue

**Fix**:
1. Verify MySQL is running
2. Check database credentials in `src/dao/DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/subscription_db";
   private static final String USER = "root";
   private static final String PASSWORD = "kanna";
   ```
3. Test connection:
   ```powershell
   mysql -u root -pkanna -e "USE subscription_db; SELECT COUNT(*) FROM plans;"
   ```

### Issue: VS Code shows old version despite rebuild

**Cause**: VS Code Java extension caching

**Fix**:
1. Close VS Code completely
2. Delete these folders:
   ```powershell
   Remove-Item -Recurse -Force bin
   Remove-Item -Recurse -Force .vscode\.java
   ```
3. Reopen VS Code
4. Press F5

### Issue: "Class not found" error

**Cause**: Classpath not set correctly

**Fix**: Check `.vscode/launch.json` has:
```json
"classPaths": [
    "${workspaceFolder}/bin",
    "${workspaceFolder}/lib/*"
]
```

## Verification Checklist

Before reporting issues, verify:

- [ ] MySQL server is running
- [ ] Database `subscription_db` exists with data
- [ ] `lib/` folder contains MySQL connector JAR
- [ ] `bin/` folder is empty before F5
- [ ] VS Code shows "Compilation successful" in terminal
- [ ] Debug console shows debug output when panels load
- [ ] No errors in Debug Console or Terminal

## Login Credentials

### Regular User (See all data):
- **Email**: john.doe@example.com
- **Password**: user123
- **Expected**: 3 subscriptions, 5 plans, 1 payment, 3 invoices

### Admin User (Full management):
- **Email**: admin@company.com
- **Password**: admin123
- **Access**: Admin Dashboard with user/subscription management

## Quick Fixes Summary

| Problem | Quick Fix |
|---------|-----------|
| Old version running | `Remove-Item -Recurse -Force bin; Press F5` |
| Empty panels | Check Debug Console for error messages |
| No debug output | Verify compilation was successful |
| Database errors | Check MySQL is running: `mysql -u root -pkanna -e "SHOW DATABASES;"` |
| Class not found | Check lib/ folder has mysql-connector.jar |

## Files Updated for This Fix

1. **UserDashboard_V2.java**
   - Fixed status comparison: "Active" → "ACTIVE"
   - Added debug output for troubleshooting

2. **.vscode/launch.json**
   - Added preLaunchTask for clean builds

3. **.vscode/tasks.json**
   - Added "Clean and Build" task

4. **run_vscode.bat**
   - Batch script for manual run

## Support

If panels are still empty after following all steps:

1. Run test: `java -cp "bin;lib/*" TestDataDisplay`
2. Check output - should show "SUCCESS" messages
3. If test succeeds but UI is empty, check Debug Console for exceptions
4. Share the Debug Console output for further diagnosis

---

**Last Updated**: February 24, 2026  
**Status**: ✅ Fixed - Case sensitivity issue resolved  
**Known Working**: VS Code with Java Extension Pack
