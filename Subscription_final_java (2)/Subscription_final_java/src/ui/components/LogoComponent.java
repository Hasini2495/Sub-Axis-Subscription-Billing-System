package ui.components;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * SubAxis logo component.
 * Displays logo image if available, falls back to styled text.
 */
public class LogoComponent extends JPanel {
    
    private BufferedImage logoImage;
    private boolean imageLoaded = false;
    @SuppressWarnings("unused")
    private int preferredWidth;
    @SuppressWarnings("unused")
    private int preferredHeight;
    
    public LogoComponent(int width, int height) {
        this.preferredWidth = width;
        this.preferredHeight = height;
        
        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        
        // Try to load logo from multiple locations
        tryLoadLogo();
    }
    
    private void tryLoadLogo() {
        // Try loading from resources folder
        String[] logoPaths = {
            "resources/subaxis-logo.png",
            "src/resources/subaxis-logo.png",
            "subaxis-logo.png",
            "logo.png"
        };
        
        for (String path : logoPaths) {
            try {
                File logoFile = new File(path);
                if (logoFile.exists()) {
                    logoImage = ImageIO.read(logoFile);
                    imageLoaded = true;
                    System.out.println("✓ Logo loaded from: " + path);
                    return;
                }
            } catch (IOException e) {
                // Continue to next path
            }
        }
        
        // Try loading from classpath
        try {
            URL resourceUrl = getClass().getClassLoader().getResource("subaxis-logo.png");
            if (resourceUrl != null) {
                logoImage = ImageIO.read(resourceUrl);
                imageLoaded = true;
                System.out.println("✓ Logo loaded from classpath");
                return;
            }
        } catch (IOException e) {
            // Fallback to text logo
        }
        
        System.out.println("ℹ Logo image not found - using text fallback");
        System.out.println("  To add logo image, place 'subaxis-logo.png' in project root or resources/ folder");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        
        if (imageLoaded && logoImage != null) {
            // Draw logo image with proper scaling
            int imgWidth = logoImage.getWidth();
            int imgHeight = logoImage.getHeight();
            
            // Calculate scaling to fit within bounds while maintaining aspect ratio
            double scale = Math.min(
                (double) width / imgWidth,
                (double) height / imgHeight
            );
            
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            int x = (width - scaledWidth) / 2;
            int y = (height - scaledHeight) / 2;
            
            g2d.drawImage(logoImage, x, y, scaledWidth, scaledHeight, null);
            
        } else {
            // Fallback: Draw styled text logo
            drawTextLogo(g2d, width, height);
        }
    }
    
    private void drawTextLogo(Graphics2D g2d, int width, int height) {
        // Gradient background circle
        int circleSize = Math.min(width, height) - 10;
        int circleX = (width - circleSize) / 2;
        int circleY = (height - circleSize) / 2;
        
        GradientPaint gradient = new GradientPaint(
            circleX, circleY, new Color(79, 70, 229),
            circleX + circleSize, circleY + circleSize, new Color(99, 102, 241)
        );
        g2d.setPaint(gradient);
        g2d.fillOval(circleX, circleY, circleSize, circleSize);
        
        // White "S" letter
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, circleSize / 2));
        
        FontMetrics fm = g2d.getFontMetrics();
        String letter = "S";
        int textWidth = fm.stringWidth(letter);
        int textHeight = fm.getHeight();
        
        int textX = (width - textWidth) / 2;
        int textY = (height - textHeight) / 2 + fm.getAscent();
        
        g2d.drawString(letter, textX, textY);
        
        // Subtle shine effect
        GradientPaint shine = new GradientPaint(
            circleX, circleY,
            new Color(255, 255, 255, 100),
            circleX + circleSize / 2, circleY + circleSize / 2,
            new Color(255, 255, 255, 0)
        );
        g2d.setPaint(shine);
        g2d.fillOval(circleX + 5, circleY + 5, circleSize / 2, circleSize / 2);
    }
    
    /**
     * Gets whether the logo image was successfully loaded.
     */
    public boolean isImageLoaded() {
        return imageLoaded;
    }
    
    /**
     * Attempts to reload the logo image.
     */
    public void reloadLogo() {
        imageLoaded = false;
        logoImage = null;
        tryLoadLogo();
        repaint();
    }
}
