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

package hmi.graphics.collada.scenegraph;
import hmi.graphics.collada.BindMaterial;
import hmi.graphics.collada.Collada;
import hmi.graphics.collada.Controller;
import hmi.graphics.collada.InstanceController;
import hmi.graphics.collada.Morph;
import hmi.graphics.collada.Skin;
import hmi.graphics.scenegraph.GNode;
import hmi.graphics.scenegraph.GShape;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * translates  Controller instances
 * @author Job Zwiers
 */
public final class InstanceControllerTranslator {
   
   private static Logger logger = LoggerFactory.getLogger(InstanceControllerTranslator.class.getName());
  
   /***/
   private InstanceControllerTranslator() {}
  
   private static final List<GShape> EMPTY_GSHAPE_LIST = new ArrayList<GShape>(0);
  
   // SKIN CONTROLLERS:
   /**
    * Adds GShapes to the specified gnode, extracted from the skins of the controllers.     
    */
   public static void addInstanceSkinControllerGShapes(Collada collada, List<InstanceController> instanceSkinControllers, GNode gnode) {
      // assume: instanceSkinControllers is non-null, but could be empty
      for (InstanceController icontrol : instanceSkinControllers) {  
          List<GShape> gshapeList = instanceSkinControllerToGShapeList(collada, icontrol);
          gnode.addGShapes(gshapeList);
      }      
   }
  
   /**
    * gets the list of GShapes for the meshes and materials for a given skin InstanceController 
    * The GMeshes include skinning data, derived from the Skin of the Controller.
    */
   public static List<GShape> instanceSkinControllerToGShapeList(Collada collada, InstanceController icontrol) {      
      Controller controller = icontrol.getController();
      if (controller == null) {
          collada.warning("Collada Translator: instance skin controller: " + icontrol.getURL() + " could not be found");
          return EMPTY_GSHAPE_LIST;
      } 
      Skin skin = controller.getSkin();
      if (skin == null) {
          collada.warning("Collada Translator: instance skin controller: " + icontrol.getURL() + " has no skin");
          return EMPTY_GSHAPE_LIST;
      } 
      // TODO: geom/mesh might be shared with other instance controllers
      BindMaterial bindMaterial = icontrol.getBindMaterial();
      String[] skeletonIds = icontrol.getSkeletonIds();  
      if (skeletonIds.length != 0) {
         List<GShape> gshapeList = InstanceGeometryTranslator.getSkinnedGShapeList(collada, skin, skeletonIds, bindMaterial);
         return gshapeList;  
      } else {
         logger.error("InstanceControllerTranslator: Empty skeletonIds");
         // could be a morph controller?
         return null;
      }        
   }
  
   // MORPH CONTROLLERS:
   /**
    * Adds GShapes to the specified gnode, extracted from morph controllers.     
    */
   public static void addInstanceMorphControllerGShapes(Collada collada, List<InstanceController> instanceMorphControllers, GNode gnode) {
      // assume: instanceMorphControllers is non-null, but could be empty
      for (InstanceController icontrol : instanceMorphControllers) {  
          List<GShape> gshapeList = instanceMorphControllerToGShapeList(collada, icontrol);
          gnode.addGShapes(gshapeList);
      }      
   }

   /**
    * gets the list of GShapes for the meshes and materials for a given  morph InstanceController 
    */
   public static List<GShape> instanceMorphControllerToGShapeList(Collada collada, InstanceController icontrol) {      
     
      Controller controller = icontrol.getController();
      if (controller == null) {
          collada.warning("Collada Translator: instance morph controller: " + icontrol.getURL() + " could not be found");
          return EMPTY_GSHAPE_LIST;
      } 
  
        
      // TODO: geom/mesh might be shared with other instance controllers
      
      Morph morph = controller.getMorph();
      if (morph == null) {
          collada.warning("Collada Translator: instance morph controller: " + icontrol.getURL() + " without morph controller");
          return EMPTY_GSHAPE_LIST;
      } 
      
      BindMaterial bindMaterial = icontrol.getBindMaterial();
      List<GShape> gshapeList = InstanceGeometryTranslator.getMorphedGShapeList(collada, morph, bindMaterial);
      return gshapeList;  
      //return EMPTY_GSHAPE_LIST;
   }

}
