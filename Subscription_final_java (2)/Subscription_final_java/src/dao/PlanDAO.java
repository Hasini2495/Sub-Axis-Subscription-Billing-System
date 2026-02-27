package dao;

import model.Plan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Plan entity.
 * Handles all database operations for plans.
 */
public class PlanDAO {
    
    /**
     * Creates a new plan in the database.
     * @param plan Plan object to create
     * @return true if successful
     */
    public boolean createPlan(Plan plan) {
        String sql = "INSERT INTO plans (plan_name, price, billing_cycle, duration) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plan.getPlanName());
            stmt.setBigDecimal(2, plan.getPrice());
            stmt.setString(3, plan.getBillingCycle());
            stmt.setInt(4, plan.getDuration());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all plans from the database.
     * @return List of all plans
     */
    public List<Plan> getAllPlans() {
        List<Plan> plans = new ArrayList<>();
        String sql = "SELECT * FROM plans";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setPlanName(rs.getString("plan_name"));
                plan.setPrice(rs.getBigDecimal("price"));
                plan.setBillingCycle(rs.getString("billing_cycle"));
                plan.setDuration(rs.getInt("duration"));
                plans.add(plan);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return plans;
    }
    
    /**
     * Retrieves a plan by ID.
     * @param planId Plan ID
     * @return Plan object or null if not found
     */
    public Plan getPlanById(int planId) {
        String sql = "SELECT * FROM plans WHERE plan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, planId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Plan plan = new Plan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setPlanName(rs.getString("plan_name"));
                plan.setPrice(rs.getBigDecimal("price"));
                plan.setBillingCycle(rs.getString("billing_cycle"));
                plan.setDuration(rs.getInt("duration"));
                return plan;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
