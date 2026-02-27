package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * GlassSaveDialog – glass-themed "Save login details?" confirmation dialog.
 *
 * Drop-in replacement for the JOptionPane.showConfirmDialog call that existed
 * in the original LoginFrame.  Backend logic is unchanged; only the visual
 * presentation is redesigned to match the dark glass theme.
 *
 * Usage
 *   GlassSaveDialog dlg = new GlassSaveDialog(parentFrame);
 *   dlg.setVisible(true);               // blocks (modal)
 *   if (dlg.isConfirmed()) { ... }
 */
public class GlassSaveDialog extends JDialog {

    // ── result ────────────────────────────────────────────────
    private boolean confirmed = false;

    // ── colours ───────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(14,  14,  20,  235);
    private static final Color TINT       = new Color(255, 255, 255,  10);
    private static final Color BORDER_COL = new Color(255, 255, 255,  40);
    private static final Color BTN_BLUE   = new Color(0x4A, 0x6C, 0xF7);
    private static final Color BTN_PURP   = new Color(0x7B, 0x2F, 0xF7);
    private static final Color BTN_GRAY1  = new Color( 55,  55,  75);
    private static final Color BTN_GRAY2  = new Color( 75,  75,  95);

    // ─────────────────────────────────────────────────────────

    public GlassSaveDialog(Frame parent) {
        super(parent, "Save Login Details", true);
        setUndecorated(true);
        // Transparent window so RoundRectangle clipping shows
        setBackground(new Color(0, 0, 0, 0));
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    // ── UI construction ──────────────────────────────────────

    private void buildUI() {
        // Root panel paints the glass card background
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                // Dark backdrop
                g2.setColor(BG_DARK);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 24, 24));

                // Subtle white glass tint
                g2.setColor(TINT);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 24, 24));

                // Thin white border
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, 24, 24));

                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(36, 40, 30, 40));
        root.setPreferredSize(new Dimension(420, 230));

        // ── content (BoxLayout column) ──
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Title
        JLabel title = new JLabel("Save Login Details?");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        // Message
        JLabel msg = new JLabel(
            "<html><div align='center'>" +
            "Would you like to save your credentials?<br>" +
            "They will be auto-filled next time you open the app." +
            "</div></html>"
        );
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(new Color(200, 200, 220));
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        msg.setAlignmentX(CENTER_ALIGNMENT);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 14, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(CENTER_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(340, 44));

        JButton yesBtn = createBtn("Yes, Save",  BTN_BLUE,  BTN_PURP);
        JButton noBtn  = createBtn("No Thanks",  BTN_GRAY1, BTN_GRAY2);

        yesBtn.addActionListener(e -> { confirmed = true;  dispose(); });
        noBtn .addActionListener(e -> { confirmed = false; dispose(); });

        btnRow.add(yesBtn);
        btnRow.add(noBtn);

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(msg);
        content.add(Box.createRigidArea(new Dimension(0, 26)));
        content.add(btnRow);

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JButton createBtn(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, c1, w, 0, c2));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
                // shimmer
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h / 2f, new Color(255,255,255,0)));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) { /* none */ }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(155, 42));
        return btn;
    }

    // ── result accessor ──────────────────────────────────────

    /** @return true if the user clicked "Yes, Save". */
    public boolean isConfirmed() {
        return confirmed;
    }

    // ── static glass notification helper ─────────────────────

    /**
     * Shows a small glass-themed notification dialog (no buttons — auto-closes
     * after 2 seconds, or the user can click it to dismiss).
     *
     * @param parent  owner frame
     * @param title   bold heading line
     * @param message sub-message line
     * @param isError true = red accent, false = green accent
     */
    public static void showMessage(Frame parent, String title, String message, boolean isError) {
        Color accent = isError ? new Color(220, 60, 80) : new Color(60, 210, 120);

        JDialog dlg = new JDialog(parent, "", true);
        dlg.setUndecorated(true);
        dlg.setBackground(new Color(0, 0, 0, 0));

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(14, 14, 20, 235));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                g2.setColor(new Color(255, 255, 255, 38));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new java.awt.geom.RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, 20, 20));
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new javax.swing.border.EmptyBorder(28, 36, 28, 36));
        root.setPreferredSize(new Dimension(360, 140));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // coloured accent line at top
        JPanel stripe = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        stripe.setOpaque(false);
        stripe.setPreferredSize(new Dimension(Short.MAX_VALUE, 3));
        stripe.setMaximumSize(new Dimension(Short.MAX_VALUE, 3));
        stripe.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLbl = new JLabel("<html><div align='center'>" + message + "</div></html>");
        msgLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msgLbl.setForeground(new Color(190, 190, 210));
        msgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        msgLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(stripe);
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(titleLbl);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(msgLbl);

        root.add(content, BorderLayout.CENTER);
        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);

        // click anywhere to dismiss early
        root.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dlg.dispose(); }
        });

        // auto-close after 2 s
        Timer t = new Timer(2000, e -> dlg.dispose());
        t.setRepeats(false);
        t.start();

        dlg.setVisible(true);
    }
}
