/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/


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
             GLShader glShader = new GLShader(shader);
//          glShader.setValues(0, 1);
          glmaterial.setGLShader(glShader);  
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
