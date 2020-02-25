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
package hmi.faceanimation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

/**
 * MorphInterpolator test cases
 * @author herwinvw
 */
public class FaceInterpolatorTest
{
    private double PRECISION = 0.00001d;

    @Test
    public void testGetParts()
    {
        String xml = "<FaceInterpolator parts=\"morph1 morph2 morph3\">" + "0.1 0.5 0.4 0.4\n" + "0.9 0.7 0.6 0.2"
                + "</FaceInterpolator>";
        FaceInterpolator ip = new FaceInterpolator();
        ip.readXML(xml);
        assertThat(ip.getParts(), IsIterableContainingInOrder.contains("morph1", "morph2", "morph3"));
    }

    @Test
    public void testStartEnd()
    {
        String xml = "<FaceInterpolator parts=\"morph1 morph2 morph3\">" + "0.1 0.5 0.4 0.4\n" + "0.9 0.7 0.6 0.2"
                + "</FaceInterpolator>";
        FaceInterpolator ip = new FaceInterpolator();
        ip.readXML(xml);
        assertEquals(0.1, ip.getStartTime(), PRECISION);
        assertEquals(0.9, ip.getEndTime(), PRECISION);
    }

    @Test
    public void testInterpolate()
    {
        String xml = "<FaceInterpolator parts=\"morph1 morph2 morph3\">" + "0 0.5 0.4 0.4\n" + "1 0.7 0.6 0.2" + "</FaceInterpolator>";
        FaceInterpolator ip = new FaceInterpolator();
        ip.readXML(xml);
        float current[] = ip.interpolate(0.5);
        assertArrayEquals(current, new float[] { 0.6f, 0.5f, 0.3f }, (float) PRECISION);
    }
    
    @Test
    public void testWrite()
    {
        String xml = "<FaceInterpolator parts=\"morph1 morph2 morph3\">" + "0.1 0.5 0.4 0.4\n" + "0.9 0.7 0.6 0.2" + "</FaceInterpolator>";
        FaceInterpolator ipIn = new FaceInterpolator();
        ipIn.readXML(xml);

        StringBuilder buf = new StringBuilder();
        ipIn.appendXML(buf);
        FaceInterpolator ipOut = new FaceInterpolator();
        ipOut.readXML(buf.toString());
        
        assertThat(ipOut.getParts(), IsIterableContainingInOrder.contains("morph1", "morph2", "morph3"));
        assertEquals(0.1, ipOut.getStartTime(), PRECISION);
        assertEquals(0.9, ipOut.getEndTime(), PRECISION);
    }
}
