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
package nl.utwente.hmi.middleware.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import nl.utwente.hmi.middleware.Middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hmi.xml.XMLScanException;
import hmi.xml.XMLTokenizer;

/**
 * This GenericMiddlewareLoader acts as a kind of factory for loading a specific MiddlewareLoader
 * @author davisond
 *
 */
public class GenericMiddlewareLoader {

	private static Logger logger = LoggerFactory.getLogger(GenericMiddlewareLoader.class.getName());

	private String specificLoader;

    private static Properties globalProps = new Properties();
    private Properties actualProps;
    
	/**
	 * Indicate which specific MiddlewareLoader should be initiated (using reflection), with which properties
	 * @param specificLoader the fully qualified name (package + classname) of the specific MiddlewareRobot
	 * @param ps the parameters required by the middlewareLoader
	 */
	public GenericMiddlewareLoader(String specificLoader, Properties ps){
		this.specificLoader = specificLoader;
		actualProps = new Properties();
		actualProps.putAll(getGlobalProperties());
		actualProps.putAll(ps);
		logger.debug("READY TO LOAD WITH with props: {}; requested props was: {}", actualProps.toString(),ps.toString());
	}
	
	/**
	 * Setter for global properties
	 * @param propFile, file name in the classpath containing the properties
	 */
	public static void setGlobalPropertiesFile(String propFile)
	{
		synchronized (globalProps)
		{
			//try to find that file; reload global props
			globalProps = new Properties();
			InputStream input = null;
		    //logger.info("loading globalprops from {}",propFile);
			try 
			{
				input = GenericMiddlewareLoader.class.getClassLoader().getResourceAsStream(propFile);
				if (input == null) 
				{
					logger.warn("Sorry, unable to find properties file in resource folder: {}", propFile);
					logger.warn("Trying to load file outside resource folder");
					try{
						input = new FileInputStream(propFile);
						globalProps.load(input);
					}
					catch (Exception e){
						logger.warn("Could not load global middleware props {}",propFile);
						e.printStackTrace();
					}

				} else {
					//load the actual properties
					logger.info("Loading properties: {}",propFile);
					globalProps.load(input);
					logger.info("Loaded globalprops: {}",globalProps.toString());
				}
			}
			catch (Exception ex)
			{
				logger.error("Could not load global middleware props {}",propFile);
			}
		}
	}

	/**
	 * Setter for global properties
	 * @param ps, properties to set
	 */
	public static void setGlobalProperties(Properties ps)
	{
		synchronized (globalProps)
		{
			globalProps = new Properties(ps);
		}
	}

	/**
	 * Getter for global properties
	 * @return
	 */
    public static Properties getGlobalProperties()
    {
		synchronized (globalProps)
		{
			return globalProps;
		}
    	
    }
    
    /**
     * Static loader function (WILL NOT USE GLOBAL PROPS!)
     * @author: jankolkmeier
     * TODO: turn this loader into a proper XMLStructureAdapter?
     */ 
	public static Middleware load(XMLTokenizer tokenizer) throws IOException {
        if(!tokenizer.atSTag(MiddlewareOptions.xmlTag())) {
            throw new XMLScanException("GenericMiddlewareLoader requires an inner MiddlewareOptions element");            
        }
        MiddlewareOptions mwOptsXML = new MiddlewareOptions();
        mwOptsXML.readXML(tokenizer);
        GenericMiddlewareLoader gml = new GenericMiddlewareLoader(mwOptsXML.getLoaderclass(), mwOptsXML.getProperties());
        return gml.load();
	}
	
	/**
	 * Actually load the Middleware
	 * @return an instance of the Middleware which is instantiated by the specific MiddlewareLoader
	 */
	public Middleware load(){
		Middleware m = null;
		ClassLoader cl = GenericMiddlewareLoader.class.getClassLoader();
		try {
            logger.info("middleware requested: {}",specificLoader);
			Class<?> loaderClass = cl.loadClass(specificLoader);
			Object loaderObject = loaderClass.newInstance();
			if(loaderObject instanceof MiddlewareLoader){
				MiddlewareLoader ml = (MiddlewareLoader)loaderObject;
				logger.debug("Loading with props: {}; global props was: {}", actualProps.toString(),globalProps.toString());
				m = ml.loadMiddleware(actualProps);
				
			}
            else
            {
                logger.error("failed making loader object with classloader {}",cl);
            }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (m==null)
            logger.error("null middleware");		
		return m;
	}
	
}
