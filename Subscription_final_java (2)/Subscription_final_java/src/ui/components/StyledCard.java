package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * StyledCard - Professional card component with shadow and proper spacing
 */
public class StyledCard extends JPanel {
    
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = new Color(226, 232, 240);
    private boolean hasShadow = true;
    private int cornerRadius = 12;
    
    public StyledCard() {
        this(20);
    }
    
    public StyledCard(int padding) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(padding, padding, padding, padding));
    }
    
    public StyledCard(LayoutManager layout) {
        setLayout(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Shadow
        if (hasShadow) {
            g2d.setColor(new Color(0, 0, 0, 8));
            for (int i = 0; i < 4; i++) {
                g2d.fillRoundRect(i, i + 2, getWidth() - i * 2, getHeight() - i * 2, cornerRadius + i, cornerRadius + i);
            }
        }
        
        // Background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        // Border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        g2d.dispose();
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    public void setShadow(boolean shadow) {
        this.hasShadow = shadow;
        repaint();
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
