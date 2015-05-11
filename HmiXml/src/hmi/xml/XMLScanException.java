/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

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
