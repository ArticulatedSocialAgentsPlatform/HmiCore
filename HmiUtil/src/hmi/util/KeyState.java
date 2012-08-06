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
 
package hmi.util;

import java.awt.*;
import java.awt.event.*;


/**
 * KeyState is a KeyListener, that keeps track of the current key states
 * So, it converts the Java event based interface for the keyboard into a state based interface. 
 * The current state of keys is available in the form of a public array "keyDown": keyDown[keycode]
 * is true as long as that key is down, i.e. pressed and not yet released. Note that several keys can
 * be down at the same time. Also, it records the state for "raw" key codes, i.e. a modifier key like
 * the SHIFT key means that keyDown[KeyEvent.VK_SHIFT] is true, but does not modify the key code for other keys. 
 * Only keycodes in the range 0 .. KEYCODES are recorded. In practice, this includes all keys on 
 * a standard keyboard, but it does not include all Unicode keys.
 * In addition to keyDown, there is an int array keyLocation, which records the keyboard location
 * of a key, while it is down. (Distinguishes between KEY_LOCATION_LEFT , KEY_LOCATION_RIGHT, KEY_LOCATION_NUMPAD etc).
 * @author Job Zwiers
 */
public class KeyState
implements KeyListener,
           FocusListener
{
   public static final int NR_OF_KEYCODES = 256;                // keycodes in the range 0..KEYCODES are handled.
   private boolean[] keyDown = new boolean[NR_OF_KEYCODES];     // records current state of all keys
   private int[] keyLocation = new int[NR_OF_KEYCODES];         // corresponding location for all pressed keys. 
                                                                // (KEY_LOCATION_LEFT , KEY_LOCATION_RIGHT, KEY_LOCATION_NUMPAD etc). 
   /** 
    * create a new KeyState.
    */
   public KeyState() {
   }

   
   /**
    * creates a KeyState that listens to Component c for KeyEvents
    */ 
   public KeyState(Component c) {
      if (c != null) {
        c.addKeyListener(this);
        c.addFocusListener(this) ;
      }
   }   

   /**
    * adds this KeyState as a KeyListener for Component c 
    */
   public void listenTo(Component c)  {
      c.addKeyListener(this);
   }


   /**
    * keyPressed handler from the KeyListener interface for this tracker. 
    * updates the keyDown and keyLocation arrays
    */
   public final void keyPressed(KeyEvent evt) { 
      //hmi.util.Console.println("KeyState.keyPressed");
      int keyCode = evt.getKeyCode();
      if (keyCode < NR_OF_KEYCODES) {   
          if ( ! keyDown[keyCode] ) {   // we are the only writer for keyDown, not need to aquire lock when no change
             synchronized(this) {    
                keyLocation[keyCode] = evt.getKeyLocation();
                keyDown[keyCode] = true;  
             }
          }
      }
   }
    
   /**
    * keyReleased handler from the KeyListener interface for this tracker. 
    * updates the keyDown and keyLocation arrays
    */
   public final  void keyReleased(KeyEvent evt) {
      int keyCode = evt.getKeyCode();
      if (keyCode < NR_OF_KEYCODES) {
         if ( keyDown[keyCode] ) {
            synchronized(this) {
               keyDown[keyCode] = false;  
            }
         }
      }      
   }

   /**
    * keyTyped handler from the KeyListener interface: keyTyped events are ignored.
    */
   public final void keyTyped(KeyEvent evt) {}


   /**
    * returns the current value of keyDown[keyCode]
    */
   public synchronized boolean  isKeyDown(int keyCode) {
      return keyDown[keyCode];
   }


   /**
    * Returns the location (on the keyboard) of the key, provided the key is down.
    * A location is one of  KeyEvent.KEY_LOCATION_STANDARD, KeyEvent.KEY_LOCATION_LEFT , KeyEvent.KEY_LOCATION_RIGHT, KeyEvent.KEY_LOCATION_NUMPAD 
    * If the key is not down, the location code returned is 0.
    */
    public int getKeyLocation(int keyCode) {
       return  keyDown[keyCode] ? keyLocation[keyCode] : 0;
    }

   /**
    * produces a descriptive String for a key location:
    * Left, Right, Numpad, S(tandard), or U(nknown).
    */
   public String locationString(int location) {
      if (location == KeyEvent.KEY_LOCATION_LEFT) {
         return "Left";
      } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
         return "Right";
      } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
         return "Numpad";
      } else if (location == KeyEvent.KEY_LOCATION_STANDARD) {
         return "S";
      } else {
         return "U";  
      } 
   }

/* ===============================
  FOCUS LISTENER -- Reset KEYSTATE on focus events
  =============================== */

   private void reset() {
      for (int i=0; i<NR_OF_KEYCODES; i++) {
         keyDown[i] = false;    
         keyLocation[i] = 0;
      }
   }
  
   public void focusGained(FocusEvent e) {
      reset();
   }
   
   public void focusLost(FocusEvent e) {
     reset();
   }
}
