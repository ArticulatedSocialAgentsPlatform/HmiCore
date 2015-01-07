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

import java.util.List;

/**
 * Some utils for testing equality and showing differences between Objects
 * 
 * @author Job Zwiers
 */
public final class Diff
{

    /**
     * An interface for objects that can be compared via a showDiff operation
     */
    public interface Differentiable
    {

        /**
         * Returns "" if obj is considered equal to this Object, else, show the difference, precede by the msg String
         */
        String showDiff(Object obj);
    }

    /*
     * Disable StringUtil Object creation
     */
    private Diff()
    {
    }

    /**
     * Show difference between two booleans
     */
    public static String showDiff(String msg, boolean a, boolean b)
    {
        if (a == b)
            return "";
        return msg + " diff : " + a + " / " + b;
    }

    /**
     * Show difference between two ints
     */
    public static String showDiff(String msg, int a, int b)
    {
        if (a == b)
            return "";
        return msg + " diff: " + a + " / " + b;
    }

    /**
     * Show difference between two floats
     */
    public static String showDiff(String msg, float a, float b)
    {
        if (a == b)
            return "";
        return msg + " diff: " + a + " / " + b;
    }

    /**
     * Show difference between two doubles
     */
    public static String showDiff(String msg, double a, double b)
    {
        if (a == b)
            return "";
        return msg + " diff: " + a + " / " + b;
    }

    /**
     * Show difference between two Strings Note there is also a more detailed StringUtil.showDiff for Strings
     */
    public static String showDiff(String msg, String a, String b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        else if (a.equals(b))
            return "";
        return msg + " diff: \"" + a + "\" / \"" + b + "\"";
    }

    /**
     * Show difference between two int arrays
     */
    public static String showDiff(String msg, int[] a, int[] b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        else if (a.length != b.length)
            return (msg + ": diff lengths: " + a.length + " / " + b.length);
        for (int i = 0; i < a.length; i++)
        {
            if (a[i] != b[i])
                return (msg + ": diff at Array pos " + i + ", values: " + a[i] + " / " + b[i]);
        }
        return "";
    }

    /**
     * Show difference between two float arrays
     */
    public static String showDiff(String msg, float[] a, float[] b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        else if (a.length != b.length)
            return (msg + ": diff lengths: " + a.length + " / " + b.length);
        for (int i = 0; i < a.length; i++)
        {
            if (a[i] != b[i])
                return (msg + ": diff at Array pos " + i + ", values: " + a[i] + " / " + b[i]);
        }
        return "";
    }

    /**
     * Show difference between two float arrays-arrays.
     */
    public static String showDiff2(String msg, float[][] a, float[][] b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        else if (a.length != b.length)//findbugs complains, but this is not a bug
            return (msg + ": diff lengths: " + a.length + " / " + b.length);
        for (int i = 0; i < a.length; i++)
        {
            String diff = showDiff(msg + ": diff at (outer) Array pos " + i + "  ", a[i], b[i]);
            if (diff != "")
                return diff;
        }
        return "";
    }

    /**
     * Show difference between two String arrays
     */
    public static String showDiff(String msg, String[] a, String[] b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        else if (a.length != b.length)
            return (msg + ": diff lengths: " + a.length + " / " + b.length);
        for (int i = 0; i < a.length; i++)
        {
            if (!a[i].equals(b[i]))
                return (msg + ": diff at Array pos " + i + ", values: " + a[i] + " / " + b[i]);
        }
        return "";
    }

    /**
     * Show difference between two Diff.Differentiable Objects
     */
    public static <T extends Diff.Differentiable> String showDiff(String msg, T a, T b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        return a.showDiff(b);
    }

    /**
     * Show difference between two Lists-of-Differentiable elements
     */
    public static String showDiff(String msg, List<? extends Diff.Differentiable> a, List<? extends Diff.Differentiable> b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        else if (a.size() != b.size())
            return (msg + ": diff list sizes: " + a.size() + " / " + b.size());
        for (int i = 0; i < a.size(); i++)
        {
            String diff = a.get(i).showDiff(b.get(i));
            if (diff != "")
                return msg + ": diff at List pos " + i + " : " + diff;
        }
        return "";
    }

    /**
     * Show difference between two Lists-of-Lists-of-Differentiable elements
     */
    public static String showDiff2(String msg, List<? extends List<? extends Diff.Differentiable>> a,
            List<? extends List<? extends Diff.Differentiable>> b)
    {
        if (a == null && b == null)
            return "";
        else if (a == null)
            return msg + ": null / non-null";
        else if (b == null)
            return msg + ": non-null / null";
        else if (a.size() != b.size())
            return (msg + ": diff list sizes: " + a.size() + " / " + b.size());
        for (int i = 0; i < a.size(); i++)
        {
            String diff = showDiff(msg + ": diff at (outer) List pos " + i + "  ", a.get(i), b.get(i));
            if (diff != "")
                return diff;
        }
        return "";
    }

}
