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
package hmi.graphics.opengl.renderobjects;

import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class LightBox implements GLRenderObject {
  
  private static Logger logger = LoggerFactory.getLogger(LightBox.class.getName());

  
   public LightBox() {
       this(0);  
   }
  
   public LightBox(int numberOfLights) {
       super();
       if (numberOfLights < 0 || numberOfLights > MAX_NUM_LIGHTS) {
           logger.error("LightBox: numLights should be between 0 and " +  MAX_NUM_LIGHTS);
       }
       for (int i=0; i<numberOfLights; i++) addLight();
      
   }
  
   /**
    * Called during initialization phase of the renderer.
    * It binds the attributes, like color, to some OpenGL light.
    */
   public void glInit(GLRenderContext glc) {
      for (int i=0; i<numLights; i++) {
           light[i].glInit(glc);
       }
   }

    /**
     * 
     */
    public void glRender(GLRenderContext glc) {
       for (int i=0; i<numLights; i++) {
           light[i].glRender(glc);
       }
    }
  
    /*
     * add one more light
     */
    private void addLight() {
       int i = numLights;
       numLights++;
       
       light[i] = new SimpleLight(i);
       
       light[i].setAmbientColor(0.0f, 0.0f, 0.0f);
       light[i].setSpecularColor(0.0f, 0.0f, 0.0f);
       light[i].setDiffuseColor(0.0f, 0.0f, 0.0f);
       light[i].setRadius(0.02f);
       light[i].setPosition(0.0f, 2.0f, 0.0f);   // The (initial) position of the light  
       light[i].setVisible(false);
       if (control != null) control.addControl(i, light[i].getPosition());
    }
    
    public void setControl(GLNavigation2 glNav) {
       this.control = glNav;
       for (int i=0; i<numLights; i++) {
           control.addLightControl(light[i]);
       } 
    }
  
//    public void addLight(SimpleLight simpleLight) {
//       light[numLights++] = simpleLight;
//    }
//  
    public SimpleLight get(int i) {
       return light[i];
    }
  
    private int numLights = 0;
    public final static int MAX_NUM_LIGHTS = 8;
    private SimpleLight[] light = new SimpleLight[MAX_NUM_LIGHTS];
    private GLNavigation2 control;
  

}