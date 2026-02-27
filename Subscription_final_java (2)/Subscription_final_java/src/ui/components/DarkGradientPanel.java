package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * DarkGradientPanel - A panel with a dark gradient background
 * for the main content area (dark theme).
 * Top: #0F2027, Middle: #203A43, Bottom: #2C5364
 */
public class DarkGradientPanel extends JPanel {
    
    private Color topColor    = new Color(26,  27,  58);  // #1A1B3A
    private Color middleColor = new Color(43,  31,  94);  // #2B1F5E
    private Color bottomColor = new Color(58,  44, 109);  // #3A2C6D
    
    public DarkGradientPanel() {
        this(null);
    }
    
    public DarkGradientPanel(LayoutManager layout) {
        super(layout);
        setOpaque(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        
        // Create multi-stop gradient
        float[] fractions = {0f, 0.5f, 1f};
        Color[] colors = {topColor, middleColor, bottomColor};
        
        LinearGradientPaint gradient = new LinearGradientPaint(
            0, 0,
            0, height,
            fractions,
            colors
        );
        
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Optional: Add subtle vignette effect
        RadialGradientPaint vignette = new RadialGradientPaint(
            width / 2f, height / 2f,
            Math.max(width, height) * 0.7f,
            new float[]{0f, 0.7f, 1f},
            new Color[]{
                new Color(0, 0, 0, 0),
                new Color(0, 0, 0, 0),
                new Color(0, 0, 0, 60)
            }
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, width, height);
        
        g2d.dispose();
    }
}
