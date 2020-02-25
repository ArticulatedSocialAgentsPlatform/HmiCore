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

import hmi.faceanimation.FaceController;
import hmi.faceanimation.converters.FACSConverter;
import hmi.faceanimation.model.FACSConfiguration;
import hmi.faceanimation.model.MPEG4Configuration;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Converts FACS AU's to MPEG-4 FAP's and sends them to a face controller
 * @author PaulRC
 */
public class FACSConverterFrame extends JFrame implements ConfigListener
{
  private static final long serialVersionUID = -633623737546748897L;
  public FACSConverter facsConverter;
  private MPEG4Configuration mpeg4Config = new MPEG4Configuration();
  private FaceController faceController;
  //private FACSConfiguration facsConfig = null;
  private Logger logger = LoggerFactory.getLogger(FACSConverterFrame.class.getName());
  
  public FACSConverterFrame(FaceController faceController)
  {
    this (new FACSConverter(), faceController);
  }
  public FACSConverterFrame(FACSConverter fc, FaceController faceController)
  {
    facsConverter = fc;
    this.faceController = faceController;
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch(Exception e) {
      logger.warn("Error setting native LAF: ",e);
    }
    
    FACSPanel fp = new FACSPanel(new FACSConfiguration(), facsConverter);
    fp.addConfigListener(this);
    JScrollPane sp = new JScrollPane(fp);
    getContentPane().add(sp);
    setPreferredSize(new Dimension(600, 600));
    setLocation(100, 100);
    setTitle("FACS to MPEG4 converter");
    pack();
    
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addWindowListener( 
      new WindowAdapter() 
      {
            @Override
            public void windowClosing(WindowEvent e)
            {
              /*if (facsConfig!=null)
                {
                  System.out.println(facsConfig.toXMLString());
                  FACSConfiguration fc = new FACSConfiguration();
                  fc.readXML(facsConfig.toXMLString());
                  System.out.println(fc.toXMLString());
                } */
                clear();
            }
      }
    );
          
    setVisible(true);
    
  }
  public void clear()
  {
    faceController.removeMPEG4Configuration(mpeg4Config);
    mpeg4Config = new MPEG4Configuration();
  }
  @Override
  public void configChanged(FACSConfiguration config)
  {
    //facsConfig = config;
    faceController.removeMPEG4Configuration(mpeg4Config);
    facsConverter.convert(config, mpeg4Config);
    faceController.addMPEG4Configuration(mpeg4Config);
  }

}