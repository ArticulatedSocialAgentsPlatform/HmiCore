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
package hmi.faceanimation.converters;

import hmi.faceanimation.model.FAP;
import hmi.faceanimation.model.MPEG4;
import hmi.faceanimation.model.MPEG4Configuration;
import hmi.util.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * convert emotions to MPEG4 configurations. Emotions are expressed as a position on Plutchiks emotion disc (angle / activation). The exact mapping is
 * configurable: Plutchik's disc contains eight archetypal emotions. Each archetypal emotion has an associated FAP profile associated with it, based
 * on the data modified from raouzaiou. A specific location on the disc is converted using these profiles.
 * 
 * @author paulrc
 * @author reidsma
 */
public class EmotionConverter
{
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(EmotionConverter.class.getName());

    public enum AE
    {
        NONE("[none]", -1, -1), ANGER("Anger", 225, 4.2f), SADNESS("Sadness", 135, 3.8f), JOY("Joy", 315, 5.4f), DISGUST("Disgust", 180,
                5.0f), FEAR("Fear", 45, 4.9f), SURPRISE("Surprise", 90, 6.5f);

        String friendlyName;
        float angle;
        float activation;

        private AE(String friendlyName, float angle, float activation)
        {
            this.friendlyName = friendlyName;
            this.angle = angle;
            this.activation = activation;
        }

        public String getFriendlyName()
        {
            return friendlyName;
        }
    };

    private static class Range
    {
        public int min;
        public int max;

        public String toString()
        {
            return "[" + min + ", " + max + "]";
        }

        public Range(int min, int max)
        {
            this.min = min;
            this.max = max;
        }

        public Range()
        {
        }
    }

    private static class Profile extends HashMap<FAP, Range>
    {
        private static final long serialVersionUID = 8370761547051241453L;
    }

    HashMap<AE, ArrayList<Profile>> profiles = new HashMap<AE, ArrayList<Profile>>();
    HashMap<AE, Integer> preferredProfiles = new HashMap<AE, Integer>();

    private ArrayList<FAP> fapsEverUsed = new ArrayList<FAP>(); // Administration that enables setting FAPs to 0 only when needed (when they were in
                                                                // use).

    public EmotionConverter()
    {
        this(new Resources(""), "Humanoids/shared/mpeg4face/raouzaiou_data_mods.txt");
    }

    public EmotionConverter(Resources r, String filename)
    {
        loadRanges(r, filename);

        for (AE ae : AE.values())
            setPreferredProfile(ae, 0);
    }

    public int getNumProfiles(AE ae)
    {
        if (!profiles.containsKey(ae)) return 0;
        return profiles.get(ae).size();
    }

    public void setPreferredProfile(AE ae, int preferredProfile)
    {
        preferredProfiles.put(ae, preferredProfile);
    }

    public int getPreferredProfile(AE ae)
    {
        return preferredProfiles.get(ae);
    }

    private enum Category
    {
        UNKNOWN, SIMILAR, IN_BETWEEN;
    }

    private float aeWidth = 22.5f;

    public MPEG4Configuration convert(float angle, float activation)
    {
        Profile profile;
        Category category = convertDetermineCategory(angle);
        if (category == Category.SIMILAR)
        {
            profile = convertSimilar(angle, activation);
        }
        else
        {
            profile = convertInBetween(angle, activation);
        }

        HashMap<Integer, FAP> faps = MPEG4.getFAPs();
        Integer[] values = new Integer[faps.size()];
        int i = 0;
        for (FAP fap : faps.values())
        {
            Integer value;
            if (profile.containsKey(fap))
            {
                int min = profile.get(fap).min;
                int max = profile.get(fap).max;

                int length = max - min;
                value = min + length / 2;

                if (!fapsEverUsed.contains(fap)) fapsEverUsed.add(fap);
            }
            else
            {
                // Reset the value if this FAP was set before.
                if (fapsEverUsed.contains(fap))
                {
                    value = 0;
                    fapsEverUsed.remove(fap);
                }
                else value = null;
            }

            values[i] = value;

            i++;
        }

        MPEG4Configuration config = new MPEG4Configuration();
        config.setValues(values);
        return config;
    }

    private AE primaryAE = AE.NONE;
    private AE secondaryAE = AE.NONE;

    private Category convertDetermineCategory(float angle)
    {
        Category category = Category.UNKNOWN;

        float aeSubwidth = aeWidth / 2;
        for (AE aev : AE.values())
        {
            if (aev == AE.NONE) continue;
            if (angle > aev.angle - aeSubwidth && angle < aev.angle + aeSubwidth)
            {
                category = Category.SIMILAR;
                primaryAE = aev;
                break;
            }
        }

        if (category == Category.UNKNOWN)
        {
            category = Category.IN_BETWEEN;

            // The point lies in between two archetypal emotions. We need to
            // find out between which.
            if (angle > AE.JOY.angle || angle < AE.FEAR.angle)
            {
                primaryAE = AE.JOY;
                secondaryAE = AE.FEAR;
            }
            else if (angle > AE.ANGER.angle)
            {
                primaryAE = AE.ANGER;
                secondaryAE = AE.JOY;
            }
            else if (angle > AE.DISGUST.angle)
            {
                primaryAE = AE.DISGUST;
                secondaryAE = AE.ANGER;
            }
            else if (angle > AE.SADNESS.angle)
            {
                primaryAE = AE.SADNESS;
                secondaryAE = AE.DISGUST;
            }
            else if (angle > AE.SURPRISE.angle)
            {
                primaryAE = AE.SURPRISE;
                secondaryAE = AE.SADNESS;
            }
            else if (angle > AE.FEAR.angle)
            {
                primaryAE = AE.FEAR;
                secondaryAE = AE.SURPRISE;
            }
        }

        // logger.debug("Angle: {}: ", new DecimalFormat("0.000").format(angle));
        if (category == Category.SIMILAR)
        {
            // logger.debug("In category {}", primaryAE);
        }
        else
        {
            // logger.debug("In between {} and {}.", primaryAE, secondaryAE );
        }

        return category;
    }

    private Profile convertSimilar(float angle, float activation)
    {
        Profile profileOut = new Profile();

        int preferredProfile = getPreferredProfile(primaryAE);
        Profile profileIn = profiles.get(primaryAE).get(preferredProfile);
        float ratio = activation / primaryAE.activation;

        for (Entry<FAP, Range> entry : profileIn.entrySet())
        {
            Range rangeIn = entry.getValue();
            int minOut = (int) (rangeIn.min * ratio);
            int maxOut = (int) (rangeIn.max * ratio);
            if (minOut != 0 || maxOut != 0) profileOut.put(entry.getKey(), new Range(minOut, maxOut));
        }
        return profileOut;
    }

    private Profile convertInBetween(float angle, float activation)
    {
        Profile profileOut = new Profile();

        int preferredProfilePrimary = getPreferredProfile(primaryAE);
        Profile profilePrimaryIn = profiles.get(primaryAE).get(preferredProfilePrimary);
        int preferredProfileSecondary = getPreferredProfile(secondaryAE);
        Profile profileSecondaryIn = profiles.get(secondaryAE).get(preferredProfileSecondary);
        Profile profilePrimary = new Profile();
        Profile profileSecondary = new Profile();

        ArrayList<FAP> vocabulary = new ArrayList<FAP>();
        for (FAP key : profilePrimaryIn.keySet())
            if (vocabulary.indexOf(key) == -1) vocabulary.add(key);

        for (FAP key : profileSecondaryIn.keySet())
            if (vocabulary.indexOf(key) == -1) vocabulary.add(key);

        // Adjust primary ranges according to activation.
        float ratioPrimary = activation / primaryAE.activation;
        for (FAP key : profilePrimaryIn.keySet())
        {
            Range rangePrimary = profilePrimaryIn.get(key);
            int minOut = (int) (rangePrimary.min * ratioPrimary);
            int maxOut = (int) (rangePrimary.max * ratioPrimary);
            if (minOut != 0 || maxOut != 0) profilePrimary.put(key, new Range(minOut, maxOut));
        }

        // Adjust secondary ranges according to activation.
        float ratioSecondary = activation / secondaryAE.activation;
        for (FAP key : profileSecondaryIn.keySet())
        {
            Range rangeSecondary = profileSecondaryIn.get(key);
            int minOut = (int) (rangeSecondary.min * ratioSecondary);
            int maxOut = (int) (rangeSecondary.max * ratioSecondary);
            if (minOut != 0 || maxOut != 0) profileSecondary.put(key, new Range(minOut, maxOut));
        }

        // Merge profilePrimary and profileSecondary by using the ratio
        // in angles. For example: target angle = 180. Primary AE angle
        // is at 160 and the secondary at 210. The first angleRatio will
        // be (180 - 160) / (210 - 160) = 0.4 and the second will be it's
        // complement: (210 - 180) / (210 - 160) = 0.6
        float primAngle = primaryAE.angle;
        float secAngle = secondaryAE.angle;
        float calAngle = angle;

        if (secAngle < primAngle)
        {
            // Wraparound-situation
            secAngle += 360;
        }

        if (calAngle < primAngle)
        {
            calAngle += 360;
        }

        float angleTotal = secAngle - primAngle - aeWidth;
        assert angleTotal > 0;
        float angleRatioSecondary = (calAngle - primAngle - aeWidth / 2) / angleTotal;
        float angleRatioPrimary = (secAngle - calAngle - aeWidth / 2) / angleTotal;

        // logger.debug("primAngle: " + primAngle + ", secAngle: " + secAngle + ", calAngle: " + calAngle + ", angleTotal: " + angleTotal);
        // logger.debug("angleRatioPrimary: {} angleRatioSecondary: {}", angleRatioPrimary , angleRatioSecondary);

        for (FAP key : vocabulary)
        {
            if (profilePrimary.containsKey(key) && profileSecondary.containsKey(key))
            {
                Range rangePrimary = profilePrimary.get(key);
                Range rangeSecondary = profileSecondary.get(key);
                profileOut.put(key, mergeRanges(key, profileOut, rangePrimary, rangeSecondary, angleRatioPrimary, angleRatioSecondary));
            }
            else if (profilePrimary.containsKey(key))
            {
                Range rangePrimary = profilePrimary.get(key);
                profileOut.put(key, mergeRanges(key, profileOut, rangePrimary, new Range(0, 0), angleRatioPrimary, angleRatioSecondary));
            }
            else if (profileSecondary.containsKey(key))
            {
                Range rangeSecondary = profileSecondary.get(key);
                profileOut.put(key, mergeRanges(key, profileOut, new Range(0, 0), rangeSecondary, angleRatioPrimary, angleRatioSecondary));
            }
        }

        return profileOut;
    }

    private Range mergeRanges(FAP key, Profile profileOut, Range rangePrimary, Range rangeSecondary, float angleRatioPrimary,
            float angleRatioSecondary)
    {
        int length, midpoint, lengthPrimary, lengthSecondary;
        float midpointPrimary, midpointSecondary;
        Range rangeOut = new Range();

        lengthPrimary = rangePrimary.max - rangePrimary.min;
        midpointPrimary = rangePrimary.min + lengthPrimary / 2;

        lengthSecondary = rangeSecondary.max - rangeSecondary.min;
        midpointSecondary = rangeSecondary.min + lengthSecondary / 2;

        length = (int) (angleRatioPrimary * lengthPrimary + angleRatioSecondary * lengthSecondary);
        midpoint = (int) (angleRatioPrimary * midpointPrimary + angleRatioSecondary * midpointSecondary);

        rangeOut.min = midpoint - length / 2;
        rangeOut.max = midpoint + length / 2;

        return rangeOut;
    }

    private void loadRanges(Resources r, String filename)
    {
        try
        {
            BufferedReader br = r.getReader(filename);
            String line;

            AE archetypalEmotion = null;

            while ((line = br.readLine()) != null)
            {
                // Empty lines.
                if (line.length() == 0) continue;

                // Comments
                if (line.charAt(0) == '#') continue;

                // Archetypal emotion profile identification line
                if (line.startsWith("Anger") || line.startsWith("Sadness") || line.startsWith("Joy") || line.startsWith("Disgust")
                        || line.startsWith("Fear") || line.startsWith("Surprise"))
                {
                    archetypalEmotion = null;

                    String[] elts = line.split("\\s+");
                    if (elts.length == 2)
                    {
                        archetypalEmotion = AE.valueOf(elts[0].toUpperCase());
                    }

                    if (profiles.get(archetypalEmotion) == null) profiles.put(archetypalEmotion, new ArrayList<Profile>());

                    continue;
                }

                // Line with profile (ranges for FAPs)
                if (line.matches("F[0-9]+.*"))
                {
                    line = line.replaceAll("\\s", "");
                    String[] elts = line.split("\\],");

                    String[] subelts;
                    String[] subsubelts;
                    int fapNumber, min, max;
                    Range range;
                    Profile profile = new Profile();

                    for (String elt : elts)
                    {
                        subelts = elt.split("\\[");
                        fapNumber = Integer.parseInt(subelts[0].substring(1));

                        subsubelts = subelts[1].split(",");
                        min = Integer.parseInt(subsubelts[0]);
                        if (subsubelts[1].charAt(subsubelts[1].length() - 1) == ']') subsubelts[1] = subsubelts[1].substring(0,
                                subsubelts[1].length() - 1);
                        max = Integer.parseInt(subsubelts[1]);

                        range = new Range();
                        range.min = min;
                        range.max = max;

                        profile.put(MPEG4.getFAPs().get(fapNumber), range);
                    }

                    profiles.get(archetypalEmotion).add(profile);
                }
            }
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
