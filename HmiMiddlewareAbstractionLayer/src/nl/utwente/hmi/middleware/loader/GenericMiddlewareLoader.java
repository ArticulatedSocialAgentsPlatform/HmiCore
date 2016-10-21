package nl.utwente.hmi.middleware.loader;

import java.io.InputStream;
import java.util.Properties;

import nl.utwente.hmi.middleware.Middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
					logger.error("Sorry, unable to find properties file: {}", propFile);
				} else {
					//load the actual properties
					logger.info("Loading properties enzo: {}",propFile);
					globalProps.load(input);
					//logger.debug("loaded globalprops: {}",globalProps.toString());
				}
			}
			catch (Exception ex)
			{
				logger.warn("Could not load global middleware props {}",propFile);
			}
		}
	}
	
    public static Properties getGlobalProperties()
    {
		synchronized (globalProps)
		{
			
			return globalProps;
		}
    	
    }
    
	/**
	 * Actually load the Middleware
	 * @return an instance of the Middleware which is instantiated by the specific MiddlewareLoader
	 */
	public Middleware load(){
		Middleware m = null;
		ClassLoader cl = GenericMiddlewareLoader.class.getClassLoader();
		try {
            System.out.println("middleware requested: "+specificLoader);		
			Class<?> loaderClass = cl.loadClass(specificLoader);
			Object loaderObject = loaderClass.newInstance();
			if(loaderObject instanceof MiddlewareLoader){
				MiddlewareLoader ml = (MiddlewareLoader)loaderObject;
				//logger.debug("Loading with props: {}; global props was: {}", actualProps.toString(),globalProps.toString());
				m = ml.loadMiddleware(actualProps);
				
			}
            else
            {
                System.out.println("failed making loader object");		
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
