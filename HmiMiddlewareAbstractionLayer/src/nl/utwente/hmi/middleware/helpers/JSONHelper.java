package nl.utwente.hmi.middleware.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

public class JSONHelper {

	private ObjectMapper om;

	public JSONHelper(){
		this.om = new ObjectMapper();
	}
	
	/**
	 * Recursively search for the JsonNode for the specified key. If found, this function will return an ObjectNode containing the key with underlying JsonNode.
	 * For instance, if searching for "key2" in JSON "{key1:{key2:{key3:value}}}" this will return "{key2:{key3:value}}"
	 * @param jn the JsonNode to search in
	 * @param key the key to search for
	 * @return the found object with {key:JsonNode} or MissingNode if not found
	 */
	public JsonNode searchKey(JsonNode jn, String key){
		//this is a null node or a leaf node
		if(jn == null || !jn.isContainerNode()) {
			return MissingNode.getInstance();
		}
		
		//yay, found it!
		//extract value and place it in clean wrapper ObjectNode with the key
		if(jn.has(key)) {
			return om.createObjectNode().set(key, jn.get(key));
		}
		
		//not found on this level... try to go deeper
		for (JsonNode child : jn) {
			if (child.isContainerNode()) {
			    JsonNode childResult = searchKey(child, key);
			    if (childResult != null && !childResult.isMissingNode()) {
			        return childResult;
			    }
			}
		}
		
		// not found at all
		return MissingNode.getInstance();
	}
	
}
