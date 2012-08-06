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

/**
 * XMLNameSpace defines an XML name space, together with a short abbreviating label.
 * @author Job Zwiers
 */
public final class XMLNameSpace  {
  
  /*
   * default constructor 
   */
   @SuppressWarnings("unused")
   private  XMLNameSpace() {}

   /**
    * XMLnamespace with (only) the full namespace String defined
    */  
   public XMLNameSpace(String namespace) {
      this.namespace = namespace.intern();
   }
   
   /**
    * XMLnamespace with a namespace label as well as the full namespace String
    */  
   public XMLNameSpace(String prefix, String namespace) {
      this.namespace = namespace.intern();
      this.prefix = prefix.intern();
   }
  
   /**
    * Set the abbreviating label for this namespace
    */
   public void setPrefix(String prefix) {
      this.prefix = prefix.intern();
   }
   
   /**
    * return the full namespace String
    */
   public String getNamespace() { return namespace; }
   
   /**
    * Return the abbreviating label for this namespace, which could be null
    */
   public String getPrefix() { return prefix; }
   
   
   
  
   private String namespace;
   private String prefix;
  
}
