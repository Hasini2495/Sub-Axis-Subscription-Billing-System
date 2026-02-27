# 🚀 Quick Start - Run in VS Code

## 1️⃣ Clean Everything (IMPORTANT!)

Open PowerShell terminal in VS Code and run:

```powershell
Remove-Item -Recurse -Force bin
```

## 2️⃣ Press F5

Just press **F5** on your keyboard (or click Run → Start Debugging)

That's it! The application will:
- ✅ Clean `bin/` folder automatically
- ✅ Recompile all files
- ✅ Launch the application

## 3️⃣ Login

- **Email**: `john.doe@example.com`
- **Password**: `user123`

## 4️⃣ Check Debug Console

Look at the "Debug Console" tab in VS Code. You should see:
```
=== createPlansPanel() called ===
Plans loaded: 5
  - Basic Monthly ($9.99)
  ...

=== createSubscriptionsPanel() called ===
Current User ID: 1
Subscriptions loaded: 3
  - Sub ID: 1, Status: ACTIVE
  ...
```

## ✅ What You Should See

- **Available Plans**: 5 plans displayed
- **My Subscriptions**: 3 active subscriptions
- **Payments**: 1 payment record
- **Invoices**: 3 invoices

## ❌ Still Empty?

If panels are still empty:

1. **Check Debug Console** - Do you see the debug output above?
   - ✅ Yes → Data is loading, might be a display issue. Try clicking different panels.
   - ❌ No → Database connection issue. Verify MySQL is running.

2. **Run Test**:
   ```powershell
   java -cp "bin;lib/*" TestDataDisplay
   ```
   Should show "SUCCESS" messages.

3. **Verify MySQL**:
   ```powershell
   mysql -u root -pkanna -e "USE subscription_db; SELECT COUNT(*) FROM plans;"
   ```
   Should return: `5`

4. **Force Clean Rebuild**:
   ```powershell
   Remove-Item -Recurse -Force bin
   Remove-Item -Recurse -Force .vscode\.java
   ```
   Then restart VS Code and press F5 again.

## 📋 Credentials

| User Type | Email | Password | Expected Data |
|-----------|-------|----------|---------------|
| Regular User | john.doe@example.com | user123 | 3 subs, 5 plans |
| Admin | admin@company.com | admin123 | Full access |

---

**Need more details?** See [VS_CODE_RUN_INSTRUCTIONS.md](VS_CODE_RUN_INSTRUCTIONS.md)
