package hmi.tts.mary5;

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
    
    private AudioInputStream speakTextToAudioInputStream(String text, MaryDataType inputType)
    {
        marytts.setInputType(inputType.toString());
        marytts.setOutputType(MaryDataType.AUDIO.toString());
        try
        {
            return marytts.generateAudio(text);
        }
        catch (SynthesisException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void speakTextFromAcousticParameters(Document doc)
    {
        AudioPlayer ap = new AudioPlayer(speakTextToAudioInputStream(doc, MaryDataType.ACOUSTPARAMS), null);
        ap.start();        
    }
    
    private void speakToFileFromAcousticParameters(Document doc, String filename)
    {
        AudioInputStream audio = speakTextToAudioInputStream(doc, MaryDataType.ACOUSTPARAMS);
        try
        {
            MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), filename, audio.getFormat());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public synchronized void speakToFile(String text, String filename, MaryDataType inputType) throws IOException
    {
        AudioInputStream audio = speakTextToAudioInputStream(text, inputType);
        MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), filename, audio.getFormat());
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
    public synchronized TimingInfo speak(String text)
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
    
    public synchronized TimingInfo speak(String text, MaryDataType inputType)
    {
        Document doc = getAcousticRealization(text, inputType);
        TimingInfo ti = getTimingFromAcousticRealization(doc);
        speakTextFromAcousticParameters(doc);
        return ti;
    }

    @Override
    public synchronized TimingInfo speakBML(String text)
    {
        Document doc = getAcousticRealizationBML(text);
        TimingInfo ti = getTimingFromAcousticRealization(doc);
        speakTextFromAcousticParameters(doc);
        return ti;
    }

    @Override
    public synchronized TimingInfo speakToFile(String text, String filename) throws IOException
    {
        Document doc = getAcousticRealization(text, MaryDataType.TEXT);
        TimingInfo ti = getTimingFromAcousticRealization(doc);
        speakToFileFromAcousticParameters(doc,filename);
        return ti;
    }

    @Override
    public synchronized TimingInfo speakBMLToFile(String text, String filename) throws IOException
    {
        Document doc = getAcousticRealizationBML(text);
        TimingInfo ti = getTimingFromAcousticRealization(doc);
        speakToFileFromAcousticParameters(doc,filename);
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
    public synchronized TimingInfo getTiming(String text)
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
            if(inputDataType.isXMLType())
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
    
    public synchronized TimingInfo getTiming(String text, MaryDataType inputDataType)
    {
        parseWordDescriptionsAndBookmarks(getAcousticRealization(text,inputDataType));
        return getAndClearTimingInfo();
    }
    
    private TimingInfo getTimingFromAcousticRealization(Document doc)
    {
        parseWordDescriptionsAndBookmarks(doc);
        return getAndClearTimingInfo();
    }

    @Override
    public synchronized TimingInfo getBMLTiming(String text)
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
