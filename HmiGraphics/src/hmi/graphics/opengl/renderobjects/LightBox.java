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