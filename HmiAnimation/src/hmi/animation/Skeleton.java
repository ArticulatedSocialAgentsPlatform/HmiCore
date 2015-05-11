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
package hmi.animation;

import hmi.math.Mat4f;
import hmi.math.Vec3f;
//import hmi.util.ExchangeBuffer;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Encapsulates a VJoint based skeleton structure.
 * 
 * One of the tasks of Skeleton is to act as a synchronized interface between
 * animator/modifier (Threads) of the skeleton, and render/user Threads, that
 * need the transform data. Basically, the roles are as follows:
 * 1) Animator Threads can freely modify VJoint rotations and other
 * local transform data, without locking.
 * 2) The animator Thread should call updateJointMatrices once in a while,
 * to transfer this data to the jointMatrices data, that is shared with other Threads.
 * This is a synchronized method.
 * 3) Render Threads should not use VJoint local data. Rather, they should call
 * the (synchronized) updateTransformMatrices method, which accesses the joint matrices
 * in a safe way. Afterwards, the render Thread can freely use the transform matrices,
 * without locking. 
 */
public class Skeleton// implements ExchangeBuffer
{
   private String id;          // id of the Skeleton as a whole. null or an interned String
   private ArrayList<VJoint> roots = new ArrayList<>();
   private ArrayList<VJoint> joints = new ArrayList<>(); // references to the actual VJoint nodes.

   // sidsSpecified is to true when jointSids have been specified explicitly,
   // either by calling setJointSids(), or when XML with a <joints> section is decoded.
   //private boolean sidsSpecified = false; 
   private ArrayList<String> jointSids = new ArrayList<>();
   // jointSidsSpecified is  set to "true" only when setJointSids is called explicitly. Remains false when addSids is used instead.
   private boolean jointSidsSpecified = false; 
 
   
   private float[][] jointMatrices; // transform matrices for all joints, linked to the global matrices within the VJoints.
   private float[][] jointMatricesBuffer; // matrix buffers, used to copy jointMatrices data.
   
   private float[][] inverseBindMatrices;
   private float[][] transformMatrices;
   private boolean invalidMatrices = true; // signals "invalid" matrix arrays, due to modifications for roots and/or jointSids
   
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
    * Initializes this Skeleton from the specified XMLSkeleton
    */
   public Skeleton(XMLSkeleton xmlSkel) {
       this();
       setId(xmlSkel.getId());
       setRoots(xmlSkel.getRoots());
       setJointSids(xmlSkel.getJointSids());  
   }
   
   /**
    * Read a Skeleton from an XMLTokenizer. 
    */
   public Skeleton(XMLTokenizer tokenizer) throws IOException {
       this(new XMLSkeleton(tokenizer));
   }
   
   /**
    * Returns an XML encoding, via XMLSkeleton.
    */
   public String toXMLString() {
       XMLSkeleton xmlSkel = new XMLSkeleton(id);
       xmlSkel.setJointSids(jointSids);
       xmlSkel.setRoots(roots);
       return xmlSkel.toXMLString();
   }
   
   /**
    * Returns an (XML encoded) String representation, as defined by XMLSkeleton.
    */
    @Override
    public String toString() {
        return toXMLString();
    }
   
   /**
    * Creates and returns a new Skeleton, from an XML encoded description,
    * as defined by XMLSkeleton.
    * @param xmlEncoding the XML encoded joint/bones structure
    * @return Skeleton as specified by the encoding, or null when incorrect.
    */
   public static Skeleton fromXML(String xmlEncoding)  {
       try {
           return new Skeleton(new XMLTokenizer(xmlEncoding));
       } catch (IOException e) { // shoukld not happen for String bases tokenizer.
           System.out.println("Skeleton.fromXML: Unexpected error: " + e);
           return null;
       }
   }
   
   /* private setter for id */
   private void setId(String id) {
       this.id = id == null ? "" : id.intern();
   }
 
   /**
    * Returns the Skeleton id.
    */
   public String getId() { return id; } 
  
   
   /**
    * Sets the List of joint sids, specified by means of
    * a String array, rather than a List.
    */
   public final void setJointSids(String[] sids) {
       setJointSids(Arrays.asList(sids));
   }
   

   /**
    * Sets the List of joint sids.
    * If VJoint trees have been added already, these will be resolved/
    */
   public final void setJointSids(List<String> sids) {
       jointSids.clear();
       jointSids.addAll(sids);
       jointSidsSpecified = true;
       joints = new ArrayList<>(jointSids.size());
       for (int i=0; i<jointSids.size(); i++) {
           joints.add(null);
       }
       for (VJoint rt : roots) {
           resolveJoints(rt);
       }
       invalidMatrices = true;
   }
   
   
   /**
    * Returns the List of joints sids.
    */
   public ArrayList<String> getJointSids() { return jointSids; }
   

   /**
    * Returns the root joints.
    */
   public ArrayList<VJoint> getRoots() { return roots; }
   
   /**
    * Returns the root joint with specified index 0.
    */
   public VJoint getRoot() {  
       return (roots.isEmpty() ? null : roots.get(0));
   }
   
   /**
    * Sets the List of roots
    */
    public final void setRoots(List<VJoint> roots) {
        this.roots.clear();
        if (! jointSidsSpecified) { jointSids.clear(); }
        for (VJoint rt : roots) { addRoot(rt); }
        invalidMatrices = true;
    }
   
   /**
    * Adds a root joint for the Skeleton, implicitly defining (part of) the Skeleton VJoint tree.
    * If joint sids have not specified explicitly, they will be derived by exploring the VJoint tree.
    * *====> TAKE CARE OF DISTINCTION JOINT/NODE
    */
   public final void addRoot(VJoint root) {
       if (root == null) {
           return;
       }
       roots.add(root);
       if (jointSidsSpecified) { 
           resolveJoints(root);         
       } else {
           addJoints(root); 
       }
       invalidMatrices = true;
   }
   
//   /* Inorder traversal, adding joint sids to jointSids List */
//   private void addSids(VJoint vj) {
//       jointSids.add(vj.getSid());
//       for (VJoint child : vj.getChildren()) { addSids(child); }          
//   }
   
   
   
   /* Inorder traversal, adding joints/joint sids to the Lists */
   private void addJoints(VJoint vj) {
       jointSids.add(vj.getSid());
       joints.add(vj);
       for (VJoint child : vj.getChildren()) { addJoints(child); }          
   }
   
   
   
   
   /*
    * Adds to the joints List by traversing the specified VJoint tree.
    * Assumption: jointSidsSpecified is true, so the jointSids
    * List is specified, and is fixed. Moreover, the joints List
    * already exists, and has the same size as jointSids, possibly
    * containing null VJoint elements.
    */
   private void resolveJoints(VJoint rt) {
       int index = 0;
       for (String sid : jointSids) {
           VJoint vj = getVJoint(rt, sid);
           if (vj != null) { // don't overwrite joints already resolved before.
               joints.set(index, vj);
           }
           index++;
       } 
   }
   
   
      /* Method for creating a VJoint id from a specified sid */
   public String makeId(String sid) {
       return id + "-" + sid;
   }
   
   
   private boolean updateOnWriteMatrices = false;
   
   public void setUpdateOnWrite(boolean status) {
       updateOnWriteMatrices = status;
   }
   
    /**
     * Implements the writeBuffer op from the ExchangeBuffer interface. 
     * This implementation is not Thread-safe.
     */
//    @Override
    public void putData() {
        //System.out.println("Skeleton.writeBuffer jointMatrices.length= " + jointMatrices.length);
        if (updateOnWriteMatrices) {
            updateJointMatrices();           
        }
        for (int i=0; i<jointMatrices.length; i++) {
            
            Mat4f.set(jointMatricesBuffer[i], jointMatrices[i]);
        } 
    }
   
   
  
    
     /**
     * Implements the readBuffer op from the ExchangeBuffer interface. 
     * This implementation is not Thread-safe.
     */
//    @Override
    public void getData() {
        //System.out.println("Skeleton.readBuffer");
       updateTransformMatrices();
    }
   
    /**
     * Calls calculateMatrices for all VJoint roots.
     * This method is not Thread-safe.
     */
    public void updateJointMatrices() {
        //System.out.println("updateJointMatrices");
        for (VJoint rt : roots) {
            rt.calculateMatrices();
        }
    }
   
    /*
     * Calculates the transform matrices, either by copying from the joint matrices,
     * or by multiplying the latter with inverse bind matrices, if the later are defined.
     * The method is synchronized, so cooperates well with updateJointmatrices calls.
     * This updateTransformMatrices method would be called typically by a render
     * Thread, or some other "user" Thread. 
     */
    private  void updateTransformMatrices() {
        if (transformMatrices != null) {
            if (inverseBindMatrices ==  null) { // just copy:
                //System.out.println("Skeleton.updateTransformMatrices -- copy");
                for (int i=0; i<transformMatrices.length; i++) {
                    if (transformMatrices[i] != null) {
                        Mat4f.set(transformMatrices[i], jointMatricesBuffer[i]);
                    }
                }             
            } else { // multiply with inverse bind matrices:
                //System.out.println("Skeleton.updateTransformMatrices -- multiply inverBindMatrices");
                for (int i=0; i<transformMatrices.length; i++) {
                    if (transformMatrices[i] != null) {
                        Mat4f.mul(transformMatrices[i], jointMatricesBuffer[i], inverseBindMatrices[i]);
                    }
                }            
            }
        }
    }
   
    
      public void showJointMatrices() {
        if (jointMatrices == null) {
            System.out.println("Null jointMatrices");
        } else {
            for (int i=0; i<jointMatrices.length; i++) {
                System.out.println("jointMatrices[" + i + "] " + Mat4f.toString(jointMatrices[i]));
            }
        }
    }
    
    public void showJointMatricesBuffer() {
        if (jointMatrices == null) {
            System.out.println("Null jointMatricesBuffer");
        } else {
            for (int i=0; i<jointMatrices.length; i++) {
                System.out.println("jointMatricesBuffer[" + i + "] " + Mat4f.toString(jointMatricesBuffer[i]));
            }
        }
    }
    
    public void showTransformMatrices() {
        if (jointMatrices == null) {
            System.out.println("Null transformMatrices");
        } else {
            for (int i=0; i<jointMatrices.length; i++) {
                System.out.println("transformMatrices[" + i + "] " + Mat4f.toString(transformMatrices[i]));
            }
        }
    }
    
    
    
    /**
     * Sets a reference to the specified matrices, to be used as inverse bind matrices
     * for the calculateMatrices calls later on.
     */
    public void setInverseBindMatrices(float[][] matrices) {
        this.inverseBindMatrices = matrices;
    }
   
    
    private void allocateJointMatrices() {
        if ( invalidMatrices) {    
            //System.out.println("allocateJointMatrices");
           jointMatrices = new float[joints.size()][];
           jointMatricesBuffer = new float[joints.size()][];
           //inverseBindMatrices = new float[jointSids.size()][];
           transformMatrices = new float[joints.size()][];
           // inverseBindMatrices are not allocated here.
           int index = 0;
           for (VJoint vj : joints) {
               if (vj != null) {
                   jointMatrices[index] = vj.getGlobalMatrix();
                   jointMatricesBuffer[index] = Mat4f.getMat4f();
                   transformMatrices[index] = Mat4f.getMat4f();
               } else {
                   System.out.println("Skeleton.getTransformMatrices: no VJoint found for sid=\"" + jointSids.get(index) + "\"");
               }
               index++;
           }
           invalidMatrices = false;
       }
    }
    
   /**
    * Returns a reference to the array of transform matrices, 
    * including one float matrix for every joint, 
    * in the order as specified by the joints List.
    * The array is never null, but might contain some null matrices,
    * when certain jointSids are not actually present in the VJoint trees.
    * This method will also allocate (but not initialize) all matrices, 
    * based upon the current roots and jointSids. 
    * The matrix values to which the array refers should be updated later on 
    * by calling calculateMatrices.
    */
   public float[][] getTransformMatricesRef() {    
       allocateJointMatrices();
       return transformMatrices;     
   }
   
   /**
    * Search for the VJoint for the specified sid, from all Skeleton roots. 
    */
   public VJoint getVJoint(String sid) {
       for (VJoint rt : roots) {
           VJoint result = getVJoint(rt, sid);
           if (result != null) {
               return result;
           }
       }
       return null;
   }
   
    /**
     * Search for the VJoint for the specified sid, 
     * from the specified VJoint as \&quot;root&quot;, 
     * which should be some existing VJoint inside one of the Skeleton trees.
     * (Not necessarily a root node of one of the trees.)
     */
   public VJoint getVJoint(VJoint vj, String sid) {
       if (vj.getSid().equals(sid)) {
           return vj;
       } else {
           for (VJoint child : vj.getChildren()) {
               VJoint result = getVJoint(child, sid);
               if (result != null) {
                   return result;
               }
           }
       }    
       return null;
   }


   private void initializeBindMatricesToIdentity()
   {
       inverseBindMatrices = new float[joints.size()][];
       for(int i=0;i<joints.size();i++)
       {
           inverseBindMatrices[i] = Mat4f.getMat4f();
           Mat4f.setIdentity(inverseBindMatrices[i]);
       }
   }
   
   /**
    * Defines the current pose to be the "neutral" pose, assumed when all
    * joint rotations are set to identity. Typically a pose like the
    * HAnim rest pose.
    */
   public void setNeutralPose() {
       for (VJoint rt : roots) {
           rt.calculateMatrices();
           
       }
           //adaptTranslationVectors(rt);
           // set bind pose.....
           
       float[] bindTranslation = Vec3f.getVec3f();
       float[] parentRotation = Mat4f.getMat4f();
       float[] rotation = Mat4f.getMat4f();
       float[] zeroVec = Vec3f.getZero();
           
//       float[] localTranslation = Vec3f.getVec3f();
//       float[] rotations = Mat4f.getMat4f();
       
       if(inverseBindMatrices==null)
       {
           initializeBindMatricesToIdentity();
       }
       
       for (int i=0; i<joints.size(); i++) {
               
           VJoint vj = joints.get(i);
           if (vj != null) {
               if (vj.getParent() != null) {
                   vj.getTranslation(bindTranslation);
                   Mat4f.set(parentRotation, vj.getParent().getGlobalMatrix());
                   Mat4f.transformVector(parentRotation, bindTranslation); // will use only rotation part of parentRotation
                   vj.setTranslation(bindTranslation);
                   
               }
               Mat4f.set(rotation, vj.getGlobalMatrix());
               Mat4f.setTranslation(rotation, zeroVec); // clear translation part
               Mat4f.mul(inverseBindMatrices[i], rotation, inverseBindMatrices[i]);    
               
               vj.clearRotation(); // sets local rotation to Id.
           }
       }
          
   
       
       
       
   }
   
//   /*
//    * Assuming that the (global) matrices contain the $V_i$ matrices that will be (left) multiplied with existing
//    * inverse bind matrices, this method adapts translation vector t_i' = V_{parent(i)}(t_i) 
//    */
//   private static void adaptTranslationVectors(VJoint joint) {
//       float[] localTranslation = Vec3f.getVec3f();
//       float[] rotations = Mat4f.getMat4f();
//       
//       if (joint.getParent() != null) {
//          joint.getTranslation(localTranslation);
//          Mat4f.set(rotations, joint.getParent().getGlobalMatrix());
//          Mat4f.transformVector(rotations, localTranslation);
//          joint.setTranslation(localTranslation);
//       }
//       for (VJoint child : joint.getChildren()) {
//           adaptTranslationVectors(child);
//       }
//   }
   
   
   
}
