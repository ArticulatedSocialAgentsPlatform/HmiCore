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
