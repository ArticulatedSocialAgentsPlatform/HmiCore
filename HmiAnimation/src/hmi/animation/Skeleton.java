package hmi.animation;

import hmi.xml.XMLTokenizer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Encapsulates a VJoint based skeleton structure.
 */
public class Skeleton 
{
   private String id;          // id of the Skeleton as a whole. null or an interned String
   private ArrayList<VJoint> roots = new ArrayList<>();

   // sidsSpecified is to true when jointSids have been specified explicitly,
   // either by calling setJointSids(), or when XML with a <joints> section is decoded.
   //private boolean sidsSpecified = false; 
   private ArrayList<String> jointSids = new ArrayList<>();;
  // private VJoint[] joints;
   
   private float[][] jointMatrices; // transform matrices for all joints, linked to the global matrices within the VJoints.
   private float[][] inverseBindMatrices;
   private float[][] transformMatrices;
   

   
   private static Logger logger = LoggerFactory.getLogger("hmi.animation.Skeleton");

   /* prevent Skeletons without id */
   private Skeleton() {}
   
  
   
   /**
    * Create a new Skeleton with specified Skeleton id.
    */
   public Skeleton(String id) {
      setId(id);
   }
   

   /**
    * Create a new Skeleton with specified Skeleton id and specified VJoint tree.
    */
   public Skeleton(String id, VJoint root) {
      this(id);
      addRoot(root);
   }
 
   
   /**
    * Read a Skeleton from an XMLTokenizer. 
    */
   public Skeleton(XMLTokenizer tokenizer) throws IOException {
      this();
      XMLSkeleton xmlSkel = new XMLSkeleton(tokenizer);
      setId(xmlSkel.getId());
      setRoots(xmlSkel.getRoots());
      setJointSids(xmlSkel.getJointSids());    
   }
   
   
   /*private setter for id */
   private void setId(String id) {
      this.id = id == null ? "" : id.intern();
   }
 
   /**
    * Returns the Skeleton id.
    */
   public String getId() {  return id; } 
  

  /**
    * Sets the List of joint sids.
    */
   public void setJointSids(ArrayList<String> sids) {
       this.jointSids = sids;
       dirty = true;
   }
   
   
   /**
    * returns the List of joints sids.
    */
   public ArrayList<String> getJointSids() {
       return jointSids;
   }
   

   /**
    * Returns the root joints.
    */
   public ArrayList<VJoint> getRoots() {  
       return roots;
   }
   
   /**
    * Returns the root joint with specified index 0.
    */
   public VJoint getRoot() {  
       if (roots.isEmpty()) {
           return null;
       } else {
           return roots.get(0);
       }
   }
   
   /**
    * Sets the List of roots
    * 
    */
    public void setRoots(ArrayList<VJoint> roots) {
        this.roots = roots;
        dirty = true;
    }
   
   /**
    * Adds a root joint for the Skeleton, implicitly defining (part of) the Skeleton VJoint tree.
    * If joints have not specified explicitly, they will be derived by exploring the VJoint tree.
    */
   public final void addRoot(VJoint root) {
      roots.add(root);
      dirty = true;
   }
   
   
   /**
    * Calls calculaterMatrices for all VJoint roots. 
    */
   public synchronized void calculateMatrices() {
       for (VJoint rt : roots) {
           rt.calculateMatrices();
       }
       if (transformMatrices != null) {
           // update, in this synchronized block
       }
   }
   
   private boolean dirty = true; // signals modifications for roots and/or jointSids
   
   /**
    * Returns a reference to the array of transform matrices, including
    * one float matrix for every joint, 
    * in the order as specified by the joint sid List.
    * The array is never null, but might contain some null matrices,
    * when certain jointSids are not actually present in the VJoint trees.
    * This method will also allocate all matrices, based upon the current
    * definitions for roots and jointSids. 
    */
   public float[][] getTransformMatrices() {    
       if (dirty) {            
           jointMatrices = new float[jointSids.size()][];
           inverseBindMatrices = new float[jointSids.size()][];
           transformMatrices = new float[jointSids.size()][];
           int index = 0;
           for (String sid : jointSids) {
               VJoint vj = searchVJoint(sid);
               if (vj != null) {
                   jointMatrices[index] = vj.getGlobalMatrix();
               }
               index++;
           }
     
           dirty = false;
       }
       return transformMatrices;     
   }
   
   /* Search for the VJoint for the specified sid, in all trees */
   private VJoint searchVJoint(String sid) {
       for (VJoint rt : roots) {
           VJoint result = searchVJoint(rt, sid);
           if (result != null) return result;
       }
       return null;
   }
   
    /* Search for the VJoint for the specified sid, via the specified tree root node */
   private VJoint searchVJoint(VJoint root, String sid) {
       if (root.getSid().equals(sid)) {
           return root;
       } else {
           for (VJoint child : root.getChildren()) {
               VJoint result = searchVJoint(child, sid);
               if (result != null) return result;
           }
       }    
       return null;
   }

//   /*
//    * Add the newSidArray elements to to the jointSid array.
//    */
//   private void addToSidsArray(String[] newSidArray) {
//       if (jointSids == null) { // first (or only) root   
//           jointSids = newSidArray;
//       } else { // we already had another root, so add new elements
//           String[] oldSidArray = jointSids;
//           int newSize = oldSidArray.length + newSidArray.length; 
//           jointSids = new String[newSize];
//           System.arraycopy(oldSidArray, 0, jointSids, 0, oldSidArray.length);
//           System.arraycopy(newSidArray, 0, jointSids, oldSidArray.length, newSidArray.length);        
//       }
//   }
//   
//   /*
//    * Create a jointSid array, collecting sids from  the specified VJoint tree.
//    */
//   private String[] createSidArray(VJoint root) {
//       List<String> sidList = new ArrayList<>();
//       addSidsToList(sidList, root);    
//       return sidList.toArray(new String[0]);
//      
//   }
//   
//   /* Auxiliary for addToSidsArray */
//   private void addSidsToList(List<String> sidList, VJoint vj) {
//       if (vj == null) return;
//       sidList.add(vj.getSid());
//       for (VJoint child : vj.getChildren()) {
//           addSidsToList(sidList, child);
//       }
//   }
//   
//   
//   /* assume the joints array is defined, create the jointSids array */
//   private void makeJointSidsArray() {
//       if (joints==null) return;
//       jointSids = new String[joints.length];
//       for (int i=0; i<jointSids.length; i++) {
//           jointSids[i] = joints[i].getSid();
//       }
//   }
// 
//   /* Add joints, using the specified joint sids */
//   private void addToJointsArray(String[] sids) {
//       if (sids == null) return;
//       VJoint[] newJoints = new VJoint[sids.length];
//       for (int i=0; i<newJoints.length; i++) {
//           newJoints[i] = new VJoint(id + "-" + sids[i], sids[i]);
//       }    
//       if (joints == null) {
//           joints = newJoints;
//       } else {
//           VJoint[] oldJoints = joints;
//           joints = new VJoint[oldJoints.length + newJoints.length];
//           System.arraycopy(oldJoints, 0, joints, 0, oldJoints.length);
//           System.arraycopy(newJoints, 0, joints, oldJoints.length, newJoints.length);        
//       }
//   }
//   
//   
//   
//   /**
//    * Specifies explicitly which joint sids will be used, for all
//    * VJoint trees together, overriding the implicitly defined sid array.
//    * When a VJoint tree(s) is/are defined, only those joints with
//    * a sid occurring in the specified sid array will be accessible via the 
//    * joint matrices etc.
//    */
//   public void setJointSids( String[] sids) {
//       this.jointSids = sids;
//       sidsSpecified = true;
//       addToJointsArray(sids);
//       linkToVJoints();
//   }   
//   
//   private void linkToVJoints() {
//       for (int i=0; i<roots.length; i++) {
//          linkToVJoints(roots[i]);
//       }
//   }
//   
//   private void linkToVJoints(VJoint root) {
//       if (root == null || jointSids == null || joints == null) return;
//       for (int i=0; i<jointSids.length; i++) {
//           VJoint vj = searchVJoint(root, jointSids[i]);
//           if (vj != null) {
//               joints[i] = vj;
//           } // else ? 
//       }
//   }
//
//     
//   /* finds a VJoint in the VJoint tree, by searching for a specified sid.
//    * Returns null when there is no such VJoint.
//    */
//   private VJoint searchVJoint(VJoint root, String sid) {
//       if (root == null) return null;
//       if (root.getSid().equals(sid)) {
//           return root;
//       } else {
//           for (VJoint child : root.getChildren()) {
//               VJoint result = searchVJoint(child, sid);
//               if (result != null) return result;
//           }
//       }    
//       return null;
//   }
//   
//   
//   
//   
////   /* Returns the index of the specified joint sid, or -1 if not present in the jointSids list */
////   private int findJointSidIndex(String sid) {
////       for (int index=0; index<jointSids.length; index++) {
////           if (jointSids[index].equals(sid) ) {
////               return index;
////           }
////       }
////       return -1;
////   }
//   
////   /* Returns the index of the specified joint sid, or -1 if not present in the jointSids list */
////   private VJoint findVJointInJoints(String sid) {
////       for (int i=0; i<joints.length; i++) {
////           if (joints[i].getSid().equals(sid)) {
////               return joints[i];
////           }
////       }
////       return null;
////   }
//   
//   /*********************************************************
//   /* joint matrices SECTION
//   /********************************************************
//  
//      
// 
//   
//   /* Assuming joints is defined, create a jointMatrices array, and link to
//    * the global matrices of the corresponding VJoints.
//    */
//   private void makeJointMatricesArray() {
//       if (joints == null) return;
//       jointMatrices = new float[joints.length][];
//       for (int i=0; i< jointMatrices.length; i++) {
//           jointMatrices[i] = joints[i].getGlobalMatrix();
//       }
//   }
//   
   
   
   
   
   
}
