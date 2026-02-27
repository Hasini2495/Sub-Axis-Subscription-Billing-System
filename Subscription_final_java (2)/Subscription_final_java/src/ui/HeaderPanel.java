package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel for the Admin Dashboard.
 * Displays application branding and user info.
 */
public class HeaderPanel extends JPanel {
    
    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(41, 128, 185)); // Professional blue
        setPreferredSize(new Dimension(0, 60));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Application title
        JLabel titleLabel = new JLabel("Subscription Billing System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        
        JLabel userLabel = new JLabel("Admin User");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        userPanel.add(userIcon);
        userPanel.add(userLabel);
        
        add(titleLabel, BorderLayout.WEST);
        add(userPanel, BorderLayout.EAST);
    }
}
