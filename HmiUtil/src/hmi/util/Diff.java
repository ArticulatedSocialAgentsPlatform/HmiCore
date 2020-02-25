/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
