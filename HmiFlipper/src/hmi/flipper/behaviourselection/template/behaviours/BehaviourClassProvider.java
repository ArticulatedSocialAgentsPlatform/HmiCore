/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.behaviours;

import hmi.flipper.behaviourselection.behaviours.BehaviourClass;
import hmi.flipper.exceptions.TemplateParseException;

import java.util.HashMap;

/**
 *
 * A provider for BehaviourClasses
 * @author Mark ter Maat
 *
 */
public class BehaviourClassProvider
{
    public HashMap<String,BehaviourClass> instantiations = new HashMap<String,BehaviourClass>();
    private static BehaviourClassProvider me;

    public BehaviourClassProvider()
    {
        me = this;
    }

    public static BehaviourClass getBehaviourClass( String name ) throws TemplateParseException
    {
        if( me == null ) {
            new BehaviourClassProvider();
        }
        if( me.instantiations.get(name) != null ) {
            return me.instantiations.get(name);
        }
        Class<?> c;
        BehaviourClass bc = null;

        /* Searches for a Class with the given name */
        try {
            c = Class.forName(name);
        }catch( ClassNotFoundException e) {
            throw new TemplateParseException("Class '"+name+"' not found.");
        }

        /* If the Class-file exists, create a new Instance of this class */
        try {
            bc = (BehaviourClass)c.newInstance();
        }catch( InstantiationException e ) {
            throw new TemplateParseException("Class '"+name+"' could not be instantiated.");
        } catch( IllegalAccessException e ) {
            throw new TemplateParseException("Class '"+name+"' could not be instantiated.");
        }

        me.instantiations.put(name, bc);
        return bc;
    }
}
