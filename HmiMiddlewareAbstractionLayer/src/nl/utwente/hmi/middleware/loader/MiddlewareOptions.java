package nl.utwente.hmi.middleware.loader;

import hmi.xml.XMLScanException;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import lombok.Getter;

public class MiddlewareOptions extends XMLStructureAdapter {
	
    @Getter
    private String loaderclass;
    @Getter
    private Properties properties;

    private static final String XMLTAG = "MiddlewareOptions";
    
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
    	loaderclass = getRequiredAttribute("loaderclass", attrMap, tokenizer);
        super.decodeAttributes(attrMap, tokenizer);
    }

	@Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException {
    	properties = new Properties();
        while (tokenizer.atSTag()) {
            String tag = tokenizer.getTagName();
            if (tag.equals(MiddlewareProperty.xmlTag())) {
        		MiddlewareProperty mwPropXML = new MiddlewareProperty();
        		mwPropXML.readXML(tokenizer);
        		properties.put(mwPropXML.getName(), mwPropXML.getValue());
            } else {
                throw new XMLScanException("unknown content in MiddlewareOptions " + tag);
            }
        }
        
    }

    @Override
    public String getXMLTag() {
        return XMLTAG;
    }
    
    public static String xmlTag() {
        return XMLTAG;
    }
}

class MiddlewareProperty extends XMLStructureAdapter {
	@Getter
	private String name;
	@Getter
	private String value;
	
    private static final String XMLTAG = "MiddlewareProperty";
    
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
    	name = getRequiredAttribute("name", attrMap, tokenizer);
    	value = getRequiredAttribute("value", attrMap, tokenizer);
        super.decodeAttributes(attrMap, tokenizer);
    }
    
    @Override
    public String getXMLTag() {
        return XMLTAG;
    }
    
    public static String xmlTag() {
        return XMLTAG;
    }
	
}