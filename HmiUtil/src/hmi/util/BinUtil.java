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

package hmi.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.*;

/**
 * Some utils for reading/writing BinaryExternalizable files
 * @author Job Zwiers
 */
public class BinUtil  {
   private BinUtil(){}
   private static Logger logger = LoggerFactory.getLogger("hmi.util.BinUtil");
   

  
 
 
   /**
    * Writes the length of the String array &quot;data&quot; followed by its elements
    * to dataOut. A null array is allowed, and writes only a length value equal to -1
    */
   public static void writeStringArray(DataOutput dataOut, String[] data) throws IOException {
      if (data == null) {
          dataOut.writeInt(-1);
      } else {
          dataOut.writeInt(data.length); 
          for (int i=0; i< data.length; i++) dataOut.writeUTF(data[i]); 
      }
   }
   

   /**
    * Complement of &quot;writeStringArray&quot;: reads back a binary encoding of a String array,
    * written by &quot;writeStringArray&quot; Could return a null array value.
    */
   public static String[] readStringArray(DataInput dataIn) throws IOException  {
      String[] data = null;
      int size = dataIn.readInt();
      if (size >= 0) {
         data = new String[size];
         for (int i=0; i<size; i++) data[i] = dataIn.readUTF();
      }
      return data;
   }
 
 
   /**
    * Writes the length of the int array &quot;data&quot; followed by its elements
    * to dataOut. A null array is allowed, and writes only a length value equal to -1
    */
   public static void writeIntArray(DataOutput dataOut, int[] data) throws IOException {
      if (data == null) {
          dataOut.writeInt(-1);
      } else {
          dataOut.writeInt(data.length); 
          for (int i=0; i< data.length; i++) dataOut.writeInt(data[i]); 
      }
   }
   

   /**
    * Complement of &quot;writeIntArray&quot;: reads back a binary encoding of an int array,
    * written by &quot;writeIntArray&quot; Could return a null array value.
    */
   public static int[] readIntArray(DataInput dataIn) throws IOException  {
      int[] data = null;
      int size = dataIn.readInt();
      if (size >= 0) {
         data = new int[size];
         for (int i=0; i<size; i++) data[i] = dataIn.readInt();
      }
      return data;
   }
 
 
    /**
    * Writes the length of the float array &quot;data&quot; followed by its elements
    * to dataOut. A null array is allowed, and writes only a length value equal to -1
    */
   public static void writeFloatArray(DataOutput dataOut, float[] data) throws IOException {
      if (data == null) {
          dataOut.writeInt(-1);
      } else {
          dataOut.writeInt(data.length); 
          for (int i=0; i< data.length; i++) dataOut.writeFloat(data[i]); 
      }
   }
   

   /**
    * Complement of &quot;writeFloatArray&quot;: reads back a binary encoding of a float array,
    * written by &quot;writeFloatArray&quot; Could return a null array value.
    */
   public static float[] readFloatArray(DataInput dataIn) throws IOException  {
      float[] data = null;
      int size = dataIn.readInt();
      if (size >= 0) {
         data = new float[size];
         for (int i=0; i<size; i++) data[i] = dataIn.readFloat();
      }
      return data;
   }
   
      /**
    * Writes the length of the BinaryExternalizable List &quot;data&quot; followed by its elements
    * to dataOut. A null List is allowed, and writes only a length value equal to -1
    */
   public static void writeBinaryList(DataOutput dataOut, List<? extends BinaryExternalizable> dataList) throws IOException {
      if (dataList == null) {
          dataOut.writeInt(-1);
      } else {
          dataOut.writeInt(dataList.size()); 
          for (BinaryExternalizable elem : dataList) elem.writeBinary(dataOut);
      }
   }
   
   
   /**
    * Complement of &quot;writeBinaryList&quot;: reads back a binary encoding of an List,
    * written by &quot;writeBinaryList&quot; Could return a null List.
    */
   public static <T extends BinaryExternalizable>  ArrayList<T> readBinaryList(DataInput dataIn, Class<T> elemClass) throws IOException  {
      int size = dataIn.readInt();
      if (size < 0) return null;
      ArrayList<T> result = new ArrayList<T>(size);
      try {
         for (int i=0; i<size; i++) {
            T elem = elemClass.newInstance();
            elem.readBinary(dataIn);
            result.add(elem);
         }
      } catch (Exception e) {
         logger.error("BinUtil.readBinaryList: " + e);
      }
      return result;
   }
   
    
   
   /**
    * Writes some BinaryExternalizable object to dataOut preceded by a +1 int, or just a -1 int, whene the object == null
    */
   public static void writeOptionalBinary(DataOutput dataOut, BinaryExternalizable bobj) throws IOException  {
      if (bobj == null) {
         dataOut.writeInt(-1);
      } else {
         dataOut.writeInt(+1);
         bobj.writeBinary(dataOut);
      }
   }
    
   /**
    * Reads some BinaryExternalizable object to dataOut preceded by a -1/+1 int, where -1 denotes a null value.
    */
   public static <T extends BinaryExternalizable> T readOptionalBinary(DataInput dataIn, Class<T> elemClass) throws IOException  {
      int hasData = dataIn.readInt();
      if (hasData < 0) return null;
      try {
         T result = elemClass.newInstance();
         result.readBinary(dataIn);
         return result;
      } catch (Exception e) {
         logger.error("BinUtil.readOptionalBinary: " + e);
         return null;
      }
   }


   /**
     * Writes some UTF String to dataOut preceded by a +1 int, or just a -1 int, when the String is null
     */
    public static void writeOptionalString(DataOutput dataOut, String str) throws IOException  {
       if (str == null) {
          dataOut.writeInt(-1);
       } else {
          dataOut.writeInt(+1);
          dataOut.writeUTF(str);
       }
    }
    
    /**
     * Reads some optional UTF String from dataIn, within dataIn preceded by a +1 int, or just a -1 int, when the String is null
     * If a non-null String is read, the interned String is returned
     */
    public static String readOptionalString(DataInput dataIn) throws IOException  {
       int hasData = dataIn.readInt();
       if (hasData < 0) {
          return null;
       } else {
          return dataIn.readUTF().intern();
       }
    }
                      
}
