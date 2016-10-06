/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.preconditions.compares;

import hmi.flipper.behaviourselection.template.preconditions.Compare;
import hmi.flipper.behaviourselection.template.value.Value;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.exceptions.TemplateRunException;
import hmi.flipper.informationstate.List;
import hmi.flipper.informationstate.Record;

/**
 * A CompareNotContains checks if the first value (which has to be a list) does not contain the second value.
 * Returns true if the list does not contain the value, or false if the list does contain the value or if the types do not match.
 * 
 * @author Mark ter Maat
 * @version 0.1
 *
 */

public class CompareNotContains extends Compare 
{
    /**
     * Creates a new CompareNotContains with the given values.
     * 
     * @param value1 - should be a list in the InformationState
     * @param value2
     * @throws TemplateParseException
     */
    public CompareNotContains( String value1, String value2 ) throws TemplateParseException
    {
        super(value1, value2, Compare.Comparator.not_contains);
    }

    /**
     * Given the current InformationState, checks if the first value is a list, and if 
     * it does not contain the second value.
     * 
     * @param is - the current InformationState
     * @returns true  - if the second value does not exists in the list
     *          false - if the second value exists in the list, or if the types are incomparable 
     */
    public boolean isValid( Record is )
    {
        /* Get the current Values of the 2 values. */
        Value value1, value2;
        try {
            value1 = abstractValue1.getValue(is);
            value2 = abstractValue2.getValue(is);
        } catch( TemplateRunException e ) {
            e.printStackTrace();
            return false;
        }

        /* Checks if the first value is a list */
        if( value1 != null && value2 != null && value1.getType() == Value.Type.List ) {
            List list = value1.getListValue();

            /* Based on the type, checks if the list does not contain the required value */
            if( value2.getType() == Value.Type.String ) {
                return list.notContains(value2.getStringValue());
            } else if( value2.getType() == Value.Type.Integer ) {
                return list.notContains(value2.getIntegerValue());
            } else if( value2.getType() == Value.Type.Double ) {
                return list.notContains(value2.getDoubleValue());
            } else {
                /* The types of the list and the value are incomparable */
                return false;
            }
        } else {
            /* The first value is not a list */
            return false;
        }
    }
}
