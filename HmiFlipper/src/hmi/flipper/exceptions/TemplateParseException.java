/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.exceptions;

/**
 * This class is used for exceptions that arise during the parsing of Templates
 * 
 * @author Mark ter Maat
 * @version 0.1
 */

public class TemplateParseException extends Exception
{
    public String fileName;
    public String templateName;
    public String functionName;

    /**
     * Creates a new TemplateParseException
     */
    public TemplateParseException()
    {
        super();
    }

    public TemplateParseException( String err, String fileName, String templateName, String functionName )
    {
        super(err);
        this.fileName = fileName;
        this.templateName = templateName;
        this.functionName = functionName;
    }

    /**
     * Creates a new TemplateParseException with an error-message.
     * @param err - the error-message
     */
    public TemplateParseException( String err )
    {
        super(err);
    }
}
