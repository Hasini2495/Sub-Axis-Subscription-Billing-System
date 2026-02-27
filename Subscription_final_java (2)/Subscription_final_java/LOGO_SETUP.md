# SubAxis Logo Setup Instructions

## Adding the Logo Image

The SubAxis application supports displaying a custom logo image in the header and sidebar.

### Steps to Add Logo:

1. **Download the logo image** (PNG format recommended)
   - Preferred size: 512x512 pixels or higher (will be automatically scaled)
   - Transparent background recommended
   - File format: PNG, JPG, or GIF

2. **Save the logo file** as one of the following:
   - `subaxis-logo.png` in the project root directory
   - `subaxis-logo.png` in the `resources/` folder
   - `logo.png` in the project root directory

3. **Restart the application** to see the logo

### File Locations (in order of priority):

```
Subscription_final_java/
├── resources/subaxis-logo.png  ← Recommended location
├── src/resources/subaxis-logo.png
├── subaxis-logo.png
└── logo.png
```

### Fallback Behavior:

If no logo image is found, the application will display a styled "S" letter in a gradient circle as a fallback.

### Console Message:

When starting the application, check the console for one of these messages:
- `✓ Logo loaded from: [path]` - Logo successfully loaded
- `ℹ Logo image not found - using text fallback` - No logo found, using fallback

### Image Requirements:

- **Format**: PNG (with transparency), JPG, or GIF
- **Recommended Size**: 512x512 pixels
- **Aspect Ratio**: 1:1 (square) recommended
- **File Name**: `subaxis-logo.png` or `logo.png`

---

## Example Logo Download

If you have the logo URL:
1. Open the URL in a browser
2. Right-click → Save Image As...
3. Save as `subaxis-logo.png` in the `resources/` folder
4. Restart the application

The logo will automatically scale to fit the display area while maintaining its aspect ratio.
