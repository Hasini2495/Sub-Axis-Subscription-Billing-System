package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Professional card panel with shadow, rounded corners, and padding.
 * Used for dashboard metrics, plan cards, etc.
 */
public class StyledCardPanel extends JPanel {
    
    private static final int BORDER_RADIUS = 12;
    private static final int SHADOW_SIZE = 4;
    private Color backgroundColor = Color.WHITE;
    private Color shadowColor = new Color(0, 0, 0, 15);
    
    public StyledCardPanel() {
        this(new Color(255, 255, 255));
    }
    
    public StyledCardPanel(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw shadow
        g2d.setColor(shadowColor);
        g2d.fillRoundRect(
            SHADOW_SIZE, 
            SHADOW_SIZE, 
            getWidth() - SHADOW_SIZE, 
            getHeight() - SHADOW_SIZE, 
            BORDER_RADIUS, 
            BORDER_RADIUS
        );
        
        // Draw card background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(
            0, 
            0, 
            getWidth() - SHADOW_SIZE, 
            getHeight() - SHADOW_SIZE, 
            BORDER_RADIUS, 
            BORDER_RADIUS
        );
        
        super.paintComponent(g);
    }
    
    public void setCardBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
}
