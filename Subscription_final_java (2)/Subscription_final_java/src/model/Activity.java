package model;

import java.time.LocalDateTime;

/**
 * Model class representing an activity log entry.
 * Records all major events in the system.
 */
public class Activity {
    private Integer activityId;
    private Integer userId;
    private String eventType; // SUBSCRIPTION_CREATED, PAYMENT_MADE, INVOICE_GENERATED, PLAN_UPGRADED, SUBSCRIPTION_CANCELLED, RENEWAL_PROCESSED
    private String description;
    private LocalDateTime timestamp;
    private Integer relatedEntityId; // Can be subscription_id, payment_id, invoice_id, etc.

    // Constructors
    public Activity() {
        this.timestamp = LocalDateTime.now();
    }

    public Activity(Integer userId, String eventType, String description, Integer relatedEntityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.description = description;
        this.relatedEntityId = relatedEntityId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Integer relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    @Override
    public String toString() {
        return description + " - " + timestamp.toString();
    }
}
