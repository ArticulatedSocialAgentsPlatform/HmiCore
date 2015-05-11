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

package hmi.graphics.collada;

import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * An extension of XMLStructureAdapter, used as base class for all COLLADA elements.
 * It deals with a few attributes like id, sid, name, that are common to many Collada elements.
 * It also deals with child nodes in the parse tree.
 * @author Job Zwiers
 */
public class ColladaElement extends XMLStructureAdapter
{
   // Strings id, sid, and name are interend String, so can be compared for equality with "==".
   private String id; // id attribute, for many ColladaNodes. id's are assumed to be unique within the document
   private String sid; // scoped-id attribute; unique only within scope of parent node, amongs siblings
   private String name; // optional descriptive name for some nodes.
   
   private List<ColladaElement> childNodes;       // child nodes for the parse tree.
   private Map<String, ColladaElement> idMap;     // mapping from id's to (child)nodes
   private Set<String> ids;                       // "cached" keySet of idMap.
   
   private Collada collada;                       // The Collada environment for this parse tree element. 
   
   public static final String COLLADANAMESPACE = "http://www.collada.org/2005/11/COLLADASchema";
   
   protected Logger logger = LoggerFactory.getLogger("hmi.graphics.collada");
   
   /**
    * Default constructor.
    */
   public ColladaElement() {
      childNodes = new ArrayList<ColladaElement>();  
   }    


//   /**
//    * Constructor that keeps track of the Collada environment
//    */
//   public ColladaElement(Collada collada) {
//      childNodes = new ArrayList<ColladaElement>();  
//      this.collada = collada;
//   }    


   /**
    * Constructor that keeps track of the Collada environment
    */
   public ColladaElement(Collada collada)  {
      childNodes = new ArrayList<ColladaElement>();  
      this.collada = collada;
   }    

   @Override
   public  String getNamespace() { return COLLADANAMESPACE; }

   /**
    * Returns the id attribute, which could be null.
    */
   public String getId() {
      return id;
   }
   
   /**
    * Returns the sid attribute, which could be null.
    */
   public String getSid() {
      return sid;
   }
   
   /**
    * Returns the name attribute, which could be null.
    */
   public String getName() {
      return name;
   }
      
      
   /**
    * returns the id, if non-null, else returns the name.
    * (which could be null also).
    */   
   public String getIdOrName() {
      if (id != null) {
         return id; 
      } else {
         return name;
      }
   } 
      
 
   
   /** Sets the id of the element */
   public void setId(String id) { this.id = id; }
   
 
   /** Sets the sid of the element */
   public void setSid(String sid) { this.sid = sid; }
   
   
   /** Sets the name of the element */
   public void setName(String name) { this.name = name; }  
      
   /**
    * returns the getCollada() attribute
    */
   public Collada getCollada() {
      return collada; 
   }   
      
   
   /**
    * Sets the getCollada() attribute
    */
   public void setCollada(Collada collada) {
      this.collada = collada; 
   }   
      
   /**
    * Adds some other ColladaElement as a child node.
    */
   public void addColladaNode(ColladaElement nod) {
      if (nod != null) childNodes.add(nod);  
   }
   
   /**
    * Adds other ColladaElements as child nodes.
    */
   public void addColladaNodes(List<? extends ColladaElement> nods) {
      if (nods != null) {
         childNodes.addAll(nods);         
      } 
   }

   /**
    * Returns (direct) child nodes.
    */
   public List<ColladaElement> getColladaNodes() {      
      return childNodes;  
   }
   
   /**
    * Returns all direct or indirect (i.e. recursive) child nodes.
    */   
   public List<ColladaElement> getRecursiveColladaNodes() {
      return getRecursiveColladaNodes(new ArrayList<ColladaElement>());
   }

   /**
    * Returns all direct or indirect child nodes, added to the node list provided as input parameter
    * (Basically, this is an auxiliary method, to implement getRecursiveColladaNodes())
    */   
   public List<ColladaElement> getRecursiveColladaNodes(List<ColladaElement> nodeList) {
      nodeList.add(this);
      for (ColladaElement nod:childNodes) {
          nod.getRecursiveColladaNodes(nodeList);
      }
      return nodeList; 
   }


   /**
    * Method that should be overwritten in classes that extend from ColladaElement:
    * the intention is that (depending on the reportType parameter) they will "report"
    * by printing to the logging handler.
    */
   public void reportNode(String reportType) {
      
   }


   /**
    * Calls reportNode for this ColladaElement, as well as all child nodes.
    */
   public void report(String reportType) {
       reportNode(reportType);
       for (ColladaElement nod:childNodes) {
          nod.report(reportType);
      }  
   }

   /**
    * Returns the Map that maps Collada Id's to ColladaNodes.
    * The first request actually allocates and fills the map, before it is returned.
    */
   public Map<String, ColladaElement> getIdMap() {
      if (idMap == null) idMap = addToIdMap(new HashMap<String, ColladaElement>());
         return idMap;
   }
   
   /**
    * Adds the id of this ColladaElement, and recursively, of the id's of all of its children, 
    * to the Id-To-ColladaElement Map provided as input parameter. The extended Map is returned.
    * Null id's are ignored.
    */
   public Map<String, ColladaElement> addToIdMap(Map<String, ColladaElement> idMap) {
      if (id!= null) idMap.put(id, this);  
      for (ColladaElement nod:childNodes) {
          nod.addToIdMap(idMap);
      } 
      return idMap; 
   }

   /**
    * Returns the Set containing the defined Collada Id's for this node.
    */
   public Set<String> getIds() {
      if (ids == null) {
         Map<String, ColladaElement> im = getIdMap();
         ids = im.keySet();
      }   
      return ids;
   }

   /**
    * Assuming that url is a fragment of the form #id, 
    * urlToId returns the id part, i.e. without the sharp character.
    */
   public  String urlToId(String url) {
      if (url == null) return null;
      if (url.length() == 0) return url;
      if (url.charAt(0) == '#') {
         return url.substring(1);
      } 
      getCollada().warning("Collada Warning: Url fragment (" + url + ") not starting with a # char");
      return url;      
   }
   
//   
//   public Resources getResources() {
//      return getCollada().getResources();
//   }

   /**
    * appends the id and sid XML attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "id", id);
      appendAttribute(buf, "sid", sid);
      appendAttribute(buf, "name", name);
      return buf;
   }


   /**
    * decodes the id and sid XML attributes.
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      id       = getOptionalAttribute("id", attrMap);
      if (id != null) id = id.intern();
      sid      = getOptionalAttribute("sid", attrMap);
      if (sid != null) sid = sid.intern();
      name     = getOptionalAttribute("name", attrMap);
      if (name != null) name = name.intern();
      super.decodeAttributes(attrMap, tokenizer);
   }

}
