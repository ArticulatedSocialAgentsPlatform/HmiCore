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
package hmi.faceanimation.model;

import hmi.faceanimation.model.ActionUnit.Symmetry;
import hmi.util.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A FACS-system
 * 
 * @author PaulRC
 */
public class FACS
{
    static public enum Side
    {
        NONE, LEFT, RIGHT
    }

    static private LinkedHashMap<Integer, ActionUnit> actionUnits;
    static private LinkedHashMap<Integer, ActionUnit> actionUnitsByIndex;

    /**
     * Static block for reading action units.
     */
    static
    {
        actionUnits = new LinkedHashMap<Integer, ActionUnit>();
        actionUnitsByIndex = new LinkedHashMap<Integer, ActionUnit>();

        // We're going to read the action units from a file.
        try
        {
            String filename = "action_units.txt";
            BufferedReader br = new Resources("Humanoids/shared/mpeg4face/").getReader(filename);
            String line;
            int index = 0;

            while ((line = br.readLine()) != null)
            {
                int number;
                Symmetry symmetry;
                String[] elts = line.split("\t");
                if (elts.length == 3)
                {
                    number = Integer.valueOf(elts[0]);
                    symmetry = (elts[1].equals("S") ? Symmetry.SYMMETRIC : Symmetry.ASYMMETRIC);
                    ActionUnit au = new ActionUnit(index, number, symmetry, elts[2]);
                    actionUnits.put(number, au);
                    actionUnitsByIndex.put(index, au);
                    index++;
                }
                else if (elts.length == 4)
                {
                    number = Integer.valueOf(elts[0]);
                    symmetry = (elts[1].equals("S") ? Symmetry.SYMMETRIC : Symmetry.ASYMMETRIC);
                    ActionUnit au = new ActionUnit(index, number, symmetry, elts[2], elts[3]);
                    actionUnits.put(number, au);
                    actionUnitsByIndex.put(index, au);
                    index++;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        StringBuffer retval = new StringBuffer();
        retval.append("[FACS, list of Action Units:\n");
        for (ActionUnit actionUnit : actionUnits.values())
        {
            retval.append("\t");
            retval.append(actionUnit.toString());
            retval.append("\n");
        }
        retval.append("]");
        return retval.toString();
    }

    static public HashMap<Integer, ActionUnit> getActionUnits()
    {
        return actionUnits;
    }

    static public HashMap<Integer, ActionUnit> getActionUnitsByIndex()
    {
        return actionUnitsByIndex;
    }

    static public ActionUnit getActionUnit(int number)
    {
        return actionUnits.get(number);
    }
}
