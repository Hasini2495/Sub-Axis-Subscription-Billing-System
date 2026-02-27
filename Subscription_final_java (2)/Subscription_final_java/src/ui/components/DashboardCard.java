package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Styled card component for displaying metrics on dashboard.
 * Professional appearance with icon, title, and value.
 */
public class DashboardCard extends JPanel {
    
    private JLabel valueLabel;
    private JLabel titleLabel;
    private Color cardColor;
    
    public DashboardCard(String title, String value, String icon, Color color) {
        this.cardColor = color;
        initializeCard(title, value, icon);
    }
    
    private void initializeCard(String title, String value, String icon) {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Icon panel (left side)
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(cardColor);
        iconPanel.setPreferredSize(new Dimension(70, 70));
        iconPanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(Color.WHITE);
        iconPanel.add(iconLabel);
        
        // Content panel (right side)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.FONT_SMALL);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(valueLabel);
        
        add(iconPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Updates the value displayed on the card.
     */
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    /**
     * Updates the title displayed on the card.
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Add subtle shadow effect
        g2.setColor(new Color(0, 0, 0, 10));
        g2.fillRoundRect(2, 2, getWidth(), getHeight(), 8, 8);
        
        g2.dispose();
    }
}
