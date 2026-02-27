package service;

import dao.ActivityDAO;
import model.Activity;
import java.util.List;

/**
 * Service layer for Activity logging.
 * Handles business logic for activity tracking.
 */
public class ActivityService {
    private final ActivityDAO activityDAO;
    
    public ActivityService() {
        this.activityDAO = new ActivityDAO();
    }
    
    /**
     * Logs a subscription creation event.
     */
    public void logSubscriptionCreated(int userId, int subscriptionId, String planName) {
        Activity activity = new Activity(
            userId,
            "SUBSCRIPTION_CREATED",
            "Subscribed to " + planName + " plan",
            subscriptionId
        );
        activityDAO.createActivity(activity);
    }
    
    /**
     * Logs a payment completion event.
     */
    public void logPaymentCompleted(int userId, int paymentId, double amount) {
        Activity activity = new Activity(
            userId,
            "PAYMENT_COMPLETED",
            String.format("Payment of $%.2f processed successfully", amount),
            paymentId
        );
        activityDAO.createActivity(activity);
    }
    
    /**
     * Logs an invoice generation event.
     */
    public void logInvoiceGenerated(int userId, int invoiceId, double amount) {
        Activity activity = new Activity(
            userId,
            "INVOICE_GENERATED",
            String.format("Invoice #%d generated for $%.2f", invoiceId, amount),
            invoiceId
        );
        activityDAO.createActivity(activity);
    }
    
    /**
     * Logs a plan upgrade event.
     */
    public void logPlanUpgraded(int userId, int subscriptionId, String oldPlan, String newPlan) {
        Activity activity = new Activity(
            userId,
            "PLAN_UPGRADED",
            "Upgraded from " + oldPlan + " to " + newPlan,
            subscriptionId
        );
        activityDAO.createActivity(activity);
    }
    
    /**
     * Logs a subscription cancellation event.
     */
    public void logSubscriptionCancelled(int userId, int subscriptionId, String planName) {
        Activity activity = new Activity(
            userId,
            "SUBSCRIPTION_CANCELLED",
            "Cancelled " + planName + " subscription",
            subscriptionId
        );
        activityDAO.createActivity(activity);
    }
    
    /**
     * Logs a subscription renewal event.
     */
    public void logSubscriptionRenewed(int userId, int subscriptionId, String planName) {
        Activity activity = new Activity(
            userId,
            "SUBSCRIPTION_RENEWED",
            "Renewed " + planName + " subscription",
            subscriptionId
        );
        activityDAO.createActivity(activity);
    }
    
    /**
     * Retrieves recent activities for a user.
     * @param userId User ID
     * @param limit Maximum number of activities
     * @return List of recent activities
     */
    public List<Activity> getRecentActivities(int userId, int limit) {
        return activityDAO.getRecentActivities(userId, limit);
    }
    
    /**
     * Retrieves all activities for a user.
     * @param userId User ID
     * @return List of all activities for the user
     */
    public List<Activity> getUserActivities(int userId) {
        return activityDAO.getActivitiesByUserId(userId);
    }
    
    /**
     * Retrieves all activities (for admin).
     * @return List of all activities
     */
    public List<Activity> getAllActivities() {
        return activityDAO.getAllActivities();
    }
}
