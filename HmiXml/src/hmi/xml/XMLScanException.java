/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.xml;

/**
 * XMLScanException is a RuntimeException that is thrown when a XML input that is not lexically
 * well-formed is scanned, or if an unexpected End-Of-Data is reached.
 * 
 * @author Job Zwiers
 */
public class XMLScanException extends java.lang.RuntimeException
{

    /**
     * default constructor for XMLScanExceptions.
     */
    public XMLScanException()
    {
        super();
    }

    /**
     * XMLScanExceptions with a String text attribute.
     */
    public XMLScanException(String s)
    {
        super(s);
    }

    /**
     * XMLScanExceptions with a String text attribute.
     */
    public XMLScanException(String s, Throwable ex)
    {
        super(s,ex);
    }
    
    /**
     * XMLScanExceptions with a String text attribute.
     */
    public XMLScanException(String s, String file, String url, int tokenLine, int tokenCharPos,
            int line, int charPos)
    {
        super(s);
        this.file = file;
        this.url = url;
        this.tokenLine = tokenLine;
        this.tokenCharPos = tokenCharPos;
        this.line = line;
        this.charPos = charPos;

    }
    
    /**
     * XMLScanExceptions with a String text attribute, caused by sourceEx
     */
    public XMLScanException(String s, String file, String url, int tokenLine, int tokenCharPos,
            int line, int charPos, Exception sourceEx)
    {
        this(s,file,url,tokenLine,tokenCharPos,line,charPos);
        initCause(sourceEx);
    }

    /**
     * produces the error message
     */
    @Override
    public String toString()
    {
        return "XMLScanException: " + getMessage();
    }

    /**
     * returns the current line number; line counts start at 1.
     */
    public final int getLine()
    {
        return line;
    }

    /**
     * returns the current character position within the current line. character counts start at 1
     * for the first character within the line. After a \n character, the character position is 0.
     * 
     */
    public final int getCharPos()
    {
        return charPos;
    }

    /**
     * returns the starting line number of the start of the the current token. line counting starts
     * at 1.
     */
    public final int getTokenLine()
    {
        return tokenLine;
    }

    /**
     * returns the character position of the start of the current token.
     */
    public final int getTokenCharPos()
    {
        return tokenCharPos;
    }
    
    private int line;

    private int tokenLine;

    private int charPos;

    private int tokenCharPos;

    

    private String url;

    public String getUrl()
    {
        return url;
    }

    public String getFile()
    {
        return file;
    }

    private String file;

    private static final long serialVersionUID = 0L;

}
