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

import hmi.faceanimation.*;
import hmi.faceanimation.converters.EmotionConverter;
import hmi.faceanimation.model.MPEG4Configuration;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class EmotionPanel extends JPanel implements ParameterListener
{
  private static final long serialVersionUID = 8365008904970611946L;
  private EmotionConverter emotionConverter;
  private MPEG4Configuration mpeg4Config = new MPEG4Configuration();
  private FaceController faceController;
  
  public EmotionPanel(EmotionConverter ec, FaceController fc)
  {
    faceController = fc;
    emotionConverter = ec;
    setLayout(new BorderLayout());
    EmotionWheelPanel ewp = new EmotionWheelPanel();
    add(ewp, BorderLayout.CENTER);
    
    JPanel infoPanel = new JPanel(new GridLayout(0, 1));
    
    // Label for the angle.
    final JLabel labelAngle = new JLabel();
    ewp.addAngleListener(new AngleListener()
    {
      @Override
      public void angleChanged(float angle)
      {
        labelAngle.setText("Angle: " + new DecimalFormat("###").format(angle) + "°");
      }
    });
    infoPanel.add(labelAngle);
    
    // Label for the activation.
    final JLabel labelActivation = new JLabel();
    ewp.addActivationListener(new ActivationListener()
    {
      @Override
      public void activationChanged(float activation)
      {
        labelActivation.setText("Activation: " + new DecimalFormat("0.00").format(activation));
      }
    });
    infoPanel.add(labelActivation);
    
    // Profile choice links.
    for (EmotionConverter.AE ae : EmotionConverter.AE.values())
    {
      int numProfiles = emotionConverter.getNumProfiles(ae);
      for (int i=0; i<numProfiles; i++)
      {
        final JLink linkProfile = new JLink(ae.getFriendlyName() + " #" + (i + 1), ae, i);
        linkProfile.addMouseListener(new ProfileChoiceMouseListener(emotionConverter, ewp));
        infoPanel.add(linkProfile);
      }
    }
    
    add(infoPanel, BorderLayout.EAST);
    
    // Register this panel with the EmotionWheelPanel to receive
    // parameter changes.
    ewp.addParameterListener(this);
    
  }
  
  @Override
  public void parameterChanged(float angle, float activation)
  {
    faceController.removeMPEG4Configuration(mpeg4Config);
    mpeg4Config = emotionConverter.convert(angle, activation);
    faceController.addMPEG4Configuration(mpeg4Config);
    if (faceController instanceof FaceControllerPose)
    {
        ((FaceControllerPose)faceController).toTarget();
    }
  }
  public void clear()
  {
    faceController.removeMPEG4Configuration(mpeg4Config);
    mpeg4Config = new MPEG4Configuration();
  }
  class ProfileChoiceMouseListener implements MouseListener
  {
    private EmotionConverter ec;
    private EmotionWheelPanel ewp;
    public ProfileChoiceMouseListener(EmotionConverter ec, EmotionWheelPanel ewp)
    {
      this.ec = ec;
      this.ewp = ewp;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) { }
    
    @Override
    public void mousePressed(MouseEvent e) { }
    
    @Override
    public void mouseExited(MouseEvent e) { }
    
    @Override
    public void mouseEntered(MouseEvent e) { }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
      JLink linkProfile = (JLink) e.getSource();
      EmotionConverter.AE ae = linkProfile.getAE();
      int index = linkProfile.getIndex();
      ec.setPreferredProfile(ae, index);
      parameterChanged(ewp.getAngle(), ewp.getActivation());
    }
  }
}
