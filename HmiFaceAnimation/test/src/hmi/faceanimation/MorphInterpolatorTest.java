package hmi.faceanimation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import hmi.faceanimation.MorphInterpolator.MorphFrame;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import com.google.common.primitives.Floats;

/**
 * MorphInterpolator test cases
 * @author herwinvw
 */
public class MorphInterpolatorTest
{
    private double PRECISION = 0.00001d;

    @Test
    public void testGetParts()
    {
        String xml = "<MorphInterpolator parts=\"morph1 morph2 morph3\">"+ "0.1 0.5 0.4 0.4\n" + "0.9 0.7 0.6 0.2" + "</MorphInterpolator>";
        MorphInterpolator ip = new MorphInterpolator();
        ip.readXML(xml);
        assertThat(ip.getParts(), IsIterableContainingInOrder.contains("morph1", "morph2", "morph3"));
    }

    @Test
    public void testStartEnd()
    {
        String xml = "<MorphInterpolator parts=\"morph1 morph2 morph3\">" + "0.1 0.5 0.4 0.4\n" + "0.9 0.7 0.6 0.2" + "</MorphInterpolator>";
        MorphInterpolator ip = new MorphInterpolator();
        ip.readXML(xml);
        assertEquals(0.1, ip.getStartTime(), PRECISION);
        assertEquals(0.9, ip.getEndTime(), PRECISION);
    }

    @Test
    public void testInterpolate()
    {
        String xml = "<MorphInterpolator parts=\"morph1 morph2 morph3\">" + "0 0.5 0.4 0.4\n" + "1 0.7 0.6 0.2" + "</MorphInterpolator>";
        MorphInterpolator ip = new MorphInterpolator();
        ip.readXML(xml);
        MorphFrame mf = ip.interpolate(0.5);
        assertThat(Floats.asList(mf.weights),IsIterableContainingInOrder.contains(0.6f,0.5f,0.3f));        
    }
}
