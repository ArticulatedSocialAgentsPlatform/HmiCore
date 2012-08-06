package opengltest;

import javax.swing.JFrame;

import org.junit.Test;

public class HmiOpenGLTest
{
    @Test
    public void test() throws InterruptedException
    {
        JFrame frame = new JFrame();
        frame.setSize(100,100);
        frame.setVisible(true);
        Thread.sleep(2000);
        frame.dispose();
    }
}
