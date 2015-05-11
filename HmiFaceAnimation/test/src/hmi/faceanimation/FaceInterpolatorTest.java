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
