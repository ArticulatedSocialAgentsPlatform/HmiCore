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
}
