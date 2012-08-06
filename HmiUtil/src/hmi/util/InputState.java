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
import java.util.*;

/**
 * An InputState tracks the current state of the keyboard and mouse, 
 * by listening to AWT InputEvents for AWT/Swing Components. 
 * The key/mouse button state (pressed or released) is tracked, and used to
 * determine which of a set of defined key patterns is currently &quot;active&quot;
 * Such a pattern is itself defined by a set of keys that must be pressed, and a set
 * of keys that are required to be released. 
 * Keys can be specified by means of virtual key codes, possibly combined with a keyboard
 * location (like LEFT, RIGHT, NUMPAD).
 * @author Job Zwiers  
 */
public class InputState implements KeyListener, MouseListener, FocusListener
{
   public static final int NR_OF_KEYCODES = 256;                // keycodes in the range 0..NR_OF_KEYCODES are handled.
                                                                // "MS-Windows keys" can have keycodes > 255, and are ignored
   private static final int NR_OF_MOUSE_BUTTONS = 3;   
   private static final int KEYDIM = NR_OF_KEYCODES + NR_OF_MOUSE_BUTTONS;       
   public static final int MB1 = NR_OF_KEYCODES; // offset in keyDown and navKey for mouse button 1
   public static final int MB2 = MB1+1;          // similar offset for mouse button 2
   public static final int MB3 = MB1+2;          // and 3
   
    // turn Java key locations, like KEY_LOCATION_NUMPAD, into bit masks that can be "or-ed" together.
   /**
    * Constants for referring to keyboard locations.
    */
   public static final int L = 1<<KeyEvent.KEY_LOCATION_LEFT;
   public static final int R = 1<<KeyEvent.KEY_LOCATION_RIGHT;
   public static final int S = 1<<KeyEvent.KEY_LOCATION_STANDARD;
   public static final int N = 1<<KeyEvent.KEY_LOCATION_NUMPAD;
   public static final int A = S|N|L|R;
   
   // navKey and keyDown are "or"-ed keyboard locations                                        
   private int[] navKey = new int[KEYDIM];      // determines which keys are actually in use, and which ones can be ignored 
   private int[] keyDown = new int[KEYDIM];     // records current state of all relevant keys 

   
   /**
    * various listener modes; they can be combined by means of the | operator 
    */
   public static final int KEYLISTENER = 1;
   public static final int MOUSELISTENER = 2;
//   public static final int MOUSEMOTIONLISTENER = 4;
//   public static final int MOUSEINPUTLISTENER = MOUSELISTENER | MOUSEMOTIONLISTENER ; // combines MOUSELISTENER and MOUSEMOTIONLISTENER
//   public static final int MOUSEWHEELLISTENER = 8;
    
   private int listenerMode = 0;
    
   // When an InputPatternState tracks MouseEvents, it stores relevant parameters for the current state of the mouse: 
   private  boolean button1Down = false;                         // current state of mouse buttons
   private  boolean button2Down = false;
   private  boolean button3Down = false;
   private  int clickCount1 = 0;                                 // current "clickcount" for mouse buttons
   private  int clickCount2 = 0;
   private  int clickCount3 = 0;
   private  int x1Pressed, y1Pressed, x1Released, y1Released;    // screen positions of last press and release events
   private  int x2Pressed, y2Pressed, x2Released, y2Released;    // for  mouse buttons. 
   private  int x3Pressed, y3Pressed, x3Released, y3Released;
   
//   private  int xDragged, yDragged, xMoved, yMoved;              // positions of last mouseDragged and mouseMoved events.
   private  int x, y;                                            // position of last mouse  press, release, drag, or move
//   private  int xDelta, yDelta;                                  // delta's for last mouse drag or move
//   
//   private  int wheelRotation = 0;               // number of "clicks" that the mouse wheel was rotated in the last mouse wheel event.
//   private  int accumulatedWheelRotation = 0;    // accumulated wheelrotation


   public boolean isButton1Down() { return button1Down; }
   public boolean isButton2Down() { return button2Down; }
   public boolean isButton3Down() { return button3Down; }
   public int getClickCount1() { return clickCount1; }
   public int getClickCount2() { return clickCount2; }
   public int getClickCount3() { return clickCount3; }
   public int getX1pressed() { return x1Pressed; }
   public int getX2pressed() { return x2Pressed; }
   public int getX3pressed() { return x3Pressed; }
   public int getY1pressed() { return y1Pressed; }
   public int getY2pressed() { return y2Pressed; }
   public int getY3pressed() { return y3Pressed; }
   
   public int getX1released() { return x1Released; }
   public int getX2released() { return x2Released; }
   public int getX3released() { return x3Released; }
   public int getY1released() { return y1Released; }
   public int getY2released() { return y2Released; }
   public int getY3released() { return y3Released; }
   
   private ArrayList<Pattern> patterns = new ArrayList<Pattern>();

   /** 
    * Creates a new InputState, not yet listening to any Component.
    */
   public InputState() {           
   }
   
   /**
    * Create a new InputState and adds it as a KeyListener to the specified Component.
    */
   public InputState(Component c) {
      listenerMode = KEYLISTENER;
      listenTo(c);
   }   


   public InputState(Component c, int listenerMode) {
      this.listenerMode = listenerMode;
      listenTo(c);
   }

   /**
    * adds listeners to Component c for all enabled events, according to the listenerMode
    */
   public void listenTo(Component c)  {
      if ( (listenerMode & KEYLISTENER) != 0)         c.addKeyListener(this);
      if ( (listenerMode & MOUSELISTENER) != 0)       c.addMouseListener(this);
      //if ( (listenerMode & MOUSEMOTIONLISTENER) != 0) c.addMouseMotionListener(this);
      //if ( (listenerMode & MOUSEWHEELLISTENER) != 0)  c.addMouseWheelListener(this);
   }
  

   /**
    * removes the listeners from Component c
    */
   public void detachFrom(Component c)  {
      if ( (listenerMode & KEYLISTENER) != 0)         c.removeKeyListener(this);
      if ( (listenerMode & MOUSELISTENER) != 0)       c.removeMouseListener(this);
      //if ( (listenerMode & MOUSEMOTIONLISTENER) != 0) c.removeMouseMotionListener(this);
      //if ( (listenerMode & MOUSEWHEELLISTENER) != 0)  c.removeMouseWheelListener(this);
   }

   
   /**
    * Adds a key pattern, consisting of keys required to be down,
    * and keys required to be up. Key locations are not specified.
    * Each of down and up can be null.
    */
   public Pattern addPattern(int[] down, int[] up) {
      Key[] downKeys = null;
      Key[]upKeys = null;
      if (down != null) {
          downKeys = new Key[down.length];
          for (int k=0; k<down.length; k++) downKeys[k] = new Key(down[k], A);
      }
      if (up != null) {
          upKeys = new Key[up.length];
          for (int k=0; k<up.length; k++) upKeys[k] = new Key(up[k], A);
      }      
      return addPattern(downKeys, upKeys);
   }

   /**
    * Adds a key pattern, consisting of keys required to be down,
    * and keys required to be up. Keys are specified by means
    * of InputState.Key elements, defining both a virtual key code
    * as well as a keyboard location (-mask). 
    */
   public Pattern addPattern(Key[] down, Key[] up) {
      Pattern newpat = new KeyPat(down, up);
      patterns.add(newpat);
      return newpat;
   }

   /**
    * Adds a key pattern, consisting of keys required to be down.
    */
   public Pattern addPattern(Key[] down) {
      Pattern newpat = new KeyPat(down, null);
      patterns.add(newpat);
      return newpat;
   }

   /**
    * Adds a key pattern, defined by a single key which must
    * be down for activation. The Key specifies both a virtual key code
    * and a keyboar 
    */
   public Pattern addPattern(Key key) {
      Pattern newpat = new KeyPat(new Key[]{key}, null);
      patterns.add(newpat);
      return newpat;
   }

   /**
    * Create a new key patterns from a given list of patterns.,
    * It is active iff at least one of the patterns from the list is active.
    */
   public Pattern orPattern(final Pattern[] pats) {
       Pattern result = new Pattern() {
           public boolean isActive() {
               for (int p=0; p<pats.length; p++) {
                  if (pats[p].isActive()) return true;
               }
               return false;
           }
           public void checkState() {};
         
       };
       return result;  
   }

   /**
    * The interface for key patterns: A pattern can be currently
    * active or not, which can be checked by calling the isActive method.
    * Internally, InputState calls checkState when the active state of a 
    * pattern might have changed, so a Pattern should inspect and update its state
    * acordingly.
    * 
    */
   public interface Pattern {
      boolean isActive();
      void checkState();
   }
   
   /**
    * A InputState.Key defines a virtual key code and a key location.
    * The virtual key code is just one of the java.awt.event.KeyEvent
    * virtual key codes, like &quot;KeyEvent.VK_UP&quot; for the &quot;up&quot; cursor key.
    * The location is a InputState defined mask, obtained by &quot;or-in&quot;
    * some of the constants S, N, L, R. (The A constant is already defined as S|N|L|R)
    */
   public static class Key {
       private int vkCode;
       private int locationMask;
       public Key(int vkCode, int locationMask) {
          this.vkCode = vkCode;
          this.locationMask = locationMask;          
       }     
       public int getVkCode() { return vkCode; }
       public int getLocationMask() { return locationMask; }
          
   }
   
   /**
    * Creates a InputState.Key consisting of virtual key code and keylocation.
    * The latter can be one of S (standrad), N (numpad), L (left),
    * R (right), or an &quot;or-ed&quot; combination of these.
    */
   public static Key key(int vkCode, int locationMask) {
       return new Key(vkCode, locationMask);  
   }
   
   /**
    * Creates a InputState.Key, defined by a virtual key code.
    * The location mask is &quot;A&quot;
    */
   public static Key key(int vkCode) {
       return new Key(vkCode, A);  
   }
   
   /**
    * Creates a InputState.Key, defined by a virtual key code.
    * The location mask is &quot;N&quot;, denoting a numpad key location.
    */
   public static Key numpad(int vkCode) {
       return new Key(vkCode, N);  
   }
   
   
   /**
    * Creates a InputState.Key, defined by a virtual key code.
    * The location mask is &quot;L&quot;, denoting a &quot;left&quot; key location.
    */
   public static Key left(int vkCode) {
       return new Key(vkCode, L);  
   }
   
    /**
    * Creates a InputState.Key, defined by a virtual key code.
    * The location mask is &quot;R&quot;, denoting a &quot;right&quot; key location.
    */
   public static Key right(int vkCode) {
       return new Key(vkCode, R);  
   }
   
   /**
    * Creates a InputState.Key, defined by a virtual key code.
    * The location mask is &quot;S&quot;, denoting a &quot;standard&quot; key location.
    */
   public static Key standard(int vkCode) {
       return new Key(vkCode, S);  
   }
   
   /**
    * standard implementation of InputState.Pattern.
    * A KeyPad Pattern is defined by a list of Keys that are required to be down,
    * and a list of keys required tyo be up. (Each list can be null).
    */
   public final class KeyPat implements Pattern {
      private int[] up;
      private int[] down;
      private int[] upLocation;
      private int[] downLocation;
      private volatile boolean active;
           
      /**
       * Crate a new KeyPat Pattern, for specified lists of
       ( down and up keys. Each of downKeys and upKeys can be null.
       */          
      public KeyPat(Key[] downKeys, Key[] upKeys) {     
         if (downKeys != null) {
             down = new int[downKeys.length];
             downLocation = new int[downKeys.length];
             for( int k=0; k<downKeys.length; k++) {
                down[k] = downKeys[k].getVkCode();
                downLocation[k] = downKeys[k].getLocationMask(); 
                navKey[down[k]] |= downLocation[k];
             }
         }
         if (upKeys != null) {
             up = new int[upKeys.length];
             upLocation = new int[upKeys.length];
             for( int k=0; k<upKeys.length; k++) {
                up[k] = upKeys[k].getVkCode();
                upLocation[k] = upKeys[k].getLocationMask(); 
                navKey[up[k]] |= upLocation[k];
             }
         }
      }
      /** returns whether this Pattern is currently active */
      public boolean isActive() { return active; }

      /* 
       *Inspects the InputState keydown array, and updates the active state
       */
      public void checkState() {
         if (down != null) {
            for (int k=0; k < down.length; k++) {
               if ((keyDown[down[k]] & downLocation[k]) == 0) {
                  active = false;
                  return;
               }              
            }  
         } 
         if (up != null) {               
            for (int k=0; k < up.length; k++) {
               if ((keyDown[up[k]] & upLocation[k]) != 0) {
                  active = false;
                  return;           
               }
            }
         }
         active = true;
      }
   }   
  
 
   /*
    * classifies the current state, by determining,
    * for all defined patterns, which patterns are currently "active",
    * as defined by their patternm keys. (A pattern is active if all its
    * "down" keys are pressed and not yet released, and all its "up" keys are currently "released")
    * The pattern ids for all active patterns are "or-ed" together, and returned.
    * todo: could be slightly more efficient by checking only patterns that might have changed state.
    */
   private synchronized void classify() {
      for (Pattern pat : patterns) {
          pat.checkState();
      }
   }
   
  

  

 
   /**
    * keyPressed handler from the KeyListener interface for this tracker. 
    * updates the keyDown array
    */
   public final void keyPressed(KeyEvent evt) {
      //hmi.util.Console.println("InputState.keyPressed: " + evt.getKeyCode());
      int keyCode = evt.getKeyCode();
      int keyloc = 1<<evt.getKeyLocation();
      if ( keyCode >= NR_OF_KEYCODES || ((navKey[keyCode] & keyloc) == 0) ) {
         return;
      }              
      if ((keyDown[keyCode] & keyloc) == 0) {    
         keyDown[keyCode] |= keyloc;   
         classify();
      }
   }
    

   /**
    * keyReleased handler from the KeyListener interface for this tracker. 
    * updates the keyDown  array
    */
   public final  void keyReleased(KeyEvent evt) {
      //hmi.util.Console.println("InputState.keyReleased: " + evt.getKeyCode());
      int keyCode = evt.getKeyCode();
      int keyloc = 1<<evt.getKeyLocation();
      if ( keyCode >= NR_OF_KEYCODES || ((navKey[keyCode] & keyloc) == 0) ) {
         return;
      }
      keyDown[keyCode] &= ~keyloc;
      classify();
   }

   /**
    * mouseClicked handler from the MouseListener interface for this tracker: ignored
    */
   public final void mouseClicked(MouseEvent e) {
   }

   /**
    * mouseEntered handler from the MouseListener interface for this tracker: ignored
    */
   public final void mouseEntered(MouseEvent e) {
   }

   /**
    * mouseExited handler from the MouseListener interface for this tracker: ignored
    */
   public final void mouseExited(MouseEvent e) {     
   }


   /**
    * mousePressed handler from the MouseListener interface for this tracker. 
    * updates the buttonDown[123] states, the [xy][123]Pressed positions,
    * and the clickCount[123] counters
    */
   public final void mousePressed(MouseEvent e) {  
      x = e.getX();
      y = e.getY();
      int button = e.getButton();
      if (button == MouseEvent.BUTTON1) {                  
         button1Down = true;
         clickCount1 = e.getClickCount();
         keyDown[MB1] = S;         
         x1Pressed = x;
         y1Pressed = y;
      } else if (button == MouseEvent.BUTTON2) {
         button2Down = true;
         clickCount2 = e.getClickCount();
         keyDown[MB2] = S;
         x2Pressed = x;
         y2Pressed = y;
      } else if (button == MouseEvent.BUTTON3) {
         button3Down = true;
         clickCount3 = e.getClickCount();
         keyDown[MB3] = S;
         x3Pressed = x;
         y3Pressed = y;
      } else {
          Console.println("Unknown mouse button number: " + button);  
      }      
      classify();
   }

   /**
    * mouseReleased handler from the MouseListener interface for this tracker. 
    * updates the buttonDown[123] states, the [xy][123]Released positions,
    * and the clickCount[123] counters
    */
   public final void mouseReleased(MouseEvent e) {
      x = e.getX();
      y = e.getY();
      int button = e.getButton();
      if (button == MouseEvent.BUTTON1) {
         button1Down = false;
         x1Released = x;
         y1Released = y;
         clickCount1 = e.getClickCount();
         keyDown[MB1] = 0;
         //Console.println("Mouse released, clickcount= " + clickCount1);
      } else if (button == MouseEvent.BUTTON2) {
         button2Down = false;
         x2Released = x;
         y2Released = y;
         clickCount2 = e.getClickCount();
         keyDown[MB2] = 0;
      } else if (button == MouseEvent.BUTTON3) {
         button3Down = false;
         x3Released = x;
         y3Released = y;
         clickCount2 = e.getClickCount();
         keyDown[MB3] = 0;
      } else {
          Console.println("Unknown mouse button number: " + button);  
      }
      classify();
   }

   /**
    * mouseDragged handler from the MouseMotionListener interface for this tracker. 
    * updates the xDragged and yDragged position.
    */
   public final void mouseDragged(MouseEvent e) {
//      //parlevink.util.Console.println("mouse dragged");
//      xDragged = e.getX();
//      yDragged = e.getY();
//      xDelta = xDragged - x;
//      yDelta = yDragged - y;      
//      x = xDragged;
//      y = yDragged;
//      if (dragListener != null) {
//         dragListener.mouseDragged(xDelta, yDelta);  
//      } else if (dragListeners != null) {
//         for (MouseDragListener mdl : dragListeners) {
//            mdl.mouseDragged(xDelta, yDelta); 
//         }         
//      }
//      //showMotion();    
   }

   /**
    * mouseMoved handler from the MouseMotionListener interface for this tracker. 
    * updates the xMoved and yMoved position.
    */
   public final void mouseMoved(MouseEvent e) {
//      xMoved = e.getX();
//      yMoved = e.getY();
//      xDelta = xMoved - x;
//      yDelta = yMoved - y;      
//      x = xMoved;
//      y = yMoved;
//      if (moveListener != null) {
//         moveListener.mouseMoved(xDelta, yDelta);  
//      } else if (moveListeners != null) {
//         for (MouseMoveListener mml : moveListeners) {
//            mml.mouseMoved(xDelta, yDelta);  
//         }         
//      }
//      //showMotion();    
   }

   /**
    * mouseDragged handler from the MouseWheelListener interface for this tracker. 
    * updates the wheelRotation and accumulatedWheelRotation counters.
    */
   public final void mouseWheelMoved(MouseWheelEvent e) {
//      //parlevink.util.Console.println("wheel");
//      wheelRotation = e.getWheelRotation();
//      accumulatedWheelRotation += wheelRotation;
//      //showState();      
   }


   /**
    * keyTyped handler required by the KeyListener interface. ketTyped calls are simply ignored.
    * (keyTyped really means keyPressed followed by keyReleased, and the latter two calls are handled)
    */
   public final void keyTyped(KeyEvent evt) {}


   /**
    * produces a descriptive String for a key location:
    * Left, Right, Numpad, S(tandard), or U(nknown).
    */
   public String locationString(int location) {
      if (location == L) {
         return "Left";
      } else if (location == R) {
         return "Right";
      } else if (location == N) {
         return "Numpad";
      } else if (location == S) {
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
         keyDown[i] = 0;    
      }
   }
  
   public void focusGained(FocusEvent e) {
      hmi.util.Console.println("focus gained");
      reset();
   }
   
   public void focusLost(FocusEvent e) {
      hmi.util.Console.println("focus lost");
     reset();
   }


}
