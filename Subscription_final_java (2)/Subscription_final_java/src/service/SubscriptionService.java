package service;

import dao.SubscriptionDAO;
import dao.PlanDAO;
import model.Subscription;
import model.Plan;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for Subscription operations.
 * Handles business logic for subscription management.
 */
public class SubscriptionService {
    private final SubscriptionDAO subscriptionDAO;
    private final PlanDAO planDAO;
    private final InvoiceService invoiceService;
    private final ActivityService activityService;
    
    public SubscriptionService() {
        this.subscriptionDAO = new SubscriptionDAO();
        this.planDAO = new PlanDAO();
        this.invoiceService = new InvoiceService();
        this.activityService = new ActivityService();
    }
    
    /**
     * Creates a new subscription with automatic end date calculation.
     * Also generates initial invoice and logs activity.
     * @param userId User ID
     * @param planId Plan ID
     * @param startDate Start date
     * @return true if successful
     */
    public boolean createSubscription(int userId, int planId, LocalDate startDate) {
        // Get plan to calculate end date
        Plan plan = planDAO.getPlanById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Plan not found");
        }
        
        // Calculate end date and next billing date based on plan duration
        LocalDate endDate = startDate.plusDays(plan.getDuration());
        LocalDate nextBillingDate = endDate; // Next billing on end date
        
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setPlanId(planId);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setNextBillingDate(nextBillingDate);
        subscription.setStatus("ACTIVE");
        subscription.setAutoRenew(true); // Default to true
        
        boolean created = subscriptionDAO.createSubscription(subscription);
        
        if (created && subscription.getSubscriptionId() != null) {
            // Generate initial invoice
            invoiceService.generateInvoice(subscription.getSubscriptionId(), plan.getPrice(), startDate);
            
            // Log activity
            activityService.logSubscriptionCreated(userId, subscription.getSubscriptionId(), plan.getPlanName());
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Retrieves all subscriptions.
     * @return List of all subscriptions
     */
    public List<Subscription> getAllSubscriptions() {
        return subscriptionDAO.getAllSubscriptions();
    }
    
    /**
     * Retrieves active subscriptions.
     * @return List of active subscriptions
     */
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionDAO.getActiveSubscriptions();
    }
    
    /**
     * Gets count of active subscriptions.
     * @return Count of active subscriptions
     */
    public int getActiveSubscriptionCount() {
        return subscriptionDAO.getActiveSubscriptions().size();
    }
    
    /**
     * Retrieves all subscriptions for a specific user.
     * @param userId User ID
     * @return List of subscriptions for the user
     */
    public List<Subscription> getSubscriptionsByUserId(int userId) {
        List<Subscription> allSubscriptions = subscriptionDAO.getAllSubscriptions();
        return allSubscriptions.stream()
            .filter(sub -> sub.getUserId().equals(userId))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Cancels a subscription and logs the activity.
     * @param subscriptionId Subscription ID
     * @param userId User ID for activity logging
     * @return true if successful
     */
    public boolean cancelSubscription(int subscriptionId, int userId) {
        Subscription subscription = getAllSubscriptions().stream()
            .filter(s -> s.getSubscriptionId().equals(subscriptionId))
            .findFirst()
            .orElse(null);
        
        if (subscription == null) {
            return false;
        }
        
        boolean cancelled = subscriptionDAO.updateSubscriptionStatus(subscriptionId, "CANCELLED");
        
        if (cancelled) {
            // Log activity
            Plan plan = planDAO.getPlanById(subscription.getPlanId());
            String planName = plan != null ? plan.getPlanName() : "Unknown Plan";
            activityService.logSubscriptionCancelled(userId, subscriptionId, planName);
        }
        
        return cancelled;
    }
    
    /**
     * Process renewals for subscriptions that are due.
     * @return Number of subscriptions renewed
     */
    public int processRenewals() {
        int renewed = 0;
        LocalDate today = LocalDate.now();
        List<Subscription> active = getActiveSubscriptions();
        
        for (Subscription sub : active) {
            // Check if renewal is due
            if (sub.getAutoRenew() != null && sub.getAutoRenew() && 
                sub.getNextBillingDate() != null && 
                !sub.getNextBillingDate().isAfter(today)) {
                
                // Get plan for duration calculation
                Plan plan = planDAO.getPlanById(sub.getPlanId());
                if (plan != null) {
                    // Update subscription dates
                    sub.setStartDate(sub.getEndDate().plusDays(1));
                    sub.setEndDate(sub.getStartDate().plusDays(plan.getDuration()));
                    sub.setNextBillingDate(sub.getEndDate());
                    
                    if (subscriptionDAO.updateSubscription(sub)) {
                        // Generate renewal invoice
                        invoiceService.generateInvoice(sub.getSubscriptionId(), plan.getPrice(), today);
                        
                        // Log activity
                        activityService.logSubscriptionRenewed(sub.getUserId(), 
                            sub.getSubscriptionId(), plan.getPlanName());
                        
                        renewed++;
                    }
                }
            }
        }
        
        return renewed;
    }
    
    /**
     * Updates an existing subscription.
     * @param subscription Subscription object with updated data
     * @return true if successful
     */
    public boolean updateSubscription(Subscription subscription) {
        return subscriptionDAO.updateSubscription(subscription);
    }
}
