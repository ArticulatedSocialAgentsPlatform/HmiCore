/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.value;

import hmi.flipper.behaviourselection.template.Template;
import hmi.flipper.behaviourselection.template.value.Value.ExportType;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.exceptions.TemplateRunException;
import hmi.flipper.informationstate.Item;
import hmi.flipper.informationstate.Record;

/**
 * An AbstractValue is a description of a value (the value-string from the XML), rather than the value itself.
 * If this is an atomic value (an Integer, a Double or a String), then the real Value can be calculated immediately.
 * However, if it is a reference to an InformationState variable, then the real Value can only be determined
 * at runtime, using the current version of the InformationState.
 * 
 * Therefore, use the function getValue() to get the real Value of this AbstractValue.
 * 
 * @version 0.1.1
 * Added support for more elaborate Strings (with spaces, and the following characters ,.?!_
 * 
 * @version 0.1
 * Basic version
 *
 * @author Mark ter Maat
 */

public class AbstractValue
{
	boolean parseValue = true;
    /* A valueString can be 1 value, or 2 values combined with an arithmetic sign (+-/*). This int keeps track of the number of components (1 or 2) */
    int numberOfComponents;

    /* The string-representation of the first value, and the real Value (NULL if the string is an IS-reference) */
    private String valueString;
    private Value value;
    /* The string-representation of the second value (if existing), and the real Value (NULL if the string is an IS-reference) */
    private String valueString2;
    private Value value2;

    /* If there are 2 components, this stores the mathematical operator (+-/*) */
    private String operator;
	private ExportType exportType;

    public AbstractValue( String valueString) throws TemplateParseException
    {
    	this(valueString, Value.ExportType.Default);
    }
    
    /**
     * Creates a new AbstractValue given a String-representation of the value.
     * 
     * @param valueString - the String-representation of the value
     * @param exportType - how should this value be exported, default is as a regular Value
     * @throws TemplateParseException
     */
    public AbstractValue( String valueString, ExportType exportType) throws TemplateParseException
    {
    	this.exportType = exportType;
    	parseValue = exportType != Value.ExportType.RawValue;
    	if (!parseValue)
    	{
            this.valueString = valueString;
            value = getStaticValue( valueString );
            numberOfComponents = 1;
    		return;
    	}
        /* Check if the string is only 1 component, or 2 components divided by an arithmetic sign */
        if(valueString.length() > 0 && (valueString.contains("+") 
                || valueString.substring(1).contains("-") || valueString.contains("/") || valueString.contains("*")) ) {
            /* Set the arithmetic sign */
            if( valueString.contains("+") ) {
                operator = "+";
            } else if( valueString.contains("-") ) {
                operator = "-";
            } else if( valueString.contains("/") ) {
                operator = "/";
            } else {
                operator = "*";
            }

            int operatorIndex = valueString.indexOf(operator);
            String tempV1 = valueString.substring(0,operatorIndex).trim();
            String tempV2 = valueString.substring(operatorIndex+1).trim();

            if( tempV1 == null ){ 
                throw new TemplateParseException("Empty valueString (first)");
            }
            if( tempV2 == null ){ 
                throw new TemplateParseException("Empty valueString (second)");
            }

            /* Check if neither component is a reference to the InformationState */
            if( !tempV1.contains("$") && !tempV2.contains("$") ) {
                /* Calculate the atomic values and combine them */
                Value v1 = getStaticValue(valueString.substring(0, valueString.indexOf(operator)).replaceAll(" ", ""));
                Value v2 = getStaticValue(valueString.substring(valueString.indexOf(operator)+1).replaceAll(" ", ""));

                this.valueString = valueString;
                numberOfComponents = 1;

                /* Calculate the new value */
                value = calcValues(v1,v2);
            } else {
                /* Calculate the atomic value if 1 component is not an IS-reference */
                this.valueString = valueString.substring(0, valueString.indexOf(operator)).replaceAll(" ", "");
                if( !this.valueString.contains("$") ) {     
                    value = getStaticValue( this.valueString );
                }
                this.valueString2 = valueString.substring(valueString.indexOf(operator)+1).replaceAll(" ", "");
                if( !this.valueString2.contains("$") ) {
                    value2 = getStaticValue( this.valueString2 );
                }
                numberOfComponents = 2;
            }
        } else {
            //this.valueString = valueString.replaceAll(" ", "");
            this.valueString = valueString;
            if( !valueString.contains("$") ) {
                value = getStaticValue( valueString );
            }
            numberOfComponents = 1;
        }
    }

    /**
     * If the value is atomic, calculate the Value and return it.
     * 
     * @param str - the String-representation of the Value
     * @return the Value
     */
    public Value getStaticValue( String str )
    {
        if( str.length() == 0 ) return new Value("");

        /* First, try to determine the type of the value. */
        boolean isInt = true;
        boolean isDouble = true;
        boolean isString = true;
        if( !str.contains(".") ) {
            isDouble = false;
        }

        /* Check the string for characters that rule out a certain type */
        for( int i=0; i<str.length(); i++ ) {
            Character c = str.charAt(i);
            if( i == 0 ) {
                if( !Character.isLetter(c) && c != '!' && c != '?' && c != '#' ) {
                    isString = false;
                }
                if( !Character.isDigit(c) && c != '-' ) {
                    isInt = false;
                }
                if( !Character.isDigit(c) && c != '.' && c != '-' ) {
                    isDouble = false;
                }
                if( !Character.isDigit(c) && !Character.isLetter(c) && c != '_' && c != ' ' && c != '.' && c != ',' && c != '!' && c != '?' && c != '{' && c != '}' && c != '[' && c != ']' ) {
                    isString = false;
                }
            } else {
                if( !Character.isDigit(c) ) {
                    isInt = false;
                }
                if( !Character.isDigit(c) && c != '.' ) {
                    isDouble = false;
                }
                if( !Character.isDigit(c) && !Character.isLetter(c) && c != '_' && c != ' ' && c != '.' && c != ',' && c != '!' && c != '?' && c != '{' && c != '}' && c != '[' && c != ']' ) {
                    isString = false;
                }
            }
        }

        /* Depending on the type of the value, create a new Value */
        if( isInt && !isDouble && !isString ) {
            return new Value( Integer.parseInt(str) );
        } else if( isDouble && !isInt && !isString ) {
            return new Value( Double.parseDouble(str) );
        } else if(!isInt && !isDouble ) {
            return new Value(str);
        }
        return null;
    }

    /**
     * Using the given String-representation of the value and the current InformationState, calculate the
     * current Value
     * 
     * @param str - the String-representation of the value
     * @param is - the current InformationState
     * @return Value
     * @throws TemplateRunException
     */
    public Value getDynamicValue( String str, Record is ) throws TemplateRunException
    {
        /* Determine the start-index and the end-index of the IS-reference, and create the IS-path */
        int startindex = str.indexOf("$");
        if( startindex == -1 ) {
            throw new TemplateRunException("Missing $-sign in referenced value ("+str+").");
        }
        int endindex = -1;
        for( int i=startindex+1; i<str.length(); i++ ) {
            char ch = str.charAt(i);
            if( !(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_' || ch == '.' || ch == '='|| ch == ':' || ch == '[' || ch == ']') ) {
                endindex = i;
            }
        }
        if( endindex == -1 ) {
            endindex = str.length();
        }
        String path = str.substring(startindex,endindex);

        /* Use the reference-path, get the InformationState-Item of the corresponding IS-variable */
        Item item = is.getValueOfPath(path, is);
        if( item == null ) {
            return null;
        } else {
        	Value v = new Value(item);
        	v.setExportType(exportType);
            return v;
        }
    }

    /**
     * Based on the current InformationState, calculate the current Value of this AbstractValue and return it.
     * 
     * @param is - the current InformationState
     * @return Value
     * @throws TemplateRunException
     */
    public Value getValue( Record is ) throws TemplateRunException
    {
        if( numberOfComponents == 1 ) {
        	if (!parseValue ) return new Value(valueString);
            /* If there is only 1 component, check if the Value has been already calculated (atomic variable). 
             * If not, then get the variable from the IS. */
            if( value != null ) {
                return value;
            } else {
                return getDynamicValue(valueString,is);
            }
        } else if( numberOfComponents == 2 ) {
            /* If there are 2 components, calculate the Value of both components, and combine them */
            /* Get the first Value */
            Value v1;
            if( value != null ) {
                v1 = value;
            } else {
                v1 = getDynamicValue(valueString,is);
            }

            /* Get the second value */
            Value v2;
            if( value2 != null ) {
                v2 = value2;
            } else {
                v2 = getDynamicValue(valueString2,is);
            }

            /* Combine the Values */
            if( v1 == null || v2 == null ) {
                return null;
            }
            try {
                return calcValues(v1, v2);
            }catch( TemplateParseException e ) {
                throw new TemplateRunException("v1_static:"+(value!=null)+",v2_static:"+(value2!=null)+e.getMessage());
            }
        } else {
            throw new TemplateRunException("Illegal number of arithmetic operations.");
        }	
    }

    /**
     * Combine the 2 given Values into 1 Value, depending on the types of the 2 Values
     * 
     * @param v1 - the first Value
     * @param v2 - the second Value
     * @return Value
     * @throws TemplateParseException
     */
    public Value calcValues( Value v1, Value v2 ) throws TemplateParseException
    {
        Value v = null;
        if( v1.getType() == Value.Type.String && v2.getType() == Value.Type.String ) {
            v = new Value(v1.getStringValue() + v2.getStringValue());
        } else if( v1.getType() == Value.Type.String && v2.getType() == Value.Type.Integer  ) {
            v = new Value(v1.getStringValue() + v2.getIntegerValue().toString());
        } else if( v1.getType() == Value.Type.String && v2.getType() == Value.Type.Double  ) {
            v = new Value(v1.getStringValue() + v2.getDoubleValue().toString());
        } else if( v1.getType() == Value.Type.Double && v2.getType() == Value.Type.Double ) {
            v = new Value(calc(v1.getDoubleValue(),v2.getDoubleValue(),operator));
        } else if( v1.getType() == Value.Type.Double && v2.getType() == Value.Type.Integer ) {
            v = new Value(calc(v1.getDoubleValue(),v2.getIntegerValue(),operator));
        } else if( v1.getType() == Value.Type.Integer && v2.getType() == Value.Type.Double ) {
            v = new Value(calc(v1.getIntegerValue(),v2.getDoubleValue(),operator));
        } else if( v1.getType() == Value.Type.Integer && v2.getType() == Value.Type.Integer ) {
            if( operator.equals("/") ) {
                v = new Value(calcDivision(v1.getIntegerValue(),v2.getIntegerValue()));
            } else {
                v = new Value(calc(v1.getIntegerValue(),v2.getIntegerValue(),operator));
            }
        } else {
            throw new TemplateParseException("Unable to calculate the 2 values '"+v1.toString()+"' ("+v1.getType()+") and '"+v2.toString()+"' ("+v2.getType()+").");
        }
        return v;
    }

    /**
     * Uses the operator to calculate 2 integers.
     * 
     * @param i1 - integer 1
     * @param i2 - integer 2
     * @param operator - the arithmetic operator
     * @return the resulting integer
     * @throws TemplateParseException
     */
    public Integer calc( Integer i1, Integer i2, String operator ) throws TemplateParseException
    {
        return calc(i1.doubleValue(),i2.doubleValue(),operator).intValue();
    }

    /**
     * Uses the operator to calculate a double and an integer.
     * 
     * @param i1 - double 1
     * @param i2 - integer 1
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    public Double calc( Double d1, Integer i1, String operator ) throws TemplateParseException
    {
        return calc(d1,i1.doubleValue(),operator);
    }

    /**
     * Uses the operator to calculate an integer and a double.
     * 
     * @param i1 - ingteger 1
     * @param i2 - double 2
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    public Double calc( Integer i1, Double d1, String operator ) throws TemplateParseException
    {
        return calc(i1.doubleValue(),d1,operator);
    }

    /**
     * Uses the operator to calculate 2 doubles.
     * 
     * @param i1 - double 1
     * @param i2 - double 2
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    public Double calc( Double d1, Double d2, String operator ) throws TemplateParseException
    {
        if( operator.equals("+") ) {
            return d1+d2;
        } else if( operator.equals("-") ) {
            return d1-d2;
        } else if( operator.equals("*") ) {
            return d1*d2;
        } else if( operator.equals("/") ) {
            return d1/d2;
        } else {
            throw new TemplateParseException("Illegal arithmetic operation.");
        }
    }

    /**
     * Calculates a division of 2 integers.
     * this is a separate method because this is the only case in which the calculation of 2 integers result in a double.
     * 
     * @param i1 - integer 1
     * @param i2 - integer 2
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    public Double calcDivision( Integer i1, Integer i2 )
    {
        return i1.doubleValue()/i2.doubleValue();
    }
}
