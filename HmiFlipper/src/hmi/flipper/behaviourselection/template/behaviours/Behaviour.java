/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.behaviours;

import hmi.flipper.behaviourselection.behaviours.BehaviourClass;
import hmi.flipper.behaviourselection.template.Template;
import hmi.flipper.behaviourselection.template.value.AbstractValue;
import hmi.flipper.behaviourselection.template.value.Value;
import hmi.flipper.behaviourselection.template.value.Value.ExportType;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.exceptions.TemplateRunException;
import hmi.flipper.informationstate.Record;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 * This class contains information about a Behaviour.
 * 
 * @author Mark ter Maat
 * @version 0.1
 *
 */

public class Behaviour
{
    /* The quality-value of this Behaviour */
    private double quality;

    /* The BehaviourClass that is used for this Behaviour */
    private BehaviourClass behaviour;
    /* The argument-names and values to supply the BehaviourClass when called */
    private ArrayList<String> argNames = new ArrayList<String>();
    private ArrayList<AbstractValue> argValues = new ArrayList<AbstractValue>();


    /**
     * Creates a new Behaviour with the given BehaviourClass-name and quality-value.
     * 
     * @param behaviourClassName - the name of the corresponding BehaviourClass
     * @param quality - the quality-value of this Behaviour
     * @throws TemplateParseException
     */
    public Behaviour( String behaviourClassName, String quality ) throws TemplateParseException
    {
        behaviour = BehaviourClassProvider.getBehaviourClass(behaviourClassName);
        try {
            this.quality = Double.parseDouble(quality);
        }catch(NumberFormatException e) {
            throw new TemplateParseException("Quality-number '"+quality+"' could not be parsed to a Double.");
        }
    }

    /**
     * Adds an argument with the given name and value to the list of arguments of this Behaviour.
     * 
     * @param name - the name of the new argument
     * @param value - the value of the new argument
     * @param exportType - the type of the value to which it should be exported (default is to parse it as a Value at runtime)
     * @throws TemplateParseException
     */
    public void addArg( String name, String value, String exportType ) throws TemplateParseException
    {
        argNames.add(name);
        ExportType et = ExportType.Default;
        if(exportType.equals(Template.EXPORTTYPE_RAWVALUE)){
        	et = ExportType.RawValue;
        } else if(exportType.equals(Template.EXPORTTYPE_JSON)) {
        	et = ExportType.JSON;
        }
        
        argValues.add(new AbstractValue(value, et));
    }
    
    /**
     * Given the name of the BehaviourClass, tries to find the Class-file of this class, and returns a new instance.
     * 
     * @param name - the name of the BehaviourClass to find.
     * @return a new instance of this BehaviourClass.<
     * @throws TemplateParseException
     */
    public BehaviourClass getBehaviourClass( String name ) throws TemplateParseException
    {
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

        return bc;
    }

    /**
     * Given the current InformationState, calculate the current values of the arguments, and call
     * the execute() method of the BehaviourClass with the list of arguments.
     * 
     * @param is - the current InformationState
     * @throws TemplateRunException
     */
    public void execute( Record is ) throws TemplateRunException
    {
        ArrayList<Value> values = prepareValues(is, argValues);
        behaviour.execute(new ArrayList<String>(argNames), new ArrayList<Value>(values));
    }

    /**
     * Given the current InformationState, calculate the current values of the arguments, and call
     * the prepare() method of the BehaviourClass with the list of arguments.
     * 
     * @param is - the current InformationState
     * @throws TemplateRunException
     */
    public void prepare( Record is ) throws TemplateRunException
    {
        ArrayList<Value> values = prepareValues(is, argValues);
        behaviour.prepare(new ArrayList<String>(argNames), new ArrayList<Value>(values));
    }

    /**
     * Helper method for preparing the various values to be passed on to the behaviour. 
     * @param is the current informationstate
     * @param values the array of values to prepare
     * @return an array of prepared values, according to the provided IS
     * @throws TemplateRunException
     */
    private ArrayList<Value> prepareValues(Record is, ArrayList<AbstractValue> values) throws TemplateRunException{
    	ArrayList<Value> retVals = new ArrayList<Value>();

        for( AbstractValue av : values) {
            retVals.add(av.getValue(is));
        }
    	
    	return retVals;
    }
    
    /**
     * Returns the quality-value of this Behaviour
     * @return quality
     */
    public double getQuality()
    {
        return quality;
    }

    /**
     * Given the DOM Behaviour-Element, returns the Behaviour that fits the XML.
     * 
     * @param behaviourElement - the DOM Element of this Behaviour
     * @return the Behaviour
     * @throws TemplateParseException
     */
    public static Behaviour parseBehaviour( Element behaviourElement, boolean inChoiceElement ) throws TemplateParseException
    {
        if( !behaviourElement.hasAttribute(Template.A_BEHAVIOURCLASS) ) {
            throw new TemplateParseException("Missing Behaviourclass in Behaviour-Element.");
        }
        String behaviourClass = behaviourElement.getAttribute(Template.A_BEHAVIOURCLASS);
        String quality = behaviourElement.getAttribute(Template.A_QUALITY);
        if( quality.length() == 0 ) {
            quality = "0.5";
        }
        Behaviour behaviour = new Behaviour(behaviourClass,quality);

        NodeList argList;
        if(inChoiceElement) {
            argList = behaviourElement.getElementsByTagName(Template.E_CARGUMENT);
        } else {
            argList = behaviourElement.getElementsByTagName(Template.E_ARGUMENT);
        }
        for( int i=0; i<argList.getLength(); i++ ) {
            Element argElement = (Element)argList.item(i);
            if( !argElement.hasAttribute(Template.A_ARGUMENTNAME) && !argElement.hasAttribute(Template.A_ARGUMENTVALUE) ) {
                throw new TemplateParseException("Missing Name or Value attribute in Behaviour-argument.");
            }
            
            String exportType = Template.EXPORTTYPE_DEFAULT;
            if( argElement.hasAttribute(Template.A_ARGUMENTEXPORT)) {
            	exportType = argElement.getAttribute(Template.A_ARGUMENTEXPORT);
        	}
            behaviour.addArg(argElement.getAttribute(Template.A_ARGUMENTNAME), argElement.getAttribute(Template.A_ARGUMENTVALUE), exportType);
        }
        
        //parse the optional "xmlcontent" blocks, which can contain a CDATA section of any inner XML to be used in the behaviour
        //the contents of this block are passed to the implementing Behaviour class as a regular argument-value using the defined name.
        NodeList xmlContentList = behaviourElement.getElementsByTagName(Template.E_XMLCONTENT);
        for(int i=0; i<xmlContentList.getLength(); i++ ){
        	Element xmlContentElement = (Element)xmlContentList.item(i);
            if( !xmlContentElement.hasAttribute(Template.A_ARGUMENTNAME)){
                throw new TemplateParseException("Missing Name attribute in Behaviour-XMLContent.");
            }
            String name = xmlContentElement.getAttribute(Template.A_ARGUMENTNAME);
            String xmlContent = xmlContentElement.getTextContent();
            behaviour.addArg(name, xmlContent, Template.EXPORTTYPE_RAWVALUE);
        }
        
        return behaviour;
    }
}
