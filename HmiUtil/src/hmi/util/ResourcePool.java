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
package hmi.util;
import java.util.*;
import java.io.*;

/**
 * A utility class for pools of resources, in particular for loading and caching resource objects.
 * @author Job zwiers
 */
public class ResourcePool   { 
   
   /**
    * A ResourcePool.ResourceLoader object should be able to load a particular type of resource files from Resources,
    * optionally using parameters passed on via the param array
    */
   public interface ResourceLoader {
      Object loadResource(Resources res, String resourceName, Object[] param) throws IOException;
   }
   
   /**
    * Create a new ResourcePool
    */
   public ResourcePool() {
   }
   
    /**
    * Create a new ResourcePool with defined ResourceLoader
    */
   public ResourcePool(ResourceLoader loader) {
      this();
      setResourceLoader(loader);
   }
   
   /**
    * Defines the ResourcePool.ResourceLoader object that will be used for loading resource files.
    */   
   public void setResourceLoader(ResourceLoader loader) {
      this.loader = loader;
   }   
   
   /**
    * Clears the resource pool, i.e. all resource directories that might have been added
    * are removed from the list of resource directories.
    */
   public void clear() {
      resourcesList.clear();  
   }
   
   /**
    * Adds the specified directory to the (front of the) list of resource directories to be searched 
    * When some resource is actually present in more than one directory in the list, the version from
    * the directory that was added latest will take preference. 
    */
   public void addResourceDirectory(String resourceDir) {
      Resources res = findResources(resourceDir);
      if (res != null) return; // already on the list, don't add again
      res = new Resources(resourceDir);
      resourcesList.add(0, res);  
   }
   
   /*
    * Return a Resources for the specified resource directory,
    * or null, when there is no such Resources on the list
    */
   private Resources findResources(String resourceDir) {
      for (Resources res : resourcesList) {
         if (res.getResourceDirectory().equals(resourceDir)) {
            return res;  
         } 
      }
      return null;
   }
   
   /*
    * returns the resource for the specified name from the Resources associated with this 
    * Pool instance. Either an already cached object, or a newly loaded version
    * will be returned, unless no such resource could be found, in which case a null value is returned.
    */
   public Object getResource(String resourceName, Object[] properties) throws IOException {
      //hmi.util.Console.println("getResource " + resourceName);
      Object resource = resourceCache.get(resourceName);
      if (resource != null) {
          //hmi.util.Console.println("getResource IN CACHE:" + resourceName);
         return resource;
      } else {
         for (Resources res : resourcesList) {
           // resource = loadResource(res, resourceName, properties);
           resource = loader.loadResource(res, resourceName, properties);
            if (resource != null)  {
               resourceCache.put(resourceName, resource);
               //hmi.util.Console.println("getResource FOUND:" + resourceName);
               return resource;
            }
         }
         //hmi.util.Console.println("getResource NOT FOUND:" + resourceName);
         return null;
      }
   }
   
   /**
    * Tries to get a resource with the specified  name , without specifying properties
    */
   public  Object getResource(String resourceName) throws IOException {
      return getResource(resourceName, null);
   }
      
   private List<Resources> resourcesList = new ArrayList<Resources>();
   private Map<String, Object> resourceCache = new HashMap<String, Object>();
   private ResourceLoader loader;
} 
