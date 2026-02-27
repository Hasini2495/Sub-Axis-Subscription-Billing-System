# Sub-Axis-Subscription-Billing-System
Enterprise SaaS Subscription Billing System built with Java, Java swing &amp; MySQL
Subscription Billing System – Enterprise Admin Dashboard Overview

A professional SaaS Admin Dashboard built with Java Swing, demonstrating enterprise-grade MVC architecture, clean separation of concerns, and production-ready coding practices.

Project Structure src/ ├── model/ # Domain entities (POJOs) │ ├── Plan.java │ ├── User.java │ ├── Subscription.java │ ├── Invoice.java │ └── Payment.java │ ├── dao/ # Data Access Layer │ ├── DatabaseConnection.java │ ├── PlanDAO.java │ ├── UserDAO.java │ ├── SubscriptionDAO.java │ ├── InvoiceDAO.java │ └── PaymentDAO.java │ ├── service/ # Business Logic Layer │ ├── PlanService.java │ ├── UserService.java │ ├── SubscriptionService.java │ ├── InvoiceService.java │ ├── PaymentService.java │ └── BillingService.java │ └── ui/ # Presentation Layer (UI) ├── AdminDashboard.java ├── HeaderPanel.java ├── SidebarPanel.java ├── StatusBarPanel.java ├── DashboardPanel.java ├── CreatePlanForm.java ├── RegisterUserForm.java ├── AssignSubscriptionForm.java ├── InvoiceViewForm.java ├── PaymentHistoryForm.java │ ├── utils/ │ ├── DialogUtils.java │ └── ValidationUtils.java │ └── tablemodels/ ├── InvoiceTableModel.java ├── PaymentTableModel.java └── SubscriptionTableModel.java Architecture Decisions Strict Layer Separation

UI Layer → Presentation only (No SQL, No business logic)

Service Layer → Business rules and orchestration

DAO Layer → Database operations

Model Layer → Plain Java Objects (POJOs)

Professional Swing Practices

Proper Layout Managers (GridBagLayout, BorderLayout, BoxLayout)

CardLayout for dynamic view switching

Custom TableModels instead of DefaultTableModel

SwingWorker for async operations

invokeLater for EDT safety

Centralized validation before service calls

Enterprise Design Patterns

Singleton → DatabaseConnection

DAO Pattern → Data abstraction

Service Layer Pattern → Business logic isolation

MVC → Clear separation of concerns

UI Features Dashboard Metrics

Active Subscriptions Count

Unpaid Invoices Count

Total Revenue Display

Color-coded metric cards

Sidebar Navigation

Dashboard

Plans (Create)

Users (Register)

Subscriptions (Assign)

Invoices (Manage)

Payments (History)

Run Billing (Automated)

Status Colors

ACTIVE / PAID → Green

UNPAID → Orange

EXPIRED / OVERDUE → Red

Setup Instructions

Database Setup mysql -u root -p < database_setup.sql
This creates:

subscription_db database

Required tables

Sample test data

Configure Database
Edit DatabaseConnection.java:

private static final String URL = "jdbc:mysql://localhost:3306/subscription_db"; private static final String USER = "root"; private static final String PASSWORD = "root"; 3. Compile cd src javac -cp .;../mysql-connector-j-9.6.0.jar ui/AdminDashboard.java 4. Run java -cp .;../mysql-connector-j-9.6.0.jar ui.AdminDashboard Testing Workflow

Create Plans

Register Users

Assign Subscriptions

View Invoices

Mark Invoice Paid

Run Billing

Interview-Ready Highlights

Clean MVC Architecture

SOLID Principles

No Magic Numbers

Proper Exception Handling

Layered Design

Scalable Structure

Maintainable Codebase

Technology Stack

Java 8+

Java Swing

MySQL 8+

JDBC (MySQL Connector/J)

MVC + DAO + Service Layer Patterns

Enterprise Features

Real-time dashboard metrics

Color-coded table statuses

Confirmation dialogs

Auto-refresh after actions

Async billing process

Professional layout management

Type-safe custom TableModels

Business Logic Examples Auto End Date Calculation LocalDate endDate = startDate.plusDays(plan.getDuration()); Billing Automation

BillingService handles:

Expired subscription detection

Status update to EXPIRED

Invoice generation

Returns billing summary

Security Considerations (For Production)

Password hashing (BCrypt)

Role-based access control

Connection pooling (HikariCP)

SSL database connection

Input sanitization

Code Quality Highlights

No null layouts

No absolute positioning

Proper resource management

try-with-resources usage

Clean naming conventions

Reusable components

Dependencies

MySQL Connector/J 9.6.0

Java 8+

Author

Silarapu Ramya Hasini B.Tech IT - Enterprise Java Developer

Built for enterprise-grade SaaS systems. This project demonstrates production-ready Java Swing architecture with clean design and scalable structure.
