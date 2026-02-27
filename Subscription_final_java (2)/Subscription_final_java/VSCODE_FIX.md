# VS Code - Fresh Build Instructions

## ✅ Issue Fixed!

Your VS Code configuration has been updated to use the latest compiled code.

---

## What Was Fixed:

1. **✅ Cleaned old .class files** from `src/` directory
2. **✅ Updated `.vscode/launch.json`** to use `lib/*` for MySQL driver
3. **✅ Updated `.vscode/settings.json`** to reference `lib/**/*.jar`
4. **✅ Cleaned and rebuilt** entire project fresh
5. **✅ Created `clean_and_build.bat`** for easy rebuilds

---

## How to Run in VS Code:

### Option 1: Using Run Button (Recommended)
1. **Open `Main.java`** in VS Code
2. **Press F5** or click the **Run** button
3. Application will launch with latest code ✅

### Option 2: Using Command Palette
1. Press `Ctrl+Shift+P`
2. Type: `Java: Run Java File`
3. Press Enter

### Option 3: Using Debug Console
1. Open Debug panel (Ctrl+Shift+D)
2. Select "SubAxis - Main Application" from dropdown
3. Click green play button

---

## If You Still See Old Code:

### Step 1: Reload VS Code Window
**Important!** VS Code's Java Language Server may be caching old files.

1. Press `Ctrl+Shift+P`
2. Type: `Developer: Reload Window`
3. Press Enter
4. Wait for Java Language Server to restart (check bottom bar)

### Step 2: Clean Java Language Server Workspace
If reload doesn't work:

1. Press `Ctrl+Shift+P`
2. Type: `Java: Clean Java Language Server Workspace`
3. Press Enter
4. Select "Reload and delete" when prompted
5. Wait for VS Code to restart and reindex

### Step 3: Manual Clean Build
Run this in PowerShell terminal:

```powershell
.\clean_and_build.bat
```

Then reload VS Code window.

---

## Configuration Files Updated:

### 1. `.vscode/launch.json`
```json
{
    "classPaths": [
        "${workspaceFolder}/bin",
        "${workspaceFolder}/lib/*"
    ]
}
```
- Now uses `lib/*` for MySQL connector
- Working directory set correctly

### 2. `.vscode/settings.json`
```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar"
    ],
    "java.project.outputPath": "bin"
}
```
- Points to `lib/` folder for all JARs
- Output goes to `bin/` directory

### 3. `.vscode/tasks.json`
- Already configured for proper compilation
- Uses `lib/*` in classpath

---

## Verification Steps:

### Check 1: Verify Compilation
```powershell
Get-ChildItem -Path "bin" -Recurse -Filter "*.class" | Measure-Object
```
Should show multiple .class files.

### Check 2: Check No Files in src/
```powershell
Get-ChildItem -Path "src" -Recurse -Filter "*.class"
```
Should find **no files** (src/ should not have .class files).

### Check 3: Run Application
```powershell
java -cp "bin;lib/*" Main
```
Should launch the latest version.

---

## Quick Reference:

| Action | Command |
|--------|---------|
| **Run in VS Code** | Press `F5` |
| **Reload VS Code** | `Ctrl+Shift+P` → "Reload Window" |
| **Clean Build** | `.\clean_and_build.bat` |
| **Clean Java Cache** | `Ctrl+Shift+P` → "Clean Java Language Server" |
| **Run in Terminal** | `java -cp "bin;lib/*" Main` |

---

## Common Issues:

### Issue: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Solution:** Make sure `lib/mysql-connector-j-9.6.0.jar` exists.

### Issue: VS Code still shows old version after F5
**Solution:** 
1. Close application
2. Run `.\clean_and_build.bat`
3. Reload VS Code window (`Ctrl+Shift+P` → "Reload Window")
4. Press F5 again

### Issue: "Cannot find Main class"
**Solution:**
1. Check that `bin/Main.class` exists
2. Reload Java Projects: `Ctrl+Shift+P` → "Java: Reload Projects"

---

## Project Structure:

```
Subscription_final_java/
├── bin/                    ← Compiled .class files (DO NOT EDIT)
├── lib/                    ← MySQL connector JAR
│   └── mysql-connector-j-9.6.0.jar
├── src/                    ← Source code (NO .class files here!)
│   ├── Main.java
│   ├── dao/
│   ├── model/
│   ├── service/
│   ├── ui/
│   └── util/
├── .vscode/                ← VS Code configurations (UPDATED)
│   ├── launch.json         ← Run configuration
│   ├── settings.json       ← Java project settings
│   └── tasks.json          ← Build tasks
├── clean_and_build.bat     ← Use this to rebuild (NEW)
└── run.bat                 ← Run from terminal
```

---

## Best Practices:

1. **Always compile to `bin/`** - Never leave .class files in `src/`
2. **Run `clean_and_build.bat`** before committing changes
3. **Reload VS Code window** after major changes
4. **Use F5 in VS Code** for running (not terminal commands in VS Code)

---

## ✨ Result:

VS Code will now run the **latest compiled version** every time you press F5!

No more stale code issues. 🎉

---

## Need Help?

If you still see old code after these steps:

1. ✅ Run: `.\clean_and_build.bat`
2. ✅ Reload VS Code: `Ctrl+Shift+P` → "Reload Window"
3. ✅ Clean Java Cache: `Ctrl+Shift+P` → "Clean Java Language Server Workspace"
4. ✅ Restart VS Code completely (close and reopen)
5. ✅ Press F5 to run

This should resolve any caching issues!
