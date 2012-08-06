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
