package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Status indicator component - Small circular light (Yellow = Active, Gray = Inactive).
 * Professional account status display.
 */
public class StatusIndicator extends JComponent {
    
    private boolean isActive;
    private static final int SIZE = 12;
    private static final Color ACTIVE_COLOR = new Color(251, 191, 36); // Amber/Yellow
    private static final Color INACTIVE_COLOR = new Color(156, 163, 175); // Gray
    
    public StatusIndicator(boolean isActive) {
        this.isActive = isActive;
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setMaximumSize(new Dimension(SIZE, SIZE));
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        repaint();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw glow effect
        Color glowColor = isActive ? ACTIVE_COLOR : INACTIVE_COLOR;
        g2d.setColor(new Color(
            glowColor.getRed(),
            glowColor.getGreen(),
            glowColor.getBlue(),
            50
        ));
        g2d.fillOval(0, 0, SIZE, SIZE);
        
        // Draw main circle
        g2d.setColor(isActive ? ACTIVE_COLOR : INACTIVE_COLOR);
        g2d.fillOval(2, 2, SIZE - 4, SIZE - 4);
        
        // Add shine effect
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval(3, 3, SIZE / 3, SIZE / 3);
    }
}
