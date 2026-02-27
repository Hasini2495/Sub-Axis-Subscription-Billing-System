package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * GlassPlanCard - Dark glassmorphism subscription plan card.
 * Glass effect ALWAYS visible. Hover: subtle lift + outer glow + colored border.
 * All painting via RoundRectangle2D with clip to guarantee rounded corners.
 */
public class GlassPlanCard extends JPanel {

    private GlassButton subscribeBtn;
    private Color planGlowColor;
    private boolean isPopular;

    private float hoverProgress = 0f;
    private Timer animationTimer;
    private boolean isHovered = false;

    private static final int CORNER_RADIUS = 28;
    private static final int LIFT_PX = 8; // max lift on hover

    public GlassPlanCard(String planName, double price, String[] features,
                         Color planGlowColor, boolean isPopular) {
        this.planGlowColor = planGlowColor;
        this.isPopular = isPopular;

        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        // Slightly smaller cards (~13% width reduction, slight height reduction)
        setPreferredSize(new Dimension(245, 390));
        setMaximumSize(new Dimension(245, 390));

        initComponents(planName, price, features);
        setupHoverAnimation();
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupHoverAnimation() {
        animationTimer = new Timer(15, e -> {
            float step = 0.07f;
            if (isHovered && hoverProgress < 1f) {
                hoverProgress = Math.min(1f, hoverProgress + step);
                repaint();
                repaintAboveRegion();
            } else if (!isHovered && hoverProgress > 0f) {
                hoverProgress = Math.max(0f, hoverProgress - step);
                repaint();
                repaintAboveRegion();
            } else {
                animationTimer.stop();
                repaintAboveRegion(); // final cleanup — erase any residual ghost pixels
            }
        });

        MouseAdapter mouse = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                isHovered = true;
                if (!animationTimer.isRunning()) animationTimer.start();
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!contains(e.getPoint())) {
                    isHovered = false;
                    if (!animationTimer.isRunning()) animationTimer.start();
                }
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    /**
     * Asks the parent to repaint the strip directly above this component.
     * This clears any ghost pixels written there by the lift-translate in paint().
     */
    private void repaintAboveRegion() {
        Container parent = getParent();
        if (parent != null) {
            parent.repaint(getX(), getY() - LIFT_PX, getWidth(), LIFT_PX + 1);
        }
    }

    /**
     * Override paint() so the lift translates BOTH background AND child components.
     */
    @Override
    public void paint(Graphics g) {
        int lift = Math.round(LIFT_PX * hoverProgress);
        if (lift > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(0, -lift);
            super.paint(g2d);
            g2d.dispose();
        } else {
            super.paint(g);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Reusable RoundRectangle2D shapes — no rectangle created anywhere
        RoundRectangle2D cardShape   = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, CORNER_RADIUS, CORNER_RADIUS);
        RoundRectangle2D innerBorder = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, CORNER_RADIUS, CORNER_RADIUS);
        RoundRectangle2D outerBorder = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, CORNER_RADIUS, CORNER_RADIUS);

        // ── 1. DROP SHADOW — rounded shape only ──
        int shadowAlpha = (int)(45 - 20 * hoverProgress); // shadow fades as card lifts
        for (int i = 4; i >= 1; i--) {
            int a = shadowAlpha - i * 3;
            if (a <= 0) continue;
            g2d.setColor(new Color(0, 0, 0, a));
            g2d.fill(new RoundRectangle2D.Float(i, i * 2, w - i * 2, h - i, CORNER_RADIUS, CORNER_RADIUS));
        }

        // ── 2. SET CLIP — all painting from here stays inside the card shape ──
        g2d.setClip(cardShape);

        // ── 3. GLOW — RadialGradientPaint, strictly pill-clipped, never rectangular ──
        if (hoverProgress > 0) {
            float radius = Math.max(w, h) * 0.75f;
            int glowAlpha = (int)(80 * hoverProgress);
            RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(w * 0.5f, h * 0.5f),
                radius,
                new float[]{ 0f, 1f },
                new Color[]{
                    new Color(planGlowColor.getRed(), planGlowColor.getGreen(), planGlowColor.getBlue(), glowAlpha),
                    new Color(planGlowColor.getRed(), planGlowColor.getGreen(), planGlowColor.getBlue(), 0)
                }
            );
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setPaint(glow);
            g2d.fill(cardShape);
        }

        // ── 4. GLASS BACKGROUND — ALWAYS present, slightly brighter on hover ──
        float glassAlpha = 0.08f + 0.05f * hoverProgress;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glassAlpha));
        g2d.setColor(Color.WHITE);
        g2d.fill(cardShape);

        // Inner highlight at top — frosted glass shimmer
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            0.06f + 0.02f * hoverProgress));
        g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.45f, new Color(255, 255, 255, 0)));
        g2d.fill(cardShape);

        // ── 5. COLORED ACCENT at top on hover — fill(cardShape) inside clip, no fillRect ──
        if (hoverProgress > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.18f * hoverProgress));
            g2d.setPaint(new GradientPaint(
                0, 0, planGlowColor,
                0, 70, new Color(planGlowColor.getRed(), planGlowColor.getGreen(),
                    planGlowColor.getBlue(), 0)));
            g2d.fill(cardShape);
        }

        // Reset composite, release clip
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setClip(null);

        // ── 6. BORDER — RoundRectangle2D shapes only, zero drawRoundRect integer calls ──
        if (hoverProgress > 0) {
            g2d.setColor(new Color(planGlowColor.getRed(), planGlowColor.getGreen(),
                planGlowColor.getBlue(), (int)(140 * hoverProgress)));
            g2d.setStroke(new BasicStroke(1.8f));
            g2d.draw(innerBorder);
        }
        g2d.setColor(new Color(255, 255, 255, (int)(45 + 35 * hoverProgress)));
        g2d.setStroke(new BasicStroke(1f));
        g2d.draw(outerBorder);

        g2d.dispose();
    }

    private void initComponents(String planName, double price, String[] features) {
        // Content container
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 25, 25, 25));
        
        // Top section (title and price)
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        
        if (isPopular) {
            JLabel popularLabel = new JLabel("POPULAR");
            popularLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            popularLabel.setForeground(planGlowColor);
            popularLabel.setAlignmentX(CENTER_ALIGNMENT);
            topSection.add(popularLabel);
            topSection.add(Box.createRigidArea(new Dimension(0, 12)));
        }
        
        JLabel nameLabel = new JLabel(planName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        topSection.add(nameLabel);
        topSection.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Price with glow effect
        JLabel priceLabel = new JLabel(String.format("$%.2f", price)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw subtle glow
                g2d.setColor(new Color(planGlowColor.getRed(), planGlowColor.getGreen(), planGlowColor.getBlue(), 40));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = fm.getAscent();
                
                // Outer glow
                for (int i = 0; i < 3; i++) {
                    g2d.drawString(text, x - i, y);
                    g2d.drawString(text, x + i, y);
                    g2d.drawString(text, x, y - i);
                    g2d.drawString(text, x, y + i);
                }
                
                // Main text
                g2d.setColor(getForeground());
                g2d.drawString(text, x, y);
                
                g2d.dispose();
            }
        };
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(CENTER_ALIGNMENT);
        topSection.add(priceLabel);
        
        JLabel periodLabel = new JLabel("/month");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        periodLabel.setForeground(new Color(207, 207, 207));
        periodLabel.setAlignmentX(CENTER_ALIGNMENT);
        topSection.add(periodLabel);
        
        contentPanel.add(topSection, BorderLayout.NORTH);
        
        // Features
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(new EmptyBorder(25, 0, 15, 0));
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel("• " + feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            featureLabel.setForeground(new Color(207, 207, 207));
            featureLabel.setAlignmentX(LEFT_ALIGNMENT);
            featuresPanel.add(featureLabel);
            featuresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        contentPanel.add(featuresPanel, BorderLayout.CENTER);
        
        // Subscribe button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        subscribeBtn = new GlassButton("Subscribe Now");
        subscribeBtn.setGlowColor(planGlowColor);
        
        buttonPanel.add(subscribeBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    public GlassButton getSubscribeButton() {
        return subscribeBtn;
    }
}

