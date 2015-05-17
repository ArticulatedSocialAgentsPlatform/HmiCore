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
package hmi.tts.mary5;

import hmi.math.signalproc.Framer;
import hmi.math.signalproc.RootMeanSquared;
import hmi.tts.AbstractTTSGenerator;
import hmi.tts.Bookmark;
import hmi.tts.Phoneme;
import hmi.tts.TimingInfo;
import hmi.tts.Visime;
import hmi.tts.WordDescription;
import hmi.tts.mary5.prosody.MaryProsodyInfo;
import hmi.tts.util.BMLTextUtil;
import hmi.tts.util.NullPhonemeToVisemeMapping;
import hmi.tts.util.PhonemeToVisemeMapping;
import hmi.tts.util.PhonemeUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.xml.parsers.ParserConfigurationException;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.datatypes.MaryDataType;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.MaryUtils;
import marytts.util.data.audio.AudioPlayer;
import marytts.util.data.audio.MaryAudioUtils;
import marytts.util.dom.DomUtils;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * MaryTTS implementation of the AbstractTTSGenerator
 * @author hvanwelbergen
 * 
 */
@ThreadSafe
public class MaryTTSGenerator extends AbstractTTSGenerator
{
    private static final int PROSODY_FRAMERATE = 120;
    private static final double RMS_WINDOW_SIZE = 0.050;

    @GuardedBy("this")
    private int currentWordOffset;

    private PhonemeToVisemeMapping visemeMapping;

    private MaryInterface marytts;

    /**
     * Constructor, sets up and starts the Mary TTS generator without a viseme mapping
     */
    public MaryTTSGenerator() throws MaryConfigurationException
    {
        this(new NullPhonemeToVisemeMapping());
    }

    /**
     * Constructor, sets up and starts the Mary TTS generator
     * 
     * @param maryDir
     *            the directory where Mary is installed and its voices can be found
     * @param visemeMappingFile
     *            the location of the phoneme to viseme mapping (e.g., "phoneme2viseme/boss2ikp.xml")
     * @throws MaryConfigurationException
     */
    public MaryTTSGenerator(PhonemeToVisemeMapping vm) throws MaryConfigurationException
    {
        marytts = new LocalMaryInterface();
        marytts.setVoice(marytts.getAvailableVoices().iterator().next());
        visemeMapping = vm;
    }

    public synchronized String getSSMLStartTag()
    {
        return "<speak xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"" + MaryUtils.locale2xmllang(marytts.getLocale()) + "\">";
    }

    public synchronized String getMaryXMLStartTag()
    {
        return "<maryxml version=\"0.5\" xmlns=\"http://mary.dfki.de/2002/MaryXML\" xml:lang=\""
                + MaryUtils.locale2xmllang(marytts.getLocale()) + "\">";
    }

    private AudioInputStream speakTextToAudioInputStream(Document doc, MaryDataType inputType)
    {
        marytts.setInputType(inputType.toString());
        marytts.setOutputType(MaryDataType.AUDIO.toString());
        try
        {
            return marytts.generateAudio(doc);
        }
        catch (SynthesisException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void speakTextFromAcousticParameters(Document doc, MaryProsody mp)
    {
        AudioInputStream audio = speakTextToAudioInputStream(doc, MaryDataType.ACOUSTPARAMS);
        try
        {
            mp.setRMSEnergy(getRMSEnergy(audio));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        AudioPlayer ap = new AudioPlayer(audio, null);
        ap.start();
    }

    private double[] getRMSEnergy(AudioInputStream audio) throws IOException
    {
        byte data[] = new byte[(int) (audio.getFrameLength() * audio.getFormat().getFrameSize())];
        audio.read(data);
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        if (audio.getFormat().isBigEndian())
        {
            bb.order(ByteOrder.BIG_ENDIAN);
        }
        double frame[] = new double[data.length / 2];
        for (int i = 0; i < frame.length; i++)
        {
            frame[i] = (double) bb.getShort() / 32767d;
        }
        float frameRate = audio.getFormat().getFrameRate();
        double frameSize = 1.0 / (double) PROSODY_FRAMERATE;
        double frames[][] = Framer.frame(frame, (int) (RMS_WINDOW_SIZE * frameRate), (int) (frameSize * frameRate));
        double rmsEnergy[] = new double[frames.length];
        for (int i = 0; i < frames.length; i++)
        {
            rmsEnergy[i] = RootMeanSquared.rootMeanSquared(frames[i]);
        }
        return rmsEnergy;
    }

    private void speakToFileFromAcousticParameters(Document doc, String filename, MaryProsody mp)
    {
        AudioInputStream audio = speakTextToAudioInputStream(doc, MaryDataType.ACOUSTPARAMS);
        
        try
        {
            mp.setRMSEnergy(getRMSEnergy(audio));
            MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), filename, audio.getFormat());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private TimingInfo getAndClearTimingInfo()
    {
        List<WordDescription> des = new ArrayList<WordDescription>();
        des.addAll(wordDescriptions);
        wordDescriptions.clear();
        List<Bookmark> bms = new ArrayList<Bookmark>();
        bms.addAll(bookmarks);
        bookmarks.clear();
        List<Visime> vis = new ArrayList<Visime>();
        vis.addAll(visimes);
        visimes.clear();
        return new TimingInfo(des, bms, vis);
    }

    @Override
    public synchronized MaryProsody speak(String text)
    {
        return speak(text, MaryDataType.TEXT);
    }

    public synchronized MaryProsodyInfo getProsodyInfo(String text) throws SynthesisException
    {
        MaryProsodyInfo p = new MaryProsodyInfo();
        marytts.setInputType(MaryDataType.TEXT.toString());
        marytts.setOutputType(MaryDataType.REALISED_ACOUSTPARAMS.toString());
        p.parse(marytts.generateXML(text));
        return p;
    }

    public synchronized MaryProsody speak(String text, MaryDataType inputType)
    {
        Document doc = getAcousticRealization(text, inputType);
        MaryProsody ti = getProsodyFromAcousticRealization(doc);
        speakTextFromAcousticParameters(doc, ti);
        return ti;
    }

    @Override
    public synchronized MaryProsody speakBML(String text)
    {
        Document doc = getAcousticRealizationBML(text);
        MaryProsody ti = getProsodyFromAcousticRealization(doc);
        speakTextFromAcousticParameters(doc, ti);
        return ti;
    }

    public synchronized MaryProsody speakToFile(String text, String filename, MaryDataType inputType) throws IOException
    {
        Document doc = getAcousticRealization(text, inputType);
        MaryProsody ti = getProsodyFromAcousticRealization(doc);
        speakToFileFromAcousticParameters(doc, filename, ti);
        return ti;
    }

    @Override
    public synchronized MaryProsody speakToFile(String text, String filename) throws IOException
    {
        Document doc = getAcousticRealization(text, MaryDataType.TEXT);
        MaryProsody ti = getProsodyFromAcousticRealization(doc);
        speakToFileFromAcousticParameters(doc, filename, ti);
        return ti;
    }

    @Override
    public synchronized MaryProsody speakBMLToFile(String text, String filename) throws IOException
    {
        Document doc = getAcousticRealizationBML(text);
        MaryProsody ti = getProsodyFromAcousticRealization(doc);
        speakToFileFromAcousticParameters(doc, filename, ti);
        return ti;
    }

    private Phoneme parsePhoneme(Node n, boolean stress)
    {
        NamedNodeMap attr = n.getAttributes();
        Node duration = attr.getNamedItem("d");
        String phoneme = attr.getNamedItem("p").getNodeValue();

        // hack to put phoneme string into a number
        int phonemeNr = PhonemeUtil.phonemeStringToInt(phoneme);
        return new Phoneme(phonemeNr, (int) Double.parseDouble(duration.getNodeValue()), stress);
    }

    private void parseSyllable(Node n, List<Phoneme> pList, List<Visime> vList)
    {
        Node child = n.getFirstChild();
        boolean stress = false;
        NamedNodeMap attr = n.getAttributes();
        Node stressNode = attr.getNamedItem("stress");
        if (stressNode != null)
        {
            stress = Integer.parseInt(stressNode.getNodeValue()) != 0;
        }

        while (child != null)
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                if (child.getNodeName().equals("ph"))
                {
                    Phoneme ph = parsePhoneme(child, stress);
                    pList.add(ph);
                    vList.add(new Visime(visemeMapping.getVisemeForPhoneme(ph.getNumber()), ph.getDuration(), stress));
                }
            }
            child = child.getNextSibling();
        }
    }

    private void parseWord(Node n)
    {
        String word = n.getTextContent();
        List<Phoneme> pList = new ArrayList<Phoneme>();
        List<Visime> vList = new ArrayList<Visime>();

        Node child = n.getFirstChild();
        while (child != null)
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                if (child.getNodeName().equals("syllable"))
                {
                    parseSyllable(child, pList, vList);
                }
            }
            child = child.getNextSibling();
        }
        WordDescription wd = new WordDescription(word, pList, vList);
        addWordDescription(wd);
    }

    private void addWordDescription(WordDescription wd)
    {
        currentWordOffset += wd.getDuration();
        wordDescriptions.add(wd);

        // add all visimes in this word to the visime list
        for (Visime v : wd.getVisimes())
        {
            visimes.add(v);
        }

        // new last word, update all bookmarks with null (=empty) word
        ArrayList<Bookmark> bmAdd = new ArrayList<Bookmark>();
        ArrayList<Bookmark> bmRemove = new ArrayList<Bookmark>();
        for (Bookmark bm : bookmarks)
        {
            if (bm.getWord() == null)
            {
                bmAdd.add(new Bookmark(bm.getName(), wd, bm.getOffset()));
                bmRemove.add(bm);
            }
        }
        bookmarks.removeAll(bmRemove);
        bookmarks.addAll(bmAdd);
    }

    private void parseMark(Node n)
    {
        NamedNodeMap attr = n.getAttributes();
        Node name = attr.getNamedItem("name");
        Bookmark bm = new Bookmark(name.getNodeValue(), null, currentWordOffset);
        bookmarks.add(bm);
    }

    // inserts an empty word with a 0 phoneme and visime for boundaries
    private void parseBoundary(Node n)
    {
        NamedNodeMap attr = n.getAttributes();
        Node durationNode = attr.getNamedItem("duration");
        if (durationNode == null) return;
        int duration = Integer.parseInt(durationNode.getNodeValue());

        Phoneme dummyPh = new Phoneme(0, duration, false);
        ArrayList<Phoneme> phList = new ArrayList<Phoneme>();
        phList.add(dummyPh);
        Visime dummyVis = new Visime(0, duration, false);
        ArrayList<Visime> vList = new ArrayList<Visime>();
        vList.add(dummyVis);
        addWordDescription(new WordDescription("", phList, vList));
    }

    private void parseWordDescriptionsAndBookmarks(Node node)
    {
        // find mark, t (word), boundary (=some sort of pause), ignore all other content
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
            if (node.getNodeName().equals("t"))
            {
                parseWord(node);
            }
            else if (node.getNodeName().equals("mark"))
            {
                parseMark(node);
            }
            else if (node.getNodeName().equals("boundary"))
            {
                parseBoundary(node);
            }
        }
        Node firstChild = node.getFirstChild();
        if (firstChild != null) parseWordDescriptionsAndBookmarks(firstChild);
        Node sibling = node.getNextSibling();
        if (sibling != null) parseWordDescriptionsAndBookmarks(sibling);
    }

    private void parseWordDescriptionsAndBookmarks(Document doc)
    {
        if (doc.hasChildNodes())
        {
            currentWordOffset = 0;
            bookmarks.clear();
            wordDescriptions.clear();
            visimes.clear();
            parseWordDescriptionsAndBookmarks(doc.getFirstChild());
        }
    }

    @Override
    public synchronized MaryProsody getTiming(String text)
    {
        return getTiming(text, MaryDataType.TEXT);
    }

    public synchronized Document getAcousticRealizationBML(String text)
    {
        return getAcousticRealization(getSSMLStartTag() + BMLTextUtil.BMLToSSML(text) + "</speak>", MaryDataType.SSML);
    }

    public synchronized Document getAcousticRealization(String text, MaryDataType inputDataType)
    {
        marytts.setInputType(inputDataType.toString());
        marytts.setOutputType(MaryDataType.REALISED_ACOUSTPARAMS.toString());
        try
        {
            if (inputDataType.isXMLType())
            {
                return marytts.generateXML(DomUtils.parseDocument(text, false));
            }
            else
            {
                return marytts.generateXML(text);
            }
        }
        catch (SynthesisException e)
        {
            throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }
        catch (SAXException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public synchronized MaryProsody getTiming(String text, MaryDataType inputDataType)
    {
        return getProsodyFromAcousticRealization(getAcousticRealization(text, inputDataType));
    }

    private TimingInfo getTimingFromAcousticRealization(Document doc)
    {
        parseWordDescriptionsAndBookmarks(doc);
        return getAndClearTimingInfo();
    }

    private MaryProsody getProsodyFromAcousticRealization(Document doc)
    {
        MaryProsody mp = new MaryProsody(getTimingFromAcousticRealization(doc));
        MaryProsodyInfo p = new MaryProsodyInfo();
        p.parse(doc);
        mp.setF0(p.getF0Contour(PROSODY_FRAMERATE));
        return mp;
    }

    @Override
    public synchronized MaryProsody getBMLTiming(String text)
    {
        return getTiming(getSSMLStartTag() + BMLTextUtil.BMLToSSML(text) + "</speak>", MaryDataType.SSML);
    }

    @Override
    public synchronized void setVoice(String speaker)
    {
        marytts.setVoice(speaker);
    }

    public synchronized String getVoice()
    {
        return marytts.getVoice();
    }

    @Override
    public synchronized String[] getVoices()
    {
        return marytts.getAvailableVoices().toArray(new String[0]);
    }
}
