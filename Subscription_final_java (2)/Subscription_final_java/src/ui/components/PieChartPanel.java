package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Glassy dark pie chart with animated pop-out slice on hover, neon borders,
 * and cursor-following tooltip. Pure Java Swing â€” no third-party libs.
 */
public class PieChartPanel extends JPanel {

    private String title;
    private Map<String, Double> data     = new LinkedHashMap<>();
    private List<Color> sliceColors = Arrays.asList(
        new Color(0, 190, 255),    // cyan neon
        new Color(57, 255, 20),    // green neon
        new Color(255, 130, 0),    // orange neon
        new Color(220, 0, 255),    // purple neon
        new Color(255, 50, 100),   // pink neon
        new Color(255, 230, 0)     // yellow neon
    );

    // Per-slice animation offset (pixels the slice is pushed outward)
    private float[]  sliceOffset;
    private int      hoveredSlice = -1;
    private Point    mousePos     = new Point(0, 0);
    private Timer    animTimer;

    // Populated during paint, used for hit-testing
    private final List<double[]> sliceAngles = new ArrayList<>(); // [startAngle, extent]

    public PieChartPanel(String title) {
        this.title = title;
        setOpaque(false);
        setPreferredSize(new Dimension(460, 360));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        animTimer = new Timer(16, e -> {
            boolean changed = false;
            for (int i = 0; i < sliceOffset.length; i++) {
                float target = (i == hoveredSlice) ? 15f : 0f;
                float delta  = (target - sliceOffset[i]) * 0.15f;
                if (Math.abs(delta) > 0.2f) { sliceOffset[i] += delta; changed = true; }
                else                          sliceOffset[i]  = target;
            }
            repaint();
            if (!changed) animTimer.stop();
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                int prev  = hoveredSlice;
                hoveredSlice = hitTest(mousePos);
                if (hoveredSlice != prev) { animTimer.start(); repaint(); }
                else repaint(); // keep tooltip following cursor
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                hoveredSlice = -1; mousePos = new Point(0, 0);
                animTimer.start(); repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !data.isEmpty()) maximizeChart();
            }
        });
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void setData(Map<String, Double> d) {
        this.data = new LinkedHashMap<>(d);
        sliceOffset = new float[d.size()];
        hoveredSlice = -1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth(), h = getHeight();
        paintGlassBackground(g2, w, h);

        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        FontMetrics fmT = g2.getFontMetrics();
        g2.drawString(title, (w - fmT.stringWidth(title)) / 2, 22);

        if (data == null || data.isEmpty()) { drawEmptyState(g2, w, h); g2.dispose(); return; }
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total == 0) { drawEmptyState(g2, w, h); g2.dispose(); return; }

        if (sliceOffset == null || sliceOffset.length != data.size()) sliceOffset = new float[data.size()];

        // Layout
        int legendW  = 140;
        int pieSize  = Math.min(w - legendW - 40, h - 60);
        pieSize = Math.max(pieSize, 100);
        int pieX     = 18;
        int pieY     = (h - pieSize) / 2 + 10;
        double cx    = pieX + pieSize / 2.0;
        double cy    = pieY + pieSize / 2.0;

        sliceAngles.clear();
        double startAngle = 0;
        int idx = 0;
        List<String> keys = new ArrayList<>(data.keySet());

        // â”€â”€ Draw slices â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        for (String key : keys) {
            double val    = data.get(key);
            double extent = (val / total) * 360.0;
            double mid    = Math.toRadians(startAngle + extent / 2.0);
            float  off    = (sliceOffset != null && idx < sliceOffset.length) ? sliceOffset[idx] : 0f;
            double ox     = Math.cos(mid) * off;
            double oy     = -Math.sin(mid) * off;

            Color base  = sliceColors.get(idx % sliceColors.size());
            Color fill  = new Color(base.getRed(), base.getGreen(), base.getBlue(), idx == hoveredSlice ? 200 : 155);

            Arc2D arc = new Arc2D.Double(
                pieX + ox, pieY + oy, pieSize, pieSize,
                startAngle, extent, Arc2D.PIE
            );

            // Neon glow for hovered slice
            if (idx == hoveredSlice) {
                for (int gp = 4; gp >= 1; gp--) {
                    double ex = gp * 3.5;
                    g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 28));
                    Arc2D gArc = new Arc2D.Double(
                        pieX + ox - ex, pieY + oy - ex, pieSize + ex * 2, pieSize + ex * 2,
                        startAngle, extent, Arc2D.PIE
                    );
                    g2.fill(gArc);
                }
            }

            // Slice fill
            g2.setColor(fill);
            g2.fill(arc);

            // Glassy sheen on top half
            GradientPaint sheen = new GradientPaint(
                (float)(pieX + ox),              (float)(pieY + oy),
                new Color(255, 255, 255, idx == hoveredSlice ? 60 : 35),
                (float)(pieX + ox + pieSize/2f), (float)(pieY + oy + pieSize/2f),
                new Color(255, 255, 255, 0)
            );
            g2.setPaint(sheen);
            g2.fill(arc);

            // Neon stroke border
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(),
                                  idx == hoveredSlice ? 255 : 180));
            g2.setStroke(new BasicStroke(idx == hoveredSlice ? 2.5f : 1.5f));
            g2.draw(arc);

            sliceAngles.add(new double[]{startAngle, extent, pieX + ox, pieY + oy});
            startAngle += extent;
            idx++;
        }

        // â”€â”€ Legend â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        int lx = pieX + pieSize + 20;
        int ly = pieY + 10;
        idx = 0;
        for (String key : keys) {
            double val   = data.get(key);
            double pct   = (val / total) * 100.0;
            Color  base  = sliceColors.get(idx % sliceColors.size());
            boolean hov  = idx == hoveredSlice;

            // Colored glassy square
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), hov ? 200 : 140));
            g2.fillRoundRect(lx, ly, 14, 14, 4, 4);
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 220));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(lx, ly, 14, 14, 4, 4);

            // Label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(hov ? Color.WHITE : new Color(200, 200, 230, 200));
            g2.drawString(key, lx + 20, ly + 11);
            // Percentage
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(),
                                  hov ? 255 : 200));
            g2.drawString(String.format("%.1f%%", pct), lx + 20, ly + 24);

            ly += 38;
            idx++;
        }

        // â”€â”€ Tooltip â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (hoveredSlice >= 0 && hoveredSlice < keys.size()) {
            String key = keys.get(hoveredSlice);
            double val = data.get(key);
            double pct = (val / total) * 100.0;
            String tip = key + " â€” " + String.format("%.1f%%", pct) + " (" + String.format("%.0f", val) + ")";
            drawTooltip(g2, tip, mousePos.x + 14, mousePos.y - 10, w, h,
                        sliceColors.get(hoveredSlice % sliceColors.size()));
        }

        g2.dispose();
    }

    // â”€â”€ Hit testing â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private int hitTest(Point p) {
        if (sliceAngles.isEmpty()) return -1;
        int w = getWidth(), h = getHeight();
        int legendW = 140;
        int pieSize = Math.max(Math.min(w - legendW - 40, h - 60), 100);
        for (int i = 0; i < sliceAngles.size(); i++) {
            double[] sa = sliceAngles.get(i);
            Arc2D arc = new Arc2D.Double(sa[2], sa[3], pieSize, pieSize, sa[0], sa[1], Arc2D.PIE);
            if (arc.contains(p)) return i;
        }
        return -1;
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void paintGlassBackground(Graphics2D g2, int w, int h) {
        for (int i = 6; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 14 * i));
            g2.fillRoundRect(i, i + 2, w - i * 2, h - i * 2, 26, 26);
        }
        g2.setPaint(new GradientPaint(0, 0, new Color(14, 14, 48, 235), w, h, new Color(30, 18, 80, 220)));
        g2.fillRoundRect(0, 0, w - 1, h - 1, 24, 24);
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 16), 0, h/3, new Color(255, 255, 255, 4)));
        g2.fillRoundRect(0, 0, w - 1, h / 3, 24, 24);
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 24, 24);
    }

    private void drawTooltip(Graphics2D g2, String text, int tx, int ty,
                             int panelW, int panelH, Color accent) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text) + 22, th = fm.getHeight() + 14;
        if (tx + tw > panelW - 6) tx = panelW - tw - 6;
        if (ty + th > panelH - 6) ty = panelH - th - 6;
        if (tx < 6) tx = 6; if (ty < 6) ty = 6;

        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(tx + 2, ty + 2, tw, th, 10, 10);
        g2.setPaint(new GradientPaint(tx, ty, new Color(40, 30, 90, 230), tx, ty + th, new Color(20, 15, 60, 220)));
        g2.fillRoundRect(tx, ty, tw, th, 10, 10);
        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(tx, ty, tw, th, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString(text, tx + 11, ty + fm.getAscent() + 7);
    }

    private void drawEmptyState(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(180, 180, 220, 130));
        String msg = "No data available";
        g2.drawString(msg, (w - g2.getFontMetrics().stringWidth(msg)) / 2, h / 2);
    }

    private void maximizeChart() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chart View", true);
        dlg.setSize(900, 600);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        PieChartPanel big = new PieChartPanel(title);
        big.setData(data);
        dlg.add(big, BorderLayout.CENTER);
        dlg.getContentPane().setBackground(new Color(10, 10, 30));
        dlg.setVisible(true);
    }
}
