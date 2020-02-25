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
package hmi.util;


import javax.swing.*;


import java.awt.*;

/**
 * A panel showing the count of a Clock.
 * 
 * @author Dennis Reidsma
 */
public class ClockPanel extends JPanel implements ClockListener
{
   
  private SystemClock clock;
  private JLabel timeLabel;
  double prevTime = -500;
  public ClockPanel(SystemClock c)
  {
    clock = c;
    initUI();
    clock.addClockListener(this);
  
  }
  protected void initUI()
  {
    //set font to something larger?
    timeLabel = new JLabel("00:00");
    //initTime(5.645312d);
    timeLabel.setFont(new Font("Times New Roman", Font.BOLD, 50));
    add(timeLabel);
  }
  public void setLabelFont(Font f)
  {
    timeLabel.setFont(f);
  }
  /**
  * initTime() is called before the Clock starts running, and sends some initial time value.
  * This will often equal the time send for the first regular time() call.
  * This is done on the same clock  Thread that is going to send the regular time() calls. 
  */
  public void initTime(double initTime)
  {
    if (initTime - prevTime > 0.010d) 
    {
      timeLabel.setText(String.format("%10.3f", initTime));
      timeLabel.repaint();
      prevTime = initTime;
    }
  }
  
  
  /**
   * time() is called, with the &quot;current time&quot; specified in seconds.
   */
  public void time(double currentTime)
  {
    if (currentTime- prevTime > 0.010d) 
    {
      timeLabel.setText(String.format("%10.3f", currentTime));
      timeLabel.repaint();
      prevTime = currentTime;
    }
  }
}
