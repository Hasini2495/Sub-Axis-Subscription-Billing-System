# 📐 SYSTEM ARCHITECTURE DIAGRAM

## Full System Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          ADMIN DASHBOARD (Main Frame)                   │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                        HEADER PANEL                               │   │
│  │  Subscription Billing System              👤 Admin User          │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│  ┌────────────┬──────────────────────────────────────────────────────┐  │
│  │  SIDEBAR   │              MAIN CONTENT (CardLayout)               │  │
│  │            │                                                      │  │
│  │ Dashboard  │  ┌────────────────────────────────────────────────┐ │  │
│  │ Plans      │  │                                                │ │  │
│  │ Users      │  │          DASHBOARD PANEL                       │ │  │
│  │ Assign Sub │  │                                                │ │  │
│  │ View Sub   │  │  ┌───────────┐ ┌───────────┐ ┌──────────┐    │ │  │
│  │ Invoices   │  │  │  Active   │ │  Unpaid   │ │  Total   │    │ │  │
│  │ Payments   │  │  │   Subs    │ │ Invoices  │ │ Revenue  │    │ │  │
│  │ Run Billing│  │  │    15     │ │     3     │ │ $1,234   │    │ │  │
│  │            │  │  └───────────┘ └───────────┘ └──────────┘    │ │  │
│  │            │  │                                                │ │  │
│  │            │  │         [Refresh Metrics]                      │ │  │
│  │            │  └────────────────────────────────────────────────┘ │  │
│  │            │                                                      │  │
│  │            │  OR: CreatePlanForm                                 │  │
│  │            │  OR: RegisterUserForm                               │  │
│  │            │  OR: AssignSubscriptionForm                         │  │
│  │            │  OR: SubscriptionViewForm                           │  │
│  │            │  OR: InvoiceViewForm                                │  │
│  │            │  OR: PaymentHistoryForm                             │  │
│  └────────────┴──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                      STATUS BAR PANEL                             │   │
│  │  Ready                                              v1.0.0        │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Layer Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI LAYER                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ AdminDashboard│  │   Forms      │  │ Table Models │          │
│  │ Header/Sidebar│  │ Create/Edit  │  │ Invoice/Pay  │          │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘          │
│         │                  │                  │                  │
│         └──────────────────┴──────────────────┘                  │
│                            │                                     │
│                    NO SQL, NO LOGIC                              │
└────────────────────────────┼─────────────────────────────────────┘
                             │
                             ↓ Service Calls Only
┌─────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ PlanService  │  │ UserService  │  │ InvoiceService│          │
│  │ - validate   │  │ - validate   │  │ - validate   │          │
│  │ - orchestrate│  │ - orchestrate│  │ - orchestrate│          │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘          │
│         │                  │                  │                  │
│         └──────────────────┴──────────────────┘                  │
│                            │                                     │
│                   Business Logic Here                            │
└────────────────────────────┼─────────────────────────────────────┘
                             │
                             ↓ DAO Calls Only
┌─────────────────────────────────────────────────────────────────┐
│                        DAO LAYER                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  PlanDAO     │  │  UserDAO     │  │ InvoiceDAO   │          │
│  │ - create()   │  │ - create()   │  │ - create()   │          │
│  │ - getAll()   │  │ - getAll()   │  │ - getAll()   │          │
│  │ - getById()  │  │ - getById()  │  │ - update()   │          │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘          │
│         │                  │                  │                  │
│         └──────────────────┴──────────────────┘                  │
│                            │                                     │
│                      Only SQL Here                               │
└────────────────────────────┼─────────────────────────────────────┘
                             │
                             ↓ JDBC
┌─────────────────────────────────────────────────────────────────┐
│                         DATABASE                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │  plans   │  │  users   │  │subscriptn│  │ invoices │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
│  ┌──────────┐                                                   │
│  │ payments │                                                   │
│  └──────────┘                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Data Flow Examples

### 1. Create Plan Flow
```
User fills form → CreatePlanForm validates input → Click "Create"
                                    ↓
                           PlanService.createPlan()
                         - Validates business rules
                         - Checks price > 0
                         - Checks duration > 0
                                    ↓
                            PlanDAO.createPlan()
                         - Executes SQL INSERT
                         - Returns success/failure
                                    ↓
                         DialogUtils.showSuccess()
                              Form resets
```

### 2. Mark Invoice as Paid Flow
```
User selects invoice row → InvoiceViewForm → Click "Mark Paid"
                                    ↓
                      InvoiceService.markInvoiceAsPaid()
                         - Gets invoice details
                         - Creates payment record
                         - Updates invoice status
                                    ↓
                  PaymentDAO.createPayment() + InvoiceDAO.updateStatus()
                         - Two SQL operations
                         - Logical transaction
                                    ↓
                         DialogUtils.showSuccess()
                           Table refreshes
```

### 3. Run Billing Process Flow
```
Admin clicks "Run Billing" → Confirmation dialog → Confirmed
                                    ↓
                         BillingService.runBillingProcess()
                         - Gets all active subscriptions
                         - For each subscription:
                           * Check if expired
                           * Update status if expired
                           * Generate invoice if active
                         - Returns count of invoices generated
                                    ↓
                    Multiple DAO calls (read subscriptions, 
                                       update status,
                                       create invoices)
                                    ↓
                         DialogUtils.showSuccess()
                       Dashboard metrics refresh
```

---

## Component Interaction Diagram

```
┌─────────────┐
│AdminDashboard│ (Main Frame)
└──────┬────┬─┘
       │    └──────────────────────────┐
       │                               │
       ↓                               ↓
┌────────────┐                  ┌──────────┐
│SidebarPanel│                  │CardLayout│
│            │                  │          │
│ [Buttons]  │ ─── onClick ───→ │ switch() │
│            │                  │          │
└────────────┘                  └─────┬────┘
                                      │
                    ┌─────────────────┼────────────────┐
                    │                 │                │
                    ↓                 ↓                ↓
            ┌──────────────┐  ┌──────────────┐  ┌──────────┐
            │DashboardPanel│  │CreatePlanForm│  │...Forms  │
            └──────┬───────┘  └──────┬───────┘  └────┬─────┘
                   │                 │               │
                   └────────┬────────┴───────────────┘
                            │
                            ↓ Service calls
                     ┌──────────────┐
                     │Service Layer │
                     └──────┬───────┘
                            │
                            ↓ DAO calls
                      ┌──────────┐
                      │DAO Layer │
                      └──────┬───┘
                             │
                             ↓ SQL
                        ┌─────────┐
                        │Database │
                        └─────────┘
```

---

## Class Relationships

```
AdminDashboard
    ├── has HeaderPanel
    ├── has SidebarPanel
    ├── has StatusBarPanel
    ├── has DashboardPanel
    │     └── uses SubscriptionService
    │     └── uses InvoiceService
    │     └── uses PaymentService
    ├── has CreatePlanForm
    │     └── uses PlanService
    ├── has RegisterUserForm
    │     └── uses UserService
    ├── has AssignSubscriptionForm
    │     └── uses UserService
    │     └── uses PlanService
    │     └── uses SubscriptionService
    ├── has InvoiceViewForm
    │     └── uses InvoiceService
    │     └── uses InvoiceTableModel
    ├── has PaymentHistoryForm
    │     └── uses PaymentService
    │     └── uses PaymentTableModel
    └── uses BillingService

Services
    ├── PlanService → PlanDAO
    ├── UserService → UserDAO
    ├── SubscriptionService → SubscriptionDAO + PlanDAO
    ├── InvoiceService → InvoiceDAO + PaymentDAO
    ├── PaymentService → PaymentDAO
    └── BillingService → SubscriptionDAO + InvoiceDAO + PlanDAO

DAOs
    ├── All use DatabaseConnection
    └── Each handles one table
```

---

## Database Schema

```
┌──────────────┐
│    plans     │
│──────────────│
│ plan_id (PK) │──┐
│ plan_name    │  │
│ price        │  │
│ billing_cycle│  │
│ duration     │  │
└──────────────┘  │
                  │
                  │    ┌──────────────────┐
                  │    │  subscriptions   │
                  │    │──────────────────│
                  └───→│ subscription_id  │──┐
┌──────────────┐      │ user_id (FK)     │  │
│    users     │      │ plan_id (FK)     │  │
│──────────────│      │ start_date       │  │
│ user_id (PK) │──┐   │ end_date         │  │
│ name         │  │   │ status           │  │
│ email        │  │   └──────────────────┘  │
│ role         │  │                         │
│ status       │  │                         │
└──────────────┘  │                         │
                  │   ┌──────────────────┐  │
                  │   │    invoices      │  │
                  │   │──────────────────│  │
                  │   │ invoice_id (PK)  │──┐
                  └──→│ subscription_id  │  │
                      │ amount           │  │
                      │ invoice_date     │  │
                      │ status           │  │
                      └──────────────────┘  │
                                            │
                      ┌──────────────────┐  │
                      │    payments      │  │
                      │──────────────────│  │
                      │ payment_id (PK)  │  │
                      │ invoice_id (FK)  │←─┘
                      │ amount           │
                      │ payment_date     │
                      │ payment_method   │
                      └──────────────────┘
```

---

## Summary

This architecture ensures:
- ✅ **Separation of Concerns**: Each layer has a single responsibility
- ✅ **Maintainability**: Changes in one layer don't affect others
- ✅ **Testability**: Each layer can be tested independently
- ✅ **Scalability**: Easy to add new features
- ✅ **Security**: Validation at multiple levels
- ✅ **Professional**: Industry-standard patterns

---

**This is a production-ready enterprise architecture!**
