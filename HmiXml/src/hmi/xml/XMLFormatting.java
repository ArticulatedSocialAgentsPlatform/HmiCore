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
   

  
}
