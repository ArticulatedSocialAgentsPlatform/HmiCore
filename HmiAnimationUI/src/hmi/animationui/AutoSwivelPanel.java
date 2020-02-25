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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.ParseException;

import hmi.neurophysics.BiologicalSwivelCostsEvaluator;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lombok.Setter;

public class AutoSwivelPanel
{
    private JPanel panel = new JPanel();
    private BiologicalSwivelCostsEvaluator eval;
    private double minSwivel = -2;
    private double maxSwivel = 1;
    private double freedomOfTheGaussianMean = 0.1;
    private JLabel swivelLabel = new JLabel(""+0);
    
    @Setter
    private double formerSwivel = 0;
    
    private final JFormattedTextField minSwivelField, maxSwivelField, freedomOfTheGaussianMeanField;
    
    private JPanel setupSwivelParam(String id, JFormattedTextField field)
    {
        JPanel posPanel = new JPanel();

        JLabel label = new JLabel(id);
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
    
    public AutoSwivelPanel()
    {
        maxSwivelField = new JFormattedTextField(NumberFormat.getNumberInstance());
        minSwivelField = new JFormattedTextField(NumberFormat.getNumberInstance());
        freedomOfTheGaussianMeanField = new JFormattedTextField(NumberFormat.getNumberInstance());
        
        maxSwivelField.setValue(maxSwivel);
        minSwivelField.setValue(minSwivel);
        freedomOfTheGaussianMeanField.setValue(freedomOfTheGaussianMean);
        
        panel.add(setupSwivelParam("min swivel", minSwivelField));
        panel.add(setupSwivelParam("max swivel", maxSwivelField));
        panel.add(setupSwivelParam("freedom of the Gaussian mean", freedomOfTheGaussianMeanField));
        panel.add(swivelLabel);
    }
    
    public void update()
    {
        try
        {
            maxSwivelField.commitEdit();
            minSwivelField.commitEdit();        
            freedomOfTheGaussianMeanField.commitEdit();
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        maxSwivel = ((Number) maxSwivelField.getValue()).floatValue();
        minSwivel = ((Number) minSwivelField.getValue()).floatValue();
        freedomOfTheGaussianMean = ((Number) freedomOfTheGaussianMeanField.getValue()).floatValue();
        swivelLabel.setText(""+getSwivel());
    }
    
    public double getSwivel()
    {
        eval = new BiologicalSwivelCostsEvaluator(minSwivel, maxSwivel, 0.45, freedomOfTheGaussianMean);        
        return eval.getSwivelAngleWithMinCost(formerSwivel);        
    }
    
    public JPanel getJPanel()
    {
        return panel;
    }
}
