package hmi.faceanimation.converters.ui;

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