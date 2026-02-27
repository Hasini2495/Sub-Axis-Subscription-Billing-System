package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Dark Glassmorphism dialog — same API as original ModernDialog.
 * All static helpers (showSuccess, showError, etc.) unchanged.
 */
public class ModernDialog extends JDialog {

    private int result = JOptionPane.CLOSED_OPTION;

    // Dark glass color system
    private static final Color BG_DARK       = new Color(10,  20,  35);
    private static final Color BG_MID        = new Color(18,  32,  52);
    private static final Color TEXT_PRIMARY  = new Color(240, 245, 255);
    private static final Color TEXT_SECONDARY = new Color(160, 175, 200);

    // Accent colors per type
    private static final Color COLOR_SUCCESS = new Color(0,  245, 160);
    private static final Color COLOR_ERROR   = new Color(239, 68,  68);
    private static final Color COLOR_WARNING = new Color(245, 158, 11);
    private static final Color COLOR_INFO    = new Color(79, 172, 254);

    public enum DialogType {
        SUCCESS, ERROR, WARNING, INFO, CONFIRMATION
    }

    public ModernDialog(Window parent, String title, String message, DialogType type) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        initDialog(message, type);
        setupKeyboardActions();
    }

    private void initDialog(String message, DialogType type) {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        Color accent = getColorForType(type);
        String iconText = getIconForType(type);
        String titleText = getTitle();

        // Paint EVERYTHING (background + icon + title + message) directly in paintComponent
        // so it works reliably in transparent JDialog on Windows.
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int pad = 14, cr = 22;

                // ── Outer accent glow ──
                int layers = 18;
                for (int i = layers; i >= 1; i--) {
                    float ratio = (float) i / layers;
                    int alpha = (int)(70 * (1f - ratio) * (1f - ratio));
                    if (alpha <= 0) continue;
                    g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), alpha));
                    g2d.setStroke(new BasicStroke(i * 0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawRoundRect(pad - i, pad - i, w - pad*2 + i*2, h - pad*2 + i*2, cr+i*2, cr+i*2);
                }

                // ── Dark card background ──
                RoundRectangle2D cardShape = new RoundRectangle2D.Float(pad, pad, w-pad*2, h-pad*2, cr, cr);
                g2d.setClip(cardShape);
                g2d.setPaint(new GradientPaint(0, pad, BG_DARK, 0, h - pad, BG_MID));
                g2d.fill(cardShape);
                // shimmer
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2d.setPaint(new GradientPaint(0, pad, Color.WHITE, 0, pad + (h-pad*2)*0.45f, new Color(255,255,255,0)));
                g2d.fill(cardShape);
                // accent top tint
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2d.setPaint(new GradientPaint(0, pad, accent, 0, pad+80, new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),0)));
                g2d.fill(cardShape);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setClip(null);
                // border
                g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 140));
                g2d.setStroke(new BasicStroke(1.6f));
                g2d.draw(new RoundRectangle2D.Float(pad+1, pad+1, w-pad*2-2, h-pad*2-2, cr-1, cr-1));
                g2d.setColor(new Color(255,255,255,25));
                g2d.setStroke(new BasicStroke(1f));
                g2d.draw(new RoundRectangle2D.Float(pad+2, pad+2, w-pad*2-4, h-pad*2-4, cr-2, cr-2));

                // ── Icon circle (top-center) ──
                int iconSize = 44;
                int iconX = w/2 - iconSize/2;
                int iconY = pad + 18;
                // glow
                for (int i = 10; i >= 1; i--) {
                    float r = (float) i / 10;
                    g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int)(45*(1-r)*(1-r))));
                    g2d.fillOval(iconX - i*2, iconY - i*2, iconSize+i*4, iconSize+i*4);
                }
                // dark circle
                g2d.setColor(BG_DARK);
                g2d.fillOval(iconX, iconY, iconSize, iconSize);
                g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2d.fillOval(iconX, iconY, iconSize, iconSize);
                // shimmer top half
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2d.setPaint(new GradientPaint(iconX, iconY, Color.WHITE, iconX, iconY+iconSize*0.5f, new Color(255,255,255,0)));
                g2d.fillOval(iconX, iconY, iconSize, iconSize/2);
                // border ring
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setColor(accent);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawOval(iconX, iconY, iconSize, iconSize);
                // icon glyph
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2d.setColor(accent);
                FontMetrics ifm = g2d.getFontMetrics();
                g2d.drawString(iconText,
                    iconX + (iconSize - ifm.stringWidth(iconText)) / 2,
                    iconY + (iconSize + ifm.getAscent() - ifm.getDescent()) / 2);

                int contentY = iconY + iconSize + 12;

                // ── Title ──
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 17));
                g2d.setColor(Color.WHITE);
                FontMetrics tfm = g2d.getFontMetrics();
                g2d.drawString(titleText, (w - tfm.stringWidth(titleText)) / 2, contentY + tfm.getAscent());
                contentY += tfm.getHeight() + 8;

                // ── Message (word-wrap) ──
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2d.setColor(new Color(190, 205, 230));
                FontMetrics mfm = g2d.getFontMetrics();
                int maxWidth = w - pad*2 - 48;
                // simple word wrap
                String[] words = message.split(" ");
                StringBuilder line = new StringBuilder();
                java.util.List<String> lines = new java.util.ArrayList<>();
                for (String word : words) {
                    String test = line.length() == 0 ? word : line + " " + word;
                    if (mfm.stringWidth(test) <= maxWidth) {
                        line = new StringBuilder(test);
                    } else {
                        if (line.length() > 0) lines.add(line.toString());
                        line = new StringBuilder(word);
                    }
                }
                if (line.length() > 0) lines.add(line.toString());
                for (String ln : lines) {
                    g2d.drawString(ln, (w - mfm.stringWidth(ln)) / 2, contentY + mfm.getAscent());
                    contentY += mfm.getHeight() + 2;
                }

                g2d.dispose();
            }
        };

        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        // Reserve space at top for icon + title + message (drawn via paintComponent above)
        // Button panel sits at the bottom with a fixed top margin
        JPanel topSpacer = new JPanel();
        topSpacer.setOpaque(false);
        topSpacer.setPreferredSize(new Dimension(440, 190));
        mainPanel.add(topSpacer, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(type, accent);
        buttonPanel.setBorder(new EmptyBorder(0, 14, 18, 14));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setSize(440, 290);
        setLocationRelativeTo(getParent());
    }

    private JLabel createIconLabel(DialogType type, Color accent) {
        String iconText = getIconForType(type);

        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 44;
                int x = (getWidth()  - size) / 2;
                int y = (getHeight() - size) / 2;

                // Glow ring
                for (int i = 10; i >= 1; i--) {
                    float ratio = (float) i / 10;
                    g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                        (int)(50 * (1f - ratio) * (1f - ratio))));
                    g2d.fillOval(x - i*2, y - i*2, size + i*4, size + i*4);
                }

                // Dark circle + colored fill
                g2d.setColor(BG_DARK);
                g2d.fillOval(x, y, size, size);

                g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 55));
                g2d.fillOval(x, y, size, size);

                // Glass shimmer
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setPaint(new GradientPaint(x, y, Color.WHITE, x, y + size * 0.5f, new Color(255,255,255,0)));
                g2d.fillOval(x, y, size, size/2);

                // Accent border
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setColor(accent);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawOval(x, y, size, size);

                super.paintComponent(g);
                g2d.dispose();
            }
        };

        iconLabel.setText(iconText);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        iconLabel.setForeground(accent);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(56, 56));
        iconLabel.setOpaque(false);
        return iconLabel;
    }

    private JPanel createButtonPanel(DialogType type, Color accent) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        if (type == DialogType.CONFIRMATION) {
            JButton cancelBtn = createGlassButton("Cancel", new Color(120, 130, 150));
            cancelBtn.addActionListener(e -> { result = JOptionPane.CANCEL_OPTION; dispose(); });

            JButton confirmBtn = createGlassButton("Confirm", accent);
            confirmBtn.addActionListener(e -> { result = JOptionPane.OK_OPTION; dispose(); });
            getRootPane().setDefaultButton(confirmBtn);

            panel.add(cancelBtn);
            panel.add(confirmBtn);
        } else {
            JButton okBtn = createGlassButton("OK", accent);
            okBtn.setPreferredSize(new Dimension(110, 40));
            okBtn.addActionListener(e -> { result = JOptionPane.OK_OPTION; dispose(); });
            getRootPane().setDefaultButton(okBtn);
            panel.add(okBtn);
        }
        return panel;
    }

    private JButton createGlassButton(String text, Color glowColor) {
        JButton btn = new JButton(text) {
            private float hover = 0f;
            private boolean hovered = false;
            private Timer t;
            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));

                t = new Timer(15, e -> {
                    hover = hovered ? Math.min(1f, hover + 0.1f) : Math.max(0f, hover - 0.1f);
                    // Repaint the PARENT so both buttons are repainted together.
                    // Repainting only 'this' in a transparent JDialog on Windows causes
                    // the RepaintManager to clear only the hovered button's region,
                    // which erases the sibling button.
                    Container p = getParent();
                    if (p != null) p.repaint();
                    else repaint();
                    if ((!hovered && hover <= 0f) || (hovered && hover >= 1f)) t.stop();
                });
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  if (!t.isRunning()) t.start(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; if (!t.isRunning()) t.start(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                // Use g.create() so we never modify the shared Graphics state.
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight(), arc = h;

                // Hard-clamp ALL drawing to this button's own bounds.
                g2d.clipRect(0, 0, w, h);

                // ── Inset glow (stays inside bounds, no negative offsets) ──
                if (hover > 0) {
                    for (int i = 6; i >= 1; i--) {
                        float ratio = (float) i / 6;
                        int alpha = (int)(45 * hover * (1 - ratio) * (1 - ratio));
                        if (alpha <= 0) continue;
                        g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alpha));
                        g2d.setStroke(new BasicStroke(i * 1.0f));
                        int off = i;
                        g2d.drawRoundRect(off, off, w - off * 2 - 1, h - off * 2 - 1, arc, arc);
                    }
                }

                // ── Glass pill fill ──
                RoundRectangle2D pill = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
                Shape savedClip = g2d.getClip();
                g2d.setClip(pill);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f + 0.08f * hover));
                g2d.setColor(Color.WHITE);
                g2d.fill(pill);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.5f, new Color(255, 255, 255, 0)));
                g2d.fill(pill);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setClip(savedClip);   // restore — never set to null

                // ── Text ──
                g2d.setFont(getFont());
                g2d.setColor(TEXT_PRIMARY);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(),
                    (w - fm.stringWidth(getText())) / 2,
                    (h - fm.getHeight()) / 2 + fm.getAscent());

                // ── Border ──
                if (hover > 0) {
                    g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(),
                        glowColor.getBlue(), (int)(100 * hover)));
                    g2d.setStroke(new BasicStroke(1.8f));
                    g2d.drawRoundRect(1, 1, w - 3, h - 3, arc - 2, arc - 2);
                }
                g2d.setColor(new Color(255, 255, 255, (int)(55 + 50 * hover)));
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                g2d.dispose();
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(110, 40));
        return btn;
    }

    private void setupKeyboardActions() {
        getRootPane().registerKeyboardAction(
            e -> { result = JOptionPane.CANCEL_OPTION; dispose(); },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private String getIconForType(DialogType type) {
        switch (type) {
            case SUCCESS:      return "\u2713";
            case ERROR:        return "\u2715";
            case WARNING:      return "!";
            case CONFIRMATION: return "?";
            default:           return "i";
        }
    }

    private Color getColorForType(DialogType type) {
        switch (type) {
            case SUCCESS:      return COLOR_SUCCESS;
            case ERROR:        return COLOR_ERROR;
            case WARNING:      return COLOR_WARNING;
            case CONFIRMATION: return COLOR_INFO;
            default:           return COLOR_INFO;
        }
    }

    public int getResult() { return result; }

    // ── Static helpers — signatures unchanged ──────────────────────────────────
    public static void showSuccess(Component parent, String title, String message) {
        new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, DialogType.SUCCESS).setVisible(true);
    }

    public static void showError(Component parent, String title, String message) {
        new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, DialogType.ERROR).setVisible(true);
    }

    public static void showWarning(Component parent, String title, String message) {
        new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, DialogType.WARNING).setVisible(true);
    }

    public static void showInfo(Component parent, String title, String message) {
        new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, DialogType.INFO).setVisible(true);
    }

    public static boolean showConfirmation(Component parent, String title, String message) {
        ModernDialog dialog = new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, DialogType.CONFIRMATION);
        dialog.setVisible(true);
        return dialog.getResult() == JOptionPane.OK_OPTION;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Glass Input Dialog
    // ══════════════════════════════════════════════════════════════════════════
    /**
     * Shows a glass-styled single-field input dialog.
     * @return the entered text, or null if cancelled
     */
    public static String showInput(Component parent, String title, String prompt, String initialValue) {
        // IMPLEMENTATION NOTE: We do NOT use a per-pixel-alpha transparent window here.
        // On Windows, transparent undecorated JDialogs use a different OS compositing
        // path that breaks child-component rendering (blank dialog, invisible buttons).
        // Instead we use an opaque dark background + inset glass decorations drawn in
        // paintComponent AFTER super.paintComponent() so Swing paints children normally.
        final String[] out = {null};
        Color accent = COLOR_INFO;

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent), title,
                                  Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        // Opaque dark background — no per-pixel alpha so children always render
        dlg.setBackground(BG_DARK);

        JPanel mainPanel = buildSolidGlassPanel(accent);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(28, 32, 24, 32));

        // Icon + title row
        JLabel icon = buildIconCircleLabel("\u270E", accent, 46);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(icon);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLbl);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel promptLbl = new JLabel(prompt);
        promptLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        promptLbl.setForeground(new Color(160, 175, 200));
        promptLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(promptLbl);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        JTextField field = buildGlassTextField(initialValue != null ? initialValue : "", accent);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(field);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton cancelBtn = buildStaticGlassButton("Cancel", new Color(120, 130, 150));
        JButton okBtn     = buildStaticGlassButton("OK", accent);
        cancelBtn.addActionListener(e -> dlg.dispose());
        okBtn.addActionListener(e -> { out[0] = field.getText(); dlg.dispose(); });
        field.addActionListener(e -> { out[0] = field.getText(); dlg.dispose(); });
        btnRow.add(cancelBtn);
        btnRow.add(okBtn);
        mainPanel.add(btnRow);

        dlg.setContentPane(mainPanel);
        dlg.setSize(420, 290);
        dlg.setLocationRelativeTo(parent);
        dlg.getRootPane().setDefaultButton(okBtn);
        dlg.getRootPane().registerKeyboardAction(e -> dlg.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        SwingUtilities.invokeLater(field::requestFocusInWindow);
        dlg.setVisible(true);
        return out[0];
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Glass Change-Password Dialog
    // ══════════════════════════════════════════════════════════════════════════
    /**
     * Shows a glass-styled change-password dialog with three password fields.
     * @return String[]{currentPwd, newPwd, confirmPwd} or null if cancelled
     */
    public static String[] showChangePassword(Component parent) {
        final String[][] out = {null};
        Color accent = new Color(147, 51, 234); // purple

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent), "Change Password",
                                  Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        dlg.setBackground(BG_DARK);

        JPanel mainPanel = buildSolidGlassPanel(accent);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel icon = buildIconCircleLabel("\u2605", accent, 46);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(icon);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel titleLbl = new JLabel("Change Password");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLbl);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        JPasswordField currentPwdField = buildGlassPasswordField(accent);
        JPasswordField newPwdField     = buildGlassPasswordField(accent);
        JPasswordField confirmPwdField = buildGlassPasswordField(accent);

        String[] labelTxts = {"Current Password", "New Password", "Confirm Password"};
        JPasswordField[] pwdFields = {currentPwdField, newPwdField, confirmPwdField};
        for (int i = 0; i < 3; i++) {
            JLabel lbl = new JLabel(labelTxts[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(160, 175, 200));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            pwdFields[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(lbl);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            mainPanel.add(pwdFields[i]);
            if (i < 2) mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        mainPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton cancelBtn  = buildStaticGlassButton("Cancel",  new Color(120, 130, 150));
        JButton confirmBtn = buildStaticGlassButton("Confirm", accent);
        cancelBtn.addActionListener(e -> dlg.dispose());
        confirmBtn.addActionListener(e -> {
            out[0] = new String[]{
                new String(currentPwdField.getPassword()),
                new String(newPwdField.getPassword()),
                new String(confirmPwdField.getPassword())
            };
            dlg.dispose();
        });
        btnRow.add(cancelBtn);
        btnRow.add(confirmBtn);
        mainPanel.add(btnRow);

        dlg.setContentPane(mainPanel);
        dlg.setSize(420, 430);
        dlg.setLocationRelativeTo(parent);
        dlg.getRootPane().setDefaultButton(confirmBtn);
        dlg.getRootPane().registerKeyboardAction(e -> dlg.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        SwingUtilities.invokeLater(currentPwdField::requestFocusInWindow);
        dlg.setVisible(true);
        return out[0];
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Private static helpers shared by input/password dialogs
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Creates an OPAQUE panel whose paintComponent:
     *   1. Calls super.paintComponent(g)  ← fills BG_DARK, lets Swing paint children
     *   2. Draws glass decorations (gradient shimmer + accent border) on top
     *
     * Because the panel is opaque and has a real background color, the Windows
     * compositing path works correctly and all child components render normally.
     * No per-pixel alpha is used on the window or its content pane.
     */
    private static JPanel buildSolidGlassPanel(Color accent) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Step 1 — let Swing fill BG_DARK and establish correct child context
                super.paintComponent(g);

                // Step 2 — draw glass decorations on top (never clips children)
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight(), cr = 0; // straight corners (opaque window)

                // Gradient shimmer overlay — drawn WITHOUT clipping so children show through
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.4f, new Color(255,255,255,0)));
                g2.fillRect(0, 0, w, h);

                // Accent top tint
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.setPaint(new GradientPaint(0, 0, accent, 0, 70, new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),0)));
                g2.fillRect(0, 0, w, h);

                // Accent border (inset so it never extends outside window)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRect(1, 1, w - 2, h - 2);

                // Inner subtle white highlight line
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(2, 2, w - 4, h - 4);

                g2.dispose();
            }
        };
        p.setOpaque(true);
        p.setBackground(BG_DARK);
        return p;
    }

    /** Creates an icon circle JLabel painted with the given glyph and accent colour. */
    private static JLabel buildIconCircleLabel(String glyph, Color accent, int size) {
        JLabel lbl = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Do NOT call super.paintComponent() here — this label is non-opaque
                // and we handle all painting ourselves.
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = (getWidth()-size)/2, cy = (getHeight()-size)/2;
                // Glow rings
                for (int i = 10; i >= 1; i--) {
                    float r = (float)i/10;
                    g2d.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),(int)(45*(1-r)*(1-r))));
                    g2d.fillOval(cx-i*2, cy-i*2, size+i*4, size+i*4);
                }
                // Dark fill + colour tint
                g2d.setColor(BG_DARK);
                g2d.fillOval(cx, cy, size, size);
                g2d.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),60));
                g2d.fillOval(cx, cy, size, size);
                // Shimmer
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2d.setPaint(new GradientPaint(cx, cy, Color.WHITE, cx, cy+size*0.5f, new Color(255,255,255,0)));
                g2d.fillOval(cx, cy, size, size/2);
                // Border ring
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setColor(accent);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawOval(cx, cy, size, size);
                // Glyph
                g2d.setFont(new Font("Segoe UI", Font.BOLD, size/2 + 2));
                g2d.setColor(accent);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(glyph, cx+(size-fm.stringWidth(glyph))/2,
                               cy+(size+fm.getAscent()-fm.getDescent())/2);
                g2d.dispose();
            }
        };
        lbl.setOpaque(false);
        lbl.setPreferredSize(new Dimension(size+40, size+16));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, size+16));
        return lbl;
    }

    /** Creates a dark glass-styled text field. */
    private static JTextField buildGlassTextField(String initial, Color accent) {
        JTextField f = new JTextField(initial) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 18, 32));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(new Color(220, 230, 250));
        f.setCaretColor(new Color(79, 172, 254));
        f.setBorder(new EmptyBorder(9, 12, 9, 12));
        f.setPreferredSize(new Dimension(356, 42));
        f.setMinimumSize(new Dimension(100, 42));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return f;
    }

    /** Creates a dark glass-styled password field. */
    private static JPasswordField buildGlassPasswordField(Color accent) {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 18, 32));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(new Color(220, 230, 250));
        f.setCaretColor(new Color(79, 172, 254));
        f.setBorder(new EmptyBorder(9, 12, 9, 12));
        f.setPreferredSize(new Dimension(356, 42));
        f.setMinimumSize(new Dimension(100, 42));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return f;
    }

    /**
     * Static version of createGlassButton — no instance context required.
     * Always visible at rest (coloured pill + border); brighter/glowing on hover.
     * Repaints the parent panel so siblings are never erased during hover animation.
     */
    private static JButton buildStaticGlassButton(String text, Color glowColor) {
        JButton btn = new JButton(text) {
            private float hover = 0f;
            private boolean hovered = false;
            private Timer t;
            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                t = new Timer(15, e -> {
                    hover = hovered ? Math.min(1f, hover + 0.1f) : Math.max(0f, hover - 0.1f);
                    // Repaint parent so sibling buttons are never erased
                    Container p = getParent();
                    if (p != null) p.repaint(); else repaint();
                    if ((!hovered && hover <= 0f) || (hovered && hover >= 1f)) t.stop();
                });
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  if (!t.isRunning()) t.start(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; if (!t.isRunning()) t.start(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), arc = h;

                // Clamp: never paint outside own bounds
                g2d.clipRect(0, 0, w, h);
                Shape savedClip = g2d.getClip();

                // Inset glow on hover
                if (hover > 0) {
                    for (int i = 5; i >= 1; i--) {
                        float ratio = (float)i/5;
                        int alpha = (int)(50 * hover * (1-ratio) * (1-ratio));
                        if (alpha <= 0) continue;
                        g2d.setColor(new Color(glowColor.getRed(),glowColor.getGreen(),glowColor.getBlue(),alpha));
                        g2d.setStroke(new BasicStroke(i));
                        g2d.drawRoundRect(i, i, w-i*2-1, h-i*2-1, arc, arc);
                    }
                }

                // Pill fill — always visible
                RoundRectangle2D pill = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
                g2d.setClip(pill);
                // Dark base
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setColor(new Color(BG_DARK.getRed(), BG_DARK.getGreen(), BG_DARK.getBlue(), 220));
                g2d.fill(pill);
                // Colour tint (rest = 25%, hover = 50%)
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f + 0.25f * hover));
                g2d.setColor(glowColor);
                g2d.fill(pill);
                // Shimmer
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.5f, new Color(255,255,255,0)));
                g2d.fill(pill);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setClip(savedClip);

                // Text — always full-opacity white
                g2d.setFont(getFont());
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (w-fm.stringWidth(getText()))/2, (h-fm.getHeight())/2+fm.getAscent());

                // Border — always visible, brighter on hover
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                                       (int)(180 + 75 * hover)));
                g2d.setStroke(new BasicStroke(1.6f));
                g2d.drawRoundRect(1, 1, w-3, h-3, arc-2, arc-2);

                g2d.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }

    // Legacy helper kept for the original ModernDialog background panel
    // (used by initDialog — the transparent-window path for SUCCESS/ERROR/etc.)
    private static JPanel buildGlassBackgroundPanel(Color accent) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight(), pad = 14, cr = 22;
                int layers = 18;
                for (int i = layers; i >= 1; i--) {
                    float ratio = (float) i / layers;
                    int alpha = (int)(70 * (1f-ratio) * (1f-ratio));
                    if (alpha <= 0) continue;
                    g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), alpha));
                    g2d.setStroke(new BasicStroke(i * 0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawRoundRect(pad-i, pad-i, w-pad*2+i*2, h-pad*2+i*2, cr+i*2, cr+i*2);
                }
                RoundRectangle2D card = new RoundRectangle2D.Float(pad, pad, w-pad*2, h-pad*2, cr, cr);
                g2d.setClip(card);
                g2d.setPaint(new GradientPaint(0, pad, BG_DARK, 0, h-pad, BG_MID));
                g2d.fill(card);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2d.setPaint(new GradientPaint(0, pad, Color.WHITE, 0, pad+(h-pad*2)*0.45f, new Color(255,255,255,0)));
                g2d.fill(card);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2d.setPaint(new GradientPaint(0, pad, accent, 0, pad+80, new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),0)));
                g2d.fill(card);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setClip(null);
                g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 140));
                g2d.setStroke(new BasicStroke(1.6f));
                g2d.draw(new RoundRectangle2D.Float(pad+1, pad+1, w-pad*2-2, h-pad*2-2, cr-1, cr-1));
                g2d.setColor(new Color(255,255,255,25));
                g2d.setStroke(new BasicStroke(1f));
                g2d.draw(new RoundRectangle2D.Float(pad+2, pad+2, w-pad*2-4, h-pad*2-4, cr-2, cr-2));
                g2d.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }
}
