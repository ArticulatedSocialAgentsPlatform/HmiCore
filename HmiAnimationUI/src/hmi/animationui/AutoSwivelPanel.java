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
