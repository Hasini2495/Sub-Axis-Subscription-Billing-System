package service;

import dao.PlanDAO;
import model.Plan;
import java.util.List;

/**
 * Service layer for Plan operations.
 * UI should only call this service, not DAO directly.
 */
public class PlanService {
    private final PlanDAO planDAO;
    
    public PlanService() {
        this.planDAO = new PlanDAO();
    }
    
    /**
     * Creates a new plan.
     * @param plan Plan to create
     * @return true if successful
     * @throws IllegalArgumentException if validation fails
     */
    public boolean createPlan(Plan plan) {
        // Business validation
        if (plan.getPlanName() == null || plan.getPlanName().trim().isEmpty()) {
            throw new IllegalArgumentException("Plan name cannot be empty");
        }
        if (plan.getPrice() == null || plan.getPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (plan.getDuration() == null || plan.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        
        return planDAO.createPlan(plan);
    }
    
    /**
     * Retrieves all plans.
     * @return List of all plans
     */
    public List<Plan> getAllPlans() {
        return planDAO.getAllPlans();
    }
    
    /**
     * Retrieves a plan by ID.
     * @param planId Plan ID
     * @return Plan object
     */
    public Plan getPlanById(int planId) {
        return planDAO.getPlanById(planId);
    }
}
