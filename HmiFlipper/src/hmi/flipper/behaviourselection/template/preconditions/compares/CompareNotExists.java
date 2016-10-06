/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.preconditions.compares;

import hmi.flipper.behaviourselection.template.preconditions.Compare;
import hmi.flipper.behaviourselection.template.value.Value;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.exceptions.TemplateRunException;
import hmi.flipper.informationstate.Record;

/**
 * A CompareNotExists checks if a value currently does not exist in the InformationState.
 * 
 * @author Mark ter Maat
 * @version 0.1
 *
 */

public class CompareNotExists extends Compare
{
    /**
     * Creates a new CompareNotEquals with the given value.
     * 
     * @param value1
     * @throws TemplateParseException
     */
    public CompareNotExists( String value1 ) throws TemplateParseException
    {
        super( value1, null, Compare.Comparator.not_exists );
    }

    /**
     * Given the current InformationState, checks if the value does not exists.
     * 
     * @param is - the current InformationState
     * @returns true  - if the value does not exist
     *          false - if the value exists
     */
    public boolean isValid( Record is )
    {
        /* Get the current Values of the 2 values. */
        Value value;
        try {
            value = abstractValue1.getValue(is);
        } catch( TemplateRunException e ) {
            e.printStackTrace();
            return true;
        }

        /* If the Value is not null, than it exists. */
        return value == null;
    }
}
