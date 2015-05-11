/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

package hmi.util;

import java.awt.*;
import java.awt.image.*;

import java.util.*;

/**
 * A utility class for dealing with screen setting, like setting the display to "fullscreen", setting the cursor, makeing the cursor invisible,
 * etcetera. The most usefull methods are : setFullScreen(Frame frame) setInvisibleCursor(Component c) setCursor(Component c, int cursorType)
 * restoreCursor(Component c) It is possible to set the display mode, dealing with refresh rate and screen resolution; with LCD monitors this is
 * usually not desirable, since refresh rates are fixed, and screen resolution has only one optimal setting.
 * 
 * @author Job Zwiers
 */
public final class Screen
{

    /**
     * constants that can be used as parameters for Screen methods like setFullScreen(), selectDisplayModes
     */
    public static final int CURRENT_VALUE = -1;
    public static final int HIGHCOLOR = 16;
    public static final int TRUECOLOR = 32;
    public static final int MAX = Integer.MAX_VALUE;
    public static final int MIN = 0;
    /**
     * value for selecting a default list of display modes
     */
    private static final int[] DEFAULTSCREENSIZELIST = new int[] { 640, 480, 800, 600, 1024, 768, 1280, 960, 1280, 1024 };
    /**
     * values for tuning the displayModeToLabel method
     */
    public static final int SHOWSCREENSIZE = 0;
    public static final int SHOWBITDEPTH = 1;
    public static final int SHOWREFRESHRATE = 2;
    public static final int SHOWFULLDISPLAYMODE = SHOWBITDEPTH | SHOWREFRESHRATE;

    /*
     * Disable Screen Object construction
     */
    private Screen()
    {
    }

    /**
     * returns the array of all available DisplayModes for the (default) screen device. Each DisplayMode specifies screen resolution, color bit depth,
     * and refresh rate.
     */
    public static DisplayMode[] getDisplayModes()
    {
        checkDefaultDevice();
        return defaultDevice.getDisplayModes();
    }

    /**
     * yields a Predicate that selects only DisplayModes with the specified bitDepth and refreshRate
     */
    public static Predicate<DisplayMode> displayModeFilter(final int minWidth, final int minHeight, final int maxWidth, final int maxHeight,
            final int minBitDepth, final int maxBitDepth, final int minRefreshRate, final int maxRefreshRate)
    {
        return new Predicate<DisplayMode>()
        {
            public boolean valid(DisplayMode m)
            {
                final int bd = m.getBitDepth();
                final int w = m.getWidth();
                final int h = m.getHeight();
                final int rf = m.getRefreshRate();
                return minBitDepth <= bd && bd <= maxBitDepth && minRefreshRate <= rf && rf <= maxRefreshRate && minWidth <= w && w <= maxWidth
                        && minHeight <= h && h <= maxHeight;
            }
        };
    }

    /**
     * returns the array of all available DisplayModes that satisfy the specified filter Predicate.
     */
    public static ArrayList<DisplayMode> selectFilteredDisplayModes(Predicate<DisplayMode> filter)
    {
        checkDefaultDevice();
        DisplayMode[] da = defaultDevice.getDisplayModes();
        ArrayList<DisplayMode> dl = new ArrayList<DisplayMode>(da.length);
        for (int i = 0; i < da.length; i++)
        {
            DisplayMode mode = da[i];
            if (filter.valid(mode))
            {
                dl.add(mode);
            }
        }
        return dl;
    }

    /**
     * returns a List with available DisplayModes, satisfying the specified constraints. Value can be specified as CURRENT_VALUE, in which case the
     * value for the current display mode is used. bit depth can also be specified as HIGHCOLOR (16 bit) or TRUECOLOR (32 bit).
     */
    public static ArrayList<DisplayMode> selectDisplayModeRange(int minWidth, int minHeight, int maxWidth, int maxHeight, int minBitDepth,
            int maxBitDepth, int minRefreshRate, int maxRefreshRate)
    {
        final int minbd = ((minBitDepth == CURRENT_VALUE) ? getBitDepth() : minBitDepth);
        final int maxbd = ((maxBitDepth == CURRENT_VALUE) ? getBitDepth() : maxBitDepth);
        final int minrf = ((minRefreshRate == CURRENT_VALUE) ? getRefreshRate() : minRefreshRate);
        final int maxrf = ((maxRefreshRate == CURRENT_VALUE) ? getRefreshRate() : maxRefreshRate);
        final int minw = ((minWidth == CURRENT_VALUE) ? getWidth() : minWidth);
        final int maxw = ((maxWidth == CURRENT_VALUE) ? getWidth() : maxWidth);
        final int minh = ((minHeight == CURRENT_VALUE) ? getHeight() : minHeight);
        final int maxh = ((maxHeight == CURRENT_VALUE) ? getHeight() : maxHeight);
        return selectFilteredDisplayModes(displayModeFilter(minw, minh, maxw, maxh, minbd, maxbd, minrf, maxrf));
    }

    /**
     * returns a List of DisplayModes that have TRUECOLOR bit depth (32 bit(), and have the same refresh rate as the current refresh rate. Moreover,
     * the width and height combination must be one of the combinations taken from DEFAULTSCREENSIZELIST
     */
    public static ArrayList<DisplayMode> selectDisplayModes()
    {
        return selectDisplayModes(DEFAULTSCREENSIZELIST, TRUECOLOR, TRUECOLOR, CURRENT_VALUE, CURRENT_VALUE);
    }

    /**
     * returns a List of DisplayModes that have TRUECOLOR bit depth (32 bit(), and have the same refresh rate as the current refresh rate. Moreover,
     * the width and height combination must be one of the combinations in the preferredScreenSizes array. For instance, new int[] { 640, 480, 800,
     * 600, 1024, 768 } would specify a list of popular screen sizes: 640 X 480, 800 X 600, 1024 X 768.
     */
    public static ArrayList<DisplayMode> selectDisplayModes(final int[] preferredScreenSizes)
    {
        return selectDisplayModes(preferredScreenSizes, TRUECOLOR, TRUECOLOR, CURRENT_VALUE, CURRENT_VALUE);
    }

    /**
     * returns a List of DisplayModes that have bit depth and refresh rate within the specified range. Moreover, the width and height combination must
     * be one of the combinations in the preferredScreenSizes array. For instance, new int[] { 640, 480, 800, 600, 1024, 768 } would specify a list of
     * popular screen sizes: 640 X 480, 800 X 600, 1024 X 768.
     */
    public static ArrayList<DisplayMode> selectDisplayModes(final int[] preferredScreenSizes, final int minBitDepth, final int maxBitDepth,
            final int minRefreshRate, final int maxRefreshRate)
    {
        final int minbd = ((minBitDepth == CURRENT_VALUE) ? getBitDepth() : minBitDepth);
        final int maxbd = ((maxBitDepth == CURRENT_VALUE) ? getBitDepth() : maxBitDepth);
        final int minrf = ((minRefreshRate == CURRENT_VALUE) ? getRefreshRate() : minRefreshRate);
        final int maxrf = ((maxRefreshRate == CURRENT_VALUE) ? getRefreshRate() : maxRefreshRate);
        Predicate<DisplayMode> selector = new Predicate<DisplayMode>()
        {
            public boolean valid(DisplayMode mode)
            {
                int rf = mode.getRefreshRate();
                int bd = mode.getBitDepth();
                int w = mode.getWidth();
                int h = mode.getHeight();
                if (bd < minbd || bd > maxbd)
                    return false;
                if (rf < minrf || rf > maxrf)
                    return false;
                for (int i = 0; i + 1 < preferredScreenSizes.length; i = i + 2)
                {
                    if (preferredScreenSizes[i] == w && preferredScreenSizes[i + 1] == h)
                        return true;
                }
                return false;
            }

        };
        return selectFilteredDisplayModes(selector);
    }

    /**
     * returns the current DisplayMode for the default screen device
     */
    public static DisplayMode getDisplayMode()
    {
        checkDefaultDevice();
        return defaultDevice.getDisplayMode();
    }

    /**
     * returns the current refresh rate for the default screen device
     */
    public static int getRefreshRate()
    {
        checkDefaultDevice();
        return defaultDevice.getDisplayMode().getRefreshRate();
    }

    /**
     * returns the current color bit depth for the default screen device
     */
    public static int getBitDepth()
    {
        checkDefaultDevice();
        return defaultDevice.getDisplayMode().getBitDepth();
    }

    /**
     * returns the current screen width for the default screen device
     */
    public static int getWidth()
    {
        checkDefaultDevice();
        return defaultDevice.getDisplayMode().getWidth();
    }

    /**
     * returns the current screen height for the default screen device
     */
    public static int getHeight()
    {
        checkDefaultDevice();
        return defaultDevice.getDisplayMode().getHeight();
    }

    /**
     * returns a String that describes the current DisplayMode
     */
    public static String getDisplayModeString()
    {
        checkDefaultDevice();
        return displayModeToString(defaultDevice.getDisplayMode());
    }

    /*
     * determines option name for a mode. This String contains full information, including bit depth and refresh rate
     */
    public static String displayModeToString(DisplayMode mode)
    {
        return mode.getWidth() + "X" + mode.getHeight() + "-" + mode.getBitDepth() + "-" + mode.getRefreshRate();
    }

    private static final int EXPECTEDLABELLENGTH = 16;
    private static final int MAXDIMENSION = 1000;

    /**
     * Detemines a String that represents a DisplayMode, in human readable form. Width and Depth are always shown in the form width X depth. BitDepth
     * and/or Refresh Rate are optionally appended, like for example: TrueColor (32 bit), 60 Hz
     * 
     */
    public static String displayModeToLabel(DisplayMode mode, int labelMode)
    {
        boolean showBitDepth = (labelMode & SHOWBITDEPTH) != 0;
        boolean showRefreshRate = (labelMode & SHOWREFRESHRATE) != 0;
        StringBuilder result = new StringBuilder(EXPECTEDLABELLENGTH);
        int w = mode.getWidth();
        result.append(w);
        if (w < MAXDIMENSION && (showBitDepth || showRefreshRate))
            result.append(' ');
        result.append(" X ");
        int h = mode.getHeight();
        if (h < MAXDIMENSION && (showBitDepth || showRefreshRate))
            result.append(' ');
        result.append(h);
        if (showBitDepth)
        {
            int bd = mode.getBitDepth();
            result.append("  ");
            if (bd == TRUECOLOR)
            {
                result.append("TrueColor (32 bit)");
            }
            else if (bd == HIGHCOLOR)
            {
                result.append("HighColor (16 bit)");
            }
            else
            {
                result.append('(');
                result.append(bd);
                result.append(" bit)");
            }
            // result.append(") ");
        }
        if (showRefreshRate)
        {
            result.append(",  ");
            result.append(mode.getRefreshRate());
            result.append(" Hz");
        }
        return result.toString();
    }

    /**
     * prints all available DisplayModes
     */
    public static void printDisplayModes()
    {
        DisplayMode[] modes = getDisplayModes();
        for (int i = 0; i < modes.length; i++)
        {
            DisplayMode mode = modes[i];
            hmi.util.Console.println("Displaymode[" + i + "]:\n" + mode.getWidth() + ", " + mode.getHeight() + ", " + mode.getBitDepth() + ", "
                    + mode.getRefreshRate());
        }
    }

    /**
     * selects an available DisplayMode that matches the specified characteristics as far as possible. If refreshRate is specified as
     * Screen.CURRENT_VALUE, the current refresh rate is assumed. The bitDepth should be Screen.HIGHCOLOR (16 bit), Screen.TRUECOLOR (32 bit) or
     * Screen.CURRENT_VALUE; (TRUECOLOR is almost always the preferred mode.) Similarly, width and height can be specified as Screen.CURRENT_VALUE.
     * For instance, selectDisplayMode(Screen.CURRENT_VALUE, Screen.CURRENT_VALUE, Screen.TRUECOLOR, Screen.CURRENT_VALUE) will ensure true color
     * mode, but will not change other display settings. If no perfect match is found, lower refresh rates(no higher rates) and different bitDepths
     * are tried, if possible. If no matching mode can be found at all, a null mode is returned.
     */
    public static DisplayMode selectBestDisplayMode(int width, int height, int bitDepth, int refreshRate)
    {
        // hmi.util.Console.println("Requested mode: " + width + " X " + height + "   " + bitDepth + "    " + refreshRate);
        checkDefaultDevice();
        DisplayMode currentMode = defaultDevice.getDisplayMode();
        int currentBitDepth = currentMode.getBitDepth();
        int currentRefreshRate = currentMode.getRefreshRate();
        int currentWidth = currentMode.getWidth();
        int currentHeight = currentMode.getHeight();

        if (width == CURRENT_VALUE)
            width = currentWidth;
        if (height == CURRENT_VALUE)
            height = currentHeight;

        if (bitDepth == CURRENT_VALUE)
            bitDepth = currentBitDepth;
        if (refreshRate == CURRENT_VALUE)
            refreshRate = currentRefreshRate;

        DisplayMode[] modes = defaultDevice.getDisplayModes();
        java.util.List<DisplayMode> selectedModes = new java.util.ArrayList<DisplayMode>();
        for (int i = 0; i < modes.length; i++)
        {
            DisplayMode m = modes[i];
            if (m.getWidth() == width && m.getHeight() == height)
            {
                selectedModes.add(m);
            }
        }
        if (selectedModes.isEmpty())
        { // no display modes with required width/height
            return null;
        }
        for (DisplayMode m : selectedModes)
        { // try to match both bitdepth and refreshrate
            if (m.getBitDepth() == bitDepth && m.getRefreshRate() == refreshRate)
            {
                return m;
            }
        }
        // no perfect match, try to use lower refresh rate
        for (DisplayMode m : selectedModes)
        {
            if (m.getBitDepth() == bitDepth && m.getRefreshRate() <= refreshRate)
            {
                return m;
            }
        }
        // still no match, any bit depth and equal or lower refresh rate
        for (DisplayMode m : selectedModes)
        {
            if (m.getRefreshRate() <= refreshRate)
            {
                return m;
            }
        }
        // still; no match: give up. (We never try higher refresh rates, even when they are available)
        return null;
    }

    /**
     * tries to set the screen to &quot;fullscreen&quot; mode. Under Windows XP this turns out to be &quot;maximized&quot;, although the
     * isFullScreenSupported() query returns true. Note that window decoration (i.e. the title/button bar at the top, and the borders around, should
     * be turned off seperately, by means of the setUndecorated(true) method for the applications's (J)Frame. The boolean result denotes whether full
     * screen mode has been set succesfully.
     */
    public static void setFullScreen(Frame frame)
    {
        setFullScreen(frame, null);
    }

    /**
     * tries to set the screen resolution. This requires that full screen mode has been set.
     */
    public static void setFullScreen(Frame frame, int width, int height)
    {
        setFullScreen(frame, selectBestDisplayMode(width, height, TRUECOLOR, CURRENT_VALUE));
    }

    /**
     * tries to set the screen resolution. This requires that full screen mode has been set.
     */
    public static void setFullScreen(Frame frame, int width, int height, int bitDepth)
    {
        setFullScreen(frame, selectBestDisplayMode(width, height, bitDepth, CURRENT_VALUE));
    }

    /**
     * tries to set the screen resolution. This requires that full screen mode has been set.
     */
    public static void setFullScreen(Frame frame, int width, int height, int bitDepth, int refreshRate)
    {
        setFullScreen(frame, selectBestDisplayMode(width, height, bitDepth, refreshRate));
    }

    /**
     * tries to set the screen resolution. This requires that full screen mode has been set.
     */
    public static void setFullScreen(Frame frame, DisplayMode mode)
    {
        checkDefaultDevice();
        try
        {
            if (defaultDevice.isFullScreenSupported())
            {
                // isFullScreenSupported() returns bogus result (true) under Windows XP, seems to be irrelevant anyway.
                defaultDevice.setFullScreenWindow(frame);
                if (frame != null)
                {
                    frame.enableInputMethods(false);
                }
            }
            else
            {
                hmi.util.Console.println("Screen: full screen mode not supported");
                return;
            }
        }
        catch (Exception e)
        {
            hmi.util.Console.println("hmi.util.Screen: " + e);
            return;
        }

        if (frame == null || mode == null)
            return;
        defaultDevice.setDisplayMode(mode);
        setFullScreen(null); // "trick" needed in combination with Jogl: causes "low level" repaint
        setFullScreen(frame); // if omitted, Jogl still sees the "old" screen size.
    }

    private static final int CURSORSIZE = 16;

    /**
     * Sets an &quot;invisble&quot; cursor for the specified Component. The current Cursor is saved, and can be restored later on by calling
     * restoreCursor(c). Note that the cursor is not really gone, but just invisible. Mouse events are still possible, and must be handled in an
     * appropriate way.
     */
    public static void setInvisibleCursor(Component c)
    {
        if (noCursor == null)
        {
            Dimension cdim = Toolkit.getDefaultToolkit().getBestCursorSize(CURSORSIZE, CURSORSIZE);// will be 32 X 32 on Windows,1);
            // hmi.util.Console.println("Best cursor size: " + cdim.width + ", " + cdim.height);
            Image img = new BufferedImage(cdim.width, cdim.height, BufferedImage.TYPE_INT_ARGB); // TYPE_INT_RGB yields a black square
            noCursor = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), "NoCursor");
        }
        prevCursor = c.getCursor();
        c.setCursor(noCursor);
    }

    /**
     * Sets a cursor for the specified Component. The specified cursor type must be one of the constants defined in java.awt.Cursor, like
     * CROSSHAIR_CURSOR, HAND_CURSOR etc. The current Cursor is saved, and can be restored later on by calling restoreCursor(c).
     */
    public static void setCursor(Component c, int cursorType)
    {
        Cursor curs = Cursor.getPredefinedCursor(cursorType);
        if (curs != null)
        {
            prevCursor = c.getCursor();
            c.setCursor(curs);
        }
        else
        {
            hmi.util.Console.println("Screen.setCursor: unknown cursor type");
        }
    }

    /**
     * Restores the Cursor that was saved before by setInvisibleCursor(c).
     */
    public static void restoreCursor(Component c)
    {
        if (prevCursor != null)
            c.setCursor(prevCursor);
    }

    /*
     * checks whether defaultDevice has been initialized, and if,not does initialize
     */
    private static void checkDefaultDevice()
    {
        if (defaultDevice == null)
        {
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            defaultDevice = genv.getDefaultScreenDevice();
        }
    }

    private static java.awt.GraphicsDevice defaultDevice;

    private static Cursor noCursor;
    private static Cursor prevCursor;

}
