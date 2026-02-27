# Credential Save Feature - Testing Instructions

## Overview
The application now supports saving login credentials and auto-filling them on subsequent logins.

## New Features Implemented

### 1. **Save Credentials Dialog**
   - After successful login, a popup appears asking: "Would you like to save your login credentials?"
   - Click "Yes" to save credentials securely
   - Click "No" to skip saving
   - Works for both Admin and User logins

### 2. **Auto-Fill Credentials**
   - When you reopen the application, saved credentials are automatically filled in the email and password fields
   - A green checkmark indicator appears: "✅ Credentials auto-filled from saved data"
   - You can simply click "Sign In" to login instantly

### 3. **Login Window Maximizable**
   - The login window now opens maximized by default
   - You can resize the window as needed
   - The window is fully responsive

## How to Test

### Test 1: Save Credentials (First Login)
1. Run the application: `java -cp "out;lib/*" Main`
2. Login with any demo credentials:
   - **Admin**: admin@company.com / admin123
   - **User**: john.doe@example.com / user123
3. After successful login, you'll see a dialog: "Would you like to save your login credentials?"
4. Click **"Yes"**
5. You'll see a success message: "✅ Credentials saved successfully!"
6. The dashboard will open

### Test 2: Auto-Fill on Next Login
1. Close the entire application
2. Run the application again: `java -cp "out;lib/*" Main`
3. **Notice**: Email and password fields are automatically filled!
4. Look for the green text: "✅ Credentials auto-filled from saved data"
5. Simply click "Sign In" to login instantly

### Test 3: Login Window Maximizing
1. Run the application
2. **Notice**: The login window opens maximized (full screen)
3. You can resize the window by dragging the edges
4. All UI elements scale properly

### Test 4: Different User Credentials
1. Login with admin@company.com / admin123
2. Choose "Yes" when asked to save
3. Close and reopen - admin credentials are auto-filled
4. **Manually change** to john.doe@example.com / user123
5. Login successfully
6. Choose "Yes" to save new credentials
7. Close and reopen - user credentials are now auto-filled (replacing admin)

### Test 5: Decline to Save
1. Login with any credentials
2. When prompted to save, click **"No"**
3. Close and reopen the application
4. **Notice**: Fields are empty (no auto-fill)
5. Login again and this time click "Yes" to save

## Technical Details

### Files Modified
- **LoginFrame.java**: Added credential save prompt and auto-fill logic
- **CredentialManager.java**: NEW - Handles saving/loading credentials

### Credential Storage
- Credentials are saved in: `C:\Users\<YourUsername>\.saas_credentials.properties`
- Passwords are encoded using Base64 (basic obfuscation)
- File is hidden in user's home directory

### Security Note
⚠️ This implementation uses Base64 encoding for obfuscation, NOT encryption. For production use, consider:
- Proper encryption (AES-256)
- Operating system credential managers (Windows Credential Manager, macOS Keychain)
- OAuth/SSO integration

## Demo Credentials

### Admin Account
- Email: admin@company.com
- Password: admin123
- Role: Administrator

### User Accounts
- Email: john.doe@example.com
- Password: user123
- Role: Regular User

- Email: jane.smith@example.com
- Password: user123
- Role: Regular User

## Troubleshooting

### Credentials Not Saving
- Check if you have write permissions in your home directory
- Look for file: `C:\Users\<YourUsername>\.saas_credentials.properties`
- Check console for any error messages

### Auto-Fill Not Working
- Ensure you clicked "Yes" when prompted to save
- Verify the credentials file exists
- Try logging in again and saving credentials

### Clear Saved Credentials
To manually clear saved credentials:
1. Navigate to: `C:\Users\<YourUsername>\`
2. Delete file: `.saas_credentials.properties`
3. Reopen application - credentials won't auto-fill

## Success Criteria

✅ **Feature 1**: Save dialog appears after successful login (Admin + User)  
✅ **Feature 2**: Credentials auto-fill when reopening application  
✅ **Feature 3**: Green indicator shows when credentials are auto-filled  
✅ **Feature 4**: Login window opens maximized and is resizable  
✅ **Feature 5**: Only most recent credentials are saved (one at a time)  
✅ **Feature 6**: Works for both Admin and User roles

## Next Steps (Optional Enhancements)

1. Add "Remember Me" checkbox on login screen
2. Add "Clear saved data" button in settings
3. Implement proper encryption for passwords
4. Support multiple saved accounts with account picker
5. Add session timeout and auto-logout
6. Integrate with OS credential managers
