/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.informationstate;

import java.util.ArrayList;

/**
 * 
 * @author Mark ter Maat
 *
 */
public interface List
{
    /**
     * Returns the Item of the given Path. The path contains the location of the Item (_first/_last).
     * After that (if the Item is a Record of a List), it is possible to specify the path of a substructure.
     * It is also possible to create a new Item at the beginning or the end of a list, using '_addfirst' or '_addlast'.
     * This will create a new Item at the required place and return this.
     * 
     * @param path - the path of the wanted Item
     * @param is - the root informationstate Record
     * @return the wanted Item
     */
    Item getValueOfPath( String path );
    Item getValueOfPath( String path , Record rootIS);
    
    void set( String path, Object value );
    
    /**
     * Adds a new Item at the end of the list, with the given value
     * @param value - the new value
     */
    void addItemEnd( String value );
    void addItemEnd( Integer value );
    void addItemEnd( Double value );
    void addItemEnd( Record value );
    void addItemEnd( List value );
    void addItemEnd( Item value );

    /**
     * Adds a new Item at the start of the list, with the given value
     * @param value - the new value
     */
    void addItemStart( String value );
    void addItemStart( Integer value );
    void addItemStart( Double value );
    void addItemStart( Record value );
    void addItemStart( List value );
    void addItemStart( Item value );

    /**
     * Returns the value at the given index
     * 
     * @param index - the index of the wanted item
     * @return the wanted item
     */
    String getString( int index );
    Integer getInteger( int index );
    Double getDouble( int index );
    Record getRecord( int index );
    List getList( int index );
    Item getItem( int index );
    
    /**
     * Removes the variable with the given path.
     * @param path - the name of the variable to delete
     */
    void remove( String path );
    
    /**
     * Returns the size of this list
     * @return size of the list
     */
    int size();
    
    /**
     * Checks if the list contains the Object o (this Object can be a String, Integer, or Double).
     * 
     * @param o - the value the list should contain
     * @return true if the list contains the value, false if it does not
     */
    boolean contains( Object o );
    
    /**
     * Checks if the list does not contain the Object o (this Object can be a String, Integer, or Double).
     * 
     * @param o - the value the list should not contain
     * @return true if the list does not contain the value, false if it does or if there is a type-mismatch
     */
    boolean notContains( Object o );
    
    ArrayList<Item> getFullList();

}
