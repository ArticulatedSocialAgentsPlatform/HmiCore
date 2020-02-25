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
package hmi.graphics.util;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Some utils for allocating direct NIO buffers.
 * @author Job Zwiers
 */
public final class BufferUtil {

   public static final int SIZEOF_BYTE = 1;
   public static final int SIZEOF_SHORT = 2;
   public static final int SIZEOF_INT = 4;
   public static final int SIZEOF_FLOAT = 4;
   public static final int SIZEOF_LONG = 8;
   public static final int SIZEOF_DOUBLE = 8;

   /***/
   private BufferUtil() {}

   /**
    * Allocates a new direct FloatBuffer with the specified number of elements. 
    * The returned buffer will have its byte order set to the host platform's native byte order. 
    */
   public static FloatBuffer directFloatBuffer(int numElements) {
      ByteBuffer bb = ByteBuffer.allocateDirect(numElements * SIZEOF_FLOAT);
      bb.order(ByteOrder.nativeOrder());  
      return bb.asFloatBuffer();
   }

   /**
    * Allocates a new direct FloatBuffer with the specified number of elements. 
    * The returned buffer will have its byte order set to the host platform's native byte order. 
    */
   public static FloatBuffer directFloatBuffer(float[] a) {
      ByteBuffer bb = ByteBuffer.allocateDirect(a.length * SIZEOF_FLOAT);
      bb.order(ByteOrder.nativeOrder());  
      FloatBuffer fb = bb.asFloatBuffer();
      fb.put(a);
      fb.rewind();
      return fb;
   }


   /** 
    * Allocates a new direct IntBuffer with the specified number of elements. 
    * The returned buffer will have its byte order set to the host platform's native byte order. 
    */
   public static IntBuffer directIntBuffer(int numElements) {
      ByteBuffer bb = ByteBuffer.allocateDirect(numElements * SIZEOF_INT);
      bb.order(ByteOrder.nativeOrder());  
      return bb.asIntBuffer();
   }

   /** 
    * Allocates a new direct IntBuffer with the specified number of elements. 
    * The returned buffer will have its byte order set to the host platform's native byte order. 
    */
   public static IntBuffer directIntBuffer(int [] a) {
      ByteBuffer bb = ByteBuffer.allocateDirect(a.length * SIZEOF_INT);
      bb.order(ByteOrder.nativeOrder());  
      IntBuffer ib = bb.asIntBuffer();
      ib.put(a);
      ib.rewind();
      return ib;
   }


   /** 
    * Allocates a new direct ShortBuffer with the specified number of elements. 
    * The returned buffer will have its byte order set to the host platform's native byte order. 
    */
   public static ShortBuffer directShortBuffer(int numElements) {
      ByteBuffer bb = ByteBuffer.allocateDirect(numElements * SIZEOF_SHORT);
      bb.order(ByteOrder.nativeOrder());  
      return bb.asShortBuffer();
   }
   
   /** 
    * Allocates a new direct ByteBuffer with the specified number of elements. 
    */
   public static ByteBuffer directByteBuffer(int numElements) {
      ByteBuffer bb = ByteBuffer.allocateDirect(numElements);
      return bb;
   }

   /**
    * Converts an int array into a direct IntBuffer.
    */
   public static IntBuffer intBuffer(int[] a) {
      IntBuffer buf = directIntBuffer(a.length);
      buf.put(a);
      buf.rewind();
      return buf;  
   }

   /**
    * Converts a float array into a direct FloatBuffer.
    */
   public static FloatBuffer floatBuffer(float[] a) {
      FloatBuffer buf = directFloatBuffer(a.length);
      buf.put(a);
      buf.rewind();
      return buf;  
   }

   /**
    * Converts an short array into a direct ShortBuffer.
    */
   public static ShortBuffer shortBuffer(short[] a) {
      ShortBuffer buf = directShortBuffer(a.length);
      buf.put(a);
      buf.rewind();
      return buf;  
   }
   
   /**
    * Converts an byte array into a direct ByteBuffer.
    */
   public static ByteBuffer byteBuffer(byte[] a) {
      ByteBuffer buf = directByteBuffer(a.length);
      buf.put(a);
      buf.rewind();
      return buf;  
   }

   /**
    * Converts a String into a direct ByteBuffer.
    */
   public static ByteBuffer byteBuffer(String s) {
      ByteBuffer buf = directByteBuffer(s.length());
      buf.put(s.getBytes());
      buf.rewind();
      return buf;  
   }
   
   /**
    * Converts a String into a direct ByteBuffer, after adding a null character at the end of the String
    */
   public static ByteBuffer stringToNullTerminatedByteBuffer(String s) {
      int len = s.length() + 1;
      ByteBuffer buf = directByteBuffer(len);
      buf.put(s.getBytes());
      buf.put( (byte)0 );
      buf.flip();
      return buf;
   }
   
   /* Herwin: disabled, never used locally
   private ByteBuffer toByteBuffer(String str, boolean nullTerminate) {
      int len = str.length();
      if (nullTerminate)
         len++;
      ByteBuffer buff = directByteBuffer(len);
      buff.put( str.getBytes() );
 
      if (nullTerminate)
         buff.put( (byte)0 );
 
      buff.flip();
      return buff;
   }
   */
   
   
   /**
    * Converts the first len bytes from ByteBuffer buf into a String.
    */
   public static String toString(ByteBuffer buf, int len) {
      byte[] bytes = new byte[len];
      buf.get(bytes);
      return new String(bytes);  
   }
   
   /**
    * Converts the bytes upto the current Buffer position from ByteBuffer buf into a String.
    
    */
   public static String toString(ByteBuffer buf) {
      byte[] bytes = new byte[buf.limit()];
      buf.get(bytes);
      return new String(bytes);  
   }
   
}
