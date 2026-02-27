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
 * Glassy dark line chart with neon gradient fill, animated hover data-point
 * tooltip that follows the cursor precisely. Pure Java Swing / Graphics2D.
 */
public class LineChartPanel extends JPanel {

    private String title, xLabel, yLabel;
    private List<String> xValues  = new ArrayList<>();
    private List<Double>  yValues  = new ArrayList<>();
    private Color lineColor = UIConstants.PRIMARY_COLOR;

    private static final int PAD  = 62;
    private static final int BPAD = 68;
    private static final int TPAD = 38;

    // Hover state
    private int    hoveredPoint = -1;
    private Point  mousePos     = new Point(0, 0);
    private float  hoverAlpha   = 0f;
    private Timer  hoverTimer;

    // Point locations, computed during painting
    private final List<Point2D> pointLocations = new ArrayList<>();

    public LineChartPanel(String title, String xLabel, String yLabel) {
        this.title  = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        setOpaque(false);
        setPreferredSize(new Dimension(460, 360));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        hoverTimer = new Timer(16, e -> {
            boolean wantVisible = hoveredPoint >= 0;
            if (wantVisible) hoverAlpha = Math.min(1f, hoverAlpha + 0.1f);
            else             hoverAlpha = Math.max(0f, hoverAlpha - 0.1f);
            repaint();
            if (!wantVisible && hoverAlpha <= 0f) hoverTimer.stop();
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                int prev  = hoveredPoint;
                hoveredPoint = nearestPoint(mousePos);
                if (hoveredPoint != prev) { hoverTimer.start(); }
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                hoveredPoint = -1; mousePos = new Point(0, 0); hoverTimer.start(); repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !yValues.isEmpty()) maximizeChart();
            }
        });
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void setData(List<String> xv, List<Double> yv) {
        this.xValues = new ArrayList<>(xv);
        this.yValues = new ArrayList<>(yv);
        pointLocations.clear();
        repaint();
    }

    public void setLineColor(Color c) { this.lineColor = c; repaint(); }

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
        g2.drawString(title, (w - fmT.stringWidth(title)) / 2, 20);

        if (yValues.isEmpty()) { drawEmptyState(g2, w, h); g2.dispose(); return; }

        // Dimensions
        int chartW = w - 2 * PAD;
        int chartH = h - PAD - BPAD - TPAD;
        int originX = PAD, originY = PAD + TPAD + chartH;

        double minVal = yValues.stream().min(Double::compare).orElse(0.0);
        double maxVal = yValues.stream().max(Double::compare).orElse(100.0);
        double range  = maxVal - minVal;
        if (range == 0) range = 1;
        maxVal += range * 0.12; minVal -= range * 0.05;
        if (minVal < 0 && yValues.stream().allMatch(v -> v >= 0)) minVal = 0;
        range = maxVal - minVal;

        // Dashed grid
        float[] dash = {6f, 4f};
        g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
        g2.setColor(new Color(255, 255, 255, 25));
        for (int i = 0; i <= 5; i++) {
            int y = originY - chartH * i / 5;
            g2.drawLine(originX, y, originX + chartW, y);
        }
        // Y labels
        g2.setStroke(new BasicStroke(1));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(180, 180, 220, 180));
        for (int i = 0; i <= 5; i++) {
            int y = originY - chartH * i / 5;
            String lbl = String.format("%.0f", minVal + range * i / 5);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, originX - fm.stringWidth(lbl) - 6, y + 4);
        }

        // Axes
        g2.setColor(new Color(255, 255, 255, 55));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(originX, PAD + TPAD, originX, originY);
        g2.drawLine(originX, originY, originX + chartW, originY);

        // Build points
        pointLocations.clear();
        for (int i = 0; i < yValues.size(); i++) {
            double px = originX + (yValues.size() > 1
                        ? chartW * i / (double)(yValues.size() - 1)
                        : chartW / 2.0);
            double py = originY - ((yValues.get(i) - minVal) / range) * chartH;
            pointLocations.add(new Point2D.Double(px, py));
        }

        if (pointLocations.size() > 1) {
            // Gradient fill under line
            Path2D fill = buildPath(pointLocations);
            fill.lineTo(originX + chartW, originY);
            fill.lineTo(originX, originY);
            fill.closePath();
            g2.setPaint(new GradientPaint(
                0, PAD + TPAD,    new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 90),
                0, originY,       new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 8)
            ));
            g2.fill(fill);

            // Neon glow: draw line 3x with decreasing alpha+width
            for (int gp = 3; gp >= 1; gp--) {
                float sw = gp * 1.8f;
                g2.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(),
                                      gp == 1 ? 255 : 60 / gp));
                g2.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(buildPath(pointLocations));
            }

            // Data points
            for (int i = 0; i < pointLocations.size(); i++) {
                Point2D pt = pointLocations.get(i);
                boolean hov = (i == hoveredPoint);
                float   r   = hov ? 7f : 5f;

                // Outer glow
                if (hov) {
                    g2.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 60));
                    g2.fill(new Ellipse2D.Double(pt.getX() - 11, pt.getY() - 11, 22, 22));
                }
                // Dot
                g2.setColor(lineColor);
                g2.fill(new Ellipse2D.Double(pt.getX() - r, pt.getY() - r, r * 2, r * 2));
                g2.setColor(new Color(255, 255, 255, hov ? 240 : 200));
                float ir = hov ? 4f : 2.5f;
                g2.fill(new Ellipse2D.Double(pt.getX() - ir, pt.getY() - ir, ir * 2, ir * 2));
            }
        }

        // X labels
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(180, 180, 220, 180));
        AffineTransform origT = g2.getTransform();
        for (int i = 0; i < xValues.size(); i++) {
            if (i >= pointLocations.size()) break;
            double x = pointLocations.get(i).getX();
            g2.translate(x, originY + 14);
            g2.rotate(Math.toRadians(-30));
            g2.drawString(xValues.get(i), 0, 0);
            g2.setTransform(origT);
        }

        // Axis labels
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(new Color(180, 180, 220, 160));
        FontMetrics fmAx = g2.getFontMetrics();
        g2.drawString(xLabel, originX + chartW / 2 - fmAx.stringWidth(xLabel) / 2, h - 8);
        AffineTransform orig = g2.getTransform();
        g2.rotate(-Math.PI / 2);
        g2.drawString(yLabel, -(originY - chartH / 2 + fmAx.stringWidth(yLabel) / 2), 14);
        g2.setTransform(orig);

        // Tooltip
        if (hoveredPoint >= 0 && hoverAlpha > 0f && hoveredPoint < yValues.size()) {
            String xVal = hoveredPoint < xValues.size() ? xValues.get(hoveredPoint) : "";
            String tip  = xVal + ": " + String.format("%.2f", yValues.get(hoveredPoint));
            Point2D hp  = pointLocations.get(hoveredPoint);
            drawTooltip(g2, tip, (int)hp.getX() + 12, (int)hp.getY() - 30, w, h);
        }

        g2.dispose();
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private Path2D buildPath(List<Point2D> pts) {
        Path2D p = new Path2D.Double();
        p.moveTo(pts.get(0).getX(), pts.get(0).getY());
        for (int i = 1; i < pts.size(); i++) p.lineTo(pts.get(i).getX(), pts.get(i).getY());
        return p;
    }

    /** Returns index of nearest point within 30px of cursor, else -1. */
    private int nearestPoint(Point p) {
        int best = -1; double bestD = 30.0;
        for (int i = 0; i < pointLocations.size(); i++) {
            double d = p.distance(pointLocations.get(i));
            if (d < bestD) { bestD = d; best = i; }
        }
        return best;
    }

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

    private void drawTooltip(Graphics2D g2, String text, int tx, int ty, int pw, int ph) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text) + 22, th = fm.getHeight() + 14;
        if (tx + tw > pw - 6) tx = pw - tw - 6;
        if (ty + th > ph - 6) ty = ph - th - 6;
        if (tx < 6) tx = 6; if (ty < 6) ty = 6;

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, hoverAlpha)));
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(tx + 2, ty + 2, tw, th, 10, 10);
        g2.setPaint(new GradientPaint(tx, ty, new Color(40, 30, 90, 230), tx, ty + th, new Color(20, 15, 60, 220)));
        g2.fillRoundRect(tx, ty, tw, th, 10, 10);
        g2.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(tx, ty, tw, th, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString(text, tx + 11, ty + fm.getAscent() + 7);
        g2.setComposite(old);
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
        LineChartPanel big = new LineChartPanel(title, xLabel, yLabel);
        big.setData(xValues, yValues);
        big.setLineColor(lineColor);
        dlg.add(big, BorderLayout.CENTER);
        dlg.getContentPane().setBackground(new Color(10, 10, 30));
        dlg.setVisible(true);
    }
}
