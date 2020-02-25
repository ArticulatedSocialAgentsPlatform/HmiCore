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
package hmi.tts.mary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import marytts.datatypes.MaryDataType;
import marytts.modules.synthesis.Voice;
import marytts.server.Mary;
import marytts.server.Request;
import marytts.util.MaryUtils;
import marytts.util.data.audio.AudioPlayer;
import marytts.util.data.audio.MaryAudioUtils;
import hmi.tts.AbstractTTSGenerator;
import hmi.tts.Bookmark;
import hmi.tts.Phoneme;
import hmi.tts.TimingInfo;
import hmi.tts.Visime;
import hmi.tts.WordDescription;
import hmi.tts.util.BMLTextUtil;
import hmi.tts.util.NullPhonemeToVisemeMapping;
import hmi.tts.util.PhonemeToVisemeMapping;
import hmi.tts.util.PhonemeUtil;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * MaryTTS implementation of the AbstractTTSGenerator
 * @author hvanwelbergen
 *
 */
@ThreadSafe
public class MaryTTSGenerator extends AbstractTTSGenerator
{
    @GuardedBy("this")
    private Voice voice;

    @GuardedBy("this")
    private int currentWordOffset;

    private PhonemeToVisemeMapping visemeMapping;
    
    /**
     * Constructor, sets up and starts the Mary TTS generator without a viseme mapping
     * @throws Exception
     */
    public MaryTTSGenerator(String maryDir) throws Exception
    {
      this (maryDir, new NullPhonemeToVisemeMapping());
    }
    
    
    /**
     * Constructor, sets up and starts the Mary TTS generator
     * 
     * @param maryDir
     *            the directory where Mary is installed and its voices can be found
     * @param visemeMappingFile
     *            the location of the phoneme to viseme mapping (e.g., "phoneme2viseme/boss2ikp.xml")
     * @throws Exception
     */
    public MaryTTSGenerator(String maryDir, PhonemeToVisemeMapping vm) throws Exception
    {
        System.setProperty("mary.base", maryDir);
        System.out.println("maryDir: " +maryDir);
        if (Mary.currentState() == Mary.STATE_OFF)
        {
            Mary.startup(false);
        }
        voice = Voice.getAvailableVoices().iterator().next();        
        visemeMapping = vm;        
    }

    public synchronized String getSSMLStartTag()
    {
        return "<speak xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\""
                + MaryUtils.locale2xmllang(voice.getLocale()) + "\">";
    }

    public synchronized String getMaryXMLStartTag()
    {
        return "<maryxml version=\"0.5\" xmlns=\"http://mary.dfki.de/2002/MaryXML\" xml:lang=\""
                + MaryUtils.locale2xmllang(voice.getLocale()) + "\">";
    }


    private AudioFileFormat getAudioFileFormat()
    {
        AudioFileFormat.Type audioType = MaryAudioUtils.getAudioFileFormatType("WAVE");        
        return new AudioFileFormat(audioType, voice.dbAudioFormat(), AudioSystem.NOT_SPECIFIED);
    }

    private void speakText(String text, MaryDataType inputType)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Request request = new Request(inputType, MaryDataType.AUDIO, voice.getLocale(), voice,
                null, null, 1, getAudioFileFormat(), false, null);
        try
        {
            request.readInputData(new StringReader(text));
            request.process();
            request.writeOutputData(baos);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        AudioInputStream ais;
        try
        {
            ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(baos.toByteArray()));
        }
        catch (UnsupportedAudioFileException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        AudioPlayer ap = new AudioPlayer(ais, null);
        ap.start();

        // TODO callbacks
    }

    public synchronized void speakToFile(String text, String filename, MaryDataType inputType)
            throws IOException
    {
        FileOutputStream fos = new FileOutputStream(new File(filename));
        Request request = new Request(inputType, MaryDataType.AUDIO, voice.getLocale(), voice,
                null, null, 1, getAudioFileFormat(), false, null);
        try
        {
            request.readInputData(new StringReader(text));
            request.process();
            request.writeOutputData(fos);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        fos.close();
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

    public synchronized TimingInfo speak(String text, MaryDataType inputType)
    {
        TimingInfo ti = getTiming(text, inputType);
        speakText(text, inputType);
        return ti;
    }

    @Override
    public synchronized TimingInfo speakBML(String text)
    {
        TimingInfo ti = getBMLTiming(text);
        speakText(BMLTextUtil.stripSyncs(text), MaryDataType.TEXT);
        return ti;
    }

    @Override
    public synchronized TimingInfo speakToFile(String text, String filename) throws IOException
    {
        TimingInfo ti = getTiming(text);
        speakToFile(text, filename, MaryDataType.TEXT);
        return ti;
    }

    @Override
    public synchronized TimingInfo speakBMLToFile(String text, String filename) throws IOException
    {
        TimingInfo ti = getBMLTiming(text);
        speakToFile(BMLTextUtil.stripSyncs(text), filename, MaryDataType.TEXT);
        return ti;
    }

    private Phoneme parsePhoneme(Node n, boolean stress)
    {
        NamedNodeMap attr = n.getAttributes();
        Node duration = attr.getNamedItem("d");
        String phoneme = attr.getNamedItem("p").getNodeValue();

        // hack to put phoneme string into a number
        int phonemeNr = PhonemeUtil.phonemeStringToInt(phoneme);
        return new Phoneme(phonemeNr, (int)Double.parseDouble(duration.getNodeValue()), stress);
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
        //System.out.println("Boundary duration: " + duration);

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

    public synchronized TimingInfo getTiming(String text, MaryDataType inputDataType)
    {
        Request request = new Request(inputDataType, MaryDataType.REALISED_ACOUSTPARAMS, voice
                .getLocale(), voice, null, null, 1, getAudioFileFormat(), false, null);
        try
        {
            request.readInputData(new StringReader(text));
            request.process();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        parseWordDescriptionsAndBookmarks(request.getOutputData().getDocument());
        return getAndClearTimingInfo();
    }

    @Override
    public synchronized TimingInfo getBMLTiming(String text)
    {
        return getTiming(getSSMLStartTag() + BMLTextUtil.BMLToSSML(text) + "</speak>",
                MaryDataType.SSML);
    }

    @Override
    public synchronized void setVoice(String speaker)
    {
        for (Voice v : Voice.getAvailableVoices())
        {
            if (v.getName().equals(speaker))
            {
                voice = v;
                return;
            }
        }
    }
    
    public synchronized String getVoice()
    {
        return voice.getName();
    }

    @Override
    public synchronized String[] getVoices()
    {
        String[] str = new String[Voice.getAvailableVoices().size()];
        int i = 0;
        for (Voice v : Voice.getAvailableVoices())
        {
            str[i] = v.getName();
            i++;
        }
        return str;
    }
}
