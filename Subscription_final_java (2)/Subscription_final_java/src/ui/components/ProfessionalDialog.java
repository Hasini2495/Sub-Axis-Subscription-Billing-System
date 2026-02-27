package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Premium dark glass dialog matching the app's indigo glassmorphism theme.
 */
public class ProfessionalDialog extends JDialog {

    private int result = JOptionPane.CANCEL_OPTION;

    public enum DialogType {
        INFO, SUCCESS, WARNING, ERROR, CONFIRMATION
    }

    public ProfessionalDialog(Frame parent, String title, String message, DialogType type) {
        super(parent, title, true);
        initializeDialog(title, message, type);
    }

    private void initializeDialog(String title, String message, DialogType type) {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // â”€â”€ Main glass panel â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), r = 22;

                // Drop shadow
                for (int i = 12; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 10));
                    g2.fillRoundRect(i, i, w - i * 2, h - i * 2, r, r);
                }

                // Dark glass background: indigo gradient
                GradientPaint bg = new GradientPaint(0, 0, new Color(27, 22, 70),
                        0, h, new Color(44, 31, 94));
                g2.setPaint(bg);
                g2.fillRoundRect(8, 8, w - 16, h - 16, r, r);

                // Glass overlay
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRoundRect(8, 8, w - 16, (h - 16) / 2, r, r);

                // Border
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(8, 8, w - 17, h - 17, r, r);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(44, 50, 38, 50));
        mainPanel.setOpaque(false);

        Color iconColor = getColorForType(type);

        // â”€â”€ Icon circle â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 64, x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;

                // Glow halo
                for (int i = 10; i > 0; i--) {
                    g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(),
                            iconColor.getBlue(), i * 6));
                    g2.fillOval(x - i * 2, y - i * 2, size + i * 4, size + i * 4);
                }
                // Circle fill: gradient
                GradientPaint gp = new GradientPaint(x, y, iconColor.brighter(),
                        x, y + size, iconColor.darker());
                g2.setPaint(gp);
                g2.fillOval(x, y, size, size);

                // Draw icon shape using Graphics2D (no encoding issues)
                g2.setColor(Color.WHITE);
                int cx2 = x + size / 2, cy2 = y + size / 2;
                if (type == DialogType.SUCCESS) {
                    g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx2 - 13, cy2, cx2 - 2, cy2 + 11);
                    g2.drawLine(cx2 - 2, cy2 + 11, cx2 + 15, cy2 - 12);
                } else if (type == DialogType.ERROR) {
                    g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx2 - 11, cy2 - 11, cx2 + 11, cy2 + 11);
                    g2.drawLine(cx2 + 11, cy2 - 11, cx2 - 11, cy2 + 11);
                } else if (type == DialogType.WARNING) {
                    g2.fillRoundRect(cx2 - 3, cy2 - 14, 7, 17, 4, 4);
                    g2.fillOval(cx2 - 4, cy2 + 7, 8, 8);
                } else if (type == DialogType.CONFIRMATION) {
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
                    FontMetrics fm2 = g2.getFontMetrics();
                    g2.drawString("?", cx2 - fm2.stringWidth("?") / 2,
                            cy2 + fm2.getAscent() / 2 - 2);
                } else {
                    g2.fillOval(cx2 - 3, cy2 - 14, 7, 7);
                    g2.fillRoundRect(cx2 - 3, cy2 - 3, 7, 17, 3, 3);
                }
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(100, 90));
        iconPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        iconPanel.setAlignmentX(CENTER_ALIGNMENT);

        // â”€â”€ Title â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // â”€â”€ Message â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JLabel messageLabel = new JLabel(
                "<html><div style='text-align:center; width:270px;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(190, 185, 230));
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // â”€â”€ Buttons â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(CENTER_ALIGNMENT);

        if (type == DialogType.CONFIRMATION) {
            JButton cancelBtn = makePillButton("Cancel", new Color(80, 75, 120), new Color(100, 95, 145));
            cancelBtn.addActionListener(e -> { result = JOptionPane.CANCEL_OPTION; dispose(); });

            JButton confirmBtn = makePillButton("Confirm", new Color(123, 92, 255), new Color(180, 80, 255));
            confirmBtn.addActionListener(e -> { result = JOptionPane.OK_OPTION; dispose(); });

            buttonPanel.add(cancelBtn);
            buttonPanel.add(confirmBtn);
        } else {
            Color c1 = iconColor.darker();
            Color c2 = iconColor;
            JButton okBtn = makePillButton("OK", c1, c2);
            okBtn.setPreferredSize(new Dimension(160, 44));
            okBtn.addActionListener(e -> { result = JOptionPane.OK_OPTION; dispose(); });
            buttonPanel.add(okBtn);
        }

        // â”€â”€ Assemble â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        mainPanel.add(iconPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 28)));
        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);
        setSize(400, 310);
        setLocationRelativeTo(getParent());

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { result = JOptionPane.CANCEL_OPTION; dispose(); }
            }
        });
    }

    /** Creates a rounded pill-style button with a horizontal gradient. */
    private JButton makePillButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, c1, w, 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, h, h);
                // hover lightening
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, w, h, h, h);
                }
                // text
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                g2.drawString(getText(),
                        (w - fm.stringWidth(getText())) / 2,
                        (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(130, 44));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Color getColorForType(DialogType type) {
        switch (type) {
            case SUCCESS:      return new Color(34, 197, 94);
            case WARNING:      return new Color(234, 179, 8);
            case ERROR:        return new Color(239, 68, 68);
            case CONFIRMATION: return new Color(123, 92, 255);
            default:           return new Color(107, 114, 228);
        }
    }

    // Symbol drawing is now done inline with Graphics2D shapes — no text needed.

    public int getResult() { return result; }

    // â”€â”€ Static helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static void showInfo(Component parent, String title, String message) {
        new ProfessionalDialog((Frame) SwingUtilities.getWindowAncestor(parent),
                title, message, DialogType.INFO).setVisible(true);
    }

    public static void showSuccess(Component parent, String title, String message) {
        new ProfessionalDialog((Frame) SwingUtilities.getWindowAncestor(parent),
                title, message, DialogType.SUCCESS).setVisible(true);
    }

    public static void showWarning(Component parent, String title, String message) {
        new ProfessionalDialog((Frame) SwingUtilities.getWindowAncestor(parent),
                title, message, DialogType.WARNING).setVisible(true);
    }

    public static void showError(Component parent, String title, String message) {
        new ProfessionalDialog((Frame) SwingUtilities.getWindowAncestor(parent),
                title, message, DialogType.ERROR).setVisible(true);
    }

    public static boolean showConfirmation(Component parent, String title, String message) {
        ProfessionalDialog dialog = new ProfessionalDialog(
                (Frame) SwingUtilities.getWindowAncestor(parent),
                title, message, DialogType.CONFIRMATION);
        dialog.setVisible(true);
        return dialog.getResult() == JOptionPane.OK_OPTION;
    }
}

