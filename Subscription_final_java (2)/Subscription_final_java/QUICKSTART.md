# 🚀 QUICK START GUIDE

## Prerequisites
- ✅ Java 8 or higher
- ✅ MySQL 8.0 or higher
- ✅ MySQL Connector/J 9.6.0 (included)

---

## 🏃 Run in 3 Steps

### Step 1: Setup Database
```bash
mysql -u root -p
```

Then run:
```sql
source database_setup.sql
```

Or on Windows:
```bash
mysql -u root -p < database_setup.sql
```

### Step 2: Configure Database (if needed)
Edit `src/dao/DatabaseConnection.java`:
```java
private static final String USER = "root";      // Your MySQL username
private static final String PASSWORD = "root";  // Your MySQL password
```

### Step 3: Run the Application
Double-click:
```
compile_and_run.bat
```

Or manually:
```bash
cd src
javac -cp .;..\mysql-connector-j-9.6.0.jar ui\AdminDashboard.java
java -cp .;..\mysql-connector-j-9.6.0.jar ui.AdminDashboard
```

---

## 🎮 Using the Application

### First Time Setup
1. **Create Plans** (Plans menu)
   - Basic Monthly: $9.99, 30 days
   - Pro Monthly: $19.99, 30 days

2. **Register Users** (Users menu)
   - Add 2-3 test users

3. **Assign Subscriptions** (Assign Subscription menu)
   - Link users to plans

### Daily Operations
4. **View Dashboard** (Dashboard menu)
   - See metrics: Active subs, unpaid invoices, revenue

5. **Run Billing** (Run Billing button)
   - Generates invoices automatically

6. **Manage Invoices** (Invoices menu)
   - Mark invoices as paid

7. **View Payments** (Payments menu)
   - See payment history

---

## 🎯 Navigation

```
┌─────────────────────────────────────────────────────────┐
│  Subscription Billing System             👤 Admin User │
├────────────┬────────────────────────────────────────────┤
│ Dashboard  │                                            │
│            │         MAIN CONTENT AREA                  │
│ Plans      │                                            │
│ Users      │                                            │
│ Assign     │                                            │
│ View Subs  │                                            │
│            │                                            │
│ Invoices   │                                            │
│ Payments   │                                            │
│            │                                            │
│ Run Billing│                                            │
├────────────┴────────────────────────────────────────────┤
│ Ready                                       v1.0.0      │
└─────────────────────────────────────────────────────────┘
```

---

## 🐛 Troubleshooting

### Database Connection Error
```
Error: Access denied for user 'root'@'localhost'
```
**Solution**: Update username/password in `DatabaseConnection.java`

### ClassNotFoundException
```
Error: com.mysql.cj.jdbc.Driver not found
```
**Solution**: Ensure `mysql-connector-j-9.6.0.jar` is in the project root

### Compilation Errors
```
Error: package does not exist
```
**Solution**: Compile from the `src` directory using the batch scripts

---

## 📁 Project Structure
```
Subscription_final_java/
├── src/
│   ├── model/           # Domain entities
│   ├── dao/             # Database access
│   ├── service/         # Business logic
│   └── ui/              # User interface
│       ├── utils/
│       └── tablemodels/
├── mysql-connector-j-9.6.0.jar
├── database_setup.sql
├── compile.bat
├── run.bat
├── compile_and_run.bat
└── README.md
```

---

## 🎨 Features

✅ **Dashboard**: Real-time metrics  
✅ **Plans**: Create subscription plans  
✅ **Users**: Register users  
✅ **Subscriptions**: Assign and manage  
✅ **Invoices**: View and mark as paid  
✅ **Payments**: Payment history  
✅ **Billing**: Automated invoice generation  

---

## 🔧 Customization

### Change Colors
Edit color codes in UI classes:
```java
new Color(41, 128, 185)  // Primary blue
new Color(46, 204, 113)  // Success green
```

### Add New Menu Item
1. Add button in `SidebarPanel.java`
2. Create new panel in `ui/`
3. Add case in `AdminDashboard.handleNavigation()`
4. Add to CardLayout

### Change Database
Update `DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://your_host:3306/your_db";
```

---

## 📊 Sample Data

The `database_setup.sql` script includes:
- ✅ 5 sample plans
- ✅ 5 sample users
- ✅ 3 sample subscriptions
- ✅ 5 sample invoices
- ✅ 3 sample payments

---

## 🎓 Learn More

- **README.md** - Full documentation
- **ARCHITECTURE.md** - Design decisions
- **PROJECT_SUMMARY.md** - Complete feature list

---

## 💡 Pro Tips

1. **Dashboard First**: Always check dashboard after operations
2. **Run Billing Daily**: Simulates real-world billing cycles
3. **Color Coding**: Green = good, Orange = warning, Red = urgent
4. **Refresh Button**: Use to reload data after external changes

---

## ✨ That's It!

You now have a fully functional enterprise-grade Subscription Billing System.

**Happy Coding! 🚀**
