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
 * A CompareExists checks if a value currently exists in the InformationState.
 * 
 * @author Mark ter Maat
 * @version 0.1
 *
 */

public class CompareExists extends Compare
{
    /**
     * Creates a new CompareEquals with the given value.
     * 
     * @param value1
     * @throws TemplateParseException
     */
    public CompareExists( String value1 ) throws TemplateParseException
    {
        super( value1, null, Compare.Comparator.exists );
    }

    /**
     * Given the current InformationState, checks if the value exists.
     * 
     * @param is - the current InformationState
     * @returns true  - if the value exists
     *          false - if the value does not exist
     */
    public boolean isValid( Record is )
    {
        /* Get the current Values of the 2 values. */
        Value value;
        try {
            value = abstractValue1.getValue(is);
        } catch( TemplateRunException e ) {
            e.printStackTrace();
            return false;
        }

        /* If the Value is not null, than it exists. */
        return value != null;
    }
}
