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

import hmi.animation.VJoint;
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderList;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.opengl.GLShape;
import hmi.graphics.opengl.GLUtil;

import java.util.ArrayList;

/**
 * A VGL node combines a VJoint (tree) with a GLRenderList.
 * The GLRenderList objects will usually link to the global transform matrix of the VJoint.
 * @author Job Zwiers
 */
public class VGLNode implements GLRenderObject
{
   
   /** The VJoint root determines the transformation for this VGLNode.   */
   private VJoint root;
   
   /** The GLRenderList shapeList specifies GLRenderObjects to be rendered. */
   private GLRenderList shapeList;
  // private GLRenderObject marker;
  
  private static final ArrayList<VGLNode> EMPTYLIST = new ArrayList<VGLNode>(0);
  private ArrayList<VGLNode> children = EMPTYLIST;
  
  
   /**
    * Creates a new VGLNode, specified name and capacity 1.
    */
   public VGLNode(String name) {
      this(name, 1);
   }
  
   /**
    * Creates a VGLNode with allocated root, with specified name,
    * and with allocated shapeList, with specified capacity.
    */
   public VGLNode(String name, int capacity) {
      root = new VJoint(name);
      shapeList = new GLRenderList(capacity);
   }
   
   /**
    * Creates a VGLNode with specified root and shapeList.
    * It is assumed here that the list elements are linked to the appropriate VJoint already,
    * which need not be the root of this VGLNode.
    */
   public VGLNode(VJoint root, GLRenderList shapeList) {
      this.root = root;
      this.shapeList = shapeList;
   }
   
   public String getName() {
      return root == null ? "" : root.getName();
   }
   
   public GLRenderList getGLShapeList() {
      return shapeList;
   }

   public void addGLShapeList(GLRenderList shapeList) 
   {    
      this.shapeList.addAll(shapeList);
   }
   
   
   public VJoint getRoot() {
      return root;
   }
   
  
   public void prependGLShape(GLShape glShape) {
      shapeList.prepend(glShape);
   }
   
   /**
    * Adds some GLShape, and links it to the root of this VGLNode
    */
   public void addGLShape(GLShape glShape) {
      shapeList.add(glShape);
      glShape.linkToTransformMatrix(root.getGlobalMatrix());  
   }
   
   private static final boolean collectShapes = false;
   
   /**
    * Adds the root of the childNode as a VJoint child, and
    * appends all shapeList elements from the childNode
    * to the shapeList of this VGLNode.
    */
   public void addChild(VGLNode childNode) {
      root.addChild(childNode.root);
      if (children == EMPTYLIST) children = new ArrayList<VGLNode>();
      if (collectShapes) {
         shapeList.addAll(childNode.shapeList); // also ok if childNode.shapeList == null 
      } else {
         children.add(childNode);
      }
      
      
   }
   
  
   
   /**
    * OpenGL initialization.
    */
   @Override
   public void glInit(GLRenderContext gl) {
      shapeList.glInit(gl);
      for (VGLNode child : children) child.glInit(gl);
   }
   
   /**
    * OpenGL rendering.
    */
   @Override
   public void glRender(GLRenderContext gl) {    
      shapeList.glRender(gl);
      if (! collectShapes) {
         for (VGLNode child : children) child.glRender(gl);
      }
   }
   
   
   /* flag that determines the amount of detail for appendAttributesTo() and toString() */   
   private static boolean showDetail = true;
  
   /**
    * Sets the showDetail mode for toString()
    */
   public static  void setShowDetail(boolean show) {
      showDetail = show;
   }
  

   /* denotes whether toString should show detail or not */
   public boolean showDetail() { return showDetail;  }
      
   
   /*
    * Appends 
    */
   public StringBuilder appendTo(StringBuilder buf, int tab) {
       GLUtil.appendSpacesString(buf, tab, "VGLNode ");
       buf.append(getName());
      
       if (showDetail()) {
           root.appendTo(buf, tab+GLUtil.TAB);
           
       }
       buf.append('\n');
       shapeList.appendTo(buf, tab+GLUtil.TAB);
       for (VGLNode child : children) child.appendTo(buf, tab+GLUtil.TAB);
       return buf;  
   }
   
   
   @Override
   public String toString() {
      StringBuilder buf = appendTo(new StringBuilder(), 0);
      return buf.toString();
   }
   
   
   

           
           
             
}
