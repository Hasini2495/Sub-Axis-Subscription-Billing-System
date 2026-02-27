package dao;

import model.Subscription;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subscription entity.
 * Handles all database operations for subscriptions.
 */
public class SubscriptionDAO {
    
    /**
     * Creates a new subscription in the database.
     * @param subscription Subscription object to create
     * @return true if successful
     */
    public boolean createSubscription(Subscription subscription) {
        String sql = "INSERT INTO subscriptions (user_id, plan_id, start_date, end_date, next_billing_date, status, auto_renew) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, subscription.getUserId());
            stmt.setInt(2, subscription.getPlanId());
            stmt.setDate(3, Date.valueOf(subscription.getStartDate()));
            stmt.setDate(4, Date.valueOf(subscription.getEndDate()));
            
            if (subscription.getNextBillingDate() != null) {
                stmt.setDate(5, Date.valueOf(subscription.getNextBillingDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, subscription.getStatus());
            stmt.setBoolean(7, subscription.getAutoRenew() != null ? subscription.getAutoRenew() : true);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        subscription.setSubscriptionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all subscriptions from the database.
     * @return List of all subscriptions
     */
    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Subscription sub = new Subscription();
                sub.setSubscriptionId(rs.getInt("subscription_id"));
                sub.setUserId(rs.getInt("user_id"));
                sub.setPlanId(rs.getInt("plan_id"));
                sub.setStartDate(rs.getDate("start_date").toLocalDate());
                sub.setEndDate(rs.getDate("end_date").toLocalDate());
                
                Date nextBilling = rs.getDate("next_billing_date");
                if (nextBilling != null) {
                    sub.setNextBillingDate(nextBilling.toLocalDate());
                }
                
                sub.setStatus(rs.getString("status"));
                sub.setAutoRenew(rs.getBoolean("auto_renew"));
                subscriptions.add(sub);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return subscriptions;
    }
    
    /**
     * Retrieves active subscriptions.
     * @return List of active subscriptions
     */
    public List<Subscription> getActiveSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions WHERE status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Subscription sub = new Subscription();
                sub.setSubscriptionId(rs.getInt("subscription_id"));
                sub.setUserId(rs.getInt("user_id"));
                sub.setPlanId(rs.getInt("plan_id"));
                sub.setStartDate(rs.getDate("start_date").toLocalDate());
                sub.setEndDate(rs.getDate("end_date").toLocalDate());
                
                Date nextBilling = rs.getDate("next_billing_date");
                if (nextBilling != null) {
                    sub.setNextBillingDate(nextBilling.toLocalDate());
                }
                
                sub.setStatus(rs.getString("status"));
                sub.setAutoRenew(rs.getBoolean("auto_renew"));
                subscriptions.add(sub);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return subscriptions;
    }
    
    /**
     * Updates subscription status.
     * @param subscriptionId Subscription ID
     * @param status New status
     * @return true if successful
     */
    public boolean updateSubscriptionStatus(int subscriptionId, String status) {
        String sql = "UPDATE subscriptions SET status = ? WHERE subscription_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, subscriptionId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates an existing subscription.
     * @param subscription Subscription object with updated data
     * @return true if successful
     */
    public boolean updateSubscription(Subscription subscription) {
        String sql = "UPDATE subscriptions SET user_id = ?, plan_id = ?, start_date = ?, end_date = ?, next_billing_date = ?, status = ?, auto_renew = ? WHERE subscription_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, subscription.getUserId());
            stmt.setInt(2, subscription.getPlanId());
            stmt.setDate(3, Date.valueOf(subscription.getStartDate()));
            stmt.setDate(4, Date.valueOf(subscription.getEndDate()));
            
            if (subscription.getNextBillingDate() != null) {
                stmt.setDate(5, Date.valueOf(subscription.getNextBillingDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, subscription.getStatus());
            stmt.setBoolean(7, subscription.getAutoRenew() != null ? subscription.getAutoRenew() : true);
            stmt.setInt(8, subscription.getSubscriptionId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
