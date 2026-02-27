package dao;

import model.Activity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Activity entity.
 * Handles all database operations for activity logs.
 */
public class ActivityDAO {
    
    /**
     * Creates a new activity log entry in the database.
     * @param activity Activity object to create
     * @return true if successful
     */
    public boolean createActivity(Activity activity) {
        String sql = "INSERT INTO activity_log (user_id, event_type, description, related_entity_id, timestamp) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activity.getUserId());
            stmt.setString(2, activity.getEventType());
            stmt.setString(3, activity.getDescription());
            if (activity.getRelatedEntityId() != null) {
                stmt.setInt(4, activity.getRelatedEntityId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setTimestamp(5, Timestamp.valueOf(activity.getTimestamp()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all activities for a specific user.
     * @param userId User ID
     * @return List of activities for the user
     */
    public List<Activity> getActivitiesByUserId(int userId) {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activity_log WHERE user_id = ? ORDER BY timestamp DESC LIMIT 20";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Activity activity = new Activity();
                activity.setActivityId(rs.getInt("activity_id"));
                activity.setUserId(rs.getInt("user_id"));
                activity.setEventType(rs.getString("event_type"));
                activity.setDescription(rs.getString("description"));
                activity.setRelatedEntityId(rs.getInt("related_entity_id"));
                activity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                activities.add(activity);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return activities;
    }
    
    /**
     * Retrieves recent activities for a user (limited to 5).
     * @param userId User ID
     * @param limit Maximum number of activities to retrieve
     * @return List of recent activities
     */
    public List<Activity> getRecentActivities(int userId, int limit) {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activity_log WHERE user_id = ? ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Activity activity = new Activity();
                activity.setActivityId(rs.getInt("activity_id"));
                activity.setUserId(rs.getInt("user_id"));
                activity.setEventType(rs.getString("event_type"));
                activity.setDescription(rs.getString("description"));
                Integer relatedId = rs.getInt("related_entity_id");
                if (!rs.wasNull()) {
                    activity.setRelatedEntityId(relatedId);
                }
                activity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                activities.add(activity);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return activities;
    }
    
    /**
     * Retrieves all activities (for admin).
     * @return List of all activities
     */
    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activity_log ORDER BY timestamp DESC LIMIT 100";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Activity activity = new Activity();
                activity.setActivityId(rs.getInt("activity_id"));
                activity.setUserId(rs.getInt("user_id"));
                activity.setEventType(rs.getString("event_type"));
                activity.setDescription(rs.getString("description"));
                Integer relatedId = rs.getInt("related_entity_id");
                if (!rs.wasNull()) {
                    activity.setRelatedEntityId(relatedId);
                }
                activity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                activities.add(activity);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return activities;
    }
}
