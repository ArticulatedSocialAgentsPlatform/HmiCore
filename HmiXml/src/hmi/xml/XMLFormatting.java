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

package hmi.xml;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XMLFormatting defines formatting for writing XML structures,
 * such as tabbing and namespace labels
 * @author Job Zwiers
 */
public class XMLFormatting  {
  
  /**
   * Default constructor 
   */
   public  XMLFormatting() {}

  /**
   * Constructor specifying a tab setting
   */
   public  XMLFormatting(int tab) {
      this.tab = tab;
   }
   
   
   /**
    * returns the current tab.
    */
   public int getTab() {
      return tab;
   }
   
   /**
    * returns the current tab.
    */
   public int getIndentedTab() {
      return tab+tabIncrement;
   }
   
   /**
    * Set the increment for the tab() method.
    */
   public void setTabIncrement(int increment) {
      tabIncrement = increment;
   }
   
   /**
    * Returns the increment for the tab() method.
    */
   public int getTabIncrement() {
      return tabIncrement;
   }
   
   /**
    * Returns the prefix currently associated with the specified namespace.
    * It is assumed that namespace is an &quot;interned&quot; String
    * If there is no associated prefix, null is returned.
    */ 
   public String getNamespacePrefix(String namespace) {
      return namespaceStack.getPrefix(namespace);
   }
   
   
   /**
    * Pushes an XMLNameSpace element on the namespace stack for this XMLFormatting.
    */
   public XMLFormatting pushXMLNameSpace(XMLNameSpace xmlNamespace) {
       namespaceStack.pushXMLNameSpace(xmlNamespace);  
       return this;
   }
   
   /**
    * Pops an XMLNameSpace element from the namespace stack for this XMLFormatting.
    */
   public XMLFormatting popXMLNameSpace() {
      namespaceStack.popXMLNameSpace();
      return this;
   }
  
   /**
    * Push the current tab, then increment the tab with the specified tabIncrement.
    */
   public XMLFormatting indent(int tabIncrement) {
      tabStack.add(tab);
      tab += tabIncrement;
      return this;
   }
   
   /**
    * Restore/Pop the current tab setting.
    */
   public XMLFormatting unIndent() {
      
      int topindex = tabStack.size()-1;
      if (topindex >= 0) {
         tab = tabStack.get(topindex);
         tabStack.remove(topindex);
      } else {
         topindex = 0;
         logger.warn("XMLFormatting.unIndent: negative indent level");  
      }
      return this;
   }
  
   /**
    * Push the current tab, then increment the tab with the current tabIncrement.
    */
   public XMLFormatting indent() {
      return indent(tabIncrement);
   }
  
  // private ArrayList<XMLNameSpace> namespaceStack = new ArrayList<XMLNameSpace>();
   private ArrayList<Integer> tabStack = new ArrayList<Integer>();
   
   private XMLNameSpaceStack namespaceStack = new XMLNameSpaceStack();
   
   /** The TAB value determines the default indentation for nested XMLStructures */ 
   public static final int TAB = 3;
   private int tab;
   private int tabIncrement = TAB;
   private static Logger logger = LoggerFactory.getLogger("hmi.xml.XMLStructure");  
   
   /**
    * Pushes a &quot;mark&quot; on the stack, informally denoting the position where a new STag element has started.
    * This is used later on to pop a number of pushes together
    */
   public void pushMark()
   {
       namespaceStack.pushMark();
   }
   
   /**
    * Pops elements from the stack up to and including the last mark.
    */
   public void popMark()
   {
       namespaceStack.popMark();
   }
  
}
