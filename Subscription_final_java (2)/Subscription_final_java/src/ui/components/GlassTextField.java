package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * GlassTextField – a rounded, semi-transparent text field that matches the
 * dark glass login-card aesthetic.
 *
 * Visual contract
 *   • Background : rgba(255,255,255,0.06)  ≈ new Color(255,255,255,15)
 *   • Border      : 1.5 px white-20% normally, purple on focus
 *   • Text        : white, caret lavender
 *   • Placeholder : white-30% italic
 *   • Height      : 42 px fixed
 *   • Corner arc  : 20 px
 *
 * All painting is done manually; Swing's built-in border is suppressed.
 */
public class GlassTextField extends JTextField {

    // ── colours ──────────────────────────────────────────────
    private static final Color BG           = new Color(255, 255, 255, 15);
    private static final Color BORDER_IDLE  = new Color(255, 255, 255, 50);
    private static final Color BORDER_FOCUS = new Color(130,  80, 255, 200);
    private static final Color PLACEHOLDER  = new Color(255, 255, 255, 80);
    private static final int   ARC          = 20;

    private final String placeholder;
    private boolean focused = false;

    // ─────────────────────────────────────────────────────────

    public GlassTextField(String placeholder) {
        this.placeholder = placeholder;

        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(new Color(180, 150, 255));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBorder(new EmptyBorder(10, 18, 10, 18));
        setPreferredSize(new Dimension(300, 42));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 42));

        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused = true;  repaint(); }
            @Override public void focusLost (FocusEvent e) { focused = false; repaint(); }
        });
    }

    // ── painting ─────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // glass background
        g2.setColor(BG);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, ARC, ARC));

        // border
        g2.setColor(focused ? BORDER_FOCUS : BORDER_IDLE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, w - 1.5f, h - 1.5f, ARC, ARC));
        g2.dispose();

        // Let Swing draw the actual text / cursor
        super.paintComponent(g);

        // Placeholder drawn on top (only when field is empty)
        if (getText().isEmpty() && placeholder != null) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gp.setColor(PLACEHOLDER);
            gp.setFont(getFont());
            FontMetrics fm  = gp.getFontMetrics();
            Insets      ins = getInsets();
            int         y   = (h + fm.getAscent() - fm.getDescent()) / 2;
            gp.drawString(placeholder, ins.left, y);
            gp.dispose();
        }
    }

    /** Suppress default Swing border so only our custom rounded border shows. */
    @Override
    protected void paintBorder(Graphics g) { /* intentionally empty */ }
}
