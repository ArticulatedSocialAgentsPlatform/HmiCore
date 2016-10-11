/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.behaviours;

import hmi.flipper.behaviourselection.template.value.Value;

import java.util.ArrayList;

/**
 * The Interface to use when writing a custom BehaviourClass.
 * 
 * @author Mark ter Maat
 *
 */
public interface BehaviourClass
{
    /**
     * This method is called when a behaviour is selected to be executed.
     * @param argNames The names of the given arguments.
     * @param argValues The values of the given arguments.
     */
    void execute( ArrayList<String> argNames, ArrayList<Value> argValues );
    
    /**
     * This method is called when a behaviour is selected to be prepared.
     * This happens when all preconditions except the Triggers are met.
     * @param argNames The names of the given arguments.
     * @param argValues The values of the given arguments.
     */
    void prepare( ArrayList<String> argNames, ArrayList<Value> argValues );
}
