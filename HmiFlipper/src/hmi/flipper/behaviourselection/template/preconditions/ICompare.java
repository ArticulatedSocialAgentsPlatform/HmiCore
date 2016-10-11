/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.preconditions;

import org.w3c.dom.Element;



import hmi.flipper.behaviourselection.template.Template;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.informationstate.Record;

/**
 * An ICompare is a Compare that is used in an Indicator. It contains a normal Compare, and a modifier (which modifiers the Indicator's proof-value). 
 * 
 * @author Mark ter Maat
 * @version 0.1
 *
 */

public class ICompare
{
    /* The Compare of this Indicator's ICompare */
    private Precondition compare;

    /* The modifier that modifies the Indicator's proof-value when this Compare is valid. */
    private String modifier;

    /**
     * Creates a new ICompare with the given Compare and the given modifier.
     * 
     * @param compare
     * @param modifier
     */
    public ICompare( Precondition compare, String modifier )
    {
        this.compare = compare;
        this.modifier = modifier;
    }

    /**
     * Checks the Compare to check if this ICompare is valid.
     * 
     * @param is
     * @return
     */
    public boolean isValid( Record is )
    {
        return compare.isValid(is);
    }

    /**
     * Returns the modifier.
     * @return
     */
    public String getModifier()
    {
        return modifier;
    }

    /**
     * Given the DOM ICompare-Element, returns the ICompare that fits the XML.
     * This class creates a new ICompare and returns this.
     * 
     * @param icompareElement - the DOM Element of this ICompare
     * @return the ICompare of the Indicator
     * @throws TemplateParseException
     */
    public static ICompare parseICompare( Element icompareElement ) throws TemplateParseException
    {
        Precondition compare = Compare.parsePrecondition(icompareElement);
        if( icompareElement.hasAttribute(Template.A_MODIFIER) ) {
            return new ICompare(compare, icompareElement.getAttribute(Template.A_MODIFIER));
        } else {
            throw new TemplateParseException("Missing modifier-attribute of Indicator-Compare.");
        }
    }
}
