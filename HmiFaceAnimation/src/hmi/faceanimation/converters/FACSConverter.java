package hmi.faceanimation.converters;

import hmi.faceanimation.model.ActionUnit;
import hmi.faceanimation.model.FACS;
import hmi.faceanimation.model.FACS.Side;
import hmi.faceanimation.model.FACSConfiguration;
import hmi.faceanimation.model.FAP;
import hmi.faceanimation.model.MPEG4;
import hmi.faceanimation.model.MPEG4Configuration;
import hmi.util.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Convert Action Units from Ekmans Facial ACtion Coding System to MPEG4 configurations.
 * 
 * Conversion based on the data from ... //XXX: complete this
 * 
 * @author reidsma
 * @author paulrc
 */
@Slf4j
public class FACSConverter
{
    private static class Influence
    {
        public Influence(ActionUnit au, Side side, FAP fap, float auBegin, float auEnd, int fapBegin, int fapEnd)
        {
            this.au = au;
            this.side = side;
            this.fap = fap;
            this.auBegin = auBegin;
            this.auEnd = auEnd;
            this.fapBegin = fapBegin;
            this.fapEnd = fapEnd;
        }

        public ActionUnit au;
        public Side side;
        @SuppressWarnings("unused")
        public FAP fap;
        public float auBegin;
        public float auEnd;
        public int fapBegin;
        public int fapEnd;
    }

    HashMap<FAP, ArrayList<Influence>> influencesByFAP = new HashMap<FAP, ArrayList<Influence>>();
    HashMap<ActionUnit, ArrayList<Influence>> influencesByAU = new HashMap<ActionUnit, ArrayList<Influence>>();

    private ArrayList<FAP> fapsEverUsed = new ArrayList<FAP>(); // Administration that enables setting FAPs to 0 only when needed (when they were in
                                                                // use).

    public FACSConverter()
    {
        this(new Resources(""), "Humanoids/shared/mpeg4face/facs_to_mpeg.txt");
    }

    public FACSConverter(Resources r, String filename)
    {

        // We're going to read influences from a file.
        try
        {
            BufferedReader br = r.getReader(filename);
            String line;

            ActionUnit au = null;
            Side side = Side.NONE;
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("#") || line.equals("")) continue;
                String[] elts = line.split("\t");
                if (elts.length == 1)
                {
                    // We got a line with an AU-index, single sided.
                    side = Side.NONE;
                    au = FACS.getActionUnit(Integer.parseInt(elts[0]));
                }
                if (elts.length == 2)
                {
                    // We got a line with an AU-index, double sided.
                    au = FACS.getActionUnit(Integer.parseInt(elts[0]));
                    if (elts[1].equals("L")) side = Side.LEFT;
                    else if (elts[1].equals("R")) side = Side.RIGHT;
                }
                else if (elts.length == 5)
                {
                    // We got a line with an influence.
                    if (au == null) continue;

                    FAP fap = MPEG4.getFAP(Integer.parseInt(elts[0]));
                    float auBegin = Float.parseFloat(elts[1]);
                    float auEnd = Float.parseFloat(elts[2]);
                    int fapBegin = Integer.parseInt(elts[3]);
                    int fapEnd = Integer.parseInt(elts[4]);

                    Influence influence = new Influence(au, side, fap, auBegin, auEnd, fapBegin, fapEnd);

                    if (!influencesByFAP.containsKey(fap)) influencesByFAP.put(fap, new ArrayList<Influence>());
                    influencesByFAP.get(fap).add(influence);

                    if (!influencesByAU.containsKey(au)) influencesByAU.put(au, new ArrayList<Influence>());
                    influencesByAU.get(au).add(influence);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getNumberOfInfluences(ActionUnit au)
    {
        if (influencesByAU.containsKey(au)) return influencesByAU.get(au).size();
        else return 0;
    }

    public void convert(FACSConfiguration facsConfig, MPEG4Configuration mpeg4Config)
    {
        for (FAP key : influencesByFAP.keySet())
        {
            ArrayList<Influence> influences = influencesByFAP.get(key);
            int numInfluences = influences.size();
            if (numInfluences == 0) continue;
            float[] targetValues = new float[numInfluences];
            int i = 0;
            for (Influence influence : influences)
            {
                int auIndex = influence.au.getIndex();
                Float auValue = facsConfig.getValue(influence.side, auIndex);
                if (auValue == null) auValue = 0.0f;
                float portion = (auValue - influence.auBegin) / (influence.auEnd - influence.auBegin);
                if (portion < 0 || portion > 1)
                {
                    /*
                    log.warn("Invalid influence portion {} for AU {}, auValue={} auBegin={} auEnd={}",
                            new Object[]{portion, influence.au.getNumber(), auValue, influence.auBegin, influence.auEnd});
                    */
                    numInfluences--;
                    continue;
                }
                int fapValue = (int) (influence.fapBegin + portion * (influence.fapEnd - influence.fapBegin));
                if (fapValue == 0) numInfluences--;
                targetValues[i++] = fapValue;
            }

            int total = 0;
            for (int j = 0; j < targetValues.length; j++)
            {
                total += targetValues[j];
            }

            Integer targetValue = (numInfluences == 0 ? 0 : total / numInfluences);
            if (targetValue != 0)
            {
                if (!fapsEverUsed.contains(key)) fapsEverUsed.add(key);
            }
            else if (fapsEverUsed.contains(key))
            {
                fapsEverUsed.remove(key);
            }
            else targetValue = null;
            mpeg4Config.setValue(key.getIndex(), targetValue);
        }
    }
}
