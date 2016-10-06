/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.defaultInformationstate;

import java.io.Serializable;
import java.util.ArrayList;

import hmi.flipper.informationstate.*;


/**
 * A List contains an ordered list of Items of the same type (can be any Item-type).
 * It is possible to get a Item at a certain index, or get the first or last Item.
 * However, in contrasts with normal lists, when using a string-path, this List can only be used as a queue or a stack. 
 * This means you can only add items to the beginning or the end of the list, 
 * and you can only get items from the beginning or the end of the list too.
 * 
 * @version 0.1.1
 * Added support for the removal of elements given a Path.
 * 
 * @version 0.1.2
 * Changed the way in which setting a new variable works.
 * 
 * @version 0.1
 * First version
 * 
 * @author Mark ter Maat
 *
 */

public class DefaultList implements Serializable, List
{
    public final static String FIRST = "_first";
    public final static String LAST = "_last";
    public final static String ADDFIRST = "_addfirst";
    public final static String ADDLAST = "_addlast";

    /* The list with Items */
    private ArrayList<Item> list = new ArrayList<Item>();

    /**
     * Creates a new List.
     */
    public DefaultList()
    {

    }

    public Item getValueOfPath( String path){
    	return getValueOfPath(path,null);
    }

    
    /**
     * Returns the Item of the given Path. The path contains the location of the Item (_first/_last), or an integer index, or a select expression like "_select[sub1=x]". The last option only allows very simple "direct subvalue exuals simple value".
     * After that (if the Item is a Record of a List), it is possible to specify the path of a substructure.
     * It is also possible to create a new Item at the beginning or the end of a list, using '_addfirst' or '_addlast'.
     * This will create a new Item at the required place and return this.
     * 
     * @param path - the path of the wanted Item
     * @return the wanted Item
     */
    public Item getValueOfPath( String path, Record rootIS )
    {
        if( list.size() == 0 ) {
            return null;
        }

        /* Get required index of list */
        int index = -1;
        String position;
        String valuePath;
        //System.out.println(path);
        if( path.contains(".") && !path.startsWith("_select[")) {
            position = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else if (!path.contains(".") ) {
            position = path;
            valuePath = null;        	
        } else {
        	int close = path.indexOf("]");
        	if (path.indexOf(".",close) > 0) {
                position = path.substring(0, path.indexOf(".",close));
                valuePath = path.substring(path.indexOf(".",close)+1, path.length());
        	} else {
                position = path;
                valuePath = null;
        	}
        }
        int pos2Index = position.indexOf(".");
        if( pos2Index == -1 ) pos2Index = position.length();
        if( position.toLowerCase().equals(FIRST) ) {
            index = 0;
        } else if( position.toLowerCase().equals(LAST) ) {
            index = list.size()-1;
        } else if (position.toLowerCase().startsWith("_select[")) {
            if (!position.toLowerCase().endsWith("]")) {
                System.err.println("Problems parsing list-index (missing parenthesis): " + path);
                return null;
            }
            String[] selector = position.substring(8,position.length()-1).split("=");
            
            if ((selector.length != 2 ) || (selector[0].length()==0) || (selector[1].length()==0)) {
                System.err.println(position.substring(8,position.length()-1));
                System.err.println("Problems parsing list-index (selector not correct): " + path);
                return null;
            }
            String sub = selector[0];
            String val = selector[1];
            
            if(val.startsWith("ROOT.")){
            	if(rootIS != null){            	
            		Item valItem = rootIS.getValueOfPath("$"+val.substring(5), rootIS);
            		if(valItem.getType() == Item.Type.Double || valItem.getType() == Item.Type.Integer || valItem.getType() == Item.Type.String){
            			val = valItem.getValue().toString();
            		} else {
            			val = "notfound";
            		}
            	} else {
            		System.out.println("List.getValueOfPath's rootIS is null, unable to parse list selector: "+position);
            	}
            }
            
            for (int listIndex = 0; listIndex < list.size(); listIndex++)
            {
                //System.out.println(sub+","+val+","+list.get(listIndex).getValueOfPath(sub).getValue().toString()+",");
                if (list.get(listIndex).getValueOfPath(sub, rootIS).getValue().toString().equals(val)) index = listIndex;
            }
        } else {
            //if we've got an integer index here, (so $something.listname.2.sub would get item 2 from list then get the sub from that)
            try {
                int parseIndex = Integer.parseInt(position.substring(0,pos2Index));
                index = parseIndex;
            }catch( NumberFormatException e ){}
        }        
        
        /* Return the value of the required index */
        if( index != -1 ) {
            return list.get(index).getValueOfPath(valuePath);
        } else {
            //System.err.println("Problems parsing list-index: " + path);
            return null;
        }
    }

    public void set( String path, Object value )
    {
        /* Get required index of list */
        String position;
        String valuePath;
        if( path.contains(".") ) {
            position = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else {
            position = path;
            valuePath = null;
        }

        if( position.toLowerCase().equals(FIRST) ) {
            if( list.size() != 0 ) {
                Item i = list.get(0);
                i.set(valuePath,value);
            } else {
                Item i = new DefaultItem(value);
                list.add(i);
                i.set(valuePath,value);
            }
        } else if( position.toLowerCase().equals(LAST) ) {
            if( list.size() != 0 ) {
                Item i = list.get(list.size()-1);
                i.set(valuePath,value);
            } else {
                Item i = new DefaultItem(value);
                list.add(i);
                i.set(valuePath,value);
            }
        } else if( position.toLowerCase().equals(ADDFIRST) ) {
            if( valuePath == null ) {
                Item i = new DefaultItem(value);
                list.add(0, i);
            } else {
                Item i;
                if( valuePath.startsWith("_") ) {
                    // Next item is a list
                    i = new DefaultItem(new DefaultList());
                } else {
                    // Next item is a Record
                    i = new DefaultItem(new DefaultRecord());
                }
                list.add(0, i);
                i.set(valuePath, value);
            }
        } else if( position.toLowerCase().equals(ADDLAST) ) {
            if( valuePath == null ) {
                Item i = new DefaultItem(value);
                list.add(i);
            } else {
                Item i;
                if( valuePath.startsWith("_") ) {
                    // Next item is a list
                    i = new DefaultItem(new DefaultList());
                } else {
                    // Next item is a Record
                    i = new DefaultItem(new DefaultRecord());
                }
                list.add(i);
                i.set(valuePath, value);
            }
        }
    }

    /**
     * Adds a new Item at the end of the list, with the given String as value
     * @param value - the new String value
     */
    public void addItemEnd( String value )
    {
        list.add(new DefaultItem(value));
    }

    /**
     * Adds a new Item at the end of the list, with the given Integer as value
     * @param value - the new Integer value
     */
    public void addItemEnd( Integer value )
    {
        list.add(new DefaultItem(value));
    }

    /**
     * Adds a new Item at the end of the list, with the given Double as value
     * @param value - the new Double value
     */
    public void addItemEnd( Double value )
    {
        list.add(new DefaultItem(value));
    }

    /**
     * Adds a new Item at the end of the list, with the given InformationState as value
     * @param value - the new InformationState value
     */
    public void addItemEnd( Record value )
    {
        list.add(new DefaultItem(value));
    }

    /**
     * Adds a new Item at the end of the list, with the given List as value
     * @param value - the new List value
     */
    public void addItemEnd( List value )
    {
        list.add(new DefaultItem(value));
    }

    /**
     * Adds a new Item at the end of the list, with the given List as value
     * @param value - the new Item value
     */
    public void addItemEnd( Item value )
    {
        list.add(value);
    }

    /**
     * Adds a new Item at the start of the list, with the given String as value
     * @param value - the new String value
     */
    public void addItemStart( String value )
    {
        list.add(0, new DefaultItem(value));
    }

    /**
     * Adds a new Item at the start of the list, with the given Integer as value
     * @param value - the new Integer value
     */
    public void addItemStart( Integer value )
    {
        list.add(0, new DefaultItem(value));
    }

    /**
     * Adds a new Item at the start of the list, with the given Double as value
     * @param value - the new Double value
     */
    public void addItemStart( Double value )
    {
        list.add(0, new DefaultItem(value));
    }

    /**
     * Adds a new Item at the start of the list, with the given InformationState as value
     * @param value - the new InformationState value
     */
    public void addItemStart( Record value )
    {
        list.add(0, new DefaultItem(value));
    }

    /**
     * Adds a new Item at the start of the list, with the given List as value
     * @param value - the new List value
     */
    public void addItemStart( List value )
    {
        list.add(0, new DefaultItem(value));
    }

    /**
     * Adds a new Item at the start of the list, with the given List as value
     * @param value - the new Item value
     */
    public void addItemStart( Item value )
    {
        list.add(0, value);
    }

    /**
     * Returns the String value at the given index
     * 
     * @param index - the index of the wanted Item
     * @return the wanted String
     */
    public String getString( int index )
    {
        String result;
        try {
            result = (String)list.get(index).getValue();
        }catch( ClassCastException e ) {
            System.err.println("Illegal String request to List.");
            return null;
        }
        return result;
    }

    /**
     * Returns the Integer value at the given index
     * 
     * @param index - the index of the wanted Item
     * @return the wanted Integer
     */
    public Integer getInteger( int index )
    {
        Integer result;
        try {
            result = (Integer)list.get(index).getValue();
        }catch( ClassCastException e ) {
            System.err.println("Illegal Integer request to List.");
            return null;
        }
        return result;
    }

    /**
     * Returns the Double value at the given index
     * 
     * @param index - the index of the wanted Item
     * @return the wanted Double
     */
    public Double getDouble( int index )
    {
        Double result;
        try {
            result = (Double)list.get(index).getValue();
        }catch( ClassCastException e ) {
            System.err.println("Illegal Double request to List.");
            return null;
        }
        return result;
    }

    /**
     * Returns the InformationState (Record) value at the given index
     * 
     * @param index - the index of the wanted Item
     * @return the wanted InformationState
     */
    public Record getRecord( int index )
    {
        Record result;
        try {
            result = (Record)list.get(index).getValue();
        }catch( ClassCastException e ) {
            System.err.println("Illegal Record request to List.");
            return null;
        }
        return result;
    }

    /**
     * Returns the List value at the given index
     * 
     * @param index - the index of the wanted Item
     * @return the wanted List
     */
    public List getList( int index )
    {
        List result;
        try {
            result = (List)list.get(index).getValue();
        }catch( ClassCastException e ) {
            System.err.println("Illegal List request to List.");
            return null;
        }
        return result;
    }

    /**
     * Returns the Item value at the given index
     * 
     * @param index - the index of the wanted Item
     * @return the wanted Item
     */
    public Item getItem( int index )
    {
        return list.get(index);
    }

    /**
     * If the Items in this list are of type String, returns a list with all values as Strings in it. Else, returns NULL.
     * @return the list with Strings
     */
    public ArrayList<String> getStringList()
    {
        ArrayList<String> aList = new ArrayList<String>();
        for( Item item : list ) {
            String value;
            try {
                value = (String)item.getValue();
            }catch( ClassCastException e ) {
                System.err.println("ClassCastException during String-list creation.");
                return null;
            }
            aList.add(value);
        }
        return aList;
    }

    /**
     * If the Items in this list are of type Integer, returns a list with all values as Integers in it. Else, returns NULL.
     * @return the list with Integers
     */
    public ArrayList<Integer> getIntegerList()
    {
        ArrayList<Integer> aList = new ArrayList<Integer>();
        for( Item item : list ) {
            Integer value;
            try {
                value = (Integer)item.getValue();
            }catch( ClassCastException e ) {
                System.err.println("ClassCastException during Integer-list creation.");
                return null;
            }
            aList.add(value);
        }
        return aList;
    }

    /**
     * If the Items in this list are of type Double, returns a list with all values as Doubles in it. Else, returns NULL.
     * @return the list with Doubles
     */
    public ArrayList<Double> getDoubleList()
    {
        ArrayList<Double> aList = new ArrayList<Double>();
        for( Item item : list ) {
            Double value;
            try {
                value = (Double)item.getValue();
            }catch( ClassCastException e ) {
                if( item.getType() == Item.Type.Integer ) {
                    value = item.getInteger().doubleValue();
                } else {
                    System.err.println("ClassCastException during Double-list creation.");
                    return null;
                }
            }
            aList.add(value);
        }
        return aList;
    }

    /**
     * If the Items in this list are of type InformationState (Record), returns a list with all values as InformationStates in it. Else, returns NULL.
     * @return the list with InformationStates
     */
    public ArrayList<Record> getRecordList()
    {
        ArrayList<Record> aList = new ArrayList<Record>();
        for( Item item : list ) {
            Record value;
            try {
                value = (Record)item.getValue();
            }catch( ClassCastException e ) {
                System.err.println("ClassCastException during String-list creation.");
                return null;
            }
            aList.add(value);
        }
        return aList;
    }

    /**
     * Removes the variable with the given path.
     * @param path - the name of the variable to delete
     */
    public void remove( String path )
    {
        /* Get required index of list */
        int index = -1;
        String position;
        String valuePath;
        if( path.contains(".") && !path.startsWith("_select[") ) {
            position = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else if (!path.contains(".") ) {
            position = path;
            valuePath = null;        	
        } else {
        	int close = path.indexOf("]");
        	if (path.indexOf(".",close) > 0) {
                position = path.substring(0, path.indexOf(".",close));
                valuePath = path.substring(path.indexOf(".",close)+1, path.length());
        	} else {
                position = path;
                valuePath = null;
        	}
        }
        if( position.toLowerCase().equals("_first") ) {
            index = 0;
        }
        if( position.toLowerCase().equals("_last") ) {
            index = list.size()-1;
        }
        if( position.toLowerCase().equals("_addfirst") ) {
            list.add(0, new DefaultItem(list.get(0).getType()));
            index = 0;
        }
        if( position.toLowerCase().equals("_addlast") ) {
            list.add(new DefaultItem(list.get(0).getType()));
            index = list.size()-1;
        }
        if (position.toLowerCase().startsWith("_select[")) {
            if (!position.toLowerCase().endsWith("]")) {
                System.err.println("Problems parsing list-index (missing parenthesis): " + path);
            }
            String[] selector = position.substring(8,position.length()-1).split("=");
            
            if ((selector.length != 2 ) || (selector[0].length()==0) || (selector[1].length()==0)) {
                System.err.println(position.substring(8,position.length()-1));
                System.err.println("Problems parsing list-index (selector not correct): " + path);
            }
            String sub = selector[0];
            String val = selector[1];
            for (int listIndex = 0; listIndex < list.size(); listIndex++)
            {
                //System.out.println(sub+","+val+","+list.get(listIndex).getValueOfPath(sub).getValue().toString()+",");
                if (list.get(listIndex).getValueOfPath(sub).getValue().toString().equals(val)) index = listIndex;
            }
        }
        try {
            int parseIndex = Integer.parseInt(position);
            index = parseIndex;
        }catch( NumberFormatException e ){}
        if (index <0)
        {
        	return;
        }
        Item i = list.get(index);
        if( i == null ) {
            // Done, nothing to remove.
            return;
        } else if( i.getType() == Item.Type.List ) {
            // If we want to remove a List item entirely, we do that here instead of passing it on to the inner Record
            if( valuePath == null)
            {
              list.remove(index);
            }
            else
            {
              i.getRecord().remove(valuePath);
            }
        } else if( i.getType() == Item.Type.Record ) {
            // If we want to remove a List item entirely, we do that here instead of passing it on to the inner Record
            if( valuePath == null)
            {
              list.remove(index);
            }
            else
            {
              i.getRecord().remove(valuePath);
            }
        } else {
            list.remove(index);
        }
    }

    /**
     * Returns the size of this list
     * @return size of the list
     */
    public int size()
    {
        return list.size();
    }

    @Override
    public String toString(){
    	return toStringHelper("");
    }
    
    /**
     * Returns this List as a textual representation, with the given String put before every line.
     * 
     * @param pre - the String to put in front of each line
     */
    public String toStringHelper( String pre )
    {
    	String ret = "";
        for( Item i : list ) {
            if( i.getType() == Item.Type.String || i.getType() == Item.Type.Integer || i.getType() == Item.Type.Double ) {
                ret += pre + "-" + i.getValue().toString() + "\n";
            } else if( i.getType() == Item.Type.Record ) {
                ret += pre + "-Record [" + "\n";
                ret += ((DefaultRecord)i.getRecord()).toStringHelper(pre + "  ");
            } else if( i.getType() == Item.Type.List ) {
                ret += pre + "-List [" + "\n";
                ret += ((DefaultList)i.getList()).toStringHelper(pre + "  ");
            }
        }
    	return ret;
    }

    /**
     * Checks if the list contains the Object o (this Object can be a String, Integer, or Double).
     * 
     * @param o - the value the list should contain
     * @return true if the list contains the value, false if it does not
     */
    public boolean contains( Object o )
    {
        Item item = new DefaultItem(o);
        if( list.get(0).getType() != item.getType() ) {
            return false;
        } else {
            for( Item listItem : list ) {
                if( listItem.getType() == Item.Type.String ) {
                    if( listItem.getString().equals(item.getString()) ) {
                        return true;
                    }
                } else if( listItem.getType() == Item.Type.Integer ) {
                    if( listItem.getInteger().equals(item.getInteger()) ) {
                        return true;
                    }
                } else if( listItem.getType() == Item.Type.Double ) {
                    if( listItem.getDouble().equals(item.getDouble()) ) {
                        return true;
                    }
                } 
            }
            return false;
        }
    }

    /**
     * Checks if the list does not contain the Object o (this Object can be a String, Integer, or Double).
     * 
     * @param o - the value the list should not contain
     * @return true if the list does not contain the value, false if it does or if there is a type-mismatch
     */
    public boolean notContains( Object o )
    {
        Item item = new DefaultItem(o);
        if( list.get(0).getType() != item.getType() ) {
            return false;
        } else {
            boolean notContains = true;
            for( Item listItem : list ) {
                if( listItem.getType() == Item.Type.String ) {
                    if( listItem.getString().equals(item.getString()) ) {
                        notContains = false;
                    }
                } else if( listItem.getType() == Item.Type.Integer ) {
                    if( listItem.getInteger().equals(item.getInteger()) ) {
                        notContains = false;
                    }
                } else if( listItem.getType() == Item.Type.Double ) {
                    if( listItem.getDouble().equals(item.getDouble()) ) {
                        notContains = false;
                    }
                }
            }
            return notContains;
        }
    }

    public ArrayList<Item> getFullList()
    {
        return list;
    }
}
