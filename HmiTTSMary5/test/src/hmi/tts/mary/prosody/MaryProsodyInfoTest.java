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
package hmi.tts.mary.prosody;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import hmi.tts.mary5.prosody.MaryProsodyInfo;
import hmi.tts.mary5.prosody.MaryProsodyInfo.Phoneme;
import hmi.tts.mary5.prosody.MaryProsodyInfo.PhraseBoundary;
import hmi.tts.mary5.prosody.MaryProsodyInfo.Word;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Unit tests for prosodyinfo
 * @author herwinvw
 *
 */
public class MaryProsodyInfoTest
{
    private static final double PRECISION = 0.001;
    
    private static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new InputSource(new StringReader(xml)));
    }
    
    String TESTXML="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
            "<maryxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"en-GB\" xmlns=\"http://mary.dfki.de/2002/MaryXML\">\r\n" + 
            "<p>\r\n" + 
            "<voice name=\"dfki-prudence\">\r\n" + 
            "<s>\r\n" + 
            "<phrase>\r\n" + 
            "<t accent=\"L+H*\" g2p_method=\"lexicon\" ph=\"h @ - ' l @U\" pos=\"UH\">Hello<syllable ph=\"h @\">\r\n" + 
            "<ph d=\"49\" end=\"0.049999997\" p=\"h\" units=\"h_L pru034 2597 0.03; h_R pru034 2598 0.02\"/>\r\n" + 
            "<ph d=\"68\" end=\"0.11775\" f0=\"(0,205) (50,321) (100,273)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" + 
            "</syllable>\r\n" + 
            "<syllable accent=\"L+H*\" ph=\"l @U\" stress=\"1\">\r\n" + 
            "<ph d=\"53\" end=\"0.17099999\" f0=\"(0,309)\" p=\"l\" units=\"l_L pru034 2601 0.027875; l_R pru034 2602 0.025375\"/>\r\n" + 
            "<ph d=\"265\" end=\"0.43543747\" f0=\"(50,349) (100,177)\" p=\"@U\" units=\"@U_L pru034 2603 0.1471875; @U_R w1532 142782 0.11725\"/>\r\n" + 
            "</syllable>\r\n" + 
            "</t>\r\n" + 
            "<t accent=\"!H*\" g2p_method=\"lexicon\" ph=\"' w r= l d\" pos=\"NN\">world<syllable accent=\"!H*\" ph=\"w r= l d\" stress=\"1\">\r\n" + 
            "<ph d=\"104\" end=\"0.53999996\" f0=\"(0,164)\" p=\"w\" units=\"w_L w1532 142783 0.044375; w_R w1382 133076 0.0601875\"/>\r\n" + 
            "<ph d=\"41\" end=\"0.580125\" f0=\"(50,268)\" p=\"r=\" units=\"r=_L w1382 133077 0.019625; r=_R w1382 133078 0.0205\"/>\r\n" + 
            "<ph d=\"169\" end=\"0.7490625\" p=\"l\" units=\"l_L w1382 133079 0.086; l_R w1382 133080 0.0829375\"/>\r\n" + 
            "<ph d=\"102\" end=\"0.8513125\" f0=\"(100,101)\" p=\"d\" units=\"d_L w1382 133081 0.05225; d_R w1382 133082 0.05\"/>\r\n" + 
            "</syllable>\r\n" + 
            "</t>\r\n" + 
            "<t pos=\".\">.</t>\r\n" + 
            "<boundary breakindex=\"5\" duration=\"200\" tone=\"L-L%\" units=\"__L w1382 133083 0.2\"/>\r\n" + 
            "</phrase>\r\n" + 
            "</s>\r\n" + 
            "</voice>\r\n" + 
            "</p>\r\n" + 
            "</maryxml>";
    @Test
    public void testParse() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        info.parse(loadXMLFromString(TESTXML));
        assertEquals(4,info.getPhraseElements().size());
        assertEquals("Hello",((Word) (info.getPhraseElements().get(0))).getWord());
        assertEquals("UH",((Word) (info.getPhraseElements().get(0))).getPos());
        assertEquals("L+H*",((Word) info.getPhraseElements().get(0)).getAccent());
        assertEquals("world",((Word) info.getPhraseElements().get(1)).getWord());
        assertEquals(".",((Word) info.getPhraseElements().get(2)).getWord());
        assertThat(info.getPhraseElements().get(3), instanceOf(PhraseBoundary.class));
        assertEquals(49+68+53+265+104+41+169+102+200,info.getPhraseElements().get(3).getEndTime());
        assertEquals(49+68+53+265+104+41+169+102+200,info.getDuration());
    }
    
    @Test
    public void testParseWithMTU() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
                "<maryxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"en-GB\" xmlns=\"http://mary.dfki.de/2002/MaryXML\">\r\n" + 
                "<p>\r\n" + 
                "<voice name=\"dfki-prudence\">\r\n" + 
                "<s>\r\n" + 
                "<phrase>\r\n" + 
                "<mtu orig=\"\">"+
                "<t accent=\"L+H*\" g2p_method=\"lexicon\" ph=\"h @ - ' l @U\" pos=\"UH\">Hello<syllable ph=\"h @\">\r\n" + 
                "<ph d=\"100\" end=\"0.1\" f0=\"(0,100) (50,200) (100,50)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" + 
                "</syllable>\r\n" + 
                "</t>\r\n" + 
                "</mtu>"+
                "</phrase>\r\n" + 
                "</s>\r\n" + 
                "</voice>\r\n" + 
                "</p>\r\n" + 
                "</maryxml>";
        info.parse(loadXMLFromString(xml));
        assertEquals(1,info.getPhonemes().size());
        assertEquals(100,info.getDuration());
    }
    
    @Test
    public void testGetPhonemes() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        info.parse(loadXMLFromString(TESTXML));
        List<Phoneme> phonemes = info.getPhonemes();
        assertEquals(8,phonemes.size());
        
        //(0,205) (50,321) (100,273)
        assertEquals(3, phonemes.get(1).getF0Frames().size());
        assertEquals(205, phonemes.get(1).getF0Frames().get(0).getFrequency(), PRECISION);
        assertEquals(0, phonemes.get(1).getF0Frames().get(0).getPercentage());
        assertEquals(321, phonemes.get(1).getF0Frames().get(1).getFrequency(), PRECISION);
        assertEquals(50, phonemes.get(1).getF0Frames().get(1).getPercentage());
        assertEquals(273, phonemes.get(1).getF0Frames().get(2).getFrequency(), PRECISION);
        assertEquals(100, phonemes.get(1).getF0Frames().get(2).getPercentage());
        
        assertEquals("d",phonemes.get(7).getPhoneme());
        assertEquals(1, phonemes.get(7).getF0Frames().size());
        assertEquals(101, phonemes.get(7).getF0Frames().get(0).getFrequency(), PRECISION);
        assertEquals(100, phonemes.get(7).getF0Frames().get(0).getPercentage());
    }
    
    @Test
    public void getF0TrajectoryOnePhonemeEdgeSpecified() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
                "<maryxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"en-GB\" xmlns=\"http://mary.dfki.de/2002/MaryXML\">\r\n" + 
                "<p>\r\n" + 
                "<voice name=\"dfki-prudence\">\r\n" + 
                "<s>\r\n" + 
                "<phrase>\r\n" + 
                "<t accent=\"L+H*\" g2p_method=\"lexicon\" ph=\"h @ - ' l @U\" pos=\"UH\">Hello<syllable ph=\"h @\">\r\n" + 
                "<ph d=\"100\" end=\"0.1\" f0=\"(0,100) (50,200) (100,50)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" + 
                "</syllable>\r\n" + 
                "</t>\r\n" + 
                "</phrase>\r\n" + 
                "</s>\r\n" + 
                "</voice>\r\n" + 
                "</p>\r\n" + 
                "</maryxml>";
        info.parse(loadXMLFromString(xml));
        double[] f0Contour = info.getF0Contour(100);
        assertEquals(11, f0Contour.length);
        assertEquals(100, f0Contour[0],PRECISION);
        assertEquals(200, f0Contour[5],PRECISION);
        assertEquals(50, f0Contour[10],PRECISION);
    }
    
    @Test
    public void getF0TrajectoryOnePhoneme() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
                "<maryxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"en-GB\" xmlns=\"http://mary.dfki.de/2002/MaryXML\">\r\n" + 
                "<p>\r\n" + 
                "<voice name=\"dfki-prudence\">\r\n" + 
                "<s>\r\n" + 
                "<phrase>\r\n" + 
                "<t accent=\"L+H*\" g2p_method=\"lexicon\" ph=\"h @ - ' l @U\" pos=\"UH\">Hello<syllable ph=\"h @\">\r\n" + 
                "<ph d=\"100\" end=\"0.1\" f0=\"(11,100) (51,200) (81,50)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" + 
                "</syllable>\r\n" + 
                "</t>\r\n" + 
                "</phrase>\r\n" + 
                "</s>\r\n" + 
                "</voice>\r\n" + 
                "</p>\r\n" + 
                "</maryxml>";
        info.parse(loadXMLFromString(xml));
        double[] f0Contour = info.getF0Contour(100);
        assertEquals(11, f0Contour.length);
        assertEquals(100, f0Contour[1],PRECISION);
        assertEquals(200, f0Contour[5],PRECISION);
        assertEquals(50, f0Contour[8],PRECISION);
        
        assertEquals(100,f0Contour[0],PRECISION);
        assertEquals(50,f0Contour[10],PRECISION);
        
        assertThat(f0Contour[2],greaterThan(100.0));
        assertThat(f0Contour[2],lessThan(200.0));
        assertThat(f0Contour[4],greaterThan(100.0));
        assertThat(f0Contour[4],lessThan(200.0));
        
        assertThat(f0Contour[6],greaterThan(50.0));
        assertThat(f0Contour[6],lessThan(200.0));
    }
    
    @Test
    public void getF0TrajectoryWithUnvoicedPhoneme() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
                "<maryxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"en-GB\" xmlns=\"http://mary.dfki.de/2002/MaryXML\">\r\n" + 
                "<p>\r\n" + 
                "<voice name=\"dfki-prudence\">\r\n" + 
                "<s>\r\n" + 
                "<phrase>\r\n" + 
                "<t accent=\"L+H*\" g2p_method=\"lexicon\" ph=\"h @ - ' l @U\" pos=\"UH\">Hello<syllable ph=\"h @\">\r\n" + 
                "<ph d=\"100\" end=\"0.1\" f0=\"(11,100) (51,200) (81,50)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" +
                "<ph d=\"100\" end=\"0.049999997\" p=\"h\" units=\"h_L pru034 2597 0.03; h_R pru034 2598 0.02\"/>\r\n" + 
                "<ph d=\"100\" end=\"0.1\" f0=\"(11,100) (51,200) (81,50)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" +
                "</syllable>\r\n" + 
                "</t>\r\n" + 
                "</phrase>\r\n" + 
                "</s>\r\n" + 
                "</voice>\r\n" + 
                "</p>\r\n" + 
                "</maryxml>";
        info.parse(loadXMLFromString(xml));
        double[] f0Contour = info.getF0Contour(100);
        assertEquals(31, f0Contour.length);
        assertEquals(100, f0Contour[0],PRECISION);
        assertEquals(50, f0Contour[30],PRECISION);
        assertEquals(50, f0Contour[8],PRECISION);
        assertEquals(50, f0Contour[9],PRECISION);
        assertEquals(0, f0Contour[10],PRECISION);
        assertEquals(0, f0Contour[19],PRECISION);
        assertEquals(100, f0Contour[20],PRECISION);
    }
    
    @Test
    public void getF0TrajectoryWithCenterBreak() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
                "<maryxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"en-GB\" xmlns=\"http://mary.dfki.de/2002/MaryXML\">\r\n" + 
                "<p>\r\n" + 
                "<voice name=\"dfki-prudence\">\r\n" + 
                "<s>\r\n" + 
                "<phrase>\r\n" + 
                "<t accent=\"L+H*\" g2p_method=\"lexicon\" ph=\"h @ - ' l @U\" pos=\"UH\">Hello<syllable ph=\"h @\">\r\n" + 
                "<ph d=\"100\" end=\"0.1\" f0=\"(11,100) (51,200) (81,50)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" +
                "</syllable></t>"+
                "<boundary breakindex=\"5\" duration=\"100\" tone=\"L-L%\" units=\"__L w1382 133083 0.2\"/>\r\n" + 
                "<t accent=\"L+H*\" g2p_method=\"lexicon\" ph=\"h @ - ' l @U\" pos=\"UH\">Hello<syllable ph=\"h @\">\r\n" + 
                "<ph d=\"100\" end=\"0.1\" f0=\"(11,100) (51,200) (81,50)\" p=\"@\" units=\"@_L pru034 2599 0.0339375; @_R pru034 2600 0.0338125\"/>\r\n" +
                "</syllable>\r\n" + 
                "</t>\r\n" + 
                "</phrase>\r\n" + 
                "</s>\r\n" + 
                "</voice>\r\n" + 
                "</p>\r\n" + 
                "</maryxml>";
        info.parse(loadXMLFromString(xml));
        double[] f0Contour = info.getF0Contour(100);
        System.out.println(Arrays.toString(f0Contour));
        assertEquals(31, f0Contour.length);
        assertEquals(100, f0Contour[0],PRECISION);
        assertEquals(50, f0Contour[30],PRECISION);
        assertEquals(50, f0Contour[8],PRECISION);
        assertEquals(50, f0Contour[9],PRECISION);
        assertEquals(0, f0Contour[10],PRECISION);
        assertEquals(0, f0Contour[19],PRECISION);
        assertEquals(100, f0Contour[20],PRECISION);
    }
    
    @Test
    public void getF0Trajectory() throws Exception
    {
        MaryProsodyInfo info = new MaryProsodyInfo();
        info.parse(loadXMLFromString(TESTXML));
        double[] f0Contour = info.getF0Contour(100);
        assertEquals(0,f0Contour[f0Contour.length-1],PRECISION);
        assertEquals(0,f0Contour[0],PRECISION);
    }
}
