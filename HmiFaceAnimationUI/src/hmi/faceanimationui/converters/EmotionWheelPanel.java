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

import hmi.util.Resources;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class EmotionWheelPanel extends JPanel implements MouseMotionListener, MouseListener
{
    private static final long serialVersionUID = 322877579547536179L;
    private BufferedImage image;
    private int center_x = 245;
    private int center_y = 275;
    private int radius = 235;
    private float angle;
    private float activation;

    final static float MAX_ACTIVATION = 7.0f;

    private ArrayList<ActivationListener> activationListeners = new ArrayList<ActivationListener>();
    private ArrayList<AngleListener> angleListeners = new ArrayList<AngleListener>();
    private ArrayList<ParameterListener> parameterListeners = new ArrayList<ParameterListener>();

    public EmotionWheelPanel()
    {
        try
        {
            String filename = "Humanoids/shared/mpeg4face/emotion_wheel_plutchik.png";
            BufferedInputStream is = new Resources("").getInputStream(filename);
            image = ImageIO.read(is);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        setBorder(new LineBorder(new Color(0, 0, 0)));

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(image, 0, 0, null);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "FE_FLOATING_POINT_EQUALITY", justification = "Comparing with previous value for change")
    private void calculateStuff(int x, int y)
    {
        float old_angle = angle;
        float old_activation = activation;

        calculateAngle(x, y);
        calculateActivation(x, y);

        if (angle != old_angle || activation != old_activation)
            fireParameterChanged();// findbugs warning can be ignored here, comparison with previous value
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "FE_FLOATING_POINT_EQUALITY",
            justification = "Comparing with previous value for change")
    private void calculateAngle(int x, int y)
    {
        float old_angle = angle;

        angle = (float) (Math.atan2(y - center_y, x - center_x) * 180 / Math.PI);
        angle = angle + 45f;
        if (angle < 0)
            angle = angle + 360;

        if (angle != old_angle)
            fireAngleChanged();// findbugs warning can be ignored here, comparison with previous value
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "FE_FLOATING_POINT_EQUALITY", 
            justification = "Comparing with previous value for change")
    private void calculateActivation(int x, int y)
    {
        float old_activation = activation;

        int dx = center_x - x;
        int dy = center_y - y;
        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        activation = (float) (radius - distance) / (radius * 0.90f);
        if (activation < 0)
            activation = 0.0f;
        if (activation > 1)
            activation = 1.0f;
        activation = activation * MAX_ACTIVATION;

        
        if (activation != old_activation)
            fireActivationChanged();
    }

    public float getAngle()
    {
        return angle;
    }

    public float getActivation()
    {
        return activation;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        calculateStuff(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        calculateStuff(e.getX(), e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    public void addActivationListener(ActivationListener al)
    {
        activationListeners.add(al);
    }

    public void removeActivationListener(ActivationListener al)
    {
        activationListeners.remove(al);
    }

    public void fireActivationChanged()
    {
        for (ActivationListener al : activationListeners)
            al.activationChanged(activation);
    }

    public void addAngleListener(AngleListener al)
    {
        angleListeners.add(al);
    }

    public void removeAngleListener(AngleListener al)
    {
        angleListeners.remove(al);
    }

    public void fireAngleChanged()
    {
        for (AngleListener al : angleListeners)
            al.angleChanged(angle);
    }

    public void addParameterListener(ParameterListener pl)
    {
        parameterListeners.add(pl);
    }

    public void removeParameterListener(ParameterListener pl)
    {
        parameterListeners.remove(pl);
    }

    public void fireParameterChanged()
    {
        for (ParameterListener pl : parameterListeners)
            pl.parameterChanged(angle, activation);
    }
}