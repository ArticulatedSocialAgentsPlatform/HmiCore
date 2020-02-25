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
/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2005/02/06 22:44:57 $      
 */

package hmi.graphics.colladatest;



import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;
import hmi.util.ClockListener;
import hmi.math.*;
import hmi.animation.VJoint;


/**
 * A simple form of keyboard based NavigationScene module.
 * Keys: A and D move left and right, W and S move back and forth, 
 * Left and right &quot;arrow&quot; keys rotate,  Up and down &quot;arrow&quot;
 * keys move back and forth. PageUp and PageDown move up and down.
 */
public class NavigationScene implements ClockListener, GLRenderObject {

   /**
    * Creates a new NavigationScene, listening to Component c for key and mouse events
    */
   public NavigationScene(Component c, VJoint viewpoint) {
      keyState = new KeyState(c); 
      this.viewpoint = viewpoint;
      Mat4f.setIdentity(rotationMatrix);
      Mat4f.setIdentity(translationMatrix);
      Mat4f.setIdentity(transformMatrix);
      linVel = 0.2f;  //was 1.0
      vertVel = 0.2f; // was 1.0
      strafeVel = 0.1f; // was 1.0
      angVel = 10.0f; // was 50.0
   }

//   /**
//    * initializes NavigationScene: set initial position, orientation, and speeds
//    */
//   public void glInit(GL gl) {
//   
//   }

 
 
    /**
    * OpenGL initialization.
    */
   @Override
   public void glInit(GLRenderContext gl) {
   
   }
   
   /**
    * OpenGL rendering.
    */ 
   @Override
   public void glRender(GLRenderContext gl) {
//         gl.glGetFloatv(GLC.GL_MODELVIEW_MATRIX, mv);
//         String premv = Mat4f.toString(mv);
//         String trafo = Mat4f.toString(transformMatrix);
//         

         gl.glMultTransposeMatrixf(transformMatrix); // since OpenGL expects column major order, we use the transposed matrix

//         gl.glGetFloatv(GLC.GL_MODELVIEW_MATRIX, mv);
//         String postmv = Mat4f.toString(mv);
//         hmi.util.Console.println("GLViewpointRender", -1, 200, "******GLViewpoint " + name + "******\n"
//         + " \nMV:\n" + premv 
//         + " \ntransformMatrix:\n" + trafo 
//           + " \nMV multiplied:\n" + postmv 
//         );
 
     
   }
 

   /**
    * set the position vector
    */
   public void setPosition(float[] pos) {
       position[0] = pos[0];   
       position[1] = pos[1]; 
       position[2] = pos[2]; 
   }

   /**
    * set the position vector
    */
   public void setPosition(float x, float y, float z) {
       position[0] = x;   
       position[1] = y; 
       position[2] = z; 
   }

   /**
    * set the three Euler angles
    */
   public void setOrientation(float xrot, float yrot, float zrot) {
       orientation[0] = xrot;   
       orientation[1] = -yrot; 
       orientation[2] = zrot; 
   }

   public void initTime(double currentTime) {   
      currentDoubleTime = currentTime;  
            lastTime = currentDoubleTime;
   }
  

   public void time(double currentTime) {   
      currentDoubleTime = currentTime;  
  
      float linearVelocity = 0.0f;
      float vertVelocity = 0.0f;
      float strafeVelocity = 0.0f;
      float angularVelocityY = 0.0f;
      float angularVelocityX = 0.0f;
      //float angularVelocityZ = 0.0f;
      float delta,angle; //deltaX,deltaZ;
            
      // Keyboard Input
     
      synchronized(keyState) {
         
         if (keyState.keyDown[KeyEvent.VK_UP] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) {
             linearVelocity = linVel;
         }
         if (keyState.keyDown[KeyEvent.VK_W] && ! keyState.keyDown[KeyEvent.VK_CONTROL])  {        
            linearVelocity = 3.0f * linVel;
         }
         
         if (keyState.keyDown[KeyEvent.VK_DOWN] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) 
             linearVelocity = -linVel;
         
         if (keyState.keyDown[KeyEvent.VK_S] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) 
             linearVelocity = -3.0f * linVel;
         
         // Check for spin left/right (Y Axis rotation)
         if( (keyState.keyDown[KeyEvent.VK_LEFT]  && ! keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) )
             angularVelocityY = angVel;
         
         // Check for left strafing
         if( (keyState.keyDown[KeyEvent.VK_LEFT]  && keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) )
             strafeVelocity = -strafeVel;
             //angularVelocityY = -angVel;
         
         if (keyState.keyDown[KeyEvent.VK_A] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) 
             strafeVelocity = -4.0f * strafeVel;
               
         
         if( (keyState.keyDown[KeyEvent.VK_RIGHT]  && ! keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) )
             angularVelocityY = -angVel;
         
         // Check for right strafing
         if( (keyState.keyDown[KeyEvent.VK_RIGHT]  && keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL]) )
             strafeVelocity = strafeVel;
         
         if (keyState.keyDown[KeyEvent.VK_D] && ! keyState.keyDown[KeyEvent.VK_CONTROL])       
             strafeVelocity = 4.0f * strafeVel;
         
         
         if (keyState.keyDown[KeyEvent.VK_PAGE_UP] && keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL])       
             vertVelocity = vertVel;     
         
         if (keyState.keyDown[KeyEvent.VK_PAGE_DOWN]&& keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL])       
             vertVelocity = -vertVel;
         
         
         if (keyState.keyDown[KeyEvent.VK_PAGE_UP] && ! keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL])       
            // angularVelocityX = 0.2f * angVel;
            vertVelocity = vertVel;
         
         
         if (keyState.keyDown[KeyEvent.VK_PAGE_DOWN] && ! keyState.keyDown[KeyEvent.VK_ALT] && ! keyState.keyDown[KeyEvent.VK_CONTROL])       
            // angularVelocityX = -0.2f * angVel;
            vertVelocity = -vertVel;       
      }
      
      // Adjust position and orientation. Get the time since the last
      // check. If the velocity = 0 (no keypress or mouse movement)
      // then the motion will be nil...
      // D = vt
      //currentDoubleTime = (double) currentTime/1000;
      delta = (float) (currentDoubleTime-lastTime);
      lastTime = currentDoubleTime;
      //Console.println("udatePosition at " + currentTime + ", delta = " + delta);
      
      // Update Rotation angles (clamp the X rotation)
      angle = orientation[0] + delta * angularVelocityX;
      
      //if((angle < 90.0f) && (angle > -90.0f))
      if (angle >= 360.0f) angle -= 360.0f;
      if (angle <= -360.0f) angle += 360.0f;
      orientation[0] = angle;
      
      angle = orientation[1] + delta * angularVelocityY;
      if (angle >= 360.0f) angle -= 360.0f;
      if (angle <= -360.0f) angle += 360.0f;
      orientation[1] = angle;
      double radAngle = degToRad * orientation[1];
      
      // Update linear position
      
      float deltalinx = -delta * linearVelocity * (float)(Math.sin(radAngle));
      //if (deltalinx != 0.0) System.out.println("deltalin "+ deltalinx);
      position[0] += deltalinx;
      float deltalinz = -delta * linearVelocity * (float)(Math.cos(radAngle));
      //if (deltaliny != 0.0) System.out.println("deltaliny "+ deltaliny);
      position[2]  += deltalinz;
      
      //Console.println("udatePosition delta = " + delta + " position: " + position[0] + ", " + position[2]);
      float deltax = delta * strafeVelocity * (float)(Math.cos(radAngle));
      //if (deltax != 0.0) System.out.println(""+ deltax);
      position[0] += deltax;
      position[2]  -= delta * strafeVelocity * (float)(Math.sin(radAngle));
      //hmi.util.Console.println("Navifation: vertVelocity = " + vertVelocity );
      position[1] += delta * vertVelocity;
      
      
//      setRollPitchYaw(orientation[2], orientation[0], orientation[1]);
//      setTranslation(position[0],position[1], position[2]);
//   
//      Mat4f.setRotation(rotationMatrix, rotation);
//      Mat4f.setTranslation(translationMatrix, translation);
//      Mat4f.mul(transformMatrix, translationMatrix, rotationMatrix);
//      viewpoint.setLocalMatrix(transformMatrix);
   
   
//      // old method: set the *inverse* of the viuewpoint in the viewpoint VJoint.
//   
      setRollPitchYaw(-orientation[2], -orientation[0], -orientation[1]);
      setTranslation(-position[0],-position[1], -position[2]);
      
 // approach 1     
      Mat4f.setRotation(rotationMatrix, rotation);
      Mat4f.setTranslation(translationMatrix, translation);
      Mat4f.mul(transformMatrix, rotationMatrix, translationMatrix);
//      viewpoint.setLocalMatrix(transformMatrix);

      
// alternative approach 2 (alternative for setLocalMatrix):      
//      Quat4f.transformVec3f(rotation, translation);
//      viewpoint.setTranslation(translation);
//      viewpoint.setRotation(rotation);


// obsolete:      
//      Mat4f.setRotation(rotationMatrix, rotation);
//      Mat4f.setTranslation(translationMatrix, translation);
//      Mat4f.mul(transformMatrix, rotationMatrix, translationMatrix);
//      gl.glMultTransposeMatrixf(transformMatrix, 0);
       
   } 
   
   public void setRollPitchYaw(float roll, float pitch, float yaw) {
       Quat4f.setFromEulerAngles(rotation, yaw*conv, pitch*conv, roll*conv);  
   }
   
   public void setTranslation(float tx, float ty, float tz) {
      translation[0] = tx;  translation[1] = ty;  translation[2] = tz; 
   }
   
   KeyState keyState;
   float[] position = new float[3];
   float[] orientation = new float[3];
   
   //long nanoTime;
   double currentDoubleTime;
   double lastTime;
   float linVel; // linear horizontal velocity
   float vertVel; // vertical velocity
   float strafeVel; // strafing velocity
   float angVel; // angular velocity, around Y-axis
   public static final double degToRad = 0.017453292519943296;
   //float maxX, minX, maxY, minY, maxZ, minZ; // min/max coordinate positions for NavigationScene          
   private static final float conv = (float)(Math.PI/180.0);
   private float[] rotation = new float[4];    
   private float[] translation = new float[3];   
   private float[] nullvec = new float[] {0.0f, 0.0f, 0.0f};
   private float scale = 1.0f;
   private float[] transformMatrix = new float[16];
   private float[] rotationMatrix = new float[16];
   private float[] translationMatrix = new float[16];
   private VJoint viewpoint;
}
