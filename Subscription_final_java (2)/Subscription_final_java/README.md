# Subscription Billing System - Enterprise Admin Dashboard

## 🎯 Overview
A professional **SaaS Admin Dashboard** built with Java Swing, demonstrating enterprise-grade MVC architecture, clean separation of concerns, and production-ready coding practices.

## 📁 Project Structure

```
src/
├── model/                  # Domain entities (POJOs)
│   ├── Plan.java
│   ├── User.java
│   ├── Subscription.java
│   ├── Invoice.java
│   └── Payment.java
│
├── dao/                    # Data Access Layer
│   ├── DatabaseConnection.java
│   ├── PlanDAO.java
│   ├── UserDAO.java
│   ├── SubscriptionDAO.java
│   ├── InvoiceDAO.java
│   └── PaymentDAO.java
│
├── service/                # Business Logic Layer
│   ├── PlanService.java
│   ├── UserService.java
│   ├── SubscriptionService.java
│   ├── InvoiceService.java
│   ├── PaymentService.java
│   └── BillingService.java
│
└── ui/                     # Presentation Layer (UI)
    ├── AdminDashboard.java       # Main frame with CardLayout
    ├── HeaderPanel.java          # Top header bar
    ├── SidebarPanel.java         # Left navigation
    ├── StatusBarPanel.java       # Bottom status bar
    ├── DashboardPanel.java       # Metrics overview
    ├── CreatePlanForm.java       # Plan creation form
    ├── RegisterUserForm.java     # User registration
    ├── AssignSubscriptionForm.java  # Subscription assignment
    ├── InvoiceViewForm.java      # Invoice management
    ├── PaymentHistoryForm.java   # Payment history
    │
    ├── utils/
    │   ├── DialogUtils.java      # Centralized dialogs
    │   └── ValidationUtils.java  # Input validation
    │
    └── tablemodels/
        ├── InvoiceTableModel.java
        ├── PaymentTableModel.java
        └── SubscriptionTableModel.java
```

## 🏗️ Architecture Decisions

### ✅ Strict Layer Separation
- **UI Layer**: Pure presentation logic, NO SQL, NO business logic
- **Service Layer**: Business rules, validation, orchestration
- **DAO Layer**: Database operations only
- **Model Layer**: Plain Java objects (POJOs)

### ✅ Professional Swing Practices
- **Proper Layout Managers**: GridBagLayout, BorderLayout, BoxLayout
- **CardLayout**: For view switching without creating/destroying panels
- **Custom TableModels**: Instead of DefaultTableModel
- **SwingWorker**: For async operations (billing process)
- **invokeLater**: UI initialization on EDT
- **Validation**: Before service calls

### ✅ Enterprise Design Patterns
- **Singleton**: DatabaseConnection
- **DAO Pattern**: Separates data access
- **Service Layer Pattern**: Business logic isolation
- **MVC**: Clear separation of concerns

## 🎨 UI Features

### Dashboard
- Active Subscriptions Count
- Unpaid Invoices Count
- Total Revenue Display
- Color-coded metric cards

### Sidebar Navigation
- Dashboard
- Plans (Create)
- Users (Register)
- Subscriptions (Assign)
- Invoices (Manage)
- Payments (History)
- Run Billing (Automated)

### Color-Coded Status
- **ACTIVE/PAID**: Green
- **UNPAID**: Orange
- **EXPIRED/OVERDUE**: Red

### Professional Touches
- Hover effects on buttons
- Loading indicators
- Confirmation dialogs
- Form validation
- Auto-refresh after actions
- Status bar updates

## 🚀 Setup Instructions

### 1. Database Setup
```sql
mysql -u root -p < database_setup.sql
```

This creates:
- `subscription_db` database
- All required tables
- Sample data for testing

### 2. Configure Database Connection
Edit `DatabaseConnection.java` if needed:
```java
private static final String URL = "jdbc:mysql://localhost:3306/subscription_db";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

### 3. Compile Project
```bash
cd src
javac -cp .;../mysql-connector-j-9.6.0.jar ui/AdminDashboard.java
```

### 4. Run Application
```bash
java -cp .;../mysql-connector-j-9.6.0.jar ui.AdminDashboard
```

## 🔧 How to Extend

### Add New Feature
1. Create model in `model/`
2. Create DAO in `dao/`
3. Create service in `service/`
4. Create UI form in `ui/`
5. Add navigation in `SidebarPanel` and `AdminDashboard`

### Example: Add "Reports" Feature
```java
// 1. Create ReportService.java
// 2. Create ReportsPanel.java
// 3. Add button in SidebarPanel:
addNavigationButton("Reports", "REPORTS", navigationListener);
// 4. Add case in AdminDashboard.handleNavigation()
// 5. Add panel to CardLayout
```

## 📊 Key Metrics Displayed

| Metric | Source | Display |
|--------|--------|---------|
| Active Subscriptions | `SubscriptionService.getActiveSubscriptionCount()` | Green card |
| Unpaid Invoices | `InvoiceService.getUnpaidInvoiceCount()` | Red card |
| Total Revenue | `PaymentService.getTotalRevenue()` | Blue card |

## 🔐 Security Considerations

**For Production, Add:**
- Password hashing (BCrypt)
- Prepared statements (✅ Already implemented)
- Connection pooling (HikariCP)
- Role-based access control
- Session management
- SSL/TLS for database
- Input sanitization (✅ Validation in place)

## 🧪 Testing Workflow

1. **Create Plans**: Navigate to Plans → Create subscription plans
2. **Register Users**: Navigate to Users → Add users
3. **Assign Subscriptions**: Navigate to Subscriptions → Assign plans to users
4. **View Invoices**: Navigate to Invoices → See generated invoices
5. **Mark Paid**: Select invoice → Mark as Paid → Creates payment record
6. **Run Billing**: Click "Run Billing" → Generates invoices for active subscriptions

## 🎓 Interview-Ready Features

✅ **Clean Code**: Professional naming, comments, structure  
✅ **MVC Pattern**: Clear separation of concerns  
✅ **SOLID Principles**: Single responsibility, open/closed  
✅ **No Magic Numbers**: Constants and enums  
✅ **Error Handling**: Try-catch with user-friendly messages  
✅ **Validation**: Input validation before service calls  
✅ **Scalability**: Easy to add new features  
✅ **Maintainability**: Each class has single responsibility  

## 🛠️ Technology Stack

- **Language**: Java 8+
- **UI Framework**: Java Swing
- **Database**: MySQL 8.0+
- **JDBC Driver**: MySQL Connector/J 9.6.0
- **Design Patterns**: MVC, DAO, Singleton, Service Layer

## 📝 Code Quality Highlights

- ✅ No null layout or absolute positioning
- ✅ Proper exception handling
- ✅ Resource management (try-with-resources)
- ✅ JavaDoc comments
- ✅ Consistent naming conventions
- ✅ No magic strings (action commands)
- ✅ Validation separated from UI logic
- ✅ Reusable components

## 🎯 Business Logic Examples

### Auto-calculate End Date
```java
// Service layer calculates end date based on plan duration
LocalDate endDate = startDate.plusDays(plan.getDuration());
```

### Automated Billing Process
```java
// BillingService handles:
// 1. Check for expired subscriptions
// 2. Update status to EXPIRED
// 3. Generate invoices for active subscriptions
// 4. Return count of invoices generated
```

## 📦 Dependencies

- MySQL Connector/J 9.6.0 (included)
- Java 8+ (for LocalDate, Lambda expressions)

## 🌟 Enterprise Features

1. **Dashboard Metrics**: Real-time business KPIs
2. **Color-Coded Tables**: Visual status indicators
3. **Confirmation Dialogs**: Prevent accidental actions
4. **Auto-Refresh**: Data updates after operations
5. **Status Bar**: User feedback on all actions
6. **Professional Layout**: No absolute positioning
7. **Async Operations**: SwingWorker for long tasks
8. **Custom Table Models**: Type-safe table data

---

**Built with ❤️ for enterprise-grade applications**

This system demonstrates production-ready Java Swing development with proper architecture, clean code, and professional UI/UX.
