package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Status indicator light - yellow for active, gray for inactive.
 */
public class ActiveStatusIndicator extends JPanel {
    
    private boolean isActive;
    private static final Color ACTIVE_COLOR = new Color(234, 179, 8);  // Yellow
    private static final Color INACTIVE_COLOR = new Color(156, 163, 175);  // Gray
    
    public ActiveStatusIndicator(boolean isActive) {
        this.isActive = isActive;
        setOpaque(false);
        setPreferredSize(new Dimension(14, 14));
        setMaximumSize(new Dimension(14, 14));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circle
        g2d.setColor(isActive ? ACTIVE_COLOR : INACTIVE_COLOR);
        g2d.fillOval(0, 0, 12, 12);
        
        // Glow effect for active
        if (isActive) {
            g2d.setColor(new Color(234, 179, 8, 50));
            g2d.fillOval(-2, -2, 16, 16);
        }
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        repaint();
    }
    
    public boolean isActive() {
        return isActive;
    }
}
