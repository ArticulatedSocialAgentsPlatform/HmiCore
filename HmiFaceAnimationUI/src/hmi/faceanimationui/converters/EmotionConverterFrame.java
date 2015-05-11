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
package hmi.faceanimationui.converters;

import hmi.faceanimation.FaceController;
import hmi.faceanimation.converters.EmotionConverter;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Converts (for now) FACS AU's in MPEG-4 FAP's
 * 
 * @author PaulRC
 */
public class EmotionConverterFrame extends JFrame
{
    private static final long serialVersionUID = -469752997219652755L;
    private EmotionPanel emotionPanel;

    private EmotionConverter emotionConverter;
    //private FaceController faceController;

    public EmotionConverterFrame(FaceController faceController)
    {
        this(new EmotionConverter(), faceController);
    }

    public EmotionConverterFrame(EmotionConverter ec, FaceController faceController)
    {
        emotionConverter = ec;
        //this.faceController = faceController;
        emotionPanel = new EmotionPanel(emotionConverter, faceController);
        getContentPane().add(emotionPanel);
        setPreferredSize(new Dimension(800, 600));
        setLocation(100, 100);
        setTitle("Emotion wheel to MPEG4 converter");
        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                emotionPanel.clear();
            }
        });
        setVisible(true);
    }
}