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
