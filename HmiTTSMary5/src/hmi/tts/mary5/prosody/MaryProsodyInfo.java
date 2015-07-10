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
package hmi.tts.mary5.prosody;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.common.collect.ImmutableList;

/**
 * Prosody information of a chunk of speech
 * @author herwinvw
 */
public class MaryProsodyInfo
{
    @Data
    public class Syllable
    {
        final int stress;
        final List<Phoneme> phonemes;
        final int startTime;

        public int getEndTime()
        {
            if (phonemes.isEmpty())
            {
                return 0;
            }
            return phonemes.get(phonemes.size() - 1).getEndTime();
        }
    }

    @Data
    public class F0Frame
    {
        final int percentage;
        final double frequency;
    }

    @Data
    public class Phoneme
    {
        final String phoneme;
        final int startTime;
        final int endTime;
        final int duration;
        final List<F0Frame> f0Frames;
    }

    public interface PhraseElement
    {
        int getEndTime();

        int getStartTime();
    }

    @Data
    public class PhraseBoundary implements PhraseElement
    {
        final int breakindex;
        final String tone;
        final int startTime;
        final int endTime;
    }

    @Data
    public class Word implements PhraseElement
    {
        final String word;
        final String accent;
        final String pos;
        final List<Syllable> syllables;
        final int startTime;
        final int endTime;
    }

    private List<PhraseElement> phraseElements = new ArrayList<PhraseElement>();

    @Getter
    private int duration = 0;

    public ImmutableList<PhraseElement> getPhraseElements()
    {
        return ImmutableList.copyOf(phraseElements);
    }

    private String getAttribute(Node n, String attr, String defaultValue)
    {
        Node attrNode = n.getAttributes().getNamedItem(attr);
        if (attrNode == null)
        {
            return defaultValue;
        }
        return attrNode.getNodeValue();
    }

    private PhraseBoundary parseBoundary(Node n, int startTime)
    {
        int duration = (int)Math.round(Double.parseDouble(getAttribute(n, "duration", "0")));
        int breakindex = Integer.parseInt(getAttribute(n, "breakindex", "0"));
        String tone = getAttribute(n, "tone", "");
        return new PhraseBoundary(breakindex, tone, startTime, startTime + duration);
    }

    private Syllable parseSyllable(Node n, int startTime)
    {
        List<Phoneme> phonemes = new ArrayList<Phoneme>();
        Node child = n.getFirstChild();
        while (child != null)
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                if (child.getNodeName().equals("ph"))
                {
                    Phoneme ph = parsePhoneme(child, startTime);
                    phonemes.add(ph);
                    startTime = ph.getEndTime();
                }
            }
            child = child.getNextSibling();
        }
        int stress = Integer.parseInt(getAttribute(n, "stress", "0"));
        return new Syllable(stress, phonemes, startTime);
    }

    private List<F0Frame> getF0Frames(String f0)
    {
        List<F0Frame> frames = new ArrayList<F0Frame>();
        if (f0.contains("("))
        {
            f0 = f0.replace(')', ' ');
            String split[] = f0.split("\\(");
            for (String frame : split)
            {
                if (!frame.trim().isEmpty())
                {
                    String fr[] = frame.split(",");
                    frames.add(new F0Frame(Integer.parseInt(fr[0].trim()), Integer.parseInt(fr[1].trim())));
                }
            }
        }
        return ImmutableList.copyOf(frames);
    }

    private Phoneme parsePhoneme(Node n, int startTime)
    {
        int duration = (int)Math.round(Double.parseDouble(getAttribute(n, "d", "0")));
        String phoneme = getAttribute(n, "p", "");
        String f0 = getAttribute(n, "f0", "");
        return new Phoneme(phoneme, startTime, startTime + duration, duration, getF0Frames(f0));
    }

    private Word parseWord(Node n, int startTime)
    {
        List<Syllable> syllables = new ArrayList<Syllable>();
        Node child = n.getFirstChild();
        int endTime = startTime;
        while (child != null)
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                if (child.getNodeName().equals("syllable"))
                {
                    Syllable syl = parseSyllable(child, endTime);
                    syllables.add(syl);
                    endTime = syl.getEndTime();
                }
            }
            child = child.getNextSibling();
        }
        return new Word(n.getTextContent().trim(), getAttribute(n, "accent", null), getAttribute(n, "pos", null), syllables, startTime,
                endTime);
    }

    private int parsePhrase(Node node, int startTime)
    {
        Node child = node.getFirstChild();
        while (child != null)
        {
            if (child.getNodeName().equals("t"))
            {
                Word wd = parseWord(child, startTime);
                phraseElements.add(wd);
                startTime = wd.getEndTime();
            }
            else if (child.getNodeName().equals("boundary"))
            {
                PhraseBoundary pb = parseBoundary(child, startTime);
                phraseElements.add(pb);
                startTime = pb.getEndTime();
            }
            else
            {
                //check one or more levels deeper
                startTime = parsePhrase(child, startTime);
            }
            child = child.getNextSibling();
        }
        return startTime;
    }

    private int parseChunk(Node node, int startTime)
    {
        if (node.getNodeName().equals("phrase"))
        {
            startTime = parsePhrase(node, startTime);
        }
        Node firstChild = node.getFirstChild();
        if (firstChild != null)
        {
            startTime = parseChunk(firstChild, startTime);
        }
        Node sibling = node.getNextSibling();
        if (sibling != null)
        {
            startTime = parseChunk(sibling, startTime);
        }
        return startTime;
    }    
    
    public void parse(Document doc)
    {
        if (doc.hasChildNodes())
        {
            duration = parseChunk(doc.getFirstChild(), 0);            
        }
    }

    public List<Word> getWords()
    {
        List<Word> words = new ArrayList<Word>();
        for (PhraseElement pe : phraseElements)
        {
            if (pe instanceof Word)
            {
                words.add((Word) pe);
            }
        }
        return ImmutableList.copyOf(words);
    }

    public List<Syllable> getSyllables()
    {
        List<Syllable> syllables = new ArrayList<Syllable>();
        for (Word wd : getWords())
        {
            syllables.addAll(wd.getSyllables());
        }
        return ImmutableList.copyOf(syllables);
    }

    public List<Phoneme> getPhonemes()
    {
        List<Phoneme> phonemes = new ArrayList<Phoneme>();
        for (Syllable syl : getSyllables())
        {
            phonemes.addAll(syl.getPhonemes());
        }
        return ImmutableList.copyOf(phonemes);
    }

    private int findNextIndexNonZero(double[] contour, int current, int end)
    {
        for (int i = current + 1; i < end; i++)
        {
            if (contour[i] != 0)
            {
                return i;
            }
        }
        return -1;
    }

    private void interpolateNonZeroValues(double contour[], int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            if (contour[i] == 0)
            {
                int index = findNextIndexNonZero(contour, i, end);
                // System.out.println("i: "+i+"index: "+index);
                if (index == -1)
                {
                    for (int j = i; j < end; j++)
                    {
                        contour[j] = contour[j - 1];
                    }
                    break;
                }
                else
                {
                    for (int j = i; j < index; j++)
                    {
                        // contour[j] = contour[i-1] * (index - j) + contour[index] * (j - (i-1)) / ( index - (i-1) );
                        if (i == start)
                        {
                            contour[j] = contour[index];
                        }
                        else
                        {
                            contour[j] = contour[j - 1] + ((contour[index] - contour[i - 1]) / (index - i + 1));
                        }
                    }
                    i = index - 1;
                }
            }
        }
    }

    public double[] getF0Contour(int frameRate)
    {
        double contour[] = new double[1 + (frameRate * duration) / 1000];
        int segmentStart = 0;
        int segmentEnd = 0;
        boolean voicedSegment = false;
        int prevPhEnd = 0;
        for (Phoneme ph : getPhonemes())
        {
            if(ph.getF0Frames().isEmpty())
            {
                if(voicedSegment)
                {
                    voicedSegment = false;
                    interpolateNonZeroValues(contour, segmentStart, segmentEnd);                    
                }
            }
            else
            {
                if(ph.getStartTime()>prevPhEnd)
                {
                    interpolateNonZeroValues(contour, segmentStart, segmentEnd);
                    segmentStart = (ph.getStartTime() * (contour.length - 1)) / duration;
                }
                if(!voicedSegment)
                {
                    voicedSegment = true;
                    segmentStart = (ph.getStartTime() * (contour.length - 1)) / duration;    
                }
                segmentEnd = (ph.getEndTime() * (contour.length - 1)) / duration;                
                for (F0Frame fr : ph.getF0Frames())
                {
                    int frameTime = ph.getStartTime() + ((ph.getEndTime() - ph.getStartTime()) * fr.getPercentage()) / 100;
                    int frameIndex = (frameTime * (contour.length - 1)) / duration;                
                    contour[frameIndex] = fr.getFrequency();
                }
            }
            prevPhEnd = ph.getEndTime();
        }
        if(voicedSegment)
        {
            if(contour.length-segmentEnd<=1)
            {
                interpolateNonZeroValues(contour, segmentStart, contour.length);
            }
            else
            {
                interpolateNonZeroValues(contour, segmentStart, segmentEnd);
            }
        }
        return contour;
    }
}
