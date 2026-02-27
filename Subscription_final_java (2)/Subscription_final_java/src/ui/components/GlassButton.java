package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * GlassButton - A modern pill-shaped button with glassmorphism effect,
 * smooth hover animations, and glow effect.
 */
public class GlassButton extends JButton {
    
    private Color normalBackground = new Color(255, 255, 255, 25);
    private Color hoverBackground = new Color(255, 255, 255, 40);
    private Color glowColor = new Color(180, 150, 255, 180); // Default: soft purple glow for glass mode
    private Color textColor = Color.WHITE;

    // Gradient fill colors (default = orange, can be overridden via setGradientColors)
    private Color gradientLeft  = new Color(0xFF, 0x9A, 0x4D); // #FF9A4D
    private Color gradientRight = new Color(0xFF, 0x6A, 0x2A); // #FF6A2A
    private boolean useCustomGradient = false;

    /** Override the fill gradient (e.g. blue→purple for the login button). */
    public void setGradientColors(Color left, Color right) {
        this.gradientLeft  = left;
        this.gradientRight = right;
        this.useCustomGradient = true;
        this.glowColor = new Color(left.getRed(), left.getGreen(), left.getBlue(), 180);
        repaint();
    }
    
    private float hoverProgress = 0f;
    private Timer animationTimer;
    private boolean isHovered = false;
    
    public GlassButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setForeground(textColor);
        setPreferredSize(new Dimension(220, 48));
        
        initAnimations();
    }
    
    private void initAnimations() {
        animationTimer = new Timer(15, e -> { // Smooth animation - 15ms interval
            if (isHovered && hoverProgress < 1f) {
                hoverProgress = Math.min(1f, hoverProgress + 0.15f);
                repaint();
            } else if (!isHovered && hoverProgress > 0f) {
                hoverProgress = Math.max(0f, hoverProgress - 0.15f);
                repaint();
            }
            
            if ((isHovered && hoverProgress >= 1f) || (!isHovered && hoverProgress <= 0f)) {
                animationTimer.stop();
            }
        });
        
        // Enhanced mouse listeners with motion tracking
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                if (!animationTimer.isRunning()) {
                    animationTimer.start();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                if (!animationTimer.isRunning()) {
                    animationTimer.start();
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                if (contains(e.getPoint())) {
                    if (!isHovered) {
                        isHovered = true;
                        if (!animationTimer.isRunning()) {
                            animationTimer.start();
                        }
                    }
                }
            }
        };
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
    
    public void setGlowColor(Color color) {
        this.glowColor = color;
    }
    
    public void setTextColor(Color color) {
        this.textColor = color;
        setForeground(color);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int w   = getWidth();
        int h   = getHeight();
        int arc = h; // full pill

        // Single pill shape reused for every operation — no rectangle ever created
        RoundRectangle2D pill        = new RoundRectangle2D.Float(0, 0, w,     h,     arc,     arc);
        RoundRectangle2D innerBorder = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, arc - 2, arc - 2);
        RoundRectangle2D outerBorder = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc,     arc);

        // ── 1. GLOW — pill-clipped radial gradient, never bleeds outside the pill ──
        if (hoverProgress > 0) {
            g2d.setClip(pill);
            int   glowAlpha = (int)(90 * hoverProgress);
            float radius    = Math.max(w, h) * 0.65f;
            RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(w * 0.5f, h * 0.5f),
                radius,
                new float[]{ 0f, 1f },
                new Color[]{
                    new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), glowAlpha),
                    new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 0)
                }
            );
            g2d.setPaint(glow);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.fill(pill);
            g2d.setClip(null);
        }

        // ── 2. GRADIENT FILL — clipped to pill ──
        g2d.setClip(pill);
        float brighten = hoverProgress * 15f;
        Color fillLeft, fillRight;
        if (useCustomGradient) {
            // Horizontal gradient (left → right) for blue-purple style
            fillLeft = new Color(
                Math.min(255, gradientLeft.getRed()   + (int)brighten),
                Math.min(255, gradientLeft.getGreen() + (int)(brighten * 0.5f)),
                Math.min(255, gradientLeft.getBlue()  + (int)brighten));
            fillRight = new Color(
                Math.min(255, gradientRight.getRed()   + (int)brighten),
                Math.min(255, gradientRight.getGreen() + (int)(brighten * 0.5f)),
                Math.min(255, gradientRight.getBlue()  + (int)brighten));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setPaint(new GradientPaint(0, 0, fillLeft, w, 0, fillRight));
            g2d.fill(pill);   // ← actually paint the gradient
        } else {
            // ── GLASS mode (default) — semi-transparent white fill, no solid colour ──
            // Base glass layer: rgba(255,255,255, ~0.08) with slight brightening on hover
            int baseAlpha  = 20 + (int)(18 * hoverProgress);   // 20 → 38 alpha
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setColor(new Color(255, 255, 255, baseAlpha));
            g2d.fill(pill);

            // Top-half shimmer — always present, lifts on hover
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.12f + 0.08f * hoverProgress));
            g2d.setPaint(new GradientPaint(0, 0, Color.WHITE,
                0, h * 0.55f, new Color(255, 255, 255, 0)));
            g2d.fill(pill);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setClip(null);
            // (no further fill — text and border drawn below)
        }

        if (useCustomGradient) {
            // Shimmer highlight only for gradient-filled buttons
            g2d.setClip(pill);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.18f + 0.06f * hoverProgress));
            g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.5f, new Color(255, 255, 255, 0)));
            g2d.fill(pill);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setClip(null);
        }

        // ── 3. TEXT — drawn without any clip so characters are never cropped ──
        g2d.setFont(getFont());
        g2d.setColor(getForeground());
        FontMetrics fm  = g2d.getFontMetrics();
        String      txt = getText();
        int         tx  = (w - fm.stringWidth(txt)) / 2;
        int         ty  = (h - fm.getHeight())       / 2 + fm.getAscent();
        g2d.drawString(txt, tx, ty);

        // ── 4. PILL BORDER — RoundRectangle2D only, zero rectangles ──
        if (hoverProgress > 0) {
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(),
                glowColor.getBlue(), (int)(90 * hoverProgress)));
            g2d.setStroke(new BasicStroke(2f));
            g2d.draw(innerBorder);
        }
        g2d.setColor(new Color(255, 255, 255, (int)(60 + 50 * hoverProgress)));
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.draw(outerBorder);

        g2d.dispose();
        // super.paintComponent is intentionally NOT called — it paints a rectangular ButtonUI background
    }
}
