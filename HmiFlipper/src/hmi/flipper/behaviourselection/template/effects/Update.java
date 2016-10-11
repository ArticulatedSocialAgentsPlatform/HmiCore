/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.effects;

import org.w3c.dom.Element;

import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.behaviourselection.template.Template;
import hmi.flipper.behaviourselection.template.value.AbstractValue;
import hmi.flipper.behaviourselection.template.value.Value;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.exceptions.TemplateRunException;
import hmi.flipper.informationstate.Item;
import hmi.flipper.informationstate.Record;

/**
 * An Update is a type of Effect. When applied, it will modify the a value in InformationState.
 * 
 * @version 0.1.2
 * Fixed some bugs with relation to the new set-methods of InformationState
 * 
 * @version 0.1.1
 * Added support for the removal of IS-elements.
 * 
 * @version 0.1
 * First version
 * 
 * @author Mark ter Maat
 *
 */

public class Update extends Effect
{
    /* The name of the IS-value to modify. */
    private String name;
    /* The new value */
    private AbstractValue abstractValue;

    /**
     * Creates a new Update with the given variable name and value.
     * 
     * @param name - the name of the IS-value to modify.
     * @param valueString - the new value of the IS-value
     * @throws TemplateParseException
     */
    public Update( String name, String valueString ) throws TemplateParseException
    {
        this.name = name;
        if( valueString != null && valueString.length() > 0 ) {
            try {
                this.abstractValue = new AbstractValue(valueString);
            } catch(TemplateParseException e) {
                e.functionName = "Update '" + name + "'";
                throw e;
            }
        } else {
            abstractValue = null;
        }
    }

    /**
     * Given the current InformationState, and the TemplateController, find the value with the correct 
     * name in the IS and modify it accordingly.
     * 
     * @param is - the current InformationState
     * @param controller - the TemplateController than manages the Template containing this Update.
     * @throws TemplateRunException
     */
    public void apply( Record is, TemplateController tc ) throws TemplateRunException
    {
        if( abstractValue == null ) {
            is.remove(name);
        } else {
            /* Calculate the new Value */
            Value value = abstractValue.getValue(is);
            if( value == null ) {
                return;
                //throw new TemplateRunException("Error calculating new Value of '"+name+"'.");
            }

            Item.Type type = is.getTypeOfPath(name, is);
            if( type == null ) {
                if( value.getType() == Value.Type.Double ) {
                    is.set(name, value.getDoubleValue());
                } else if( value.getType() == Value.Type.Integer ) {
                    is.set(name, value.getIntegerValue());
                } else if( value.getType() == Value.Type.String ) {
                    is.set(name, value.getStringValue());
                } else if( value.getType() == Value.Type.Record ) {
                    is.set(name, value.getRecordValue());
                } else if( value.getType() == Value.Type.List ) {
                    is.set(name, value.getListValue());
                }
                return;
            }
            /* Verify that the types of the new Value and the IS-value match */
            if( !type.toString().equals(value.getType().toString()) ) {
                if(!( (type.toString().equals("Integer") && value.getType().toString().equals("Double")) ||
                        (type.toString().equals("Double") && value.getType().toString().equals("Integer")))) {
                    throw new TemplateRunException("Non-matching types of value of '"+name+"'."+type.toString()+" "+value.getType().toString());
                }
            }

            /* Set the new value */
            if( type == Item.Type.String ) {
                is.set(name, value.getStringValue());
                //item.setStringValue(value.getStringValue());
            } else if( type == Item.Type.Double ) {
                if( value.getType() == Value.Type.Double ) {
                    is.set(name, value.getDoubleValue());
                    //item.setDoubleValue(value.getDoubleValue());
                } else if( value.getType() == Value.Type.Integer ) {
                    is.set(name, value.getIntegerValue());
                    //item.setDoubleValue(new Double(value.getIntegerValue()));
                }
            } else if( type == Item.Type.Integer ) {
                if( value.getType() == Value.Type.Integer ) {
                    is.set(name, value.getIntegerValue());
                    //item.setIntegerValue(value.getIntegerValue());
                } else if( value.getType() == Value.Type.Double ) {
                    is.set(name, value.getDoubleValue());
                    //item.setIntegerValue(new Double(value.getDoubleValue()).intValue());
                }
            } else if( type == Item.Type.Record ) {
                is.set(name, value.getRecordValue());
            } else if( type == Item.Type.List ) {
                is.set(name, value.getListValue());
            } else {
                throw new TemplateRunException("Non-atomic value of update.");
            }
        }
    }

    /**
     * Returns the name of variable that has to be changed.
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Given the DOM Update-Element, returns the Effect that fits the XML.
     * This class creates a new Update and returns this.
     * 
     * @param updateElement - the DOM Element of this Update
     * @return the Effect of the Update
     * @throws TemplateParseException
     */
    public static Effect parseUpdate( Element updateElement ) throws TemplateParseException
    {
        if( !updateElement.hasAttribute(Template.A_EFFECTVARNAME) ) {
            throw new TemplateParseException("Missing Name attribute in Update-rule.");
        }
        Update update = new Update( updateElement.getAttribute(Template.A_EFFECTVARNAME), updateElement.getAttribute(Template.A_EFFECTVARVALUE) );
        return update;
    } 
}
