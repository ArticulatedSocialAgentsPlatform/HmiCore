package hmi.animation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XML representation of a VJoint Skeleton.
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
public class Skeleton extends XMLStructureAdapter
{
   private String id;          // id of the Skeleton as a whole. null or an interned String
   private VJoint root;        // root joint of the skeleton tree.
   private List<VJoint> roots; // when there is more than one root.
   
   private List<String> jointSids;
   private List<String> parentSids;
   private List<VJoint> joints;
   private int jointCount = -1; // value < 0 denotes an  unspecified value.
   private boolean encodeTranslation = true; // default is to encode only translations
   private boolean encodeRotation = false;
   private boolean encodeScale = false;
   private String encoding = ""; // possible strings: T, TR TRS an empty String or null is interpreted as "T"
   
   private float[][] jointMatrices; // transform matrices for all joints, linked to the global matrices within the VJoints.
   
   
   private static Logger logger = LoggerFactory.getLogger("hmi.animation.Skeleton");

   /* private Constructor to discourage Skeletons without id */     
   private Skeleton()
   {
     id = "";
   }
   
   /**
    * Create a new Skeleton with specified Skeleton id.
    */
   public Skeleton(String id)
   {
      this();
      setId(id);
   }
   

   /**
    * Create a new Skeleton with specified Skeleton id and specified VJoint tree.
    */
   public Skeleton(String id, VJoint root)
   {
      this();
      setId(id);
      setRoot(root);
   }
 
   
   /**
    * Read a Skeleton from an XMLTokenizer. 
    */
   public Skeleton(XMLTokenizer tokenizer) throws IOException
   {
      this();
      readXML(tokenizer);
   }
   
   
   /*private setter for id */
   private void setId(String id) 
   {
      this.id = id == null ? "" : id.intern();
   }
 
   /**
    * Returns the Skeleton id, possibly &quot;&quot;
    */
   public String getId() 
   {
      return id;
   } 
  

   /**
    * Sets the root joint for the Skeleton, implicitly defining the complete Skeleton VJoint tree.
    */
   public void setRoot(VJoint root)
   {
      this.root = root;
   }



   /**
    * Returns the root joint of the VJoint tree structure that defines the Skeleton joints.
    */
   public VJoint getRoot()
   {
      return root;
   }
   
   
   /**
    * Uses the specified list of joints sids, and tries to locate the global matrices for those sids.
    * The refs to those matrices are set into the specified matrices array. 
    * if certain sids are not found in this Skeleton, the corresponding matrices element
    * is not touched. (So one could use several Skeletons to collect matrices)
    */
   public void linkToJointMatrices(String[] sids, float[][] matrices) 
   {
       if (joints == null || sids == null || matrices == null) return;
       for (int i=0; i<sids.length; i++) 
       {
           String sid = sids[i];
           for (int j=0; j<joints.size(); j++) 
           {
               if (joints.get(j).getSid().equals(sid)) 
               {
                   matrices[i] = joints.get(j).getGlobalMatrix();
                   break;            
               }          
           }   
       }
   }
  
  /* Calculate  number of joints in the VJoint tree */
   private int calculateJointCount(VJoint vj) 
   {
      if (vj == null) return 0;
      int result = 1;
      for (VJoint child : vj.getChildren())
      {
         result += calculateJointCount(child);
      }
      return result;
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
   public StringBuilder appendAttributeString(StringBuilder buf) 
   {     
      appendAttribute(buf, "id", id);
      jointCount = calculateJointCount(root);
      if (HAS_JOINTCOUNT_ATTRIBUTE) appendAttribute(buf, "jointCount", jointCount);
      if ( ! (encoding == null || encoding.equals(""))) appendAttribute(buf, "encoding", encoding); // only append when explicitly set
      return buf;
   }


    /**
     * Decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        setId(getOptionalAttribute("id", attrMap, null));
        jointCount = getOptionalIntAttribute("jointCount", attrMap, -1);
        encoding = getOptionalAttribute("encoding", attrMap, ""); // default is "T", reperseneted here by ""
        setEncoding(encoding);
        super.decodeAttributes(attrMap, tokenizer);
    }


    /**
     * Append the XML content part
     */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        int tab = fmt.getTab();
        appendSTag(buf, "bones", fmt);
        if (root != null) appendVJoint(buf, root, fmt.indent());
        appendETag(buf, "bones", fmt.unIndent());
        return buf;        
    }
    
    
    /* 
     * Recursive method for appending one line per VJoint: 
     * joint-sid, parent-sid, x, y, z
     */
    private StringBuilder appendVJoint(StringBuilder buf, VJoint vj, XMLFormatting fmt) 
    {
       buf.append('\n');
       appendSpaces(buf, fmt);
       buf.append(vj.getSid());
       buf.append("   ");
       VJoint parent = vj.getParent();
       buf.append(parent==null ? "null" : parent.getSid());
       
       if (encodeTranslation)
       {
          buf.append("   ");
          float[] trans = Vec3f.getVec3f();
          vj.getTranslation(trans);
          buf.append(trans[0]);
          buf.append(' '); buf.append(trans[1]);
          buf.append(' '); buf.append(trans[2]);    
       }
        if (encodeRotation)
       {
          buf.append("   ");
          float[] rot = Quat4f.getQuat4f();
          vj.getRotation(rot);
          buf.append(rot[0]);
          buf.append(' '); buf.append(rot[1]);
          buf.append(' '); buf.append(rot[2]);    
          buf.append(' '); buf.append(rot[3]);    
       }
       if (encodeScale)
       {
          buf.append("   ");
          float[] scal = Vec3f.getVec3f();
          vj.getScale(scal);
          buf.append(scal[0]);
          buf.append(' '); buf.append(scal[1]);
          buf.append(' '); buf.append(scal[2]);    
       }
       for (VJoint child : vj.getChildren())
       {
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
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
       while (tokenizer.atSTag())
       {
          String tag = tokenizer.getTagName();
          if (tag.equals("bones"))     
          {
              tokenizer.takeSTag();
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
              int listSize = (jointCount >= 0) ? jointCount : DEFAULT_MAX_BONE_COUNT;
              
              jointSids = new ArrayList<String>(listSize);
              joints = new ArrayList<VJoint>(listSize);
              parentSids = new ArrayList<String>(listSize);
              int count = 0;
              
              try {
                 while (sectionTokenizer.hasMoreTokens())
                 {                
                    if (LINE_BASED)
                    {
                       String boneLine = sectionTokenizer.nextToken(); // complete line
                       boneTokenizer = new StringTokenizer(boneLine, ATTRIBUTE_TOKEN_DELIMITERS);
                    }
                    
                    String jointSid =  boneTokenizer.nextToken(); // joint sid
                    if (LINE_BASED && jointSid.startsWith("//")) { 
                      // skip comment
                    }
                    else 
                    {
                       //System.out.println(" decode bone " + jointSid);
                       jointSids.add(jointSid);
                       VJoint jnt = new VJoint(id + "-" + jointSid, jointSid);
                       
                       String parentSid = boneTokenizer.nextToken(); // parent
                       parentSids.add(parentSid);
                       if (encodeTranslation)
                       {
                          float x = Float.parseFloat(boneTokenizer.nextToken());
                          float y = Float.parseFloat(boneTokenizer.nextToken());
                          float z = Float.parseFloat(boneTokenizer.nextToken());
                          jnt.setTranslation(x, y, z);
                       }
                       if (encodeRotation)
                       {
                          float s = Float.parseFloat(boneTokenizer.nextToken());
                          float x = Float.parseFloat(boneTokenizer.nextToken());
                          float y = Float.parseFloat(boneTokenizer.nextToken());
                          float z = Float.parseFloat(boneTokenizer.nextToken());
                          jnt.setRotation(s, x, y, z);
                       }
                       if (encodeScale)
                       {
                          float sx = Float.parseFloat(boneTokenizer.nextToken());
                          float sy = Float.parseFloat(boneTokenizer.nextToken());
                          float sz = Float.parseFloat(boneTokenizer.nextToken());
                          jnt.setScale(sx, sy, sz);
                       }
                       joints.add(jnt);
                       count++;    
                    }
                 }
              }
              catch (NoSuchElementException e)
              {
                logger.warn(tokenizer.getErrorMessage("Skeleton: incorrect bone encoding: " + boneEncoding)); 
              }
              if (jointCount >= 0 && count != jointCount) 
              {
                 System.out.println("Skeleton " + id + " count =\"" + jointCount + "\" has different number of actual bones: " + count);
              }
              
              for (int i=0; i<joints.size(); i++) 
              {
                  String parentSid = parentSids.get(i);
                  if (parentSid.equals("null") || parentSid.equals("Null")) // root joint
                  {
                     if (getRoot() == null)
                     {
                         setRoot(joints.get(i));
                     }
                     else 
                     {
                        System.out.println("Skeleton " + id + " has more than one root: "  + root.getSid() + ", " + joints.get(i).getSid());
                     }
                  }
                  else 
                  {
                     boolean foundParent = false;
                     for (VJoint vj : joints)
                     {
                        foundParent = (vj.getSid().equals(parentSid));
                        if (foundParent) 
                        {
                           vj.addChild(joints.get(i));
                           break;
                        }
                     }
                     if ( ! foundParent)
                     { // should have found parent by now
                        System.out.println("Skeleton " + id + " joint " + joints.get(i).getSid() + " with unknow parent: " + parentSid);
                     }
                  } 
              }
              
              tokenizer.takeETag();
          } 
          else 
          {
              logger.warn(tokenizer.getErrorMessage("Skeleton: skip: " + tokenizer.getTagName()));
              tokenizer.skipTag();
          } 
       }
    }   
    

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
