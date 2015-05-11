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