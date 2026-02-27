package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Sidebar navigation panel for the Admin Dashboard.
 * Contains buttons for navigating to different views.
 */
public class SidebarPanel extends JPanel {
    
    public SidebarPanel(ActionListener navigationListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(44, 62, 80)); // Dark blue-gray
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Navigation buttons
        addNavigationButton("Dashboard", "DASHBOARD", navigationListener);
        addSeparator();
        addNavigationButton("Plans", "PLANS", navigationListener);
        addNavigationButton("Users", "USERS", navigationListener);
        addNavigationButton("Assign Subscription", "SUBSCRIPTIONS", navigationListener);
        addNavigationButton("View Subscriptions", "SUBSCRIPTIONS_VIEW", navigationListener);
        addSeparator();
        addNavigationButton("Invoices", "INVOICES", navigationListener);
        addNavigationButton("Payments", "PAYMENTS", navigationListener);
        addSeparator();
        addNavigationButton("Run Billing", "BILLING", navigationListener);
        
        // Push buttons to top
        add(Box.createVerticalGlue());
    }
    
    /**
     * Creates and adds a navigation button.
     * @param text Button text
     * @param actionCommand Action command for identification
     * @param listener Action listener
     */
    private void addNavigationButton(String text, String actionCommand, ActionListener listener) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(true);
                button.setBackground(new Color(52, 73, 94));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(false);
            }
        });
        
        button.addActionListener(listener);
        add(button);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    /**
     * Adds a visual separator.
     */
    private void addSeparator() {
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(180, 1));
        separator.setForeground(new Color(127, 140, 141));
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(separator);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }
}
