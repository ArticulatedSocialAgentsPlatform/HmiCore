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

package hmi.graphics.scenegraph;

import hmi.animation.VJoint;
import hmi.math.Mat3f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.util.BinUtil;
import hmi.util.BinaryExternalizable;
import hmi.util.Diff;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * GNode represent a scenegraph node, that contains GNode typed children,
 * references GShapes, and specifies a local 3D transformation.
 * The latter is specified by either a 4X4 matrix, and/or by the following:
 * <ul>
 * <li> A <em>translation vector</em>, defined by a Vec3f float array </li>
 * <li> A <em>rotation quaternion </em>, defined by a Quat4f float array</li>
 * <li> A <em>scaling</em>, defined by either a scale vector (a Vec3f array) or a scaling matrix (a Mat3f array)</li>
 * </ul>
 * It is allowed to set either the matrix, or the rotation/translation/scaling attributes,
 * then ask for any of them; automatic conversion between these two alternatives is performed. 
 * @author Job Zwiers
 */
public class GNode extends XMLStructureAdapter implements BinaryExternalizable, Diff.Differentiable { 

   private String id;       // (optionally) id, supposed to be a "globally" unique identifier within the scenegraph
   private String sid;      // (optionally) Scoped id: unique amongs siblings in the scene graph, not necesarily globally unique
   private String name;     // (optionally) Friendly name, without constraints.
   private String type;     //  (optionally) node type, like "JOINT" or "NODE"
   private GNode parent;               // parent node in the scene graph, possibly null
   private List<GNode> gnodes;         // list of child nodes in the scene graph
   private List<GShape> gshapes;       // list of gshapes, containing geometry and materials
   private VJoint joint = new VJoint(); // a link to a VJoint that determines the rotation, translation, and scaling for this GNode.
   
   
   private static final List<GNode> EMPTYGNODELIST = new ArrayList<GNode>(0);
   private static final List<GShape> EMPTYGSHAPELIST = new ArrayList<GShape>(0);
   
   private float[] vec = Vec3f.getVec3f(); // Vec3f for intermediate calculations.
   private static Logger logger = LoggerFactory.getLogger(GNode.class.getName());

   
   /**
    * GNode.Predicates are Objects that implement a boolean test on GNodes,
    * in the form of their "valid" method. If some GNode vo satifies
    * some predicate pred, then pred.valid(vo) should yield "true".
    */
   public interface Predicate {
      boolean valid(GNode obj);
   } 
   
   
   /**
    * Creates a new GNode with null id.
    */
   public GNode() {
   }
   
   /**
    * Creates a new GNode with specified id. 
    */
   public GNode(String id) {
      this();
      setId(id);
   }
   
   
   public GNode(XMLTokenizer tokenizer) throws IOException {
       this();
       readXML(tokenizer);  
   }
   
   
   /**
    * show differences
    */
   public String showDiff(Object gnodeObj) {
      GNode gnode = (GNode) gnodeObj;
      if (gnode==null) return "GNode " + id + ", diff: null GNode";
      String diff = Diff.showDiff("GNode, id", id, gnode.id);
      if (diff != "") return diff;
      diff = Diff.showDiff("GNode " + id + ", diff sid", sid, gnode.sid);
      if (diff != "") return diff;
      diff = Diff.showDiff("GNode " + id + ", diff name", name, gnode.name);
      if (diff != "") return diff;
      diff = Diff.showDiff("GNode " + id + ", diff type", type, gnode.type);
      if (diff != "") return diff;
      String parId1 = parent == null ? null : parent.getId();
      String parId2 = gnode.parent == null ? null : gnode.parent.getId();
      diff = Diff.showDiff("GNode " + id + ", diff parent", parId1, parId2);
      if (diff != "") return diff;
      diff = Diff.showDiff("GNode " + id + ", diff gshapes", gshapes, gnode.gshapes);
      if (diff != "") return diff;
      diff = Diff.showDiff("GNode " + id + ", diff child nodes", gnodes, gnode.gnodes); // recursive showDiff
      if (diff != "") return diff;
      diff = joint.showLocalDiff("GNode " + id + ", diff VJoints ", gnode.joint);
      if (diff != "") return diff;
      return "";
   }
   
   
   /** 
    * Sets the id attribute.
    */
   public void setId(String id) {  this.id = (id==null) ? null : id.intern();  joint.setId(id); }
   
   /** 
    * Returns the id attribute.
    */
   public String getId() {  return id;  }
   
   /** 
    * Sets the Collada sid attribute.
    */
   public void setSid(String sid) { this.sid = (sid==null) ? null : sid.intern(); joint.setSid(sid);  }
   
   /** 
    * Returns the Collada sid attribute.
    */
   public String getSid() {  return sid; }
   
   /** 
    * Sets the name attribute.
    */
   public void setName(String name) { this.name = (name==null) ? null : name.intern(); joint.setName(name);  }
   
   /** 
    * Returns the name attribute.
    */
   public String getName() {  return name; }
   
   /** 
    * Sets the Collada type attribute.
    */
   public void setType(String type) { this.type = (type==null) ? null : type.intern();   }
   
   /** 
    * Returns the Collada type attribute.
    */
   public String getType() {  return type; }
   
 
   /**
    * Selects recursively all offspring
    */
   public List<GNode> selectGNodes() {
      return selectGNodes(null, null);  
   }

   /**
    * Selects recursively all child GNodes that satisfy the predicate
    */
   public List<GNode> selectGNodes(Predicate select) {
      return selectGNodes(select, null);  
   }
  
   /**
    * Selects recursively child GNodes that satisfy the predicate.
    * The recursive search is pruned for GNodes that satisfy the prune predicate
    */
   public List<GNode> selectGNodes(Predicate select, Predicate prune) {
      return selectGNodes(select, prune, new ArrayList<GNode>());   
   }
   
   /**
    * Selects recursively child GNodes that satisfy the predicate.
    * The recursive search is pruned for GNodes that satisfy the prune predicate.
    * The results are added to the specified List.
    */
   public List<GNode> selectGNodes(Predicate select, Predicate prune, ArrayList<GNode> list) {
      if (select == null || select.valid(this)) list.add(this);
      if (prune == null || ! prune.valid(this)) {
         for (GNode gchild : gnodes) {
            gchild.selectGNodes(select, prune, list);
         }  
      }
      return list;    
   }
   
 
 
   /**
    * returns the VJoint for this GNode
    */
   public VJoint getVJoint() {
      return joint;
   }
   
   
   private static final int LISTSIZE = 4;
   
   /** 
    * Adds some GNode as a child node.
    * Also updates the parent-child relationship for
    * the VJoints within thisGNode and the child GNode.
    */
   public void addChildNode(GNode sn) {
      if (gnodes == null) gnodes = new ArrayList<GNode>(LISTSIZE);
      if (sn.parent != null) {
         sn.parent.removeChildNode(sn);  
      }
      gnodes.add(sn);
      sn.parent = this;
      joint.addChild(sn.getVJoint());
   }

   /**
    * Removes the specified GNode from 
    * the List of child gnodes.
    */
   public void removeChildNode(GNode sn) {
      if (gnodes == null) return;
      gnodes.remove(sn);  
   }

   /**
    * Returns a List with GNode children of this GNode.
    */
   public List<GNode> getChildNodes() {
      if (gnodes == null) return EMPTYGNODELIST;
      return gnodes;
   } 

   /**
    * Returns the parent GNode of this GNode, which could be null.
    */
   public GNode getParent() {
      return parent;  
   }


   /**
    * Adds a GShape 
    */
   public void addGShape(GShape gshape) {
      if (gshapes == null) gshapes = new ArrayList<GShape>(LISTSIZE);
      gshapes.add(gshape);
   }

   /**
    * Adds  all elements of a List of  GShapes 
    */
   public void addGShapes(List<GShape> gshapeList) {
      if (gshapes == null) gshapes = new ArrayList<GShape>(LISTSIZE);
      gshapes.addAll(gshapeList);
   }


   /**
    * Returns a List with the GShape children of this GNode.
    */
   public List<GShape> getGShapes() {
      if (gshapes == null) return EMPTYGSHAPELIST;
      return gshapes;
   } 

   
   /**
    * Determines whether a GNode based scene graph, with this GNode as root,
    * includes some GShapes at all.
    */
   public boolean hasGShapes() {
      if (getGShapes().size() > 0) return true;
      for (GNode childNode : getChildNodes()) {
          if (childNode.hasGShapes()) return true;  
      }
      return false;
   }
   
   

   /**
    * Searches for a GNode with specified sid. The search is recursive,
    * among the child nodes of this GNode, and include this GNode as well.
    */
   public GNode getPartBySid(String sid) {
      if  (this.sid != null && this.sid.equals(sid)) return this;
      for (GNode childNode : getChildNodes()) {
          GNode gn = childNode.getPartBySid(sid);
          if (gn != null) return gn;  
      }
      return null;      
   }
   
   /**
    * Searches for a GNode with specified id. The search is recursive,
    * among the child nodes of this GNode, and include this GNode as well.
    */
   public GNode getPartById(String id) {
      if  (this.id != null && this.id.equals(id)) return this;
      for (GNode childNode : getChildNodes()) {
          GNode gn = childNode.getPartById(id);
          if (gn != null) return gn;  
      }
      return null;      
   }
   
   /**
    * Searches for a GNode with specified id and/or sid. The search is recursive,
    * among the child nodes of this GNode, and include this GNode as well.
    */
   public GNode getPart(String id_or_sid) {
      if  (this.sid != null && this.sid.equals(id_or_sid) ) return this;
      if  (this.id != null && this.id.equals(id_or_sid)   ) return this;
      for (GNode childNode : getChildNodes()) {
          GNode gn = childNode.getPart(id_or_sid);
          if (gn != null) return gn;  
      }
      return null;      
   }
   
   /**
    * Searches for a GNode with a name matching the specified regular pattern.
    * For example getPartByNamePattern(".*L.*Clavicle$") would find GNodes with names like "CWom0023-L_Clavicle"
    * See the Javadoc for java.util.regex.Pattern for the syntax of allowed regular expressions.
    */
   public GNode getPartByNamePattern(String namePattern) {   
      return getPartByNamePattern(Pattern.compile(namePattern));
   }
   
   
   /**
    * Searches for a GNode with a name matching the specified (java.util.regex) regular pattern.
    */
   public GNode getPartByNamePattern(Pattern namePat) {
      if (name != null && namePat.matcher(name).matches()) {
          return this;  
      }
      for (GNode childNode : getChildNodes()) {
          GNode gn = childNode.getPartByNamePattern(namePat);
          if (gn != null) return gn;  
      }
      return null;      
   }
   
  
  
   /**
    * recursively renames the sids and names of GNodes
    */
   public void renameJoints(Map<String, String> renaming) {
      String newName = null;
      if (sid != null)                      newName = renaming.get(sid) ; // first try: use sid, if available
//      if (newName == null && name != null)  newName = renaming.get(name); // second try, use name, if available
//      if (newName == null && id != null)    newName = renaming.get(id);   // last try, use id, if available
//      if (newName == null  && name != null) newName = name;               // no renaming found, fall back on name, if defined
//      if (newName == null  && sid != null)  newName = sid;                // no renaming, no name, so use sid
//      if (newName == null  && id != null)   newName = id;                 // no renaming, no name, no sid so use id
//      // By now, newName should not be null, unless all of id, sid, and name were null
      if (newName != null) {
          //System.out.println("GNode Rename: " + id + ", " + name + ", " + sid + " -> " + newName);
          setSid(newName);
          // setName(newName);
      }
      for (GNode childNode : getChildNodes()) {
         childNode.renameJoints(renaming );
      }
   }
  
  
//     /**
//    * recursively renames the sids and names of GNodes
//    */
//   public void renameJoints(Map<String, String> renaming) {
//      String newName = null;
//      if (sid != null)                      newName = renaming.get(sid) ; // first try: use sid, if available
//      if (newName == null && name != null)  newName = renaming.get(name); // second try, use name, if available
//      if (newName == null && id != null)    newName = renaming.get(id);   // last try, use id, if available
//      if (newName == null  && name != null) newName = name;               // no renaming found, fall back on name, if defined
//      if (newName == null  && sid != null)  newName = sid;                // no renaming, no name, so use sid
//      if (newName == null  && id != null)   newName = id;                 // no renaming, no name, no sid so use id
//      // By now, newName should not be null, unless all of id, sid, and name were null
//      if (newName != null) {
//          //System.out.println("GNode Rename: " + id + ", " + name + ", " + sid + " -> " + newName);
//          setSid(newName); setName(newName);
//      }
//      for (GNode childNode : getChildNodes()) {
//         childNode.renameJoints(renaming );
//      }
//   }
 
  
  
   /**
    * Sets the translation of the associated VJoint
    */
   public void setTranslation(float[] translation) {
      joint.setTranslation(translation);
   }
   
   
   /**
    * Returns the translation of the associated VJoint
    */
   public void getTranslation(float[] result) {
      joint.getTranslation(result);  
   }
   
   /**
    * Returns the translation of the associated VJoint
    */
   public float[] getTranslation() {
      float[] result = Vec3f.getVec3f();
      joint.getTranslation(result);  
      return result;
   }
   
   
   /**
    * Sets the rotation quaternion of the associated VJoint
    */
   public void setRotation(float[] rotation) {
      joint.setRotation(rotation);
   }
   
   /**
    * Return the rotation quaternion of the associated VJoint
    */
   public void getRotation(float[] rotation) {
      joint.getRotation(rotation);
   }
   
   
   /**
    * Returns the rotation quaternion of the associated VJoint
    */
   public float[] getRotation() {
      float[] result = Quat4f.getQuat4f();
      joint.getRotation(result);  
      return result;
   }
   
   /**
    * Sets the scale vector of the associated VJoint
    */
   public void setScale(float[] scaleVec) {
      joint.setScale(scaleVec);
   }
   
   /**
    * SeGetsts the scale vector of the associated VJoint
    */
   public void getScale(float[] scaleVec) {
      joint.getScale(scaleVec);
   }
   
   /**
    * Returns the Vec3f scale vector of the associated VJoint
    */
   public float[] getScale() {
      float[] result = Vec3f.getVec3f();
      joint.getScale(result);  
      return result;
   }
   
   /**
    * Sets the skewing matrix of the associated VJoint
    */
   public void setSkewMatrix(float[] skewMatrix) {
      joint.setSkewMatrix(skewMatrix) ;
   }   
    
   /**
    * Sets the local transform matrix of the associated VJoint
    */ 
   public void setLocalTransform(float[] m) {
      joint.setLocalTransform(m);
   }
   
   /**
    * Gets the local transform matrix of the associated VJoint
    */ 
   public float[] getLocalMatrix() {
      return joint.getLocalMatrix();
   }
   
   /**
    * Gets the global transform matrix of the associated VJoint
    */ 
   public float[] getGlobalMatrix() {
      return joint.getGlobalMatrix();
   }
   
   
   
   public void clearLocalLinearTransform() {
      joint.clearLocalLinearTransform();
   }
   
   
   public void clearRotations() {
      joint.clearRotation();
      for (GNode child : getChildNodes()) child.clearRotations();
   }
   
   public void clearJointRotations() {
      if (type == "JOINT" || type =="Joint") {
         joint.clearRotation();
      }
      for (GNode child : getChildNodes()) child.clearJointRotations();
   }
   
   /**
    * Transforms, recursively, the associated VJoint matrices.
    */
   public void affineTransformVJoint(float[] mat4x4) { 
      joint.affineTransform(mat4x4);
      for (GNode child : getChildNodes()) child.affineTransformVJoint(mat4x4);
   } 
    
   /**
    * Transforms, recursively, the attached GShapes
    */
   public void affineTransformGShapes(float[] mat4x4) { 
      //joint.affineTransform(mat4x4);
      for (GShape gshape : getGShapes()) gshape.affineTransform(mat4x4);
      for (GNode child : getChildNodes()) child.affineTransformGShapes(mat4x4);
   } 
   
   
   /**
    * Transforms, recursively, the VJoint matrix and the attached GShapes
    */
   public void affineTransform(float[] mat4x4) { 
      joint.affineTransform(mat4x4);
      for (GShape gshape : getGShapes()) gshape.affineTransform(mat4x4);
      for (GNode child : getChildNodes()) child.affineTransform(mat4x4);
   } 
   
//   /**
//    * Removes the scaling transformations from this scenegraph by applying scaling to geometry.
//    */ 
//   public void removeScaling() {
//      removeScaling(new float[] {1.0f, 1.0f, 1.0f});
//   }
//    
//  
//    
//   /**
//    * Removes the scaling, rotation, and translation transformations from this scenegraph by applying the transformations to geometry.
//    */  
//   public void removeAffineTransforms() {
//      removeAffineTransforms(Mat4f.ID);
//   } 
//    
//   /**
//    * Removes the transforms for this GNode and its descendants
//    * The specified mat4x4 (a 4X4 matrix) is applied to the current transform
//    * T o L, so it becomes ma4X4 o T o L. This transform is then applied to 
//    * the geometry attached to this GNode. The transform of this GNode is then set to identity (i.e. no translation, rotation, or scaling)
//     The combined transform is then applied (recursively) to all
//    * child GNodes. 
//    */ 
//   public void removeAffineTransforms(float[] mat4x4) {
//      float[] localMatrix = joint.getLocalMatrix();
//      float[] combinedTransform = Mat4f.getMat4f();
//      Mat4f.mul(combinedTransform, mat4x4, localMatrix);
//      joint.clearLocalAffineTransform();
//      for (GShape gs : getGShapes()) gs.getGMesh().affineTransform(combinedTransform);
//      for (GNode child : getChildNodes()) child.removeAffineTransforms(combinedTransform);
//   } 
//  
//   
   /**
    * Removes the scaling and rotation transformations from this scenegraph by applying the linear part of the transformations to geometry.
    * Translation is modified accordingly, but not eliminated.
    */ 
   public void removeLinearTransforms() {
      removeLinearTransforms(Mat3f.getIdentity());
   }
  
  
   /**
    * Removes the linear component of the transform of this GNode and its descendant.
    * That, the upper left 3X3 part, specifying the linear transform part, will be set to
    * identity, but a translation part will remain.
    */
   public void removeLinearTransforms(float[] mat3x3) {
      float[] localLinear = Mat3f.from4x4(joint.getLocalMatrix());
      float[] combinedTransform = Mat3f.getMat3f();
      Mat3f.mul(combinedTransform, mat3x3, localLinear);
      for (GShape gs : getGShapes()) gs.getGMesh().linearTransform(combinedTransform);
      joint.getTranslation(vec);
      Mat3f.transform(mat3x3, vec);
      joint.setTranslation(vec);
      joint.clearLocalLinearTransform();  
      for (GNode child : getChildNodes()) child.removeLinearTransforms(combinedTransform);
   } 
     
   
   /**
    * Returns the XML encoding of this GNode
    */
   @Override
   public String toString() {
      return toXMLString();
   }
   
   
   /**
    * appends the id, sid, name XML attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "id", id);
      appendAttribute(buf, "sid", sid);
      appendAttribute(buf, "name", name);
      appendAttribute(buf, "type", type);
    //  if (type != null && (type == "Joint" || type == "JOINT")) {
         appendAttribute(buf, "translation", getTranslation(), ' '); 
         appendAttribute( buf, "rotation", getRotation() , ' ');
         appendAttribute( buf, "scale", getScale() , ' ');
   //   }
      return buf;
   }



   /**
    * decodes the id and sid XML attributes.
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      setId(getOptionalAttribute("id", attrMap));
      setSid(getOptionalAttribute("sid", attrMap));
      setName(getOptionalAttribute("name", attrMap));
      setType(getOptionalAttribute("type", attrMap));
      String tr = getOptionalAttribute("translation", attrMap);
      if (tr != null) setTranslation(decodeFloatArray(tr));
      String rot = getOptionalAttribute("rotation", attrMap);
      if (rot != null) setRotation(decodeFloatArray(rot));
      String sc = getOptionalAttribute("scale", attrMap);
      if (sc != null)  setScale(decodeFloatArray(sc));
      super.decodeAttributes(attrMap, tokenizer);
   }

   /**
    * append XML contents section to buf
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, gnodes);
      appendXMLStructureList(buf, fmt, gshapes);
      return buf;
   }

   /**
    * decodes XML content section
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();  
         if (tag.equals(GNode.xmlTag())) {
            addChildNode(new GNode(tokenizer));
         } else if (tag.equals(GShape.xmlTag())) {
            addGShape(new GShape(tokenizer));   
         } else {
            logger.error(tokenizer.getErrorMessage("GNode: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }      
   }

 

   /*
    * The XML tag for XML encoding
    */
   private static final String XMLTAG = "gnode";
 
   /**
    * The XML tag for XML encoding
    */
   public static String xmlTag() { return XMLTAG; }
 
   /**
    * returns the XML tag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   }
   

   /**
    * Writes a binary encoding to dataOut
    */
   public void writeBinary(DataOutput dataOut) throws IOException { 
      BinUtil.writeOptionalString(dataOut, id);
      BinUtil.writeOptionalString(dataOut, sid);
      BinUtil.writeOptionalString(dataOut, name);
      BinUtil.writeOptionalString(dataOut, type);
      BinUtil.writeFloatArray(dataOut, getTranslation());
      BinUtil.writeFloatArray(dataOut, getRotation());
      BinUtil.writeFloatArray(dataOut, getScale());      
      BinUtil.writeBinaryList(dataOut, gshapes);
      BinUtil.writeBinaryList(dataOut, gnodes); 
   }
    
   /**
    * Reads a binary encoding from dataIn
    */ 
   public void readBinary(DataInput dataIn) throws IOException {
      setId(BinUtil.readOptionalString(dataIn));
      setSid(BinUtil.readOptionalString(dataIn));
      setName(BinUtil.readOptionalString(dataIn));
      setType(BinUtil.readOptionalString(dataIn));
      setTranslation(BinUtil.readFloatArray(dataIn));
      setRotation(BinUtil.readFloatArray(dataIn));
      setScale(BinUtil.readFloatArray(dataIn));
      gshapes = BinUtil.readBinaryList(dataIn, GShape.class);
      ArrayList<GNode> childNodes = BinUtil.readBinaryList(dataIn, GNode.class);
      if (childNodes == null) {
         gnodes = null;
      } else {
         for (GNode child : childNodes) addChildNode(child);
      }
   }    

 

}
