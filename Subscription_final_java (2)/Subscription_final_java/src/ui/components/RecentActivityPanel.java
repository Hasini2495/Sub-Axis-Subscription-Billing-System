package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Glassy dark recent activity panel with alternating dark tints,
 * neon icon badges, and custom translucent scrollbar.
 * Pure Java Swing â€” no third-party libs.
 */
public class RecentActivityPanel extends JPanel {

    private JPanel activityContainer;
    private int    activityCount = 0;

    public RecentActivityPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Latest system events and transactions");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(new Color(180, 180, 220, 160));
        subLabel.setAlignmentX(LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(subLabel);

        // Container for activity rows
        activityContainer = new JPanel();
        activityContainer.setLayout(new BoxLayout(activityContainer, BoxLayout.Y_AXIS));
        activityContainer.setOpaque(false);

        // Scroll pane with dark background and minimal scrollbar
        JScrollPane scroll = new JScrollPane(activityContainer) {
            @Override public void paintComponent(Graphics g) {
                // transparent â€” outer glass panel shows through
            }
        };
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getVerticalScrollBar().setOpaque(false);
        // Minimal scrollbar UI
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor          = new Color(140, 100, 255, 100);
                trackColor          = new Color(255, 255, 255, 12);
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
            private JButton zeroButton() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0)); b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x + 2, r.y, r.width - 4, r.height, 6, 6);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(r.x, r.y, r.width, r.height);
                g2.dispose();
            }
        });

        add(titlePanel,  BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        // Drop shadow
        for (int i = 5; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 12 * i));
            g2.fillRoundRect(i, i + 2, w - i * 2, h - i * 2, 24, 24);
        }
        // Glass body
        g2.setPaint(new GradientPaint(0, 0, new Color(16, 14, 52, 230), w, h, new Color(28, 18, 76, 215)));
        g2.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);
        // Sheen
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 15), 0, h/3, new Color(255, 255, 255, 3)));
        g2.fillRoundRect(0, 0, w - 1, h / 3, 22, 22);
        // Border
        g2.setColor(new Color(255, 255, 255, 28));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 22, 22);

        g2.dispose();
        super.paintComponent(g);
    }

    public void addActivity(String icon, String description, String timestamp) {
        activityContainer.add(createRow(icon, description, timestamp, activityCount));
        activityContainer.add(Box.createRigidArea(new Dimension(0, 1)));
        activityCount++;
        revalidate(); repaint();
    }

    public void clearActivities() {
        activityContainer.removeAll();
        activityCount = 0;
        revalidate(); repaint();
    }

    // â”€â”€ Row builder â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private JPanel createRow(String icon, String desc, String time, int idx) {
        Color iconColor = getIconColor(icon);
        boolean even    = idx % 2 == 0;

        JPanel row = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Alternating subtle tint
                Color bg = even ? new Color(255, 255, 255, 8) : new Color(255, 255, 255, 4);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        // Icon badge
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glow halo
                g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 35));
                g2.fillOval(-4, -4, 48, 48);
                // Badge fill
                g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 70));
                g2.fillOval(0, 0, 38, 38);
                // Badge border
                g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 160));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(1, 1, 36, 36);
                // Icon text
                String displayIcon = getDisplayIcon(icon);
                g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(iconColor);
                int ix = (38 - fm.stringWidth(displayIcon)) / 2;
                int iy = (38 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(displayIcon, ix, iy);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(38, 38));

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(230, 230, 255, 230));
        descLabel.setAlignmentX(LEFT_ALIGNMENT);

        content.add(descLabel);

        // Timestamp right
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(160, 160, 210, 160));

        row.add(badge,     BorderLayout.WEST);
        row.add(content,   BorderLayout.CENTER);
        row.add(timeLabel, BorderLayout.EAST);
        return row;
    }

    private Color getIconColor(String icon) {
        switch (icon) {
            case "âœ“":   return new Color(57, 255, 20);      // neon green
            case "ðŸ’³":  return new Color(0, 190, 255);      // cyan
            case "ðŸ“„":  return new Color(255, 140, 0);      // orange
            case "â¬†ï¸": case "ðŸ”„": return new Color(190, 30, 255); // purple
            case "âŒ":  return new Color(255, 60, 60);      // red
            default:    return new Color(160, 160, 210);    // muted
        }
    }

    private String getDisplayIcon(String icon) {
        switch (icon) {
            case "âœ“":   return "âœ“";
            case "ðŸ’³":  return "ðŸ’³";
            case "ðŸ“„":  return "ðŸ“„";
            case "â¬†ï¸": return "â†‘";
            case "ðŸ”„":  return "â†»";
            case "âŒ":  return "âœ•";
            default:    return icon.length() > 2 ? "â€¢" : icon;
        }
    }
}
