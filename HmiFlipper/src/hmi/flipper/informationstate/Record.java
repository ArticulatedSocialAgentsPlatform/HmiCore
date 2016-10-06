/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.informationstate;

import java.util.HashMap;


/**
 * 
 * @author Mark ter Maat
 *
 */
public interface Record
{
    /**
     * Returns the Item at the place of the given path.
     * The path specifies the name of the variable you want. Substructures can be used by using a dot.
     * For example, an element var1 of the record r1 can be called by 'r1.var1'.
     * A list does not use indices, you can only get the first or the last element (thereby using it as a queue or a stack).
     * This is done with 'list1._first' or 'list1._last'.
     * 
     * @param path - the path of the variable in the InformationState you want.
     * @param is - the root informationstate Record
     * @return the wanted Item
     */
    Item getValueOfPath( String path );
    Item getValueOfPath( String path , Record rootIS);
    
    /**
     * Returns the type of the item at the place of the given path.
     * @param path - the path of the variable in the InformationState you want.
     * @param is - the root informationstate Record
     * @return the Type at the wanted place.
     */
    Item.Type getTypeOfPath( String path );
    Item.Type getTypeOfPath( String path , Record rootIS);
    
    /**
     * Set a new variable with the given name and the given value.
     * @param name - the name of the new variable
     * @param value - the new value
     */
    void set( String name, Object value );
    void set( String name, String value );
    void set( String name, Integer value );
    void set( String name, Double value );
    void set( String name, Record value );
    void set( String name, List value );
    
    /** Returns the value of the variable with the given name, and of the given type.
     * @param name - the name of the wanted variable
     * @return the value of the given type
     */
    String getString( String name );
    Integer getInteger( String name );
    Double getDouble( String name );
    Record getRecord( String name );
    List getList( String name );

    String getString( String name , Record rootIS);
    Integer getInteger( String name , Record rootIS);
    Double getDouble( String name , Record rootIS);
    Record getRecord( String name , Record rootIS);
    List getList( String name , Record rootIS);

    /**
     * Removes the variable with the given path.
     * @param path - the name of the variable to delete
     */
    void remove( String path );
    
    HashMap<String,Item> getItems();
}
