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
package hmi.animation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XML representation of a Skeleton.
 * Captures only sid, parent sid, and translation/position relative to the root.
 * @author Job Zwiers
 *
 <skeleton id="basic_skeleton">
   <joints>
      HumanoidRoot vl5 vt6 vt1 skullbase l_hip l_knee l_subtalar l_midtarsal ...
   </joints>
   <bones jointCount=12 encoding="T"> <!-- name parentname x y z coordinates for relative position of joint centre of rotation within skeleton-->
      HumanoidRoot Null           0.0   0.0  0.0     
      vl5          HumanoidRoot   0.0  -0.2  0.0
      vt10         vl5            0.0  -0.7  0.0
      vt6          vt10           0.0  -0.3  0.0
      vt1          vt6              ......
      vc4          vt1
      skullbase    vc4
      l_hip        HumanoidRoot
      l_knee       l_hip
      l_ankle      l_knee
      l_subtalar   l_ankle
      l_midtarsal  l_subtalar
      ...
   </bones>
</skeleton>
 */
public class XMLSkeleton extends XMLStructureAdapter
{
   private String id;          // id of the Skeleton as a whole. null or an interned String
   
   private List<String> jointSids = null; 
   private List<VJoint> roots = new ArrayList<>(); // The skeleton roots. Typically of length one. 
   
   // XML related:
   private int jointCount = -1; // value < 0 denotes an  unspecified value.
   private boolean encodeTranslation = true; // default is to encode only translations
   private boolean encodeRotation = false;
   private boolean encodeScale = false;
   private String  encoding = ""; // possible strings: T, TR TRS an empty String or null is interpreted as "T"
    
   
   private static Logger logger = LoggerFactory.getLogger("hmi.animation.Skeleton");

   /* prevent Skeletons without id */
   private XMLSkeleton() {}
   
  
   
   /**
    * Create a new Skeleton with specified Skeleton id.
    */
   public XMLSkeleton(String id) {
      setId(id);
   }
   
   /**
    * Read a Skeleton from an XMLTokenizer. 
    */
   public XMLSkeleton(XMLTokenizer tokenizer) throws IOException {
      this();
      readXML(tokenizer);
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
   public void setJointSids(List<String> sids) {
       this.jointSids = sids;
   }
   
   
   /**
    * returns the List of joints sids.
    */
   public List<String> getJointSids() {
       return jointSids;
   }
   
   /**
    * returns the List of root VJoints. 
    * Typically a one element list, with just a single root.
    */
   public List<VJoint> getRoots() {
       return roots;
   }
   
   
  
   
   /**
    * Sets the List of roots of the VJoint tree(s).
    */
   public void setRoots(List<VJoint> roots) {
       this.roots = roots;
   }
   
   /*********************************************************
   /* XML SECTION
   /********************************************************
  
   /**
    * Sets the type of XML encoding: T, TR, or TRS
    * denoting Translation and optionally Rotation and Scale
    * The default is that only Translations are encoded/decoded
    */
   public void setEncoding(String encoding) 
   {
      this.encoding = encoding;
      if (encoding == null || encoding.equals("") || encoding.equals("T"))
      {
         encodeTranslation = true;  
         encodeRotation = false;
         encodeScale = false;
      }
      else if (encoding.equals("TR"))
      {
         encodeTranslation = true;  
         encodeRotation = true;
         encodeScale = false;
      } 
      else if (encoding.equals("TRS"))
      {
         encodeTranslation = true;  
         encodeRotation = true;
         encodeScale = true;
      } 
      else
      {
         System.out.println("Skeleton.setEncoding, unknown/unsupported type: " + encoding);      
      }     
   }
   
   /**
    * Returns the String that denotes the encoding type: T, TR or TRS
    */
   public String getEncoding()
   {
      return encoding;
   }
  
  
   /**
    * Static flag, that denotes whether jointCount attribute will be included or not for writing XML.
    * (The jointCount attribute is always recognized when reading XML)
    */
   public static boolean HAS_JOINTCOUNT_ATTRIBUTE = true;
   
   
   /**
    *  Appends the XML attributes
    */
   @Override
   public StringBuilder appendAttributeString(StringBuilder buf) {     
      appendAttribute(buf, "id", id);
      jointCount = (jointSids==null) ? 0 : jointSids.size();
      if (HAS_JOINTCOUNT_ATTRIBUTE && jointCount > 0) {
           appendAttribute(buf, "jointCount", jointCount);
       }
      if ( ! (encoding == null || encoding.equals(""))) {
           appendAttribute(buf, "encoding", encoding);
       } // only append when explicitly set
      return buf;
   }


    /**
     * Decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
        setId(getOptionalAttribute("id", attrMap, null));
        jointCount = getOptionalIntAttribute("jointCount", attrMap, -1);
        encoding = getOptionalAttribute("encoding", attrMap, ""); // default is "T", reperseneted here by ""
        setEncoding(encoding);
        super.decodeAttributes(attrMap, tokenizer);
    }

   public static final int NR_JOINTS_PER_LINE = 30;
    
    /**
     * Append the XML content part
     */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
        int tab = fmt.getTab();
        if (jointSids == null || jointSids.isEmpty()) {
            appendEmptyTag(buf, fmt,  "joints");
        } else {
            appendSTag(buf, "joints", fmt);
            appendNewLine(buf, fmt.indent().getTab());
            appendStrings(buf, jointSids.toArray(new String[0]), ' ', fmt, NR_JOINTS_PER_LINE);
            appendETag(buf, "joints", fmt.unIndent());
        }
        if (roots == null || roots.isEmpty()) {
            appendEmptyTag(buf, fmt,  "bones");
        } else {
            appendSTag(buf, "bones", fmt);
            fmt.indent();
            for (VJoint root : roots) {     
                appendVJoint(buf, root, fmt);
            }
            appendETag(buf, "bones", fmt.unIndent());
        }
        return buf;        
    }
    
    
    /* 
     * Recursive method for appending one line per VJoint: 
     * joint-sid, parent-sid, x, y, z
     */
    private StringBuilder appendVJoint(StringBuilder buf, VJoint vj, XMLFormatting fmt) {
       buf.append('\n');
       appendSpaces(buf, fmt);
       buf.append(vj.getSid());
       buf.append("   ");
       VJoint parent = vj.getParent();
       buf.append(parent==null ? "null" : parent.getSid());
       
       if (encodeTranslation) {
          buf.append("   ");
          float[] trans = Vec3f.getVec3f();
          vj.getTranslation(trans);
          buf.append(trans[0]);
          buf.append(' '); buf.append(trans[1]);
          buf.append(' '); buf.append(trans[2]);    
       }
       if (encodeRotation) {
          buf.append("   ");
          float[] rot = Quat4f.getQuat4f();
          vj.getRotation(rot);
          buf.append(rot[0]);
          buf.append(' '); buf.append(rot[1]);
          buf.append(' '); buf.append(rot[2]);    
          buf.append(' '); buf.append(rot[3]);    
       }
       if (encodeScale) {
          buf.append("   ");
          float[] scal = Vec3f.getVec3f();
          vj.getScale(scal);
          buf.append(scal[0]);
          buf.append(' '); buf.append(scal[1]);
          buf.append(' '); buf.append(scal[2]);    
       }
       for (VJoint child : vj.getChildren()) {
           appendVJoint(buf, child, fmt);
       }
       return buf;
    }
    
    private static final int DEFAULT_MAX_BONE_COUNT = 32; // used in decodeContent for List allocation
    
    public boolean LINE_BASED = true; // denotes whether bones are encoded on single lines, or are allowed to spread over several lines.
    // When LINE_BASED = true, lines in the bones section can be "commented out" using C/Java style comment chars of the form //
    
    /**
     * Decodes XML content, and converts it into the double time values and float cofig data.
     */
    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException  {
        List<VJoint> vjointList = null; //temp list
        List<String> parentSidList = null; // temp list
        while (tokenizer.atSTag()) {
            String tag = tokenizer.getTagName();
            switch (tag) {
                case "joints":
                    tokenizer.takeSTag();
                    String jointsEncoding = tokenizer.takeOptionalCharData();
                    StringTokenizer jointsTokenizer = new StringTokenizer(jointsEncoding, ATTRIBUTE_TOKEN_DELIMITERS);
                    
                    jointSids = new ArrayList<>(jointCount>0 ? jointCount : DEFAULT_MAX_BONE_COUNT);
                    while (jointsTokenizer.hasMoreTokens()) {
                        jointSids.add(jointsTokenizer.nextToken());
                    }
                    tokenizer.takeETag();
                    if (jointCount < 0) {
                        jointCount = jointSids.size();
                    } else {
                        if (jointCount != jointSids.size()) {
                            System.out.println("XMLSkeleton: jointCount (" + jointCount 
                                    + ") inconsistent with actual number of joints (" + jointSids.size() +")");
                        }
                    }                
                    break;
                case "bones":
                    HashMap<String,String> attrMap = tokenizer.getAttributes();
                    String positionSpec = getOptionalAttribute("position", attrMap, "relative"); // <++++++++ needs handling
                    String originSpec = getOptionalAttribute("origin", attrMap, "floor");
                    tokenizer.takeSTag();
                    
                    vjointList = new ArrayList<>(jointCount>0 ? jointCount : DEFAULT_MAX_BONE_COUNT); // temp list
                    parentSidList = new ArrayList<>(jointCount>0 ? jointCount : DEFAULT_MAX_BONE_COUNT); // temp list while decoding <bones> section
                       
                    String boneEncoding = tokenizer.takeOptionalCharData();
                    StringTokenizer sectionTokenizer;
                    StringTokenizer boneTokenizer;
                    if (LINE_BASED) {
                        sectionTokenizer = new StringTokenizer(boneEncoding, LINE_DELIMITERS);   // split into lines
                        boneTokenizer = null;
                        // boneTokenizer will be allocated per line
                    } else {
                        sectionTokenizer = new StringTokenizer(boneEncoding, ATTRIBUTE_TOKEN_DELIMITERS); // immediately split into bone tokens
                        boneTokenizer = sectionTokenizer; // reuse same tokenizer for bones
                    }
                    try {
                        while (sectionTokenizer.hasMoreTokens()) {                
                            if (LINE_BASED)  {
                                String boneLine = sectionTokenizer.nextToken(); // complete line
                                boneTokenizer = new StringTokenizer(boneLine, ATTRIBUTE_TOKEN_DELIMITERS);
                            }
                            if(!boneTokenizer.hasMoreTokens())continue;
                            
                            String jointSid =  boneTokenizer.nextToken(); // joint sid
                            if (LINE_BASED && jointSid.startsWith("//")) { 
                              // skip comment
                            } else {
                                //System.out.println(" decode bone " + jointSid);
                                VJoint vj = new VJoint(id + "-" + jointSid, jointSid);
                                vjointList.add(vj); // not yet linked to parent                              
                                String parentSid = boneTokenizer.nextToken(); // parent                         
                                parentSidList.add(parentSid);

                                // process extra fields encoding translation, rotation etc.
                                if (encodeTranslation) {
                                    float x = Float.parseFloat(boneTokenizer.nextToken());
                                    float y = Float.parseFloat(boneTokenizer.nextToken());
                                    float z = Float.parseFloat(boneTokenizer.nextToken());
                                    vj.setTranslation(x, y, z);
                                }
                                if (encodeRotation) {
                                    float s = Float.parseFloat(boneTokenizer.nextToken());
                                    float x = Float.parseFloat(boneTokenizer.nextToken());
                                    float y = Float.parseFloat(boneTokenizer.nextToken());
                                    float z = Float.parseFloat(boneTokenizer.nextToken());
                                    vj.setRotation(s, x, y, z);
                                }
                                if (encodeScale) {
                                    float sx = Float.parseFloat(boneTokenizer.nextToken());
                                    float sy = Float.parseFloat(boneTokenizer.nextToken());
                                    float sz = Float.parseFloat(boneTokenizer.nextToken());
                                    vj.setScale(sx, sy, sz);
                                }
                            }
                        }
                    } catch (NoSuchElementException e) { // when the tokenizer expects some elemenet that isn't there
                        logger.error("Skeleton Bone XML: erroneous encoding",e);
                    }
                    // end loop
                    tokenizer.takeETag(); // </bones>
                    if (jointCount < 0) {
                        jointCount = vjointList.size();
                    } else {
                        if (jointCount != vjointList.size()) {
                            System.out.println("XMLSkeleton: jointCount (" + jointCount 
                                    + ") inconsistent with number of bones (" + vjointList.size() +")");
                        }
                    }        
                    break;
                default:
                    logger.warn(tokenizer.getErrorMessage("Skeleton: skip: " + tokenizer.getTagName()));
                    tokenizer.skipTag();
            } // end switch
        } // end while/XML parsing
        if (jointSids == null ) { // no <joints> section, so use bones ordering
            jointSids = new ArrayList<>(vjointList.size());
            for (VJoint vj : vjointList) {
                jointSids.add(vj.getSid());
            }            
        } 
        // Now that the list are established, we still must resolve parents.
        if (vjointList == null) return; // special case, where no <bones> section was present. (Not completely legal)
        Iterator<String> iter = parentSidList.iterator();
        for (VJoint vj : vjointList) {
            String parentSid = iter.next();
            if (parentSid.equals("null") | parentSid.equals("Null")) { // root node.
                roots.add(vj);
            } else { // resolve parent
                for (VJoint par : vjointList) {
                    if (par.getSid().equals(parentSid)) {
                        par.addChild(vj);
                        break;
                    }
                }
            }
        }       
    } // end decodeContent
    

    private static final String XMLTAG = "skeleton";
    
    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given String equals
     * the xml tag for this class
     */
    public static String xmlTag() { return XMLTAG; }
  
    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an object
     */
    @Override
    public String getXMLTag() {
       return XMLTAG;
    }
}
