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