package ui.components;

import ui.utils.IconManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * Quick action button for dashboard.
 * Glass-morphism card with smooth animated hover glow.
 * Supports both emoji and circular image icons.
 */
public class QuickActionButton extends JPanel {

    private Color accentColor;
    private float hoverAlpha = 0f;   // 0.0 (rest) → 1.0 (fully hovered)
    private boolean hovered = false;
    private Timer hoverTimer;
    private JLabel iconLabel;

    private static final int ARC = 20;

    // Private constructor — glass panel, no opaque background
    private QuickActionButton(String title, Color accentColor) {
        this.accentColor = accentColor;

        setOpaque(false);
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(170, 130));
        setMaximumSize(new Dimension(170, 130));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(18, 18, 18, 18));
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, ARC, ARC);

        // drop shadow (deepens slightly on hover)
        for (int i = 4; i >= 1; i--) {
            int a = (int) ((22 - i * 4) * (1 + hoverAlpha * 0.6f));
            if (a <= 0) continue;
            g2.setColor(new Color(0, 0, 0, Math.min(255, a)));
            g2.fill(new RoundRectangle2D.Float(i, i * 2, w - i * 2, h - i, ARC, ARC));
        }

        // radial accent glow on hover
        if (hoverAlpha > 0.01f) {
            float radius = Math.max(w, h) * 0.85f;
            RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(w / 2f, h / 2f), radius,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), (int) (65 * hoverAlpha)),
                    new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 0)
                }
            );
            g2.setPaint(glow);
            g2.fill(s);
        }

        // glass fill (brightens on hover)
        g2.setClip(s);
        float fillAlpha = 0.10f + hoverAlpha * 0.10f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fillAlpha));
        g2.setColor(Color.WHITE);
        g2.fill(s);

        // top shimmer
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.5f, new Color(255, 255, 255, 0)));
        g2.fill(s);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2.setClip(null);

        // border: lerp white-translucent → accent-colored on hover
        int br = clamp((int) (255 * (1 - hoverAlpha) + accentColor.getRed()   * hoverAlpha));
        int bg = clamp((int) (255 * (1 - hoverAlpha) + accentColor.getGreen() * hoverAlpha));
        int bb = clamp((int) (255 * (1 - hoverAlpha) + accentColor.getBlue()  * hoverAlpha));
        int ba = (int) (40 + hoverAlpha * 130);
        g2.setColor(new Color(br, bg, bb, Math.min(255, ba)));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(s);

        g2.dispose();
    }

    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    // Factory method for IconManager vector icon
    public static QuickActionButton withIcon(String title, IconManager.IconType iconType, Color accentColor) {
        QuickActionButton button = new QuickActionButton(title, accentColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Render a large vector icon in accent color
        button.iconLabel = new JLabel(IconManager.createIcon(iconType, accentColor, 44));
        button.add(button.iconLabel, gbc);

        gbc.insets = new Insets(12, 0, 0, 0);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        button.add(titleLabel, gbc);

        button.setupHoverEffect();
        return button;
    }

    // Factory method for emoji icon
    public static QuickActionButton withEmoji(String title, String emoji, Color accentColor) {
        QuickActionButton button = new QuickActionButton(title, accentColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        button.iconLabel = new JLabel(emoji);
        button.iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        button.iconLabel.setForeground(accentColor);
        button.add(button.iconLabel, gbc);

        gbc.insets = new Insets(12, 0, 0, 0);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        button.add(titleLabel, gbc);

        button.setupHoverEffect();
        return button;
    }

    // Factory method for image icon
    public static QuickActionButton withImage(String title, String imagePath, Color accentColor) {
        QuickActionButton button = new QuickActionButton(title, accentColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        ImageIcon circularIcon = button.loadCircularImage(imagePath, 56);
        button.iconLabel = new JLabel(circularIcon);
        button.add(button.iconLabel, gbc);

        gbc.insets = new Insets(12, 0, 0, 0);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        button.add(titleLabel, gbc);

        button.setupHoverEffect();
        return button;
    }

    // Legacy constructor for backward compatibility
    public QuickActionButton(String title, String symbol, Color accentColor) {
        this.accentColor = accentColor;

        setOpaque(false);
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(170, 130));
        setMaximumSize(new Dimension(170, 130));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(18, 18, 18, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        if (symbol.endsWith(".png") || symbol.endsWith(".jpg") || symbol.endsWith("jpeg")) {
            ImageIcon circularIcon = loadCircularImage(symbol, 56);
            iconLabel = new JLabel(circularIcon);
        } else {
            iconLabel = new JLabel(symbol);
            iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            iconLabel.setForeground(accentColor);
        }
        add(iconLabel, gbc);

        gbc.insets = new Insets(12, 0, 0, 0);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, gbc);

        setupHoverEffect();
    }

    private ImageIcon loadCircularImage(String imagePath, int size) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            int hdSize = size * 2;
            BufferedImage hdImage = new BufferedImage(hdSize, hdSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = hdImage.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

            Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, hdSize, hdSize);
            g2d.setClip(circle);

            Image scaledImage = originalImage.getScaledInstance(hdSize, hdSize, Image.SCALE_SMOOTH);
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.setClip(null);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            GradientPaint innerShadow = new GradientPaint(0, 0, new Color(0, 0, 0, 60), 0, hdSize * 0.3f, new Color(0, 0, 0, 0));
            g2d.setPaint(innerShadow);
            g2d.fill(circle);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            GradientPaint glassHighlight = new GradientPaint(0, 0, new Color(255, 255, 255, 180), 0, hdSize * 0.5f, new Color(255, 255, 255, 0));
            g2d.setPaint(glassHighlight);
            g2d.fill(new Ellipse2D.Double(hdSize * 0.1, hdSize * 0.05, hdSize * 0.8, hdSize * 0.4));

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 120), 0, hdSize, accentColor.brighter()));
            g2d.draw(circle);
            g2d.dispose();

            Image finalImage = hdImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = result.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(finalImage, 0, 0, null);
            g.dispose();
            return new ImageIcon(result);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath);
            e.printStackTrace();
            return createFallbackIcon(size);
        }
    }

    private ImageIcon createFallbackIcon(int size) {
        BufferedImage fallbackImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallbackImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(new GradientPaint(0, 0, accentColor.brighter(), size, size, accentColor.darker()));
        g2d.fillOval(0, 0, size, size);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 200), 0, size * 0.5f, new Color(255, 255, 255, 0)));
        g2d.fillOval((int)(size * 0.1), (int)(size * 0.05), (int)(size * 0.8), (int)(size * 0.4));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(accentColor.brighter());
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(1, 1, size - 3, size - 3);
        g2d.dispose();
        return new ImageIcon(fallbackImage);
    }

    private void setupHoverEffect() {
        hoverTimer = new Timer(16, e -> {
            float target = hovered ? 1f : 0f;
            float step = 0.07f;
            if (Math.abs(hoverAlpha - target) <= step) {
                hoverAlpha = target;
                ((Timer) e.getSource()).stop();
            } else {
                hoverAlpha += hovered ? step : -step;
            }
            // Repaint self AND parent so the transparent glass composites correctly
            repaint();
            Container p = getParent();
            if (p != null) p.repaint(getX(), getY(), getWidth(), getHeight());
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                hoverTimer.restart();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                hoverTimer.restart();
            }
        });
    }

    public void addClickListener(ActionListener listener) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.actionPerformed(new ActionEvent(QuickActionButton.this,
                    ActionEvent.ACTION_PERFORMED, "click"));
            }
        });
    }
}
