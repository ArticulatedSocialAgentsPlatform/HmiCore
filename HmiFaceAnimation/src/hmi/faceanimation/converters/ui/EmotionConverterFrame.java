package hmi.faceanimation.converters.ui;

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