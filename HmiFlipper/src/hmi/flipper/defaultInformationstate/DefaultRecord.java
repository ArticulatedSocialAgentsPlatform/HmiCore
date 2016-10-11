/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.defaultInformationstate;

import hmi.flipper.informationstate.*;
import hmi.flipper.informationstate.Item.Type;

import java.io.Serializable;
import java.util.HashMap;

/**
 * An InformationState contains variables with a name, a type and a value.
 * It can have be an atomic value (the type is a String, an Integer, or a Double), but it can also
 * be a substructure, namely:
 * - a List- a list of elements of the same type which don't have a name, only an order.
 * - a Record - a substructure which is basically an inner-InformationState (or, the InformationState is basically a Record).
 * 
 * If you know what type a certain variable is, you can directly ask for that type (for example: getString(name)).
 * However, if you do not, then you get an Item, which includes the value and the type.
 * 
 * It is also possible to specify a path to a variable (for example if it is inside a record or a list, or even deeper).
 * 
 * @version 0.1.3
 * getType() methods now accept Paths too.
 * 
 * @version 0.1.2
 * Changed the way setting a new variable works, it will now accept and create a path too.
 * 
 * @version 0.1.1
 * Added support for the removal of elements given a Path.
 * Fixed a crash when asking the value of a non-existing item.
 * 
 * @version 0.1
 * First version
 * 
 * @author Mark ter Maat
 */

public class DefaultRecord implements Serializable, Record
{
    /* This record, programmed as a HashMap where the key is the name of the variable, and the value the Item-object of the value. */
    private HashMap<String,Item> is = new HashMap<String,Item>();

    /**
     * Creates a new InformationState
     */
    public DefaultRecord()
    {

    }

    /**
     * Returns the Item at the place of the given path.
     * The path specifies the name of the variable you want. Substructures can be used by using a dot.
     * For example, an element var1 of the record r1 can be called by 'r1.var1'.
     * A list does not use indices, you can only get the first or the last element (thereby using it as a queue or a stack).
     * This is done with 'list1._first' or 'list1._last'.
     * 
     * @param path - the path of the variable in the InformationState you want.
     * @return the Item at the wanted place, or NULL if it does not exist.
     */
    public Item getValueOfPath( String path, Record rootIS )
    {
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        /* Determine the name (the name of the variable of this InformationState) and the valuePath 
         * (the path of the substructure of the first Name, only used if the value of the first Name is a Record or a List) */
        String name;
        String valuePath;
        if( path.contains(".") ) {
            name = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else {
            name = path;
            valuePath = null;
        }

        /* Process the remaining ValuePath in the Item at the found place, and return the resulting Item */
        Item i = is.get(name);
        if( i != null ) {
            return i.getValueOfPath(valuePath, rootIS);
        } else {
            return null;
        }
    }

    public Item.Type getTypeOfPath( String path , Record rootIS)
    {
        path = path.replaceAll(DefaultList.ADDFIRST, DefaultList.FIRST);
        path = path.replaceAll(DefaultList.ADDLAST, DefaultList.LAST);
        Item i = getValueOfPath(path, rootIS);
        if( i != null ) {
            return i.getType();
        } else {
            return null;
        }
    }

    public void set( String path, Object value )
    {
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        if( path.contains(".") ) {
            String p1 = path.substring(0, path.indexOf("."));
            String p2 = path.substring(path.indexOf(".")+1, path.length());
            Item i = is.get(p1);
            if( i == null ) {
                if( p2.startsWith(DefaultList.FIRST) || p2.startsWith(DefaultList.LAST) 
                        || p2.startsWith(DefaultList.ADDFIRST) || p2.startsWith(DefaultList.ADDLAST) ) {
                    i = new DefaultItem(new DefaultList());
                } else {
                    i = new DefaultItem(new DefaultRecord());
                }
            }
            i.set( p2, value );
            is.put(p1,i);
        } else {
        	if(value instanceof Item){
        		is.put(path, (Item)value);
        	} else {
        		is.put(path, new DefaultItem(value));
        	}
        }
    }

    /**
     * Set a new variable with the given name and the given String as value.
     * @param name - the name of the new variable
     * @param value - the new String value
     */
    public void set( String name, String value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given Integer as value.
     * @param name - the name of the new variable
     * @param value - the new Integer value
     */
    public void set( String name, Integer value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given Double as value.
     * @param name - the name of the new variable
     * @param value - the new Double value
     */
    public void set( String name, Double value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given InformationState (Record) as value.
     * @param name - the name of the new variable
     * @param value - the new InformationState value
     */
    public void set( String name, Record value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given List as value.
     * @param name - the name of the new variable
     * @param value - the new List value
     */
    public void set( String name, List value )
    {
        set( name,(Object)value );
    }

    /**
     * Returns the String-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the String value
     */
    public String getString( String name , Record rootIS)
    {
        Item i = getValueOfPath(name, rootIS);
        if( i != null ) {
            return i.getString();
        } else {
            return null;
        }
    }

    /**
     * Returns the Integer-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Integer value
     */
    public Integer getInteger( String name , Record rootIS)
    {
        Item i = getValueOfPath(name, rootIS);
        if( i != null ) {
            return i.getInteger();
        } else {
            return null;
        }
    }

    /**
     * Returns the Double-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Double value
     */
    public Double getDouble( String name , Record rootIS)
    {
        Item i = getValueOfPath(name, rootIS);
        if( i != null ) {
            return i.getDouble();
        } else {
            return null;
        }
    }

    /**
     * Returns the Record-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Record value
     */
    public Record getRecord( String name , Record rootIS)
    {
        Item i = getValueOfPath(name, rootIS);
        if( i != null ) {
            return i.getRecord();
        } else {
            return null;
        }
    }

    /**
     * Returns the List-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the List value
     */
    public List getList( String name , Record rootIS)
    {
        Item i = getValueOfPath(name, rootIS);
        if( i != null ) {
            return i.getList();
        } else {
            return null;
        }
    }

    /**
     * Returns the Item-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Item value
     */
    public Item getItem( String name )
    {
        Item i = is.get(name);
        return i;
    }

    /**
     * Removes the variable with the given path.
     * @param path - the name of the variable to delete
     */
    public void remove( String path )
    {
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        /* Determine the name (the name of the variable of this InformationState) 
         * and the valuePath (the path of the substructure of the first Name, only used if the value of the first Name is a Record or a List) */
        String name;
        String valuePath;
        if( path.contains(".") ) {
            name = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else {
            name = path;
            valuePath = null;
        }

        /* Process the remaining ValuePath in the Item at the found place, and return the resulting Item */
        Item i = is.get(name);
        if( i == null ) {
            // Done, nothing to remove.
            return;
        } else if( valuePath == null ) {
            is.remove(name);
        } else if( i.getType() == Item.Type.List ) {
            i.getList().remove(valuePath);
        } else if( i.getType() == Item.Type.Record ) {
            i.getRecord().remove(valuePath);
        } else {
            is.remove(name);
        }
    }

    /**
     * Returns the InformationState as a textual representation.
     */
    @Override
    public String toString()
    {
        return toStringHelper("");
    }

    /**
     * Returns the InformationState as a textual representation with the given String before each line (used for structuring).
     * @param pre - the String to put in front of each line
     */
    public String toStringHelper( String pre )
    {
    	String ret = "";
        for( String key : is.keySet() ) {
            Item i = is.get(key);
            if( i.getType() == Item.Type.String || i.getType() == Item.Type.Integer || i.getType() == Item.Type.Double ) {
                ret += pre + "-" + key + " = " + i.getValue().toString() + "\n";
            } else if( i.getType() == Item.Type.Record ) {
                ret += pre + key + " = Record: [" + "\n";
                ret += ((DefaultRecord)i.getRecord()).toStringHelper(pre + "  ");
            } else if( i.getType() == Item.Type.List ) {
                ret += pre + key + " = List: [" + "\n";
                ret += ((DefaultList)i.getList()).toStringHelper(pre + "  ");
            }
        }
        return ret;
    }
    
    public HashMap<String,Item> getItems()
    {
        return is;
    }

	@Override
	public Item getValueOfPath(String path) {
		return getValueOfPath(path, null);
	}

	@Override
	public Type getTypeOfPath(String path) {
		return getTypeOfPath(path,null);
	}

	@Override
	public String getString(String name) {
		return getString(name, null);
	}

	@Override
	public Integer getInteger(String name) {
		return getInteger(name,null);
	}

	@Override
	public Double getDouble(String name) {
		return getDouble(name,null);
	}

	@Override
	public Record getRecord(String name) {
		return getRecord(name, null);
	}

	@Override
	public List getList(String name) {
		return getList(name,null);
	}
}
