package nl.utwente.hmi.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class MiddlewareWrapperFactory {

    private static Logger logger = LoggerFactory.getLogger(MiddlewareWrapperFactory.class.getName());

    /**
     * Method for creating a MiddlewareWrapper with a configuration file
     * @param jn, a JsonNode that represents properties of the middleware in the format of
     *            {
     *              "loader" : "nl.utwente.hmi.*",
     *              "properties" : {
     *
     *              }
     *            }
     * @return, the MiddlewareWrapper
     */
    public static MiddlewareWrapper createMiddlewareWrapper(JsonNode jn){
        if(jn.has("loader")) {
            Properties mwProps = new Properties();
            String mw = jn.get("loader").textValue();
            mwProps.put("loader",mw);
            JsonNode props = jn.get("properties");
            Iterator<Map.Entry<String, JsonNode>> it = props.fields();
            while(it.hasNext()) {
                Map.Entry<String, JsonNode> prop = it.next();
                mwProps.setProperty(prop.getKey(),prop.getValue().textValue());
            }
            return new MiddlewareWrapper(mwProps) {
                @Override
                public void processData(JsonNode jn) {
                    logger.debug("Received Data: {}",jn.toString());
                }
            };
        }
        else{
            logger.error("Invalid format for instantiating middleware");
            return null;
        }

    }
}
