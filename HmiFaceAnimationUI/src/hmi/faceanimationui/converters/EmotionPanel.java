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
