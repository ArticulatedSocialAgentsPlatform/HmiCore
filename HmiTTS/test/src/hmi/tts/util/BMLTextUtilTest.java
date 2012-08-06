package hmi.tts.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the BMLTextUtil
 * @author welberge
 */
public class BMLTextUtilTest
{
    @Test
    public void testStrip()
    {
        String stripped = BMLTextUtil.stripSyncs("blah blah<sync id=\"id1\"/>");
        assertEquals("blah blah ",stripped);
    }
    
    @Test
    public void testStripWithNamespace()
    {
        String stripped = BMLTextUtil.stripSyncs("blah blah<sync xmlns=\"http://blah.com\" id=\"id1\"/>");
        assertEquals("blah blah ",stripped);
    }
    
    @Test
    public void testStripWithPrefixedNamespace()
    {
        String stripped = BMLTextUtil.stripSyncs("blah blah<sync xmlns:ns=\"http://blah.com\" id=\"id1\"/>");
        assertEquals("blah blah ",stripped);
    }
    
    @Test
    public void testBMLToSSML()
    {
        String ssml = BMLTextUtil.BMLToSSML("blah blah<sync id=\"id1\"/>");
        assertEquals("blah blah<mark name=\"id1\"/>",ssml);
    }
    
    @Test
    public void testBMLToSSML2()
    {
        String ssml = BMLTextUtil.BMLToSSML("blah blah<sync id=\"id1\"></sync>");
        assertEquals("blah blah<mark name=\"id1\"></mark>",ssml);
    }
    
    @Test
    public void testBMLToSSML3()
    {
        String ssml = BMLTextUtil.BMLToSSML("blah blah<sync xmlns:ns=\"http://blah.com\" id=\"id1\"></sync>");
        assertEquals("blah blah<mark name=\"id1\"></mark>",ssml);
    }
}
