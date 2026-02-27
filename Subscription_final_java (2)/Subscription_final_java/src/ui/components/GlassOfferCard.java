package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * GlassOfferCard - Same glass + hover mechanics as GlassPlanCard.
 * Drop-in container for the Special Offers panel.
 * Glass effect ALWAYS visible. Hover: lift 8px + outer glow + colored top accent.
 */
public class GlassOfferCard extends JPanel {

    private Color accentColor;
    private float hoverProgress = 0f;
    private Timer animationTimer;
    private boolean isHovered = false;

    private static final int CORNER_RADIUS = 28;
    private static final int LIFT_PX = 8;

    public GlassOfferCard(Color accentColor) {
        this.accentColor = accentColor;
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setupHoverAnimation();
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
     * Override paint() so lift translates both background AND child components.
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
        int shadowAlpha = (int)(45 - 20 * hoverProgress);
        for (int i = 4; i >= 1; i--) {
            int a = shadowAlpha - i * 3;
            if (a <= 0) continue;
            g2d.setColor(new Color(0, 0, 0, a));
            g2d.fill(new RoundRectangle2D.Float(i, i * 2, w - i * 2, h - i, CORNER_RADIUS, CORNER_RADIUS));
        }

        // ── 2. SET CLIP — all painting from here stays inside the card shape ──
        g2d.setClip(cardShape);

        // ── 3. GLOW — RadialGradientPaint, strictly card-clipped, never rectangular ──
        if (hoverProgress > 0) {
            float radius = Math.max(w, h) * 0.75f;
            int glowAlpha = (int)(80 * hoverProgress);
            RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(w * 0.5f, h * 0.5f),
                radius,
                new float[]{ 0f, 1f },
                new Color[]{
                    new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), glowAlpha),
                    new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 0)
                }
            );
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setPaint(glow);
            g2d.fill(cardShape);
        }

        // ── 4. GLASS BACKGROUND — always present ──
        float glassAlpha = 0.08f + 0.05f * hoverProgress;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glassAlpha));
        g2d.setColor(Color.WHITE);
        g2d.fill(cardShape);

        // Top shimmer highlight
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            0.06f + 0.02f * hoverProgress));
        g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.45f, new Color(255, 255, 255, 0)));
        g2d.fill(cardShape);

        // ── 5. COLORED ACCENT at top on hover — fill(cardShape) inside clip, no fillRect ──
        if (hoverProgress > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.18f * hoverProgress));
            g2d.setPaint(new GradientPaint(
                0, 0, accentColor,
                0, 70, new Color(accentColor.getRed(), accentColor.getGreen(),
                    accentColor.getBlue(), 0)));
            g2d.fill(cardShape);
        }

        // Reset composite, release clip
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setClip(null);

        // ── 6. BORDER — RoundRectangle2D shapes only, zero drawRoundRect integer calls ──
        if (hoverProgress > 0) {
            g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(),
                accentColor.getBlue(), (int)(140 * hoverProgress)));
            g2d.setStroke(new BasicStroke(1.8f));
            g2d.draw(innerBorder);
        }
        g2d.setColor(new Color(255, 255, 255, (int)(45 + 35 * hoverProgress)));
        g2d.setStroke(new BasicStroke(1f));
        g2d.draw(outerBorder);

        g2d.dispose();
    }
}
