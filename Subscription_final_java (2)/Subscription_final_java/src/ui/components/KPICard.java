package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Neon-Glow KPI card with glassy dark background, animated hover effect.
 * Pure Java Swing — no third-party dependencies.
 */
public class KPICard extends JPanel {

    private String iconText;
    private String titleText;
    private String valueText;
    private final Color accentColor;

    // Hover animation state
    private float hoverProgress = 0f;   // 0.0 → 1.0
    private boolean hovered = false;
    private Timer hoverTimer;

    public KPICard(String icon, String title, String value, Color accentColor) {
        this.iconText  = icon;
        this.titleText = title;
        this.valueText = value;
        this.accentColor = accentColor;

        setOpaque(false);
        setPreferredSize(new Dimension(220, 130));
        setMinimumSize(new Dimension(180, 110));

        // Hover timer — 20 ms tick, animate hoverProgress 0↔1
        hoverTimer = new Timer(20, e -> {
            if (hovered) {
                hoverProgress = Math.min(1f, hoverProgress + 0.08f);
            } else {
                hoverProgress = Math.max(0f, hoverProgress - 0.08f);
            }
            repaint();
            if (!hovered && hoverProgress <= 0f) hoverTimer.stop();
            if (hovered  && hoverProgress >= 1f) hoverTimer.stop();
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true;
                hoverTimer.start();
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered = false;
                hoverTimer.start();
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth(), h = getHeight();
        int arc = 22;

        // ── Drop shadow ──────────────────────────────────────────────────────
        for (int i = 6; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 18 * i));
            g2.fillRoundRect(i, i + 3, w - i * 2, h - i * 2, arc + 2, arc + 2);
        }

        // ── Glass card background ─────────────────────────────────────────────
        // Base: very dark navy with slight transparency
        GradientPaint cardGrad = new GradientPaint(
            0, 0,    new Color(18, 18, 55, 230),
            w, h,    new Color(35, 23, 90, 210)
        );
        g2.setPaint(cardGrad);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // Frosted glass sheen on upper half
        GradientPaint sheen = new GradientPaint(
            0, 0,    new Color(255, 255, 255, 18),
            0, h/2,  new Color(255, 255, 255, 4)
        );
        g2.setPaint(sheen);
        g2.fillRoundRect(0, 0, w - 1, h / 2, arc, arc);

        // ── Neon glow border (multi-pass) ─────────────────────────────────────
        float glowAlphaBase = 0.4f + 0.6f * hoverProgress;
        int glowPasses = 4;
        for (int pass = glowPasses; pass >= 1; pass--) {
            float strokeW = 1.5f + pass * 1.8f;
            float alpha   = glowAlphaBase * (1f - (float)(pass - 1) / glowPasses) * (pass == 1 ? 1f : 0.35f);
            alpha = Math.max(0f, Math.min(1f, alpha));
            Color glowColor = new Color(
                accentColor.getRed(),
                accentColor.getGreen(),
                accentColor.getBlue(),
                (int)(alpha * 255)
            );
            g2.setColor(glowColor);
            g2.setStroke(new BasicStroke(strokeW));
            g2.drawRoundRect((int)(strokeW / 2), (int)(strokeW / 2),
                             w - (int) strokeW - 1, h - (int) strokeW - 1, arc, arc);
        }
        // Crisp inner border
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(),
                              (int)(80 + 120 * hoverProgress)));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

        // ── Icon badge ────────────────────────────────────────────────────────
        int badgeSize = 44;
        int badgeX = 16, badgeY = 14;
        // Colored glow behind badge
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 45));
        g2.fillRoundRect(badgeX - 4, badgeY - 4, badgeSize + 8, badgeSize + 8, 14, 14);
        // Badge background
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80));
        g2.fillRoundRect(badgeX, badgeY, badgeSize, badgeSize, 12, 12);
        // Badge border
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 140));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(badgeX, badgeY, badgeSize, badgeSize, 12, 12);
        // Icon text
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        FontMetrics fmIcon = g2.getFontMetrics();
        int iconX = badgeX + (badgeSize - fmIcon.stringWidth(iconText)) / 2;
        int iconY = badgeY + (badgeSize + fmIcon.getAscent() - fmIcon.getDescent()) / 2;
        g2.drawString(iconText, iconX, iconY);

        // ── Title (top-right of badge) ────────────────────────────────────────
        int textX = badgeX + badgeSize + 14;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(200, 200, 230, 200));
        g2.drawString(titleText, textX, badgeY + 20);

        // ── Value (large bold) with text shadow ───────────────────────────────
        g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        FontMetrics fmVal = g2.getFontMetrics();
        int valueY = h - 26;
        // Shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.drawString(valueText, 17, valueY + 1);
        // Accent-tinted bright text
        Color valColor = new Color(
            Math.min(255, (int)(200 + 55 * hoverProgress)),
            Math.min(255, (int)(200 + 55 * hoverProgress)),
            255
        );
        g2.setColor(valColor);
        g2.drawString(valueText, 16, valueY);

        g2.dispose();
    }

    public void setValue(String value) {
        this.valueText = value;
        repaint();
    }

    public void setTrend(String trend, boolean isPositive) {
        // kept for API compatibility — trend drawn as subtitle if needed
        repaint();
    }
}
