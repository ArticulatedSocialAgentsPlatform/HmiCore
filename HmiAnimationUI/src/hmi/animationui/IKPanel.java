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

import hmi.math.Vec3f;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * UI element for the IK positioning of a single limb
 * @author hvanwelbergen
 * 
 */
public class IKPanel
{
    @Getter
    private final JPanel panel = new JPanel();

    private final JFormattedTextField xField, yField, zField, swivelField;
    private final AnalyticalIKController ikController;
    private final JCheckBox autoSwivel = new JCheckBox("auto swivel");
    private final JButton applyButton = new JButton("Apply");
    
    private final AutoSwivelPanel autoSwivelPanel = new AutoSwivelPanel();
    
    
    private JPanel setupPosBox(String coord, JFormattedTextField field)
    {
        JPanel posPanel = new JPanel();

        JLabel label = new JLabel(coord);
        posPanel.add(label);
        posPanel.add(field);

        field.addPropertyChangeListener("value", new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                update();
            }
        });
        return posPanel;
    }

    private void update()
    {
        float swivel = getSwivel();
        autoSwivelPanel.setFormerSwivel(swivel);
        ikController.setJointRotations(getPosition(), swivel);
    }
    
    
    
    public IKPanel(String limbName, AnalyticalIKController c, float[] startPos, float startSwivel)
    {
        
        
        this.ikController = c;
        JLabel label = new JLabel(limbName);
        
        panel.add(label);

        xField = new JFormattedTextField(NumberFormat.getNumberInstance());
        yField = new JFormattedTextField(NumberFormat.getNumberInstance());
        zField = new JFormattedTextField(NumberFormat.getNumberInstance());
        swivelField = new JFormattedTextField(NumberFormat.getNumberInstance());
        
        xField.setText(""+startPos[0]);
        yField.setText(""+startPos[1]);
        zField.setText(""+startPos[2]);
        swivelField.setText(""+startSwivel);

        panel.add(setupPosBox("x:", xField));
        panel.add(setupPosBox("y:", yField));
        panel.add(setupPosBox("z:", zField));
        panel.add(autoSwivel);
        
        final JPanel manualSwivelPanel = setupPosBox("swivel:", swivelField);
        final JPanel autoSwivP = autoSwivelPanel.getJPanel();
        
        autoSwivel.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(autoSwivel.isSelected())
                {
                    manualSwivelPanel.setVisible(false);
                    autoSwivP.setVisible(true);
                }                
                else
                {
                    manualSwivelPanel.setVisible(true);
                    autoSwivP.setVisible(false);
                }
            }
        });
        autoSwivP.setVisible(false);
        panel.add(autoSwivP);
        panel.add(manualSwivelPanel);
        applyButton.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                update();                
                autoSwivelPanel.update();
            }
        });
        panel.add(applyButton);
    }

    private float getSwivel()
    {
        try
        {
            swivelField.commitEdit();
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        
        if(autoSwivel.isSelected())
        {
            return (float)autoSwivelPanel.getSwivel();
        }
        else
        {
            return ((Number) swivelField.getValue()).floatValue();            
        }
    }

    private float[] getPosition()
    {
        try
        {
            xField.commitEdit();
            yField.commitEdit();        
            zField.commitEdit();
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        float x = ((Number) xField.getValue()).floatValue();
        float y = ((Number) yField.getValue()).floatValue();
        float z = ((Number) zField.getValue()).floatValue();
        return Vec3f.getVec3f(x, y, z);
    }
}
