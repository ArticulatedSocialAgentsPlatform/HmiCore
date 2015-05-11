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


package hmi.graphics.opengl.scenegraph;

import hmi.graphics.opengl.GLShader;
import hmi.graphics.opengl.GLTexture;
import hmi.graphics.opengl.GLTextureLoader;
import hmi.graphics.opengl.state.GLMaterial;
import hmi.graphics.scenegraph.GMaterial;
import hmi.graphics.scenegraph.GTexture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.media.opengl.*; 


/**
 * translates a scenegraph GMaterial into GLMaterial
 * @author Job zwiers
 */
public final class GMaterialTranslator {
  
   private static Logger logger = LoggerFactory.getLogger(GMaterialTranslator.class.getName());

  
   /***/
   private GMaterialTranslator() {}
   
   
   /**
    * Translates GMaterial into GLMaterial
    */ 
   public static GLMaterial fromGMaterialToGLMaterial(GMaterial gmaterial) {
      logger.debug("GMaterialTranslator.GMaterialToGLMaterial");
      GLMaterial glmaterial = new GLMaterial(gmaterial.getName());
      glmaterial.setEmissionColor(gmaterial.getEmissionColor());
      glmaterial.setAmbientColor(gmaterial.getAmbientColor());
      glmaterial.setDiffuseColor(gmaterial.getDiffuseColor());
      glmaterial.setSpecularColor(gmaterial.getSpecularColor());
      glmaterial.setShininess(gmaterial.getShininess());
      GTexture gDiffuseTex = gmaterial.getDiffuseTexture();
      if (gDiffuseTex != null) {
         int texUnit = 0;
            GLTexture glDiffuseTex = fromGTextureToGLTexture(gDiffuseTex, texUnit, glmaterial.getName());
            glmaterial.setDiffuseTexture(glDiffuseTex, texUnit);
      }
      glmaterial.setRepeatS(gmaterial.getRepeatS());
      glmaterial.setRepeatT(gmaterial.getRepeatT());
      glmaterial.setOffsetS(gmaterial.getOffsetS());
      glmaterial.setOffsetT(gmaterial.getOffsetT());
      
      
      if (gmaterial.isTransparencyEnabled()) {
         logger.debug("GMaterialTranslator, transparency enabled for " + gmaterial.getName());
         glmaterial.setAlphaBlendingEnabled(true) ;
         //String opaqueMode = gmaterial.getOpaqueMode();
         glmaterial.setBlendSrcFactor(GL.GL_SRC_ALPHA);
         glmaterial.setBlendDestFactor(GL.GL_ONE_MINUS_SRC_ALPHA);
        // glmaterial.setBlendDestFactor(GLC.GL_ONE);
//         
//         if (opaqueMode.equals("A_ONE")) {
//            glmaterial.setBlendSrcFactor(GLC.GL_SRC_ALPHA);
//            glmaterial.setBlendDestFactor(GLC.GL_ONE_MINUS_SRC_ALPHA);
//         } else if (opaqueMode.equals("RGB_ZERO")) {
//            glmaterial.setBlendSrcFactor(GLC.GL_ONE_MINUS_SRC_COLOR);
//            glmaterial.setBlendDestFactor(GLC.GL_SRC_COLOR);
//         } else {
//             logger.error("GMaterialTranslator, unknow opaque mode: " + opaqueMode);           
//         }

         float[] transparentColor = gmaterial.getTransparentColor();
         if (transparentColor != null) {
             glmaterial.setTransparentColor(transparentColor);  
         }
         
         GTexture gTransparantTex = gmaterial.getTransparentTexture();
         if (gTransparantTex != null) {
               int texUnit = (gDiffuseTex == null) ? 0 : 1;
               GLTexture glTransparantTex = fromGTextureToGLTexture(gTransparantTex, texUnit, glmaterial.getName());
               glmaterial.setTransparantTexture(glTransparantTex, texUnit);
         }
      } else {
  //        logger.error("transparency NOT enabled for " + gmaterial.getName());  
      }
      String shader = gmaterial.getShader();
      if (shader != null) { // expect blinn, phong, etc. 
//          if (shader.equals("blinnTextured1")) {
//            logger.debug("GMaterialTranslator shader: " + shader);
//         } else {
//             shader = "textured"; //temporary patch
//         }
             //hmi.util.Console.println("GMaterialTranslator glShader " + shader);
             GLShader glShader = new GLShader(shader);
//          glShader.setValues(0, 1);
          glmaterial.setGLShader(glShader);  
          if (shader.equals("blinnEyeTextured1")) {
          	  glmaterial.setPupilSize(0.08f);
          }
      }
      return glmaterial;
   }   
  
   public static GLTexture fromGTextureToGLTexture(GTexture gtex, int texUnit, String materialName) {
      String imageName = gtex.getImageFileName();
      String texName = imageName;
//      int texUnit = gtex.getTexUnit();
//      int texUnit = 0;
      boolean flip = true;
      boolean rescale = true;
      GLTexture texture = GLTextureLoader.getGLTexture(texUnit, texName, flip, rescale);
      if (texture == null) {
          logger.error("GTextureToGLTexture for GLMaterial " + materialName + ": Could not load texture \"" + texName + "\"");  
      } else {
          texture.setImageFileName(imageName);
      }
      return texture;
   }
  
  
//    /**
//    * Translates GMaterial into a GLRenderList .
//    * A null GMaterial is allowed, and returns the default GLMaterial
//    */ 
//   public static GLRenderList GMaterialToRenderList(GMaterial gmaterial) {
//      GLRenderList statelist = new GLRenderList();
//      if (gmaterial != null) {
//         float[] emissionColor = gmaterial.getEmissionColor();
//         if ( emissionColor != null) {
//             statelist.add(new GLStateComponentIF4(GLC.GL_FRONT, GLC.GL_EMISSION, emissionColor));
//         }
//         float[] ambientColor = gmaterial.getAmbientColor();
//         if ( ambientColor != null) {
//             statelist.add(new GLStateComponentIF4(GLC.GL_FRONT, GLC.GL_AMBIENT, ambientColor));
//             statelist.add(new GLStateComponentIF4(GLC.GL_FRONT, GLC.GL_DIFFUSE, ambientColor));
//         }
//         float[] diffuseColor = gmaterial.getDiffuseColor();
//         if ( diffuseColor != null) {
//            //hmi.util.Console.println("GMaterialTranslator: Non-null diffuseColor");
//             statelist.add(new GLStateComponentIF4(GLC.GL_FRONT, GLC.GL_DIFFUSE, diffuseColor));
//         }
//         float[] specularColor = gmaterial.getSpecularColor();
//         if ( specularColor != null) {
//            //hmi.util.Console.println("GMaterialTranslator: Non-null specularColor");
//            
//            
//            
//             statelist.add(new GLStateComponentIF4(GLC.GL_FRONT, GLC.GL_SPECULAR, specularColor));
//             float shininess = gmaterial.getShininess();
//             statelist.add(new GLStateComponentIF(GLC.GL_FRONT, GLC.GL_SHININESS, shininess));
//         }
//         GTexture gtex = gmaterial.getGTexture();
//         if (gtex != null) {
//            //hmi.util.Console.println("GMaterialTranslator: non-null GTexture");
//             GLRenderList texstate =   GTextureToGLRenderList(gtex);
//             statelist.addAll(texstate);
//         }
//      }
//      return statelist;
//   }   
//  
//  
//   public static GLRenderList GTextureToGLRenderList(GTexture gtex) {
//      GLRenderList texstate = new GLRenderList();
//      //String ogl_wrap_s = "GL_" + gtex.getWrap_S();
//      int ogl_wrap_s = GLState.getGLId("GL_" + gtex.getWrap_S());
//      int ogl_wrap_t = GLState.getGLId("GL_" + gtex.getWrap_T());
//      int ogl_wrap_r = GLState.getGLId("GL_" + gtex.getWrap_R());
//      texstate.add(new GLTextureStateComponent(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_S, ogl_wrap_s));
//      texstate.add(new GLTextureStateComponent(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_T, ogl_wrap_t));
//      texstate.add(new GLTextureStateComponent(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_R, ogl_wrap_r));
//     //hmi.util.Console.println("wrapS: " + ogl_wrap_s);
//      return texstate;
//      
//   }
//   

             
}
