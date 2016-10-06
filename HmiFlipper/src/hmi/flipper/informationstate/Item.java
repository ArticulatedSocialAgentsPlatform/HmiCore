/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.informationstate;

/**
 * 
 * @author Mark ter Maat
 *
 */
public interface Item
{
    /**  */
    enum Type {String, Integer, Double, Record, List};
    
    /**
     * @return the value of this Item
     */
    String getString();
    Integer getInteger();
    Double getDouble();
    Record getRecord();
    List getList();
    
    /**
     * @param value - the new value to set
     */
    void setStringValue(String stringValue);
    void setIntegerValue(Integer integerValue);
    void setDoubleValue(Double doubleValue);
    void setRecordValue(Record recordValue);
    void setListValue(List listValue);
    
    /**
     * @return the Type of this Item
     */
    Type getType();
    
    /**
     * @return the value of this Item as an Object
     */
    Object getValue();
    
    /**
     * Retrieves the value of the give path. 
     * If the path is NULL, or if the type of this Item is a String, Integer, or Double it will return the value of this Item.
     * If the path is not NULL (and the type is a Record or a List), it will pass the path to the Record/List, and return the resulting Item. 
     * 
     * @param path - the substructure-path of the item you want
     * @param is - the root informationstate Record
     * @return the wanted Item
     */
    Item getValueOfPath( String path );
    Item getValueOfPath( String path , Record rootIS);
    
    void set( String path, Object value );
}
