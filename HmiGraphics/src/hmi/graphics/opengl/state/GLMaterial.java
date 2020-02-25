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
package hmi.graphics.opengl.state;


import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLShader;
import hmi.graphics.opengl.GLTexture;
import hmi.graphics.opengl.GLUtil;

/**
 * @author Job Zwiers
 */
public class GLMaterial implements GLStateComponent {
   
   private String name = "";
   private static int scid = -1;  // not allocated yet.
 
  
   private float[] emissiveColor = null;
   private float[] ambientColor = null;
   private float[] diffuseColor = null;
   private float[] specularColor = null;
   private float[] transparent = null;
   private float shininess = 1.0f;

   private GLTexture diffuseTexture;
   private GLTexture transparentTexture;
   private int diffuseTextureUnit ;
   private int transparentTextureUnit;
   private float repeatS = 1.0f; // multiplier to apply on (all!) texture(s) coordinates before sampling. 
   private float repeatT = 1.0f;
   private float offsetS = 0.0f;  // offset to add on (all!) texture(s) coordinates before sampling.
   private float offsetT = 0.0f;
   
   
   private boolean alphaBlendingEnabled = false;
   private int srcFactor, destFactor;  // factors for alpha blending 
   
   private GLShader shader;
   
   private boolean debugMode = false;
   
   private float rp = 0.0f; // pupil size for eye shader
   
   //private float[] white = new float[]{1f, 1f, 1f, 1f};
   //private float[] black = new float[]{0f, 0f, 0f, 1f};
   
  
   
   /**
    * Create an GLMaterial.
    */
   public GLMaterial() {
       this("");
   }
   
   /**
    * Create an GLMaterial.
    */
   public GLMaterial(String name) {
      this.name = name;
      if (scid <= 0) {
          scid = GLState.createSCId();  
      }
      init();
   }
   
   private static final int VEC4_SIZE = 4;
   private void init() {
   }
   
   
   public void setGLShader(GLShader shader) {
      this.shader = shader;
      if (shader.getProgramName().equals("debug")) {
          debugMode = true;  
      }
   }
   
   public boolean getAlphaBlendingEnabled() {
      return alphaBlendingEnabled;
   }
   
   public void setAlphaBlendingEnabled(boolean mode) {
      alphaBlendingEnabled = mode;
   }
   
   public void setBlendSrcFactor(int srcFactor) {
      this.srcFactor = srcFactor;  
   }
   
   public void setBlendDestFactor(int destFactor) {
      this.destFactor = destFactor;  
   }
   
   public void setEmissionColor(float[] color) {
      if (emissiveColor == null)  emissiveColor = new float[4];      
      int len = color.length;
      System.arraycopy(color, 0, emissiveColor, 0, len);
      if (len < 4) emissiveColor[3] = 1.0f;
   }
   
   public void setAmbientColor(float[] color) {
      if (ambientColor == null)  ambientColor = new float[4];      
      int len = color.length;
      System.arraycopy(color, 0, ambientColor, 0, len);
      if (len < 4) ambientColor[3] = 1.0f;
   }
   
   public void setDiffuseColor(float[] color) {
      if (diffuseColor == null)  diffuseColor = new float[4];      
      int len = color.length;
      System.arraycopy(color, 0, diffuseColor, 0, len);
      if (len < 4) diffuseColor[3] = 1.0f;
   }
   
   public void setSpecularColor(float[] color) {
      if (specularColor == null)  specularColor = new float[4];      
      int len = color.length;
      System.arraycopy(color, 0, specularColor, 0, len);
      if (len < 4) specularColor[3] = 1.0f;
   }
   
   public void setShininess(float shininess) {
      this.shininess = shininess;
   }
   
   public void setTransparentColor(float[] color) { // transparency values, either in A_ONE or RGB_ZERO format;
      if (transparent == null)  transparent = new float[4];      
      int len = color.length;
      System.arraycopy(color, 0, transparent, 0, len);
      if (len < 4) transparent[3] = 1.0f;
   }
   
   public void setTransparantTexture(GLTexture glTex, int texUnit) {
      transparentTexture = glTex;
      transparentTextureUnit = texUnit;
   }
   
   public void setDiffuseTexture(GLTexture glTex, int texUnit) {
      diffuseTexture = glTex;
      diffuseTextureUnit = texUnit;
   }
   
   /** Sets the repeat factor for the S coordinate  */
   public void setRepeatS(float r) { repeatS = r; }
   
   /** Returns the repeat factor for the S coordinate  */
   public float getRepeatS() { return repeatS; }
   
    /** Sets the repeat factor for the T coordinate  */
   public void setRepeatT(float r) { repeatT = r; }
   
   /** Returns the repeat factor for the T coordinate  */
   public float getRepeatT() { return repeatT; }
   
  
   /** Sets the offset for the S coordinate  */
   public void setOffsetS(float r) { offsetS = r; }
   
   /** Returns the offset for the S coordinate  */
   public float getOffsetS() { return offsetS; }
   
    /** Sets the offset for the T coordinate  */
   public void setOffsetT(float r) { offsetT = r; }
   
   /** Returns the offset for the T coordinate  */
   public float getOffsetT() { return offsetT; }
   
   /**
    * Sets the pupil radius for the eye shader
    * typical value: 0.01 < rp < 0.8
    */
   public void setPupilSize(float rp) {
   	    hmi.util.Console.println("GLMaterial.setPupilSize: " + rp);
   	    this.rp = rp;
   }
   
   
   /**
    * Returns the id for this type of state component
    */
   @Override
   public final int getSCId() {
       return scid;  
   }
   
   /**
    * Returns the name of this GLMaterial
    */
   public String getName() { return name; }

   public final boolean eq(GLMaterial glm) {
      return false;
   }

   /**
    * Required by GLRenderObject interface.
    */
   @Override
   public final void glInit(GLRenderContext glc) { 
      if (shader != null) {
         if (emissiveColor != null) shader.setValue("emissiveColor", emissiveColor);
         if (ambientColor != null)  shader.setValue("ambientColor",  ambientColor);
         if (diffuseColor != null)  shader.setValue("diffuseColor",  diffuseColor);
         if (specularColor != null) shader.setValue("specularColor", specularColor);
         if (shininess != 0.0)      shader.setValue("shininess",     shininess);
         //shader.setValue("lightPosition", 1.0f, 2.0f, 3.0f, 1.0f); // ignored by shader for the moment also does not belong to GLMaterial
      }
      if (diffuseTexture != null) {
         diffuseTexture.glInit(glc);
         shader.setValue("TextureSampler", diffuseTextureUnit);
         // we use the same repeatX/offsetX values for *all* textures
         shader.setValue("repeatS", repeatS);
         shader.setValue("repeatT", repeatT);
         shader.setValue("offsetS", offsetS);
         shader.setValue("offsetT", offsetT);
      }
      if (alphaBlendingEnabled) {
         if (transparentTexture != null) {
            transparentTexture.glInit(glc);
            shader.setValue("TransparentSampler", transparentTextureUnit);
         }
         if (transparent != null) {
            shader.setValue("transparentColor", transparent);
         }
         
      }
//      if (rp > 0.0f) {
//      	  shader.setValue("rp", rp);
//      }
      
      if (shader != null) shader.glInit(glc);
   }
   
   /**
    * Required by GLRenderObject interface.
    */
   @Override
   public final void glRender(GLRenderContext glc) {
     
      GL2ES2 gl = glc.gl;
      if (debugMode) return;
      if (alphaBlendingEnabled) {
         gl.glEnable(GL.GL_BLEND);
         gl.glBlendFunc(srcFactor, destFactor);
         gl.glDepthMask(false);
      } else {
         gl.glDisable(GL.GL_BLEND);
         gl.glDepthMask(true);
      }
      if (diffuseTexture!= null ) {
         diffuseTexture.glRender(glc);
      } else {

      }
      if (transparentTexture != null) {
         transparentTexture.glRender(glc);
      }
      if (shader != null && rp > 0.0f) {
      	  shader.setValue("rp", rp);
      }
      
      if (shader != null) {
         shader.glRender(glc);
      } else {
         gl.glUseProgram(0);  
      }
   }
   
   private String fbts(float[] buf) {
       if (buf == null) return "null";
       return "[" + buf[0] + ", " + buf[1] + ", " + buf[2] + ", " + buf[3] + "]";
   }
      
   /* flag thaty determined the amount of detail for appendAttributesTo() and toString() */   
   private static boolean showDetail = true;
  
   /**
    * Sets the showDetail mode for toString()
    */
   public static  void setShowDetail(boolean show) {
      showDetail = show;
   }
  

   /* denotes whether toString should show detail or mot */
   public boolean showDetail() { return showDetail;  }
      
   
   /*
    * Appends the Mesh' attributes to the specified StringBuilder.
    * (Introduced for GLBasicMesh extensions like GLSkinnedMesh)
    */
   public StringBuilder appendTo(StringBuilder buf, int tab) {
       GLUtil.appendSpaces(buf, tab);
       buf.append("GLMaterial " + getName() + "[");
       buf.append("shader=" + shader);
       if (showDetail()) {
          GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "emissiveColor=" + fbts(emissiveColor)); 
          GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "ambientColor=" + fbts(ambientColor)); 
          GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "diffuseColor=" + fbts(diffuseColor)); 
          GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "specularColor=" + fbts(specularColor)); 
          GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "shininess=" + shininess); 
          if (diffuseTexture != null)   GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "diffuseTexture=" + diffuseTexture); 
          if (transparentTexture != null) GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "transparentTexture=" + transparentTexture); 
          buf.append('\n');
       }
       buf.append(']');
       return buf;  
   }
   
   
   @Override
   public String toString() {
      StringBuilder buf = appendTo(new StringBuilder(), 0);
      return buf.toString();
   }
   
                 
}
