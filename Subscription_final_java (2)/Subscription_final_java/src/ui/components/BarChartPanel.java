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
 * Glassy dark bar chart with neon gradient bars, animated hover tooltip,
 * and dashed grid lines. Pure Java Swing / Graphics2D â€” no third-party libs.
 */
public class BarChartPanel extends JPanel {

    private String title, xLabel, yLabel;
    private List<String> categories = new ArrayList<>();
    private List<Double>  values     = new ArrayList<>();
    private List<Color>   barColors  = new ArrayList<>();

    private static final int PAD    = 60;
    private static final int BPAD   = 70;
    private static final int TPAD   = 40;

    // Hover state
    private int    hoveredBar  = -1;
    private Point  mousePos    = new Point(0, 0);
    private float  hoverAlpha  = 0f;
    private Timer  hoverTimer;

    // Stored bar rects for hit-testing (filled in paintComponent)
    private final List<Rectangle2D> barRects = new ArrayList<>();

    public BarChartPanel(String title, String xLabel, String yLabel) {
        this.title  = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        setOpaque(false);
        setPreferredSize(new Dimension(460, 360));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        hoverTimer = new Timer(16, e -> {
            boolean wantVisible = hoveredBar >= 0;
            if (wantVisible) hoverAlpha = Math.min(1f, hoverAlpha + 0.1f);
            else             hoverAlpha = Math.max(0f, hoverAlpha - 0.1f);
            repaint();
            if (!wantVisible && hoverAlpha <= 0f) hoverTimer.stop();
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                int prev = hoveredBar;
                hoveredBar = -1;
                for (int i = 0; i < barRects.size(); i++) {
                    if (barRects.get(i).contains(mousePos)) { hoveredBar = i; break; }
                }
                if (hoveredBar != prev) { hoverTimer.start(); repaint(); }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                hoveredBar = -1; mousePos = new Point(0, 0); hoverTimer.start(); repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !values.isEmpty()) maximizeChart();
            }
        });
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void setData(List<String> categories, List<Double> values, List<Color> colors) {
        this.categories = new ArrayList<>(categories);
        this.values      = new ArrayList<>(values);
        this.barColors   = new ArrayList<>(colors);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // â”€â”€ Glassy dark background â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        paintGlassBackground(g2, w, h);

        // â”€â”€ Title â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        FontMetrics fmT = g2.getFontMetrics();
        g2.drawString(title, (w - fmT.stringWidth(title)) / 2, 22);

        if (values.isEmpty()) { drawEmptyState(g2, w, h); g2.dispose(); return; }

        // â”€â”€ Chart dimensions â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        int chartW = w - 2 * PAD;
        int chartH = h - PAD - BPAD - TPAD;
        int originX = PAD, originY = PAD + TPAD + chartH;

        double maxVal = values.stream().map(Math::abs).max(Double::compare).orElse(100.0);
        if (maxVal == 0) maxVal = 100;
        maxVal *= 1.15;

        // â”€â”€ Dashed grid lines â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        float[] dash = {6f, 4f};
        g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
        g2.setColor(new Color(255, 255, 255, 28));
        for (int i = 0; i <= 5; i++) {
            int y = originY - chartH * i / 5;
            g2.drawLine(originX, y, originX + chartW, y);
        }
        // Y-axis labels
        g2.setStroke(new BasicStroke(1));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(180, 180, 220, 180));
        for (int i = 0; i <= 5; i++) {
            int y = originY - chartH * i / 5;
            String lbl = String.format("%.0f", maxVal * i / 5);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, originX - fm.stringWidth(lbl) - 6, y + 4);
        }

        // â”€â”€ Axes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(originX, PAD + TPAD, originX, originY);
        g2.drawLine(originX, originY,    originX + chartW, originY);

        // â”€â”€ Bars â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        barRects.clear();
        int n = categories.size();
        int barW  = Math.min(70, chartW / (n * 2 + 1));
        int space = (chartW - barW * n) / (n + 1);

        for (int i = 0; i < n; i++) {
            double val = values.get(i);
            int bH  = (int)((Math.abs(val) / maxVal) * chartH);
            int bX  = originX + space + i * (barW + space);
            int bY  = originY - bH;

            boolean isHov = (i == hoveredBar);
            Color base = i < barColors.size() ? barColors.get(i) : UIConstants.PRIMARY_COLOR;
            Color bright = brighten(base, isHov ? 0.45f : 0f);

            // Store rect for hit-testing (slightly wider)
            barRects.add(new Rectangle2D.Double(bX - 4, bY - 4, barW + 8, bH + 8));

            // Neon glow behind hovered bar
            if (isHov) {
                for (int gp = 4; gp >= 1; gp--) {
                    int ex = gp * 3;
                    g2.setColor(new Color(bright.getRed(), bright.getGreen(), bright.getBlue(),
                                          (int)(hoverAlpha * 35)));
                    g2.fillRoundRect(bX - ex, bY - ex, barW + ex * 2, bH + ex * 2, 14, 14);
                }
            }

            // Main bar gradient
            GradientPaint barGrad = new GradientPaint(
                bX, bY,      bright,
                bX, bY + bH, new Color(bright.getRed(), bright.getGreen(), bright.getBlue(), 130)
            );
            g2.setPaint(barGrad);
            g2.fill(new RoundRectangle2D.Double(bX, bY, barW, bH, 10, 10));

            // Glass sheen (left half, white overlay)
            GradientPaint sheen = new GradientPaint(
                bX,           bY, new Color(255, 255, 255, isHov ? 70 : 45),
                bX + barW/2f, bY, new Color(255, 255, 255, 0)
            );
            g2.setPaint(sheen);
            g2.fill(new RoundRectangle2D.Double(bX, bY, barW / 2.0, bH, 10, 10));

            // Top accent line
            g2.setColor(new Color(bright.getRed(), bright.getGreen(), bright.getBlue(),
                                  isHov ? 255 : 200));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(bX + 6, bY + 1, bX + barW - 6, bY + 1);

            // Category label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(200, 200, 230, 200));
            FontMetrics fmL = g2.getFontMetrics();
            g2.drawString(categories.get(i),
                bX + (barW - fmL.stringWidth(categories.get(i))) / 2,
                originY + 18);
        }

        // â”€â”€ Axis labels â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(new Color(180, 180, 220, 160));
        FontMetrics fmAx = g2.getFontMetrics();
        // X label
        g2.drawString(xLabel, originX + chartW / 2 - fmAx.stringWidth(xLabel) / 2, h - 8);
        // Y label (rotated)
        AffineTransform orig = g2.getTransform();
        g2.rotate(-Math.PI / 2);
        g2.drawString(yLabel, -(originY - chartH / 2 + fmAx.stringWidth(yLabel) / 2), 14);
        g2.setTransform(orig);

        // â”€â”€ Hover tooltip â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (hoveredBar >= 0 && hoverAlpha > 0f) {
            String tip = categories.get(hoveredBar) + ": " +
                         String.format("%.2f", values.get(hoveredBar));
            drawTooltip(g2, tip, mousePos.x + 12, mousePos.y - 10, w, h);
        }

        g2.dispose();
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void paintGlassBackground(Graphics2D g2, int w, int h) {
        // Drop shadow
        for (int i = 6; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 14 * i));
            g2.fillRoundRect(i, i + 2, w - i * 2, h - i * 2, 26, 26);
        }
        // Dark glass body
        g2.setPaint(new GradientPaint(0, 0, new Color(14, 14, 48, 235), w, h, new Color(30, 18, 80, 220)));
        g2.fillRoundRect(0, 0, w - 1, h - 1, 24, 24);
        // Glass sheen
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 16), 0, h/3, new Color(255, 255, 255, 4)));
        g2.fillRoundRect(0, 0, w - 1, h / 3, 24, 24);
        // Border
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 24, 24);
    }

    private void drawTooltip(Graphics2D g2, String text, int tx, int ty, int panelW, int panelH) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text) + 22;
        int th = fm.getHeight() + 14;
        // Keep inside bounds
        if (tx + tw > panelW - 6) tx = panelW - tw - 6;
        if (ty + th > panelH - 6) ty = panelH - th - 6;
        if (tx < 6) tx = 6;
        if (ty < 6) ty = 6;

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, hoverAlpha)));

        // Shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(tx + 2, ty + 2, tw, th, 10, 10);
        // Glass body
        g2.setPaint(new GradientPaint(tx, ty, new Color(40, 30, 90, 230), tx, ty + th, new Color(20, 15, 60, 220)));
        g2.fillRoundRect(tx, ty, tw, th, 10, 10);
        // Neon border
        g2.setColor(new Color(140, 120, 255, 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(tx, ty, tw, th, 10, 10);
        // Text
        g2.setColor(Color.WHITE);
        g2.drawString(text, tx + 11, ty + fm.getAscent() + 7);

        g2.setComposite(old);
    }

    private Color brighten(Color c, float factor) {
        if (factor <= 0) return c;
        int r = Math.min(255, (int)(c.getRed()   + (255 - c.getRed())   * factor));
        int gr= Math.min(255, (int)(c.getGreen() + (255 - c.getGreen()) * factor));
        int b = Math.min(255, (int)(c.getBlue()  + (255 - c.getBlue())  * factor));
        return new Color(r, gr, b, c.getAlpha());
    }

    private void drawEmptyState(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(180, 180, 220, 130));
        String msg = "No data available";
        g2.drawString(msg, (w - g2.getFontMetrics().stringWidth(msg)) / 2, h / 2);
    }

    private void maximizeChart() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chart View", true);
        dlg.setSize(1000, 600);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        BarChartPanel big = new BarChartPanel(title, xLabel, yLabel);
        big.setData(categories, values, barColors);
        dlg.add(big, BorderLayout.CENTER);
        dlg.getContentPane().setBackground(new Color(10, 10, 30));
        dlg.setVisible(true);
    }
}
