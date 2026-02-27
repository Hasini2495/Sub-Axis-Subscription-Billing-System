import service.*;
import model.*;
import java.util.List;

/**
 * Test to verify that the data is correctly loaded and filtered
 */
public class TestDataDisplay {
    public static void main(String[] args) {
        System.out.println("=== Testing Data Display (Post-Fix) ===\n");
        
        UserService userService = new UserService();
        User user = userService.getUserByEmail("john.doe@example.com");
        
        if (user == null) {
            System.out.println("ERROR: User not found!");
            return;
        }
        
        System.out.println("User: " + user.getName() + " (ID: " + user.getUserId() + ")");
        System.out.println("Status: " + user.getStatus());
        
        // Test Plans
        System.out.println("\n1. Testing Plans Display:");
        PlanService planService = new PlanService();
        List<Plan> plans = planService.getAllPlans();
        System.out.println("   Plans loaded: " + plans.size());
        if (plans.isEmpty()) {
            System.out.println("   ERROR: Plans panel will show EMPTY!");
        } else {
            System.out.println("   SUCCESS: Plans will be displayed");
        }
        
        // Test Subscriptions
        System.out.println("\n2. Testing Subscriptions Display:");
        SubscriptionService subscriptionService = new SubscriptionService();
        List<Subscription> allSubs = subscriptionService.getSubscriptionsByUserId(user.getUserId());
        System.out.println("   Total subscriptions for user: " + allSubs.size());
        
        long activeCount = allSubs.stream()
            .filter(s -> "ACTIVE".equals(s.getStatus()))
            .count();
        System.out.println("   Active subscriptions (ACTIVE): " + activeCount);
        
        long activeCountWrong = allSubs.stream()
            .filter(s -> "Active".equals(s.getStatus()))
            .count();
        System.out.println("   Active subscriptions (Active - WRONG): " + activeCountWrong);
        
        if (activeCount == 0) {
            System.out.println("   ERROR: Subscriptions panel will show EMPTY!");
        } else {
            System.out.println("   SUCCESS: " + activeCount + " subscription(s) will be displayed");
        }
        
        // Test Payments
        System.out.println("\n3. Testing Payments Display:");
        PaymentService paymentService = new PaymentService();
        List<Payment> payments = paymentService.getPaymentsByUserId(user.getUserId());
        System.out.println("   Payments loaded: " + payments.size());
        if (payments.isEmpty()) {
            System.out.println("   INFO: Payments panel will show EMPTY (expected if no payments yet)");
        } else {
            System.out.println("   SUCCESS: Payments will be displayed");
        }
        
        // Test Invoices
        System.out.println("\n4. Testing Invoices Display:");
        InvoiceService invoiceService = new InvoiceService();
        List<Invoice> invoices = invoiceService.getInvoicesByUserId(user.getUserId());
        System.out.println("   Invoices loaded: " + invoices.size());
        if (invoices.isEmpty()) {
            System.out.println("   INFO: Invoices panel will show EMPTY (expected if no invoices yet)");
        } else {
            System.out.println("   SUCCESS: Invoices will be displayed");
        }
        
        System.out.println("\n=== Test Complete ===");
        System.out.println("\nSummary:");
        System.out.println("- Plans: " + (plans.size() > 0 ? "✓ OK" : "✗ EMPTY"));
        System.out.println("- Subscriptions: " + (activeCount > 0 ? "✓ OK" : "✗ EMPTY"));
        System.out.println("- Payments: " + (payments.size() > 0 ? "✓ OK" : "○ Empty (expected)"));
        System.out.println("- Invoices: " + (invoices.size() > 0 ? "✓ OK" : "○ Empty (expected)"));
    }
}
