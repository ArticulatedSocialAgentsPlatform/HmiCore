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
