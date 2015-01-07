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

package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * Technique for  Collada-FX (but NOT for standard Collada)
 * parents: profile_XYZ, like  profile_COMMON, profile_GLSL etc.  
 * @author Job Zwiers
 */
public class TechniqueFX extends ColladaElement {
 
   //private String profile; //  = "COMMON"; // default value
  
   private Asset asset;
   private ArrayList<Newparam> newparamList;
   private ArrayList<Setparam> setparamList;
   private ArrayList<ColladaImage> imageList;
   private ArrayList<Code> codeList;
   private ArrayList<Pass> passList;
   // fixed function shading:
   private FixedFunctionShader shader; // combines constant, lambert, blinn and phong shaders

   private ArrayList<Extra> extras;
   private ShaderInclude include;
   
   
   public TechniqueFX() {
      super();
   }
   
   public TechniqueFX(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);   
   }
 
   public List<Newparam> getNewparamList() { return newparamList; }
   public List<Setparam> getSetparamList() { return setparamList; }
   public List<ColladaImage> getImageList() { return imageList; }
   public List<Code> getCodeList() { return codeList; }
   public List<Pass> getPassList() { return passList; }
   
   
   /**
    * Returns a shader for this technique.
    * Currently, we support just FixedFunction shaders
    */
   public FixedFunctionShader getShader() {
       return shader;
   }
   

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {  
      setSid(getRequiredAttribute("sid", attrMap, tokenizer)); // *required* attribute    
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, newparamList);
      appendXMLStructureList(buf, fmt, setparamList);
      appendXMLStructureList(buf, fmt, imageList);
      appendXMLStructureList(buf, fmt, codeList);
      appendXMLStructure(buf, fmt, include);
      appendXMLStructureList(buf, fmt, passList);
      appendXMLStructure(buf, fmt, shader);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();       
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);  
         } else if (tag.equals(Newparam.xmlTag()))  {   
                 if (newparamList == null)  newparamList = new ArrayList<Newparam>();                   
                 newparamList.add(new Newparam(getCollada(), tokenizer));  
         } else if (tag.equals(Setparam.xmlTag()))  {     
                 if (setparamList == null)  setparamList = new ArrayList<Setparam>();             
                 setparamList.add(new Setparam(getCollada(), tokenizer));  
         } else if (tag.equals(ColladaImage.xmlTag()))  {    
                 if (imageList == null)  imageList = new ArrayList<ColladaImage>();              
                 imageList.add(new ColladaImage(getCollada(), tokenizer));
         } else if (tag.equals(Code.xmlTag()))  {         
                 if (codeList == null)  codeList = new ArrayList<Code>();         
                 codeList.add(new Code(getCollada(), tokenizer));
         } else if (tag.equals(Include.xmlTag()))  {
                 include = new ShaderInclude(getCollada(), tokenizer);
         } else if (tag.equals(Pass.xmlTag()))  {     
                 if (passList == null)  passList = new ArrayList<Pass>();             
                 passList.add(new Pass(getCollada(), tokenizer));
         } else if (tag.equals(Blinn.xmlTag()))  {                
                 shader = new Blinn(getCollada(), tokenizer); 
         } else if (tag.equals(Constant.xmlTag()))  {                
                 shader = new Constant(getCollada(), tokenizer);
         } else if (tag.equals(Lambert.xmlTag()))  {                
                 shader = new Lambert(getCollada(), tokenizer);     
         } else if (tag.equals(Phong.xmlTag()))  {                
                 shader = new Phong(getCollada(), tokenizer);     
          } else if (tag.equals(Eye.xmlTag()))  {                
                 shader = new Eye(getCollada(), tokenizer);   
                  
         } else if (tag.equals(Extra.xmlTag()))  {   
                 if (extras == null)  extras = new ArrayList<Extra>();          
                 extras.add(new Extra(getCollada(), tokenizer));                
         } else {      
            // Missing,  for other profiles than profile_COMMON:  annotate,
            // include, code, setparam, pass 
            getCollada().warning(tokenizer.getErrorMessage("TechniqueFX: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }   
      addColladaNode(asset);
      addColladaNodes(newparamList);
      addColladaNodes(setparamList);
      addColladaNodes(imageList);
      addColladaNodes(codeList);
      addColladaNode(include);
      addColladaNodes(passList);
      addColladaNode(shader);
      addColladaNodes(extras);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "technique";
 
   /**
    * The XML Stag for XML encoding
    */
   public static String xmlTag() { return XMLTAG; }
 
   /**
    * returns the XML Stag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   }
}
