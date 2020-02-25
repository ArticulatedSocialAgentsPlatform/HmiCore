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