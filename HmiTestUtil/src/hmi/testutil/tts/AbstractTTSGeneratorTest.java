package hmi.testutil.tts;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hmi.tts.AbstractTTSGenerator;
import hmi.tts.Bookmark;
import hmi.tts.TimingInfo;
import hmi.tts.Visime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

/**
 * Abstract test class for TTSGenerator tests
 * 
 * @author Herwin
 */
public abstract class AbstractTTSGeneratorTest
{
    protected static AbstractTTSGenerator ttsG = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTTSGeneratorTest.class.getName());
    private static final double PARAMETER_PRECISION = 0.0001;
    
    @Test
    public void testVisimes()
    {
        for (String voice : ttsG.getVoices())
        {
            ttsG.setVoice(voice);
            TimingInfo tInfo = ttsG
                    .getBMLTiming("<sync id=\"deicticheart1\"/>Welcome!<sync id=\"deicticheart2\"/> "
                            + "I am Griet, I am 16 year old and I would like to tell you something about my life. "
                            + "People know me as the girl with the pearl ear ring. My father can no longer work since he was blinded. "
                            + "<sync id=\"beat1b1\"/>Because we still need to eat, I took service with a local painter. "
                            + "His name is Johannes <sync id=\"vermeer1\"/>Vermeer.<sync id=\"vermeer2\"/>");
            List<Visime> vs = tInfo.getVisimes();
            double totalDuration = 0;
            for (Visime v : vs)
            {
                totalDuration += v.getDuration() / 1000d;
            }
            assertEquals(totalDuration, tInfo.getDuration(), 0.015);
        }
    }

    @Test
    public void testBookmarks()
    {
        for (String voice : ttsG.getVoices())
        {
            ttsG.setVoice(voice);
            TimingInfo tInfo = ttsG
                    .getBMLTiming("<sync id=\"deicticheart1\"/>Welcome!<sync id=\"deicticheart2\"/> "
                            + "I am Griet, I am 16 year old and I would like to tell you something about my life. "
                            + "People know me as the girl with the pearl ear ring. "
                            + "My father can no longer work since he was blinded. "
                            + "<sync id=\"beat1b1\"/>Because we still need to eat, I took service with a local painter. "
                            + "His name is Johannes <sync id=\"vermeer1\"/>Vermeer.<sync id=\"vermeer2\"/>");

            Bookmark b1 = tInfo.getBookmark("deicticheart1");
            Bookmark b2 = tInfo.getBookmark("deicticheart2");
            assertThat(b2.getOffset(), greaterThan(b1.getOffset()));
            assertEquals(0, b1.getOffset());

            Bookmark v1 = tInfo.getBookmark("vermeer1");
            Bookmark v2 = tInfo.getBookmark("vermeer2");
            assertThat(v2.getOffset(), greaterThan(v1.getOffset()));
            assertEquals(v2.getOffset(),Math.round(tInfo.getDuration() * 1000), PARAMETER_PRECISION);

            return;
        }
    }

    @Test
    public void testBookmarksWav() throws IOException
    {
        for (String voice : ttsG.getVoices())
        {
            ttsG.setVoice(voice);
            File f;
            f = File.createTempFile("testBookmarksWav", ".wav");
            TimingInfo tInfo = ttsG
                    .speakBMLToFile(
                            "<sync id=\"deicticheart1\"/>Welcome!<sync id=\"deicticheart2\"/>"
                                    + " I am Griet, I am 16 year old and I would like to tell you something about my life."
                                    + " People know me as the girl with the pearl ear ring. My father can no longer work since he was blinded. "
                                    + "<sync id=\"beat1b1\"/>Because we still need to eat, I took service with a local painter. "
                                    + "His name is Johannes <sync id=\"vermeer1\"/>Vermeer.<sync id=\"vermeer2\"/>",
                            f.getAbsolutePath());
            if (!f.delete())
            {
                LOGGER.warn("Can't delete temp file! {}",f.getName());
            }

            Bookmark b1 = tInfo.getBookmark("deicticheart1");
            Bookmark b2 = tInfo.getBookmark("deicticheart2");
            assertThat(b2.getOffset(), greaterThan(b1.getOffset()));
            assertEquals(0, b1.getOffset());

            Bookmark v1 = tInfo.getBookmark("vermeer1");
            Bookmark v2 = tInfo.getBookmark("vermeer2");
            assertThat(v2.getOffset(), greaterThan(v1.getOffset()));
            assertEquals(v2.getOffset(), Math.round(tInfo.getDuration() * 1000));
        }
    }

    @Test
    public void testDuration()
    {
        TimingInfo tInfo = ttsG.getBMLTiming("test");
        assertEquals("test",tInfo.getWordDescriptions().get(0).getWord());
        assertThat(tInfo.getDuration(), greaterThan(0d));
    }

    @Test
    public void testSpeakDuration()
    {
        TimingInfo tInfo = ttsG.speak("test");
        assertEquals("test",tInfo.getWordDescriptions().get(0).getWord());
        assertThat(tInfo.getDuration(), greaterThan(0d));
    }

    @Test
    public void testWavDuration() throws IOException
    {
        java.io.File f = File.createTempFile("test", ".wav");
        TimingInfo tInfo = ttsG.speakBMLToFile("test", f.getAbsolutePath());
        if (!f.delete())
        {
            LOGGER.warn("Can't delete temp file!");
        }
        assertEquals("test",tInfo.getWordDescriptions().get(0).getWord());
        assertThat(tInfo.getDuration(), greaterThan(0d));
    }
}
