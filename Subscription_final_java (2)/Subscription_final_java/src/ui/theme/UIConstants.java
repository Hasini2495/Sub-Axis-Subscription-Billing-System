package ui.theme;

import java.awt.*;

/**
 * Centralized UI constants for consistent theming.
 * Professional color scheme and styling values for SubAxis.
 */
public class UIConstants {
    
    // Brand Identity
    public static final String BRAND_NAME = "SubAxis";
    public static final String BRAND_TAGLINE = "Subscription Management Platform";
    
    // Color Palette — Premium Dark Glass Theme
    public static final Color PRIMARY_COLOR = new Color(123, 92, 255);     // Soft violet #7B5CFF
    public static final Color PRIMARY_DARK  = new Color(87,  58, 200);     // Deeper violet
    public static final Color PRIMARY_LIGHT = new Color(161, 137, 255);    // Light violet

    public static final Color ACCENT_COLOR = new Color(255, 140,  66);     // Warm orange #FF8C42
    public static final Color ACCENT_DARK  = new Color(255, 106,  42);     // Deep orange #FF6A2A

    public static final Color SUCCESS_COLOR = new Color(34,  197,  94);    // Green
    public static final Color WARNING_COLOR = new Color(255, 140,  66);    // Orange #FF8C42
    public static final Color DANGER_COLOR  = new Color(239,  68,  68);    // Red
    public static final Color INFO_COLOR    = new Color(123,  92, 255);    // Violet

    public static final Color BACKGROUND_COLOR = new Color(26,  27,  58);  // Deep indigo #1A1B3A
    public static final Color CARD_BACKGROUND  = new Color(43,  31,  94);  // Mid-indigo #2B1F5E
    public static final Color SIDEBAR_COLOR    = new Color(18,  18,  45);  // Darkest indigo
    public static final Color SIDEBAR_HOVER    = new Color(43,  31,  94);  // Mid indigo hover

    public static final Color TEXT_PRIMARY   = new Color(255, 255, 255);   // White #FFFFFF
    public static final Color TEXT_SECONDARY = new Color(207, 207, 234);   // Soft lavender #CFCFEA
    public static final Color TEXT_LIGHT     = Color.WHITE;
    
    public static final Color BORDER_COLOR = new Color(226, 232, 240);     // Light Border
    public static final Color BORDER_FOCUS = PRIMARY_COLOR;
    
    // Status Colors
    public static final Color STATUS_ACTIVE = new Color(34, 197, 94);      // Green
    public static final Color STATUS_EXPIRED = new Color(161, 161, 170);   // Gray
    public static final Color STATUS_UNPAID = new Color(251, 146, 60);     // Orange
    public static final Color STATUS_CANCELLED = new Color(239, 68, 68);   // Red
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    
    // Dimensions
    public static final int BUTTON_HEIGHT = 40;
    public static final int BUTTON_WIDTH = 140;
    public static final int BUTTON_RADIUS = 8;
    
    public static final int CARD_PADDING = 20;
    public static final int FORM_PADDING = 30;
    public static final int COMPONENT_SPACING = 10;
    
    public static final Dimension BUTTON_SIZE = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
    public static final Dimension LARGE_BUTTON_SIZE = new Dimension(180, 45);
    
    // Sidebar
    public static final int SIDEBAR_WIDTH = 260;
    public static final int HEADER_HEIGHT = 70;
    public static final int FOOTER_HEIGHT = 35;
    
    // Icons (Unicode)
    public static final String ICON_DASHBOARD = "📊";
    public static final String ICON_USERS = "👥";
    public static final String ICON_PLANS = "📋";
    public static final String ICON_SUBSCRIPTION = "🔖";
    public static final String ICON_INVOICE = "🧾";
    public static final String ICON_PAYMENT = "💳";
    public static final String ICON_ANALYTICS = "📈";
    public static final String ICON_SETTINGS = "⚙️";
    public static final String ICON_LOGOUT = "🚪";
    public static final String ICON_PROFILE = "👤";
    public static final String ICON_OFFERS = "🎁";
    public static final String ICON_REPORTS = "📄";
    public static final String ICON_NOTIFICATIONS = "🔔";
    
    private UIConstants() {
        // Private constructor to prevent instantiation
    }
}
