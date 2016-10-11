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
package hmi.tts.fluency8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import java.nio.file.*;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hmi.tts.AbstractTTSGenerator;
import hmi.tts.Bookmark;
import hmi.tts.Phoneme;
import hmi.tts.TimingInfo;
import hmi.tts.Visime;
import hmi.tts.WordDescription;
import hmi.tts.util.BMLTextUtil;
import hmi.tts.util.NullPhonemeToVisemeMapping;
import hmi.tts.util.PhonemeToVisemeMapping;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * TTSGenerator for Fluency 8
 * 
 * For now, only works with WAV tts, not with direct tts !!!
 * to fix this, among other things you need to reinstate the callback stuff from SAPI5 example: the callbacks to callback and permanentcallback
 * 
 * @author Dennis Reidsma
 */
@ThreadSafe  
public class Fluency8TTSGenerator extends AbstractTTSGenerator
{
    private native int FluencySpeak(String text);

    private native int FluencyInit();

    private native int FluencyCleanup();

    private native int FluencySetSpeaker(String speaker);

    private native int FluencySpeakToFile(String text, String filename);

    private native String[] FluencyGetVoices();

    private PhonemeToVisemeMapping visemeMapping;

    private String speaker;
    
    @GuardedBy("this")
    private boolean initialized;

    // temp vars used to gather bookmarks, visimes and phonemes while speaking
    @GuardedBy("this")
    HashMap<String,String> fluencySyncToBmlSync = new HashMap<String,String>();

    @GuardedBy("this")
    private ArrayList<BookMarker> currentBookmarks = new ArrayList<BookMarker>();

    @GuardedBy("this")
    private ArrayList<Visime> currentVisimes = new ArrayList<Visime>();

    @GuardedBy("this")
    private ArrayList<Phoneme> currentPhonemes = new ArrayList<Phoneme>();

    @GuardedBy("this")
    private String currentWord = null;

    @GuardedBy("this")
    private int currentWordOffset = 0;

    @GuardedBy("this")
    int prevOffset=0;
    @GuardedBy("this")
    int prevLength=0;
    @GuardedBy("this")
    private boolean wordBeforePhoneme = false;

    @GuardedBy("this")
    private String text;

    private ExecutorService executorService;

    private static final Logger logger = LoggerFactory.getLogger(Fluency8TTSGenerator.class.getName());
    
    static { logger.warn("Fluency TTS only works with wavtts not with direct.tts");}
    
    private <T> T callAndWait(Callable<T> c)
    {
        Future<T> f = executorService.submit(c);
        T t;
        try
        {
            t = f.get();
        }
        catch (InterruptedException e)
        {
            Thread.interrupted();
            return null;
        }
        catch (ExecutionException e)
        {
            throw new RuntimeException(e);
        }
        return t;
    }

    /**
     * Constructor, sets up and starts the Fluency TTS generator without a viseme mapping
     */
    public Fluency8TTSGenerator() 
    {
        this(new NullPhonemeToVisemeMapping());
    }

    /**
     * Constructor, sets up and starts the Fluency TTS generator
     * 
     * @param visemeMappingFile
     *            the location of the phoneme to viseme mapping (e.g., "phoneme2viseme/fluency2ikp.xml")
     */
    public Fluency8TTSGenerator(PhonemeToVisemeMapping ptvm)
    {
        if (ptvm==null) ptvm = new NullPhonemeToVisemeMapping();
    	visemeMapping = ptvm;
        initialized = false;
        executorService = Executors.newSingleThreadExecutor();
        int error = callAndWait(new Callable<Integer>()
        {
            public Integer call()
            {
                return FluencyInit();
            }
        });
        if (error == -1) throw new RuntimeException("Fluency8TTSGenerator initialization failed");
        initialized = (error == 0);
    }

    /**
     * Destroys the tts generator, it should no longer be used after calling this.
     */
    public synchronized void cleanup()
    {
        if (initialized)
        {
            callAndWait(new Callable<Integer>()
            {
                public Integer call()
                {
                    return FluencyCleanup();
                }
            });
            initialized = false;
        }
        else
        {
            logger.warn("Attempting to cleanup Fluency8TTSGenerator that was never initialized");
        }
        shutdown();
    }

    private void shutdown()
    {
        executorService.shutdown();
        try
        {
            executorService.awaitTermination(1000, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            Thread.interrupted();
        }
    }

    @Override
    protected void finalize()
    {
        if (initialized)
        {
            cleanup();
        }
        else
        {
            shutdown();
        }        
    }

    @Override
    public synchronized TimingInfo getTiming(String text)
    {
        if (!initialized)
        {
            logger.error("Not initialized in getTiming");
            throw new RuntimeException("Not initialized");
        }
        speak(text, true, null);
        return getAndClearTimingInfo();
    }

    public synchronized String getVoice()
    {
        return speaker;
    }
    
    @Override
    public synchronized void setVoice(final String speaker)
    {
        if (!initialized)
        {
            logger.error("Not initialized in setVoice");
            throw new RuntimeException("Not initialized");
        }
        int error = callAndWait(new Callable<Integer>()
        {
            public Integer call()
            {
                return FluencySetSpeaker(speaker);
            }
        });
        if (error==0)
        {
            this.speaker = speaker;
        }
        else
        {
            logger.error("Voice not available: {}", speaker);
        }
    }

    @Override
    public synchronized TimingInfo speak(String str)
    {
        if (!initialized)
        {
            logger.error("Not initialized in speak");
            throw new RuntimeException("Not initialized");
        }
        speak(str, false, null);
        return getAndClearTimingInfo();
    }

    private void speak(String str, boolean timeOnly, final String filename)
    {
        text = str + " ";
        currentWordOffset = 0;
        prevOffset=0;
        prevLength=0;
        currentWord = null;
        currentPhonemes.clear();
        currentVisimes.clear();
        wordDescriptions.clear();
        bookmarks.clear();
        visimes.clear();
        currentBookmarks.clear();
        if (timeOnly)
        {
            int result = callAndWait(new Callable<Integer>()
            {
                public Integer call()
                {
                    try
                    {
                        //make tmp filename
                        Path tmpFile = Files.createTempFile(null,null);
                        int result = FluencySpeakToFile(text,tmpFile.toString());
                        //delete tmp file
                        tmpFile.toFile().delete();
                        return result;
                    }
                    catch (Exception e)
                    {
                        logger.error("Error in dummyspeak:");
                        e.printStackTrace();
                        return -1;
                    }
                }
            });

            if (result != 0)
            {
                throw new RuntimeException("Error in FluencyDummySpeak call");
            }
        }
        else if (filename == null)
        {
            int result = callAndWait(new Callable<Integer>()
            {
                public Integer call()
                {
                    return FluencySpeak(text);
                }
            });

            if (result != 0)
            {
                throw new RuntimeException("Error in FluencySpeak call");
            }
        }
        else
        {
            int result = callAndWait(new Callable<Integer>()
            {
                public Integer call()
                {
                    return FluencySpeakToFile(text, filename);
                }
            });

            if (result != 0)
            {
                throw new RuntimeException("Error in FluencySpeakToFile call");
            }
        }
        //add the last word still standing
        WordDescription wd = new WordDescription(currentWord, currentPhonemes, currentVisimes);
        wordDescriptions.add(wd);
        
        for (BookMarker bmr : currentBookmarks)
        {
            // System.out.println("Adding bookmark at the end of sentence: "+bmr.name);
            Bookmark b;
            if (bmr.wordStart)
            {
                b = new Bookmark(bmr.name, wd, currentWordOffset);
            }
            else
            {
                b = new Bookmark(bmr.name, null, currentWordOffset + wd.getDuration());
            }
            //System.out.println("adding bookmark: " + bmr.name + " " + b);
            bookmarks.add(b);
        }
        
        //collect all word-by-word visemes into one list
        for (WordDescription wdesc : wordDescriptions)
        {
            for (Visime v : wdesc.getVisimes())
            {
                visimes.add(v);
            }
        }
    }

    private void phonemeCallback(String phoneme, int duration, String nextPhoneme, int stress)
    {
    	int phnr = PhonemeNameToNumber.getPhonemeNumber(phoneme);
    	int nphnr = PhonemeNameToNumber.getPhonemeNumber(nextPhoneme);
        //System.out.println("phcall " + phoneme + " " + duration + " " + nextPhoneme + " " + stress);
    	currentPhonemes.add(new Phoneme(phnr, duration, stress == 1));
        
        int visime = visemeMapping.getVisemeForPhoneme(PhonemeNameToNumber.getPhonemeNumber(phoneme));
        int nextVisime = visemeMapping.getVisemeForPhoneme(PhonemeNameToNumber.getPhonemeNumber(nextPhoneme));
    	if (phnr >= 1 && phnr <= 3) //h, H, ? --> use next phoneme for viseme!
    	{
            visime = nextVisime;
    	}
   		currentVisimes.add(new Visime(visime, duration, stress == 1));
        if (callback != null)
        {
            callback.phonemeCallback(phnr, duration, nphnr, stress == 1);
        }
        if (permanentCallback != null)
        {
            permanentCallback.phonemeCallback(phnr, duration, nphnr, stress == 1);
        }
        if (callback != null)
        {
            callback.visimeCallback(visime, duration, nextVisime, stress == 1);
        }
        if (permanentCallback != null)
        {
            permanentCallback.visimeCallback(visime, duration, nextVisime, stress == 1);
        }
    }

    private void bookmarkCallback(String bookmark)
    {
        //System.out.println("Bookmark "+fluencySyncToBmlSync.get(bookmark)+"!"+currentPhonemes.size());
        BookMarker m;
        if(currentPhonemes.size()>0&&wordDescriptions.size()>0)
        {
            //it's a bookmark at the end of the sentence; do something with the offset.
            m = new BookMarker(fluencySyncToBmlSync.get(bookmark), false, true);
        }
        else
        {
            m = new BookMarker(fluencySyncToBmlSync.get(bookmark), false);
        }

        currentBookmarks.add(m);
        if (callback != null)
        {
            callback.bookmarkCallback(bookmark);
        }
        if (permanentCallback != null)
        {
            permanentCallback.bookmarkCallback(bookmark);
        }
    }

    private void handleBookmarks(WordDescription wd)
    {
        ArrayList<BookMarker> removeMarks = new ArrayList<BookMarker>();
        for (BookMarker bm : currentBookmarks)
        {
            if (bm.isEnd && bm.wordStart)
            {
                removeMarks.add(bm);
                //System.out.println("Bookmarker => bookmark "+currentWordOffset+" "+bm.name);
                Bookmark b = new Bookmark(bm.name, null, currentWordOffset+wd.getDuration());
                bookmarks.add(b);
                //System.out.println("adding "+b);                
            }
            else if (!bm.isEnd && bm.wordStart)
            {
                removeMarks.add(bm);
                //System.out.println("Bookmarker => bookmark "+currentWordOffset+" "+bm.name);
                Bookmark b = new Bookmark(bm.name, wd, currentWordOffset);
                bookmarks.add(b);
                //System.out.println("adding "+b);
            }
            else
            {
                // System.out.println("Bookmarker set word start "+bm.name);
                bm.wordStart = true;
            }
        }
        currentBookmarks.removeAll(removeMarks);
    }

    private void wordBoundryCallback(int offset, int length)
    {
        WordDescription wd = null;
        //System.out.println("Word boundry "+currentWord + " " + offset + " " + length);
        if (currentWord == null)
        {
            handleBookmarks(wd);
        }
        else
        {
            handleBookmarks(wd);
//            if (wordBeforePhoneme)
  //          {
                wd = new WordDescription(currentWord, currentPhonemes, currentVisimes);
                wordDescriptions.add(wd);
                currentPhonemes = new ArrayList<Phoneme>();
                currentVisimes = new ArrayList<Visime>();
    /*        }
            else
            {
                Phoneme p = currentPhonemes.get(currentPhonemes.size() - 1);
                currentPhonemes.remove(p);
                Visime v = currentVisimes.get(currentVisimes.size() - 1);
                currentVisimes.remove(v);

                wd = new WordDescription(currentWord, currentPhonemes, currentVisimes);
                wordDescriptions.add(wd);
                currentPhonemes = new ArrayList<Phoneme>();
                currentPhonemes.add(p);

                currentVisimes = new ArrayList<Visime>();
                currentVisimes.add(v);
            }*/
            handleBookmarks(wd);
            currentWordOffset += wd.getDuration();
            if (offset==-1)
            {
                offset=prevOffset+prevLength;
                length=0;
            }
            prevOffset = offset;
            prevLength = length;
        }
        currentWord = text.substring(offset, offset + length);
        if (callback != null)
        {
            callback.wordBoundryCallback(offset, length);
        }
        if (permanentCallback != null)
        {
            permanentCallback.wordBoundryCallback(offset, length);
        }
    }

    private void sentenceBoundryCallback(int offset, int length)
    {
        //System.out.println("Sentence boundary");
        if (callback != null)
        {
            callback.sentenceBoundryCallback(offset, length);
        }
        if (permanentCallback != null)
        {
            permanentCallback.sentenceBoundryCallback(offset, length);
        }
    }

    private boolean stopCallback()
    {
        if (callback != null)
        {
            return callback.stopCallback();
        }
        if (permanentCallback != null)
        {
            return permanentCallback.stopCallback();
        }
        return false;
    }

    private static final class BookMarker
    {
        private BookMarker(String n, boolean ws)
        {
            this(n,ws,false);
        }
        private BookMarker(String n, boolean ws, boolean isEndOfSentence)
        {
            name = n;
            wordStart = ws;
            isEnd = isEndOfSentence;
        }

        private String name;

        private boolean wordStart;
        
        private boolean isEnd;
    }

    static
    {
        System.loadLibrary("fluency8interface");
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
        //TODO: misschien op dit punt even doorlopen en duplicaten samenvoegen
        visimes.clear();
        fluencySyncToBmlSync.clear();
        return new TimingInfo(des, bms, vis);
    }

    @Override
    public synchronized TimingInfo getBMLTiming(String s)
    {
        if (!initialized)
        {
            logger.error("Not initialized in getBMLTiming");
            throw new RuntimeException("Not initialized");
        }
        speak(BMLTextUtil.BMLToFluency(s,fluencySyncToBmlSync), true, null);
        return getAndClearTimingInfo();
    }

    @Override
    public synchronized TimingInfo speakBML(String s)
    {
        if (!initialized)
        {
            logger.error("Not initialized in speakBML");
            throw new RuntimeException("Not initialized");
        }
        speak(BMLTextUtil.BMLToFluency(s, fluencySyncToBmlSync), false, null);
        return getAndClearTimingInfo();
    }

    @Override
    public synchronized TimingInfo speakBMLToFile(String s, String filename)
    {
        if (!initialized)
        {
            logger.error("Not initialized in speakBMLToFile text:{} filename:{}", s, filename);
            throw new RuntimeException("Not initialized");
        }
        return speakToFile(BMLTextUtil.BMLToFluency(s,fluencySyncToBmlSync), filename);
    }

    @Override
    public synchronized TimingInfo speakToFile(String text, String filename)
    {
        if (!initialized)
        {
            logger.error("Not initialized in speakToFile");
            throw new RuntimeException("Not initialized");
        }
        speak(text, false, filename);
        return getAndClearTimingInfo();
    }

    @Override
    public synchronized String[] getVoices()
    {
        if (!initialized)
        {
            logger.error("Not initialized in getVoices");
            throw new RuntimeException("Not initialized");
        }

        return callAndWait(new Callable<String[]>()
        {
            public String[] call()
            {
                return FluencyGetVoices();
            }
        });
    }

    /*
    public static void main(String[] args) throws Exception
    {
        Fluency8TTSGenerator  ftg = new Fluency8TTSGenerator();
        //ftg.speakToFile("Dit is een andere test test", "tttqp.wav");
        //ftg.speak("Dit is een andere test");
        //ftg.getTiming("blabla");

        String[] voices = ftg.getVoices();
        System.out.println("Number of voices: " + voices.length);
        for (int i = 0; i < voices.length; i++)
        {
            System.out.println("Voice: " + voices[i]);
        }

        //ftg.setVoice("Piet"); //will not do a voice change as the voice does not exist; also Fluency will pop up a warning dialog
        //ftg.setVoice("Isabelle (MBROLA)"); //this one generally exists...
        //ftg.speakBML("test sync no digits <sync id=\"sf\"/> test");
        ftg.speakBML("<sync id=\"dd\"/>Dit<sync id=\"dwa\"/> is <sync id=\"pddd\"/>een<sync id=\"fdsd\"/> andere <sync id=\"sf1\"/>test.<sync id=\"sf2\"/>");
        TimingInfo ti = ftg.speak("test");
        System.out.println(ti.getWordDescriptions().get(0).getWord());
        System.out.println(ti.getDuration());
                 
        ftg.cleanup();
    }
    */
}
