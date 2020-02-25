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
package hmi.animationui;

import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update ik positions
 * 
 * @author hvanwelbergen
 */
public class IKView
{
    @Getter
    private JPanel panel = new JPanel();
    
    public IKView(List<AnalyticalIKController> controllers)
    {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (AnalyticalIKController c:controllers)
        {
            IKPanel ikPanel = new IKPanel(c.getId(), c, c.getStartPos(), c.getStartSwivel());
            panel.add(ikPanel.getPanel());            
        }
        panel.add(Box.createVerticalGlue());
    }    
}
