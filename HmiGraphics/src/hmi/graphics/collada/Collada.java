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
package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * The class for parsing complete Collada documents.
 * @author Job Zwiers
 */
public class Collada extends ColladaElement {
 
   // attributes:
   private static List<String> supportedVersion = new ArrayList<String>();
   private String specifiedVersion= "";
   private String xmlns="http://www.collada.org/2005/11/COLLADASchema";
   
   // child elements:
   private Asset asset;  // required, unique element
   private Scene scene;
   private List<Library<Animation>> librariesAnimations = new ArrayList<Library<Animation>>(2);
   private List<Library<AnimationClip>> librariesAnimationClips = new ArrayList<Library<AnimationClip>>(2);
   private List<Library<Camera>> librariesCameras  = new ArrayList<Library<Camera>>(2);
   private List<Library<Controller>> librariesControllers = new ArrayList<Library<Controller>>(2);
   private List<Library<Effect>> librariesEffects = new ArrayList<Library<Effect>>(2);
   private List<Library<ForceField>> librariesForceFields  = new ArrayList<Library<ForceField>>(2);
   private List<Library<Geometry>> librariesGeometries = new ArrayList<Library<Geometry>>(2);
   private List<Library<ColladaImage>> librariesImages = new ArrayList<Library<ColladaImage>>(2);
   private List<Library<Light>> librariesLights  = new ArrayList<Library<Light>>(2);
   private List<Library<Material>> librariesMaterials = new ArrayList<Library<Material>>(2);
   private List<Library<Node>> librariesNodes = new ArrayList<Library<Node>>(2);
   private List<Library<PhysicsMaterial>> librariesPhysicsMaterials  = new ArrayList<Library<PhysicsMaterial>>(2);
   private List<Library<PhysicsModel>> librariesPhysicsModels  = new ArrayList<Library<PhysicsModel>>(2);
   private List<Library<PhysicsScene>> librariesPhysicsScenes  = new ArrayList<Library<PhysicsScene>>(2);
   private List<Library<VisualScene>> librariesVisualScenes = new ArrayList<Library<VisualScene>>(2);
   private Extra extra;
   
   public List<Library<Animation>>       getLibrariesAnimations()       { return librariesAnimations; }
   public List<Library<AnimationClip>>   getLibrariesAnimationClips()   { return librariesAnimationClips; }
   public List<Library<Camera>>          getLibrariesCameras()          { return librariesCameras; }
   public List<Library<Controller>>      getLibrariesControllers()      { return librariesControllers; }
   public List<Library<Effect>>          getLibrariesEffects()          { return librariesEffects; }
   public List<Library<ForceField>>      getLibrariesForceFields()      { return librariesForceFields; }
   public List<Library<Geometry>>        getLibrariesGeometries()       { return librariesGeometries; }
   public List<Library<ColladaImage>>    getLibrariesImages()           { return librariesImages; }
   public List<Library<Light>>           getLibrariesLights()           { return librariesLights; }
   public List<Library<Material>>        getLibrariesMaterials()        { return librariesMaterials; }
   public List<Library<Node>>            getLibrariesNodes()            { return librariesNodes; }
   public List<Library<PhysicsMaterial>> getLibrariesPhysicsMaterials() { return librariesPhysicsMaterials; }
   public List<Library<PhysicsModel>>    getLibrariesPhysicsModels()    { return librariesPhysicsModels; }
   public List<Library<PhysicsScene>>    getLibrariesPhysicsScenes()    { return librariesPhysicsScenes; }
   public List<Library<VisualScene>>     getLibrariesVisualScenes()     { return librariesVisualScenes; }
   
   /** returns the Scene */
   public Scene getScene() {
      return scene;
   }
   
   /** returns the Asset */
   public Asset getAsset() {
      return asset;
   }
   
   
   public Extra getExtra() { return extra; }
   
   public String getRenamingList() {
      Extra extra = getExtra();
      if (extra == null) return "";
      ElckerlycProfile elckerlycProfile  = extra.getElckerlycProfile();
      if (elckerlycProfile == null) return "";
      String renaming = elckerlycProfile.getRenamingList();
      return renaming;
   }
   
   
   static {
      supportedVersion.add("1.4.0");
      supportedVersion.add("1.4.1");
   }
      
   public Collada() {
      super();    
   }
  
    public Collada(XMLTokenizer tokenizer) throws IOException {
       super();  
       setCollada(this);
//       this.resources = null;
       readXML(tokenizer);  
   }
 
//   public Collada(XMLTokenizer tokenizer, Resources resources) throws IOException {
//       super();  
//       setCollada(this);
//       this.resources = resources;
//       readXML(tokenizer);  
//   }
 
   /**
    * Create a new Collada object for an input stream specified by means of an URL,
    * possibly a file URL like: file:///C:/JavaProjects/xyz/data/datafile.dae
    */
   public static Collada forURL(String daeURL) throws IOException {
      URL sourceURL = new URL(daeURL);
      XMLTokenizer tokenizer = new XMLTokenizer(sourceURL);
      if (tokenizer.atEndOfDocument()) {
         Collada col = new Collada();
         col.severe("Collada.forURL \"" + daeURL + "\":  EMPTY or NULL DOCUMENT");
          return col;
      } else {
         return new Collada(tokenizer);
      }
   }
 
 
   /**
    * Searches for the dae file within the given Resource directory. 
    */
   public static Collada forResource(String daeFile) throws IOException {
      XMLTokenizer tokenizer = XMLTokenizer.forResource(daeFile);
      if (tokenizer.atEndOfDocument()) {
         Collada col = new Collada();
         col.severe("Collada.forResource \"" + daeFile + "\":  EMPTY or NULL DOCUMENT");
          return col;
      } else {
         return new Collada(tokenizer);
      }
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, librariesAnimations);
      appendXMLStructureList(buf, fmt, librariesAnimationClips);
      appendXMLStructureList(buf, fmt, librariesCameras);
      appendXMLStructureList(buf, fmt, librariesControllers);
      appendXMLStructureList(buf, fmt, librariesEffects);
      appendXMLStructureList(buf, fmt, librariesForceFields);
      appendXMLStructureList(buf, fmt, librariesGeometries);
      appendXMLStructureList(buf, fmt, librariesImages);
      appendXMLStructureList(buf, fmt, librariesLights);
      appendXMLStructureList(buf, fmt, librariesMaterials);
      appendXMLStructureList(buf, fmt, librariesNodes);
      appendXMLStructureList(buf, fmt, librariesPhysicsMaterials);
      appendXMLStructureList(buf, fmt, librariesPhysicsModels);
      appendXMLStructureList(buf, fmt, librariesPhysicsScenes);
      appendXMLStructureList(buf, fmt, librariesVisualScenes);
      appendXMLStructure(buf, fmt, scene);
      return buf;
   }

   /**
    * collada --> asset [library|scene]*
    * asset is required, 
    * scene is optional, and can occur at most once, at the end.
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      asset = new Asset(getCollada(), tokenizer); addColladaNode(asset); 
      boolean sceneReached = false;
      Collada collada = getCollada();
      while (tokenizer.atSTag() && ! sceneReached) {
         String tag = tokenizer.getTagName();  
         //System.out.println("== == > Collada tag=" + tag);
//         hmi.util.Console.delay(100);
         if (tag.equals(LibraryAnimations.xmlTag())) {
             librariesAnimations.add(new LibraryAnimations(collada, tokenizer));  
         } else if (tag.equals(LibraryAnimationClips.xmlTag())) {
             librariesAnimationClips.add(new LibraryAnimationClips(collada, tokenizer));   
         } else if (tag.equals(LibraryCameras.xmlTag())) {
             librariesCameras.add(new LibraryCameras(collada, tokenizer));  
         } else if (tag.equals(LibraryControllers.xmlTag())) {
             librariesControllers.add(new LibraryControllers(collada, tokenizer));
         } else if (tag.equals(LibraryEffects.xmlTag())) {
             librariesEffects.add(new LibraryEffects(collada, tokenizer));               
         } else if (tag.equals(LibraryForceFields.xmlTag())) {
             librariesForceFields.add(new LibraryForceFields(collada, tokenizer));              
         } else if (tag.equals(LibraryGeometries.xmlTag())) {
             librariesGeometries.add(new LibraryGeometries(collada, tokenizer));           
         } else if (tag.equals(LibraryImages.xmlTag())) {
             librariesImages.add(new LibraryImages(collada, tokenizer));             
         } else if (tag.equals(LibraryLights.xmlTag())) {
             librariesLights.add(new LibraryLights(collada, tokenizer));            
         } else if (tag.equals(LibraryMaterials.xmlTag())) {
             librariesMaterials.add(new LibraryMaterials(collada, tokenizer));     
         } else if (tag.equals(LibraryNodes.xmlTag())) {
             librariesNodes.add(new LibraryNodes(collada, tokenizer));      
         } else if (tag.equals(LibraryPhysicsMaterials.xmlTag())) {
             librariesPhysicsMaterials.add(new LibraryPhysicsMaterials(collada, tokenizer));               
         } else if (tag.equals(LibraryPhysicsModels.xmlTag())) {
             librariesPhysicsModels.add(new LibraryPhysicsModels(collada, tokenizer));      
         } else if (tag.equals(LibraryPhysicsScenes.xmlTag())) {
             librariesPhysicsScenes.add(new LibraryPhysicsScenes(collada, tokenizer));  
         } else if (tag.equals(LibraryVisualScenes.xmlTag())) {
             librariesVisualScenes.add(new LibraryVisualScenes(collada, tokenizer)); 
         } else if (tag.equals(Extra.xmlTag())) {             
             extra = new Extra(collada, tokenizer);       
         } else if (tag.equals(Scene.xmlTag())) {
             sceneReached = true;
             scene = new Scene(collada, tokenizer);    
         } else {
            warning(tokenizer.getErrorMessage("Collada: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }   
      addColladaNodes(librariesAnimations); 
      addColladaNodes(librariesAnimationClips);
      addColladaNodes(librariesCameras);
      addColladaNodes(librariesControllers); 
      addColladaNodes(librariesEffects);    
      addColladaNodes(librariesForceFields);    
      addColladaNodes(librariesGeometries);
      addColladaNodes(librariesImages);
      addColladaNodes(librariesLights);
      addColladaNodes(librariesMaterials);
      addColladaNodes(librariesNodes);
      addColladaNodes(librariesPhysicsMaterials); 
      addColladaNodes(librariesPhysicsModels); 
      addColladaNodes(librariesPhysicsScenes);
      addColladaNodes(librariesVisualScenes);
      addColladaNode(extra);  
      addColladaNode(scene);
      
   }
   
   
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "xmlns", xmlns);
      appendAttribute(buf, "version", specifiedVersion);
      return buf;
   }


   /**
    * decodes XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      specifiedVersion = getRequiredAttribute("version", attrMap, tokenizer);
      boolean recognizedVersion = false;
      for (String version : supportedVersion) {       
         if (specifiedVersion.equals(version)) recognizedVersion = true;
      }
      
      if ( ! recognizedVersion ) {
             logger.warning("Collada: unrecognized specified version: " + specifiedVersion );  
      }     
      
      String specifiedXmlns = getOptionalAttribute("xmlns", attrMap);
      if ( specifiedXmlns != null && ! specifiedXmlns.equals(xmlns) ) {
             logger.warning("Collada: unknown/unexpected xmlns: "  + specifiedXmlns);  
      }           
      super.decodeAttributes(attrMap, tokenizer);
   }





 /**
    * Returns the Library item for id, which could be null.
    * The url parameter can optionally start with a # character.
    * The first argument is a List of libraries to be searched.,
    * like the Collada libraries libraries_geometries etc. 
    */
   public <X extends ColladaElement> X getLibItem(List<Library<X>> libList, String url) {
      String id = (url.charAt(0) == '#') ? url.substring(1) : url;
      if (libList.size() == 0) {
         warning("Collada.getLibItem: No library for " + url);  
      }
      for (Library<X> lib : libList) {
         X libItem = lib.getLibItem(id);
         if (libItem != null) return libItem;
      }
      return null;
   }




   /**
    * adds a float array to the global map, identified by id.
    */
   public  void addFloatArray(String id, float[] floats) {
      if (id == null) {
          throw new RuntimeException("Collada.addFloatArray with null id");
      } else if (floats == null) {
          throw new RuntimeException("Collada.addFloatArray " + id + " with null floats");
      } else {
          floatArrays.put(id, floats);
          arrayTypes.put(id, FLOAT);
      }
   }
   
   /**
    * returns the float array (possibly null) from the global map, identified by id.
    */
   public  float[] getFloatArray(String id) {
      return floatArrays.get(id);  
   }

   /**
    * adds an int array to the global map, identified by id.
    */
   public  void addIntArray(String id, int[] ints) {
      if (id == null) {
          throw new RuntimeException("Collada.addIntArray with null id");
      } else if (ints == null) {
          throw new RuntimeException("Collada.addIntArray " + id + " with null ints");
      } else {
          intArrays.put(id, ints);
          arrayTypes.put(id, INT);
      }
   }
   
   /**
    * returns the int array (possibly null) from the global map, identified by id.
    */
   public  int[] getIntArray(String id) {
      return intArrays.get(id);  
   }
   
   /**
    * adds a boolean array to the global map, identified by id.
    */
   public  void addBoolArray(String id, boolean[] bools) {
      if (id == null) {
          throw new RuntimeException("Collada.addBoolArray with null id");
      } else if (bools == null) {
          throw new RuntimeException("Collada.addBoolArray " + id + " with null bools");
      } else {
          boolArrays.put(id, bools);
          arrayTypes.put(id, BOOL);
      }
   }
   
   /**
    * returns the boolean array (possibly null) from the global map, identified by id.
    */
   public  boolean[] getBoolArray(String id) {
      return boolArrays.get(id);  
   }

   /**
    * adds a String array, used for Collada names, to the global map, identified by id.
    */
   public  void addNameArray(String id, String[] names) {
      if (id == null) {
          throw new RuntimeException("Collada.addNameArray with null id");
      } else if (names == null) {
          throw new RuntimeException("Collada.addNameArray " + id + " with null names");
      } else {
          nameArrays.put(id, names);
          arrayTypes.put(id, NAME);
      }
   }
   
   /**
    * returns the String array (possibly null) from the global map, identified by id.
    */
   public  String[] getNameArray(String id) {
      return nameArrays.get(id);  
   }
   
   /**
    * adds a String array, used for Collada idRefs, to the global map, identified by id.
    */
   public  void addIDREFArray(String id, String[] idrefs) {
      if (id == null) {
          throw new RuntimeException("Collada.addIDREFArray with null id");
      } else if (idrefs == null) {
          throw new RuntimeException("Collada.addIDREFArray " + id + " with null idrefs");
      } else {
          idrefArrays.put(id, idrefs);
          arrayTypes.put(id, IDREF);
      }
   }
   
   /**
    * returns the String array (possibly null) from the global map, identified by id.
    * The possible types are: FLOAT, INT, BOOL, NAME, IDREF, all defined as Collada
    * int constants.
    */
   public  String[] getIDREFArray(String id) {
      return idrefArrays.get(id);  
   }


   /**
    * returns the Collada type of the array identified by id (null, if there is no such array)
    */
   public  int getArrayType(String id) {
      Integer res = arrayTypes.get(id); 
      return (res==null ? 0 : (int) res);
   }

   /**
    * adds a Collada Source to the global map, identified by id.
    * Assumption: Sources will always have globally unique is. (although this is not required by Collada(?))
    */
   public  void addSource(String id, Source source) {
      if (id == null) {
          logger.warning("addSource with null id");
      } else {
          Source prev = sources.put(id, source);
          if (prev != null) {
              logger.severe("addSource for " + id + " overwrites previous data"); 
          }
      }
   }
   
   /**
    * returns the Source (possibly null), identified by id.
    */
   public  Source getSource(String id) {
      return sources.get(id);  
   }

 
  

   /* global maps for arrays and sources */
   
   public static final int FLOAT = 1;
   public static final int INT = 2;
   public static final int BOOL = 3;
   public static final int NAME = 4;
   public static final int IDREF = 5;
   
   private Map<String, Integer> arrayTypes = new HashMap<String, Integer>();
   
   private Map<String, float[]> floatArrays = new HashMap<String, float[]>();
   private Map<String, int[]> intArrays = new HashMap<String, int[]>();
   private Map<String, boolean[]> boolArrays = new HashMap<String, boolean[]>();
   private Map<String, String[]> nameArrays = new HashMap<String, String[]>();
   private Map<String, String[]> idrefArrays = new HashMap<String, String[]>();
   private Map<String, Source> sources = new HashMap<String, Source>(); 
   
 

   /**
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "COLLADA";

   /**
    * The XML Stag for XML encoding
    */
   public static String xmlTag() { return XMLTAG; }
    
   /**
    * returns the XML Stag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   }


   private  java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.graphics.collada");

      

   public  void severe(String msg) {
      logger.severe(msg);  
   }

   public  void warning(String msg) {
      logger.warning(msg);  
   }

   public  void info(String msg) {
      logger.info(msg);  
   }

}
