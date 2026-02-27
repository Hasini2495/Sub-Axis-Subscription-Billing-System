package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * GlassPanel - A glassmorphism-styled panel with semi-transparent background,
 * rounded corners, and subtle border.
 */
public class GlassPanel extends JPanel {
    
    private int cornerRadius = 25;
    private Color glassBackground = new Color(255, 255, 255, 15); // rgba(255,255,255,0.06)
    private Color borderColor = new Color(255, 255, 255, 38); // rgba(255,255,255,0.15)
    private boolean showShadow = true;
    
    public GlassPanel() {
        this(null);
    }
    
    public GlassPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
    
    public void setGlassBackground(Color color) {
        this.glassBackground = color;
        repaint();
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    public void setShowShadow(boolean show) {
        this.showShadow = show;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        
        // Enable anti-aliasing for smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw subtle shadow
        if (showShadow) {
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillRoundRect(2, 4, width - 4, height - 4, cornerRadius, cornerRadius);
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fillRoundRect(1, 3, width - 2, height - 2, cornerRadius, cornerRadius);
        }
        
        // Draw glass background — rgba(255,255,255,0.06)
        g2d.setColor(glassBackground);
        g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Purple tint overlay — rgba(120,80,255,0.08)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(new Color(120, 80, 255, 20));
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

        // Draw border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        
        g2d.dispose();
    }
}
