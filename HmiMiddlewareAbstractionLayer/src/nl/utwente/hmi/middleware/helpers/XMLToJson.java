package nl.utwente.hmi.middleware.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

public class XMLToJson {

    private XmlMapper xMapper;
    private ObjectMapper oMapper;

    public XMLToJson(){
        this.xMapper = new XmlMapper();
        this.oMapper = new ObjectMapper();
    }

    public JsonNode toJson(String xml){
        try {
            return this.xMapper.readTree(xml.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return oMapper.createObjectNode();
    }

    public String toJsonString(String xml){
        try{
            JsonNode node = this.xMapper.readTree(xml.getBytes());
            return oMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public String toXML(JsonNode node){
        try {
            return xMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
