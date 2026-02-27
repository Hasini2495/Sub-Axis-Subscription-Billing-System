package service;

import dao.SubscriptionDAO;
import dao.InvoiceDAO;
import dao.PlanDAO;
import model.Subscription;
import model.Plan;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for automated billing operations.
 * Handles recurring billing processes.
 */
public class BillingService {
    private final SubscriptionDAO subscriptionDAO;
    private final InvoiceDAO invoiceDAO;
    private final PlanDAO planDAO;
    
    public BillingService() {
        this.subscriptionDAO = new SubscriptionDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.planDAO = new PlanDAO();
    }
    
    /**
     * Runs the billing process for all active subscriptions.
     * Generates invoices and updates expired subscriptions.
     * @return Number of invoices generated
     */
    public int runBillingProcess() {
        int invoicesGenerated = 0;
        List<Subscription> activeSubscriptions = subscriptionDAO.getActiveSubscriptions();
        LocalDate today = LocalDate.now();
        
        for (Subscription subscription : activeSubscriptions) {
            // Check if subscription has expired
            if (subscription.getEndDate().isBefore(today)) {
                subscriptionDAO.updateSubscriptionStatus(
                    subscription.getSubscriptionId(), 
                    "EXPIRED"
                );
                continue;
            }
            
            // Generate invoice for active subscription
            Plan plan = planDAO.getPlanById(subscription.getPlanId());
            if (plan != null) {
                model.Invoice invoice = new model.Invoice();
                invoice.setSubscriptionId(subscription.getSubscriptionId());
                invoice.setAmount(plan.getPrice());
                invoice.setInvoiceDate(today);
                invoice.setStatus("UNPAID");
                
                if (invoiceDAO.createInvoice(invoice)) {
                    invoicesGenerated++;
                }
            }
        }
        
        return invoicesGenerated;
    }
}
