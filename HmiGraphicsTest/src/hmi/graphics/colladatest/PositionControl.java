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
/* 
 */

package hmi.graphics.colladatest;

import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;
import hmi.util.ClockListener;
import hmi.math.*;
import hmi.animation.VJoint;


/**
 * A simple form of keyboard based PositionControl module.
 * Keys: A and D move left and right, W and S move back and forth, 
 * Left and right &quot;arrow&quot; keys rotate,  Up and down &quot;arrow&quot;
 * keys move back and forth. PageUp and PageDown move up and down.
 */
public class PositionControl implements ClockListener {

   /**
    * Creates a new PositionControl, listening to Component c for key and mouse events
    */
   public PositionControl(Component c) {
      keyState = new KeyState(c); 
        //currentDoubleTime = (double) currentTime/1000;
      linVel = 1.0f;
      vertVel = 1.0f;
      strafeVel = 1.0f;
      angVel = 10.0f;
  
    
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


   /**
    * The callback method that will be called by the clock, initializing time.
    */
   public synchronized void initTime(double t) { 
      currentDoubleTime = t;
      lastTime = currentDoubleTime;
   }

   public void time(double currentTime) {   
      currentDoubleTime = currentTime;  
      //hmi.util.Console.println("PositionControl.time: " + currentDoubleTime);
   }

   /**
    * callback function that must be called regularly, in essence
    * every time a new frame is rendered.
    * it updates the current PositionControl position and orientation
    */
   public void glRender(GLRenderContext glc) {
      float linearVelocity = 0.0f;
      float vertVelocity = 0.0f;
      float strafeVelocity = 0.0f;
      float angularVelocityX = 0.0f;
      float angularVelocityY = 0.0f;
      float angularVelocityZ = 0.0f;
    
      float delta; 
            
      // Keyboard Input
     
      synchronized(keyState) {
         
         // horizontal translation
         
         if (keyState.keyDown[KeyEvent.VK_W] &&  keyState.keyDown[KeyEvent.VK_CONTROL])  
            linearVelocity = -3.0f * linVel; 
         
         if (keyState.keyDown[KeyEvent.VK_S] &&  keyState.keyDown[KeyEvent.VK_CONTROL]) 
             linearVelocity = 3.0f * linVel;
              
         if (keyState.keyDown[KeyEvent.VK_A] &&  keyState.keyDown[KeyEvent.VK_CONTROL]) 
             strafeVelocity = -3.0f * strafeVel;
         
          if (keyState.keyDown[KeyEvent.VK_D] &&  keyState.keyDown[KeyEvent.VK_CONTROL])       
             strafeVelocity = 3.0f * strafeVel;
               
                 
         // vertical translation
         if (keyState.keyDown[KeyEvent.VK_PAGE_UP] &&  keyState.keyDown[KeyEvent.VK_CONTROL])       
            // angularVelocityX = 0.2f * angVel;
            vertVelocity = vertVel;
               
         if (keyState.keyDown[KeyEvent.VK_PAGE_DOWN]&&  keyState.keyDown[KeyEvent.VK_CONTROL])       
            // angularVelocityX = -0.2f * angVel;
            vertVelocity = -vertVel;
            
            
         // Check for spin left/right (Y Axis rotation)
         if( (keyState.keyDown[KeyEvent.VK_LEFT]  &&  keyState.keyDown[KeyEvent.VK_CONTROL]) )
             angularVelocityY = -angVel;
         
         if( (keyState.keyDown[KeyEvent.VK_RIGHT]  &&  keyState.keyDown[KeyEvent.VK_CONTROL]) )
             angularVelocityY = angVel;
             
         if (keyState.keyDown[KeyEvent.VK_UP] &&  keyState.keyDown[KeyEvent.VK_CONTROL]) 
             angularVelocityX = angVel;
         
          if (keyState.keyDown[KeyEvent.VK_DOWN] &&  keyState.keyDown[KeyEvent.VK_CONTROL]) 
             angularVelocityX = -angVel;
                
      }
      
      
      delta = (float) (currentDoubleTime-lastTime);
      lastTime = currentDoubleTime;
      
      
      if (linearVelocity != 0.0f || vertVelocity != 0.0f || strafeVelocity != 0.0f) {     
         translationDelta[2] = delta * linearVelocity;  // move in Z direction
         translationDelta[1] = delta * vertVelocity;    // Up/Down in Y direction
         translationDelta[0] = delta * strafeVelocity;  // strafe in X direction
        // hmi.util.Console.println("translation" + Vec3f.toString(translationDelta));
         vjoint.translate(translationDelta);
      }


      if (angularVelocityX != 0.0f || angularVelocityY != 0.0f || angularVelocityZ != 0.0f) {
         float roll  = delta * angularVelocityZ;
         float pitch = delta * angularVelocityX;
         float yaw   = delta * angularVelocityY;
         
         //hmi.util.Console.println("roll, pitch, yaw, angularVelocityY = " + roll + ", " + pitch + ", " + yaw);
         Quat4f.setFromRollPitchYaw(rotationDelta, roll, pitch, yaw);
         
         //hmi.util.Console.println("rotationDelta = " + Quat4f.toString(rotationDelta));
         vjoint.rotate(rotationDelta);  
      }

       
   } 
   
   
   
   public void setVJoint(VJoint vjoint) {
      this.vjoint = vjoint;
   }
   
   KeyState keyState;
   
   private VJoint vjoint;
   
   float[] position = new float[3];
   float[] orientation = new float[3];
   
   double currentDoubleTime;
   double lastTime;
   float linVel; // linear horizontal velocity
   float vertVel; // vertical velocity
   float strafeVel; // strafing velocity
   float angVel; // angular velocity, around Y-axis
      
  // private static final float conv = (float)(Math.PI/180.0);
//   private float[] rotation = new float[4];    
//   private float[] translation = new float[3];    
   private float[] translationDelta = new float[3]; // incremental translation
   private float[] rotationDelta = new float[4];    // incremental rotation quaternion
   
}
