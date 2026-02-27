import dao.*;
import model.*;
import service.*;
import java.util.List;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");
        
        // Test Plans
        System.out.println("\n1. Testing PlanDAO...");
        PlanDAO planDAO = new PlanDAO();
        List<Plan> plans = planDAO.getAllPlans();
        System.out.println("Plans found: " + plans.size());
        for (Plan plan : plans) {
            System.out.println("  - " + plan.getPlanName() + " ($" + plan.getPrice() + ")");
        }
        
        // Test Users
        System.out.println("\n2. Testing UserService...");
        UserService userService = new UserService();
        User user = userService.getUserByEmail("john.doe@example.com");
        if (user != null) {
            System.out.println("User found: " + user.getName() + " (ID: " + user.getUserId() + ")");
        } else {
            System.out.println("User NOT found!");
        }
        
        // Test Subscriptions
        System.out.println("\n3. Testing SubscriptionDAO...");
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
        List<Subscription> allSubs = subscriptionDAO.getAllSubscriptions();
        System.out.println("Total subscriptions: " + allSubs.size());
        for (Subscription sub : allSubs) {
            System.out.println("  - Sub ID: " + sub.getSubscriptionId() + 
                             ", User ID: " + sub.getUserId() + 
                             ", Plan ID: " + sub.getPlanId() + 
                             ", Status: " + sub.getStatus());
        }
        
        // Test Service Layer
        System.out.println("\n4. Testing SubscriptionService...");
        SubscriptionService subscriptionService = new SubscriptionService();
        if (user != null) {
            List<Subscription> userSubs = subscriptionService.getSubscriptionsByUserId(user.getUserId());
            System.out.println("Subscriptions for user " + user.getUserId() + ": " + userSubs.size());
            for (Subscription sub : userSubs) {
                System.out.println("  - Sub ID: " + sub.getSubscriptionId() + 
                                 ", Status: " + sub.getStatus());
            }
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
