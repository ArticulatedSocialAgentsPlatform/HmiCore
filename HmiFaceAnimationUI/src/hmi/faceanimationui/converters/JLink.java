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

import hmi.faceanimation.converters.EmotionConverter;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JLabel;

class JLink extends JLabel
{
    private static final long serialVersionUID = -7374381763928406669L;
    private EmotionConverter.AE ae;
    private int index;

    public JLink(String text, EmotionConverter.AE ae, int index)
    {
        super(text);
        this.ae = ae;
        this.index = index;
        setForeground(Color.BLUE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public EmotionConverter.AE getAE()
    {
        return ae;
    }

    public int getIndex()
    {
        return index;
    }
}