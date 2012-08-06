/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.util;


import java.awt.Font;

import javax.swing.JLabel;

/**
 * As ClockPanel, but then a JLabel instead of JPanel
 * 
 * @author Dennis Reidsma
 */
public class ClockLabel extends JLabel implements ClockListener
{
   
  private SystemClock clock;
  double prevTime = -500;
  public ClockLabel(SystemClock c)
  {
    clock = c;
    initUI();
    clock.addClockListener(this);
  
  }
  protected void initUI()
  {
    setFont(new Font("Times New Roman", Font.BOLD, 50));
  }
  public void setLabelFont(Font f)
  {
    setFont(f);
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
      setText(String.format("%10.3f", initTime));
      repaint();
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
      setText(String.format("%10.3f", currentTime));
      repaint();
      prevTime = currentTime;
    }
  }
}
