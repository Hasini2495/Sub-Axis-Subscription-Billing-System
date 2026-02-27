package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Panel with smooth gradient background.
 * Used for sidebar and decorative backgrounds.
 */
public class GradientPanel extends JPanel {
    
    private Color startColor;
    private Color endColor;
    private boolean verticalGradient;
    
    public GradientPanel(Color startColor, Color endColor) {
        this(startColor, endColor, true);
    }
    
    public GradientPanel(Color startColor, Color endColor, boolean verticalGradient) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.verticalGradient = verticalGradient;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Create gradient
        GradientPaint gradient;
        if (verticalGradient) {
            gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
        } else {
            gradient = new GradientPaint(0, 0, startColor, getWidth(), 0, endColor);
        }
        
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    public void setGradientColors(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        repaint();
    }
}
