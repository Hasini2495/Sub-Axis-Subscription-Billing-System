package ui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * IconManager - Professional icon rendering system
 * Replaces placeholder boxes with scalable, high-quality icons
 */
public class IconManager {
    
    /**
     * Creates a professional icon with specified symbol and color
     */
    public static Icon createIcon(IconType type, Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.setColor(color);
                
                switch (type) {
                    case DASHBOARD:
                        drawDashboardIcon(g2d, x, y, size);
                        break;
                    case PLAN:
                        drawPlanIcon(g2d, x, y, size);
                        break;
                    case SUBSCRIPTION:
                        drawSubscriptionIcon(g2d, x, y, size);
                        break;
                    case PAYMENT:
                        drawPaymentIcon(g2d, x, y, size);
                        break;
                    case INVOICE:
                        drawInvoiceIcon(g2d, x, y, size);
                        break;
                    case OFFER:
                        drawOfferIcon(g2d, x, y, size);
                        break;
                    case SETTINGS:
                        drawSettingsIcon(g2d, x, y, size);
                        break;
                    case USER:
                        drawUserIcon(g2d, x, y, size);
                        break;
                    case CALENDAR:
                        drawCalendarIcon(g2d, x, y, size);
                        break;
                    case CHECK:
                        drawCheckIcon(g2d, x, y, size);
                        break;
                    case SYNC:
                        drawSyncIcon(g2d, x, y, size);
                        break;
                    case CREDIT_CARD:
                        drawCreditCardIcon(g2d, x, y, size);
                        break;
                    case DOLLAR:
                        drawDollarIcon(g2d, x, y, size);
                        break;
                    case BELL:
                        drawBellIcon(g2d, x, y, size);
                        break;
                    case ACTIVITY:
                        drawActivityIcon(g2d, x, y, size);
                        break;
                    case DOWNLOAD:
                        drawDownloadIcon(g2d, x, y, size);
                        break;
                    case COPY:
                        drawCopyIcon(g2d, x, y, size);
                        break;
                    case TRASH:
                        drawTrashIcon(g2d, x, y, size);
                        break;
                    case LOCK:
                        drawLockIcon(g2d, x, y, size);
                        break;
                    case EMAIL:
                        drawEmailIcon(g2d, x, y, size);
                        break;
                }
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return size;
            }
            
            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
    
    private static void drawDashboardIcon(Graphics2D g2d, int x, int y, int size) {
        int gridSize = size / 3;
        int gap = size / 12;
        
        g2d.fillRoundRect(x, y, gridSize - gap, gridSize - gap, 3, 3);
        g2d.fillRoundRect(x + gridSize + gap, y, gridSize - gap, gridSize - gap, 3, 3);
        g2d.fillRoundRect(x, y + gridSize + gap, gridSize - gap, gridSize - gap, 3, 3);
        g2d.fillRoundRect(x + gridSize + gap, y + gridSize + gap, gridSize - gap, gridSize - gap, 3, 3);
    }
    
    private static void drawPlanIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setStroke(new BasicStroke(size / 10f));
        g2d.drawRect(x + size / 6, y + size / 6, size * 2 / 3, size * 2 / 3);
        
        int lineY = y + size / 3;
        for (int i = 0; i < 3; i++) {
            g2d.drawLine(x + size / 4, lineY, x + size * 3 / 4, lineY);
            lineY += size / 6;
        }
    }
    
    private static void drawSubscriptionIcon(Graphics2D g2d, int x, int y, int size) {
        Path2D star = new Path2D.Float();
        for (int i = 0; i < 5; i++) {
            double angle = Math.PI / 2 + (2 * Math.PI * i) / 5;
            double xPoint = x + size / 2 + Math.cos(angle) * size / 2.5;
            double yPoint = y + size / 2 + Math.sin(angle) * size / 2.5;
            if (i == 0) {
                star.moveTo(xPoint, yPoint);
            } else {
                star.lineTo(xPoint, yPoint);
            }
        }
        star.closePath();
        g2d.fill(star);
    }
    
    private static void drawPaymentIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRoundRect(x + size / 8, y + size / 4, size * 3 / 4, size / 2, size / 8, size / 8);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + size / 8, y + size / 3, size * 3 / 4, size / 10);
    }
    
    private static void drawInvoiceIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRect(x + size / 5, y + size / 8, size * 3 / 5, size * 3 / 4);
        
        g2d.setColor(Color.WHITE);
        int lineY = y + size / 3;
        for (int i = 0; i < 4; i++) {
            g2d.fillRect(x + size / 3, lineY, size / 3, size / 20);
            lineY += size / 6;
        }
    }
    
    private static void drawOfferIcon(Graphics2D g2d, int x, int y, int size) {
        Polygon tag = new Polygon();
        tag.addPoint(x, y + size / 2);
        tag.addPoint(x + size / 2, y);
        tag.addPoint(x + size, y + size / 2);
        tag.addPoint(x + size / 2, y + size);
        g2d.fill(tag);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x + size * 2 / 5, y + size * 2 / 5, size / 5, size / 5);
    }
    
    private static void drawSettingsIcon(Graphics2D g2d, int x, int y, int size) {
        double cx = x + size / 2.0;
        double cy = y + size / 2.0;
        int teeth      = 8;
        double outerR  = size * 0.46;   // tip of tooth
        double innerR  = size * 0.33;   // valley between teeth
        double holeR   = size * 0.18;   // center hole radius
        double toothHalfAngle = Math.PI / teeth * 0.42; // how wide each tooth is

        // Build gear outline path
        GeneralPath gear = new GeneralPath();
        for (int i = 0; i < teeth; i++) {
            double baseAngle = 2 * Math.PI * i / teeth;
            // valley before tooth
            double a1 = baseAngle - Math.PI / teeth + toothHalfAngle;
            gear.moveTo(cx + innerR * Math.cos(a1), cy + innerR * Math.sin(a1));
            // rising edge of tooth
            double a2 = baseAngle - toothHalfAngle;
            gear.lineTo(cx + outerR * Math.cos(a2), cy + outerR * Math.sin(a2));
            // top of tooth
            double a3 = baseAngle + toothHalfAngle;
            gear.lineTo(cx + outerR * Math.cos(a3), cy + outerR * Math.sin(a3));
            // falling edge
            double a4 = baseAngle + Math.PI / teeth - toothHalfAngle;
            gear.lineTo(cx + innerR * Math.cos(a4), cy + innerR * Math.sin(a4));
        }
        gear.closePath();

        // Subtract center hole using Area
        Area gearArea = new Area(gear);
        Ellipse2D hole = new Ellipse2D.Double(cx - holeR, cy - holeR, holeR * 2, holeR * 2);
        gearArea.subtract(new Area(hole));

        g2d.fill(gearArea);
    }
    
    private static void drawUserIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillOval(x + size / 3, y + size / 5, size / 3, size / 3);
        g2d.fillOval(x + size / 6, y + size * 3 / 5, size * 2 / 3, size * 2 / 5);
    }
    
    private static void drawCalendarIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRect(x + size / 6, y + size / 4, size * 2 / 3, size * 2 / 3);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + size / 6, y + size / 3, size * 2 / 3, size / 20);
    }
    
    private static void drawCheckIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setStroke(new BasicStroke(size / 8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x + size / 4, y + size / 2, x + size * 2 / 5, y + size * 3 / 5);
        g2d.drawLine(x + size * 2 / 5, y + size * 3 / 5, x + size * 3 / 4, y + size / 4);
    }
    
    private static void drawSyncIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setStroke(new BasicStroke(size / 10f));
        g2d.drawArc(x + size / 6, y + size / 6, size * 2 / 3, size * 2 / 3, 45, 270);
        
        // Arrow
        Polygon arrow = new Polygon();
        arrow.addPoint(x + size * 3 / 4, y + size / 4);
        arrow.addPoint(x + size * 3 / 4 + size / 6, y + size / 4);
        arrow.addPoint(x + size * 3 / 4 + size / 12, y + size / 8);
        g2d.fill(arrow);
    }
    
    private static void drawCreditCardIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRoundRect(x + size / 10, y + size / 4, size * 4 / 5, size / 2, size / 12, size / 12);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + size / 10, y + size / 3, size * 4 / 5, size / 10);
    }
    
    private static void drawDollarIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setStroke(new BasicStroke(size / 10f));
        g2d.drawLine(x + size / 2, y + size / 6, x + size / 2, y + size * 5 / 6);
        
        Path2D s = new Path2D.Float();
        s.moveTo(x + size * 3 / 4, y + size / 3);
        s.curveTo(x + size * 3 / 4, y + size / 5, x + size / 4, y + size / 5, x + size / 4, y + size / 3);
        s.curveTo(x + size / 4, y + size / 2, x + size * 3 / 4, y + size / 2, x + size * 3 / 4, y + size * 2 / 3);
        s.curveTo(x + size * 3 / 4, y + size * 4 / 5, x + size / 4, y + size * 4 / 5, x + size / 4, y + size * 2 / 3);
        g2d.draw(s);
    }
    
    private static void drawBellIcon(Graphics2D g2d, int x, int y, int size) {
        Path2D bell = new Path2D.Float();
        bell.moveTo(x + size / 2, y + size / 4);
        bell.curveTo(x + size / 4, y + size / 4, x + size / 6, y + size / 2, x + size / 6, y + size * 2 / 3);
        bell.lineTo(x + size * 5 / 6, y + size * 2 / 3);
        bell.curveTo(x + size * 5 / 6, y + size / 2, x + size * 3 / 4, y + size / 4, x + size / 2, y + size / 4);
        g2d.fill(bell);
        
        g2d.fillRect(x + size / 6, y + size * 2 / 3, size * 2 / 3, size / 20);
    }
    
    private static void drawActivityIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setStroke(new BasicStroke(size / 12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D line = new Path2D.Float();
        line.moveTo(x, y + size / 2);
        line.lineTo(x + size / 4, y + size / 2);
        line.lineTo(x + size * 3 / 8, y + size / 4);
        line.lineTo(x + size * 5 / 8, y + size * 3 / 4);
        line.lineTo(x + size * 3 / 4, y + size / 2);
        line.lineTo(x + size, y + size / 2);
        g2d.draw(line);
    }
    
    private static void drawDownloadIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setStroke(new BasicStroke(size / 10f));
        g2d.drawLine(x + size / 2, y + size / 6, x + size / 2, y + size * 2 / 3);
        
        Polygon arrow = new Polygon();
        arrow.addPoint(x + size / 2, y + size * 3 / 4);
        arrow.addPoint(x + size / 3, y + size / 2);
        arrow.addPoint(x + size * 2 / 3, y + size / 2);
        g2d.fill(arrow);
        
        g2d.drawLine(x + size / 6, y + size * 5 / 6, x + size * 5 / 6, y + size * 5 / 6);
    }
    
    private static void drawCopyIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setStroke(new BasicStroke(size / 15f));
        g2d.drawRect(x + size / 4, y + size / 6, size / 2, size * 2 / 3);
        g2d.drawRect(x + size / 3, y + size / 4, size / 2, size * 2 / 3);
    }
    
    private static void drawTrashIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRect(x + size / 4, y + size / 3, size / 2, size / 2);
        g2d.fillRect(x + size / 6, y + size / 4, size * 2 / 3, size / 15);
        g2d.fillRect(x + size * 2 / 5, y + size / 6, size / 5, size / 10);
    }
    
    private static void drawLockIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRect(x + size / 4, y + size / 2, size / 2, size / 3);
        g2d.setStroke(new BasicStroke(size / 12f));
        g2d.drawArc(x + size / 3, y + size / 5, size / 3, size / 3, 0, 180);
    }
    
    private static void drawEmailIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRect(x + size / 8, y + size / 3, size * 3 / 4, size / 2);
        
        Polygon envelope = new Polygon();
        envelope.addPoint(x + size / 8, y + size / 3);
        envelope.addPoint(x + size / 2, y + size / 2);
        envelope.addPoint(x + size * 7 / 8, y + size / 3);
        g2d.fill(envelope);
    }
    
    /**
     * Icon types available
     */
    public enum IconType {
        DASHBOARD, PLAN, SUBSCRIPTION, PAYMENT, INVOICE, OFFER, SETTINGS,
        USER, CALENDAR, CHECK, SYNC, CREDIT_CARD, DOLLAR, BELL, ACTIVITY,
        DOWNLOAD, COPY, TRASH, LOCK, EMAIL
    }
}
