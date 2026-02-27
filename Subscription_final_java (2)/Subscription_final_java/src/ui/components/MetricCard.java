package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Professional metric card for dashboard.
 * Shows icon, title, value, and optional subtitle.
 */
public class MetricCard extends StyledCardPanel {
    
    private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel subtitleLabel;
    
    public MetricCard(String icon, String title, String value, Color accentColor) {
        this(icon, title, value, null, accentColor);
    }
    
    public MetricCard(String icon, String title, String value, String subtitle, Color accentColor) {
        super();
        setLayout(new BorderLayout(15, 10));
        setPreferredSize(new Dimension(280, 140));
        
        // Left side - Icon with colored background
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Circular background
                g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setLayout(new GridBagLayout());
        
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconPanel.add(iconLabel);
        
        // Right side - Text content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(new Color(17, 24, 39));
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(valueLabel);
        
        if (subtitle != null && !subtitle.isEmpty()) {
            subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitleLabel.setForeground(new Color(156, 163, 175));
            subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
            contentPanel.add(subtitleLabel);
        }
        
        add(iconPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    public void setSubtitle(String subtitle) {
        if (subtitleLabel != null) {
            subtitleLabel.setText(subtitle);
        }
    }
}
