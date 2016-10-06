/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.effects;


import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.exceptions.TemplateRunException;
import hmi.flipper.informationstate.Record;

/**
 * This is an abstract class of an Effect. Depending on what kind of effect is required, a different subclass should be used.
 * 
 * @author Mark ter Maat
 * @version 0.1
 *
 */

public abstract class Effect
{
    /**
     * Given the current InformationState, and the TemplateController, apply the effects.
     * 
     * @param is
     * @param controller
     * @throws TemplateRunException
     */
    public abstract void apply( Record is, TemplateController controller ) throws TemplateRunException;
}
