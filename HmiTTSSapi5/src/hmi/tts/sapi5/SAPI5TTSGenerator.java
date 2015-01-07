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
package hmi.tts.sapi5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import hmi.tts.*;
import hmi.tts.util.BMLTextUtil;

/**
 * TTSGenerator for Microsoft SAPI5
 * 
 * @author Herwin
 */
@ThreadSafe
public class SAPI5TTSGenerator extends AbstractTTSGenerator
{
    private native int SAPISpeak(String text);

    private native int SAPIDummySpeak(String text);

    private native int SAPIInit();

    private native int SAPICleanup();

    private native int SAPISetSpeaker(String speaker);

    private native int SAPISpeakToFile(String text, String filename);

    private native String[] SAPIGetVoices();

    private String speaker;
    
    @GuardedBy("this")
    private boolean initialized;

    // temp vars used to gather bookmarks, visimes and phonemes while speaking
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
    private boolean wordBeforePhoneme = false;

    @GuardedBy("this")
    private String text;

    private ExecutorService executorService;

    private static final Logger logger = LoggerFactory.getLogger(SAPI5TTSGenerator.class.getName());

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

    public SAPI5TTSGenerator()
    {
        initialized = false;
        executorService = Executors.newSingleThreadExecutor();
        int error = callAndWait(new Callable<Integer>()
        {
            public Integer call()
            {
                return SAPIInit();
            }
        });
        if (error == -1) throw new RuntimeException("SAPI5TTSGenerator initialization failed");
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
                    return SAPICleanup();
                }
            });
            initialized = false;
        }
        else
        {
            logger.warn("Attempting to cleanup SAPI5TTSGenerator that was never initialized");
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
        callAndWait(new Callable<Integer>()
        {
            public Integer call()
            {
                return SAPISetSpeaker(speaker);
            }
        });
        this.speaker = speaker;
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
        text = str;
        currentWordOffset = 0;
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
                    return SAPIDummySpeak(text);
                }
            });

            if (result != 0)
            {
                throw new RuntimeException("Error in SAPIDummySpeak call");
            }
        }
        else if (filename == null)
        {
            int result = callAndWait(new Callable<Integer>()
            {
                public Integer call()
                {
                    return SAPISpeak(text);
                }
            });

            if (result != 0)
            {
                throw new RuntimeException("Error in SAPISpeak call");
            }
        }
        else
        {
            int result = callAndWait(new Callable<Integer>()
            {
                public Integer call()
                {
                    return SAPISpeakToFile(text, filename);
                }
            });

            if (result != 0)
            {
                throw new RuntimeException("Error in SAPISpeakToFile call");
            }
        }

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
            bookmarks.add(b);
        }
        for (WordDescription wdesc : wordDescriptions)
        {
            for (Visime v : wdesc.getVisimes())
            {
                visimes.add(v);
            }
        }
    }

    private void phonemeCallback(int phoneme, int duration, int nextPhoneme, int stress)
    {
        currentPhonemes.add(new Phoneme(phoneme, duration, stress == 1));
        if (callback != null)
        {
            callback.phonemeCallback(phoneme, duration, nextPhoneme, stress == 1);
        }
        if (permanentCallback != null)
        {
            permanentCallback.phonemeCallback(phoneme, duration, nextPhoneme, stress == 1);
        }
    }

    private void visimeCallback(int visime, int duration, int nextVisime, int stress)
    {
        currentVisimes.add(new Visime(visime, duration, stress == 1));
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
        // System.out.println("Bookmark "+bookmark+"!");
        BookMarker m = new BookMarker(bookmark, false);

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
            if (bm.wordStart)
            {
                removeMarks.add(bm);
                // System.out.println("Bookmarker => bookmark "+currentWordOffset+" "+bm.name);
                bookmarks.add(new Bookmark(bm.name, wd, currentWordOffset));
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
        /*
         * Loquendo does a wordBoundryCallback before a phonemeCallback, Microsoft voices create a
         * phonemeCallback before a wordBoundryCallback
         */
        WordDescription wd = null;
        // System.out.println("Word boundry "+currentWord);
        if (currentWord == null)
        {
            if (currentPhonemes.isEmpty())
            {
                wordBeforePhoneme = true;
            }
            else
            {
                wordBeforePhoneme = false;
            }
            handleBookmarks(wd);
        }
        else
        {
            if (wordBeforePhoneme)
            {
                wd = new WordDescription(currentWord, currentPhonemes, currentVisimes);
                wordDescriptions.add(wd);
                currentPhonemes = new ArrayList<Phoneme>();
                currentVisimes = new ArrayList<Visime>();
            }
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
            }
            handleBookmarks(wd);
            currentWordOffset += wd.getDuration();
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
            name = n;
            wordStart = ws;
        }

        private String name;

        private boolean wordStart;
    }

    static
    {
        System.loadLibrary("sapi5interface");
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
    public synchronized TimingInfo getBMLTiming(String s)
    {
        if (!initialized)
        {
            logger.error("Not initialized in getBMLTiming");
            throw new RuntimeException("Not initialized");
        }
        speak(BMLTextUtil.BMLToSAPI(s), true, null);
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
        speak(BMLTextUtil.BMLToSAPI(s), false, null);
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
        return speakToFile(BMLTextUtil.BMLToSAPI(s), filename);
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
                return SAPIGetVoices();
            }
        });
    }
}
