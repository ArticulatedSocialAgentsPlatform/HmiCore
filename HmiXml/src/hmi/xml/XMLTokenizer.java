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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A scanner of XML input streams.
 * <p>
 * An XML scanner enforces only the simple lexical well-formedness constraints of XML 1.0. An XML stream is a sequence of lexical tokens. 
 * These lexical tokens have an external (string) representation, and an internal representation. 
 * The recognized lexical tokens, and their external representations are:
 * <ul>
 * <li>STag : Start tags, of the form &lt;identifier ... &gt; 
 * Here, the "dots" indicate a sequence of Attributes, 
 * externally represented in the form ATTRIBUTENAME = ATTRIBUTEVALUE</li>
 * <li>ETAG : End tags, of the form </identifier></li>
 * <li>CHARDATA : Character data, in the form of character strings, see below.</li>
 * <li>PI : Processing instruction, of the form <?string?></li>
 * <li>DECL : Declarations, of the form <!string> (Special cases are:
 * <ul>
 * <li>comments of the form <!--string-->, where string must not contain --</li>
 * <li>document type declarations: <!DOCTYPE= ..... ></li>
 * </ul>
 * </li>
 * <li>EndOfData : EndOfData is a pseudo token that signals that no more tokens are available.</li>
 * </ul>
 * 
 * identifiers consist exclusively of the following characters: a-zA-Z0-9-_.: 
 * and must start with one of the characters a-zA-Z_
 * <p>
 * A start tag immediately followed by the corresponding end tag can be represented externally 
 * as an "empty tag" of the form: <identifier/>
 * <p>
 * CHARDATA, or "content" is considered to be "parsed character data" in XML terminology, 
 * which means the following:
 * <ul>
 * <li>The external representation is assumed not to contain &apos;<&apos; characters</li>
 * <li>&apos;&&apos; characters are assumed to start an entity reference 
 * of the form: &amp;lt; &amp;gt; &amp;amp; &amp;quot; &amp;apos;</li>
 * </ul>
 * Such entity references are translated to their internal representation: &lt;,&gt;,
 *  &amp;,&quot;, &apos; 
 * <li> The XML standard assumes also that the character sequence ]]>
 * does not occur in character data. 
 * Note that the characters > " ' are not forbidden in character data. 
 * However, the easiest way to translate
 * arbitrary character strings into legal XML character data is to do the following:
 * <ul>
 * <li>replace < by &amp;lt;</li>
 * <li>replace > by &amp;gt; (This automatically takes care of ]]> sequences)</li>
 * <li>replace & by &amp;amp;</li>
 * </ul>
 * <p>
 * The regular expression that describes the possible streams of lexical tokens is: 
 * ( (Stag (AttrName AttrValue)*) | ETAG | CHARDATA | PI | DECL )* EndOfData
 * <p>
 * 
 * @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 */
public class XMLTokenizer
{

    /*
     * initial default values.
     */
    public static final boolean SKIPDOCTYPE = true;
    public static final boolean SKIPCOMMENT = true;
    public static final boolean SKIPPI = true;
    public static final boolean RECOGNIZENAMESPACES = true;
    public static final boolean LOG = false;
    public static final int SECTIONBUFSIZE = 4096; // initial size of section buffer StringBuilder
    public static final int DISCARDED_TOKEN_LIMIT = 5; // max number of discarded tokens shown i one recoverX call

    private static Logger logger = LoggerFactory.getLogger("hmi.xml.XMLTokenizer");

    /**
     * Create a XMLTokenizer for the specified Reader. 
     * Note that in general a BufferedReader is preferred; A non-buffered reader will be wrapped
     * internally into a BufferedReader.
     */
    public XMLTokenizer(Reader in)
    {
        /* all other constructors rely on this one */
        setDefaultModes();
        setReader(in); // wraps in BufferedReader if necessary, calls initState()....
    }

    /**
     * Create a XMLTokenizer for the specified InputStream.
     * Reading from a Stream implies that error messages cannot refer to a file name or URL.
     */
    public XMLTokenizer(InputStream in)
    {
        this(new BufferedReader(new InputStreamReader(in)));
    }

    /**
     * Create XMLTokenizer for a null Reader.
     */
    public XMLTokenizer()
    {
        this((BufferedReader) null);
    }

    /**
     * Like XMLTokenizer(Reader), with a Reader constructed from a StringReader for xmlString. 
     * No file name or URL is defined, 
     * so error messages cannot refer to these attributes.
     */
    public XMLTokenizer(String xmlString)
    {
        this(new StringReader(xmlString)); // a BufferedReader wrap will be added by setReader()
    }

    /**
     * Create a XMLTokenizer for a (buffered) Reader constructed from the specified File. 
     * Also, the base URL is set to the file URL derived from the File.
     */
    public XMLTokenizer(File inFile) throws FileNotFoundException
    {
        this((BufferedReader) null);
        setFile(inFile);
    }

    /**
     * Create a XMLTokenizer for a (buffered) Reader constructed from the specified URL.
     */
    public XMLTokenizer(URL url)
    {
        this((BufferedReader) null);
        try
        {
            openURL(url);
        }
        catch (IOException e)
        {
            throw new XMLScanException("Could not set XMLTokenizer URL: " + e);
        }
    }

    /**
     * Creates a XMLTokenizer for a resource file.
     * The base name of resourceFile is taken as the resource directory.
     */
    public static XMLTokenizer forResource(String resourceFile) {
        String fullPath = resourceFile.replace('\\', '/');
        int lastSlash = fullPath.lastIndexOf('/');
        if (lastSlash < 0) 
        {
            return forResource("", resourceFile); 
        }
        else 
        {
            return forResource(resourceFile.substring(0, lastSlash), resourceFile.substring(lastSlash+1)); 
        }
    }


    /**
     * Creates a XMLTokenizer for a resource file (specified by  resourceFile) 
     * within a resource directory, specified by resourceDir
     */
    public static XMLTokenizer forResource(String resourceDir, String resourceFile) {
       XMLTokenizer tokenizer = new XMLTokenizer();
       tokenizer.setResourceDir(resourceDir);
       tokenizer.setFileName(resourceFile);
       tokenizer.openResourceReader();
       return tokenizer;
    }

    /* Tries to open a BufferedReader for the current resource based on current resourceRoot and current fileName */
    private BufferedReader openResourceReader() 
    {
        if (resourceRoot == null)
        {
        	throw getXMLScanException("XMLTokenizer could not open a Resource Reader for " + fileName + " (null resourceDir)");  
        }
        if (fileName == null || fileName.equals(""))
        {
        	throw getXMLScanException("XMLTokenizer could not open a Resource Reader for " + fileName);  
        }
        String resourcePath = resourceRoot + fileName;
        InputStream inps = getClass().getClassLoader().getResourceAsStream(resourceRoot+fileName);
        if (inps == null)
        {
        	 throw getXMLScanException("XMLTokenizer could not open a Resource Reader for " +resourcePath);  
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(inps));
        setReader(in);
        return in;
    }

    /*
     * Sets the resource file
     */
    private void setFileName(String fileName) 
    {
       
        this.fileName = fileName.replace('\\', '/');
    }

    /*
     * Sets the resource directory to be used for includes 
     * Also sets baseDir to null
     *XXXXXXX alleen gebruikt door HmiEnvironment --> vervangen (bij new XMLTokenizer(File)
     */
    private void setResourceDir(String newResourceDir) 
    {
        if (newResourceDir == null) {
          resourceRoot = null;
          return;       
        }
        resourceRoot = newResourceDir.replace('\\', '/');
        int len = resourceRoot.length();
        if (len > 0 && resourceRoot.charAt(len-1) != '/')
        {
            resourceRoot = resourceRoot + '/'; // ensure a "/" at the end, unless resourceRoot == ""
        }
        baseDir = null;
    }

    /*
     * Sets the base directory to be used for includes 
     * Also sets resourceRoot to null.
     */
    private void setBaseDir(String newBaseDir) 
    {
        if (newBaseDir == null) {
            baseDir = null;
            return;
        }  
        baseDir = newBaseDir.replace('\\', '/');
        int len = baseDir.length();
        if (len > 0 && baseDir.charAt(len-1) != '/')
        {
            baseDir = baseDir + '/'; // ensure a "/" at the end, unless baseDir == ""
        }
        resourceRoot = null;
    }
  


    /**
     * Sets the current File, and opens a Reader for the File. 
     * Also, the base URL is set to the file URL derived from the File.  DISABLED
     */
    private void setFile(File inFile) throws FileNotFoundException
    {
        setReader(new BufferedReader(new FileReader(inFile)));
        this.file = inFile;
//        try
//        {
//            setBaseURL(inFile.toURI().toURL()); // ?? why not use the basename?
//        }
//        catch (MalformedURLException me)
//        {
//            logger.error("Malformed file URL: " + me);
//        }
    }

    /**
     * returns the current file, which could be null
     */
    public final File getFile()
    {
        return file;
    }

    /**
     * like setURL, in that it defines the current URL, but does not attempt to open a new Reader for the URL. 
     * Therefore, it is allowed to use an URL that specifies a directory name, 
     * ending with a slash character, rather than a file name. 
     * This URL will be effectively used as the ``base URL''
     * when setURL is called with a relative URL specification.
     */
    private void setBaseURL(URL url)
    {
        this.url = url;
    }

    /**
     * like setURL, in that it defines the current URL, but unlike setURL, 
     * setBaseURL does not attempt to open a new Reader for the URL. 
     * Therefore, it is allowed to use an URL that specifies a directory name, 
     * ending with a slash character, rather than a file name. 
     * This URL will be effectively used as the ``base URL'' 
     * when setURL is called next, with a relative URL specification. 
     * The base url specification can be absolute, or relative to the current URL.
     */
    private void setBaseURL(String urlSpec)
    {
        try
        {
            URL newUrl = null;
            if (url != null) 
            {
               newUrl = new URL(url, urlSpec);
            } 
            else 
            {
              newUrl = new URL(urlSpec);
            }
            if (newUrl != null)
            {
                url = newUrl;
            }
        }
        catch (java.net.MalformedURLException e)
        {
            logger.error("Malformed URL: " + urlSpec);
            throw getXMLScanException("Malformed URL: " + urlSpec);
        }
    }

    /**
     * Sets the current URL and, if not null, tries to open a Reader for the new URL. 
     * The current URL is ignored, that is, it is not regarded as a ``base URL''.
     */
    private void openURL(URL url) throws IOException
    {
        if (url != null)
        {
            this.url = url;
            setBaseURL(url);
            InputStream is = url.openStream();
            if (is == null) 
            {
            	throw getXMLScanException("XMLTokenizer could not open an Resource Reader for " + url);  
            }
            BufferedReader ir = new BufferedReader(new InputStreamReader(is));
            this.url = url;
            setReader(ir);
        }
        else
        {
             this.url = null;
             this.baseUrl = null;
        }
    }

    /*
     * starts reading from a new URL. 
     * The urlSpec String is regarded as relative to the current URL, 
     * unless it has the form of an absolute URL.
     */
    private void setURL(String urlSpec) throws IOException
    {
        if (urlSpec == null) {
           url = null;
           baseUrl = null;
           return;
        }
        try
        {
            String fullUrlSpec = urlSpec.replace('\\', '/'); // just in case
            int lastSlash = fullUrlSpec.lastIndexOf('/');
            String newBaseSpec = null;
            String newFileSpec = null;
            if (lastSlash < 0) 
            {
               newBaseSpec = "";
               newFileSpec = fullUrlSpec;
            }
            else 
            {
               newBaseSpec = fullUrlSpec.substring(0, lastSlash);
               newFileSpec = fullUrlSpec.substring(lastSlash+1);
            }   
            baseUrl = new URL(url, newBaseSpec);
            url = new URL(baseUrl, newFileSpec);
        }
        catch (java.net.MalformedURLException e)
        {
            logger.error("Malformed URL: " + urlSpec);
            throw getXMLScanException("Malformed URL: " + urlSpec);
        }
    }

    /**
     * returns the current URL, which could be null.
     */
    public URL getURL()
    {
        return url;
    }

    /*
     * Starts the scanner on the current input.
     * Only called from setReader
     */
    private void initState()
    {
        // initial state is the same as if a \n has just been read, and has been consumed,
        // so we are at line 1, position 0 (because of \n), tokenLine = 1, tokenCharPos = 1
        // (because ci == CONSUMED).
        // we start in CHARDATA mode, and pretend the "last" token has been consumed too.
        ci = CONSUMED;
        if (in == null)
        {
            token = ENDOFDOCUMENT;
            tokenMode = ENDOFDOCUMENT_MODE;
        }
        else
        {
            token = NULLTOKEN;
            tokenMode = CHARDATA_MODE;
        }
        tokenConsumed = true; // will cause the first atX/takeX/getX call to read the first token.
        line = 1;
        charPos = 0;
        setTokenPos();
    }
    /**
     * replaces the Reader that this tokenizer should process. 
     * If the specified Reader is not already a BufferedReader, it will be wrapped
     * inside a new BufferedReader.
     * The previous Reader is returned, which could be null.
     */
    public final BufferedReader setReader(Reader in)
    {
        BufferedReader oldreader = this.in;
        if (in == null)
        {
            this.in = null;
        }
        else if (in instanceof BufferedReader)
        {
            this.in = (BufferedReader) in;
        }
        else
        {
            this.in = new BufferedReader(in);
        }
        initState();
        return oldreader;
    }

    /**
     * Gets the Reader that this tokenizer is currently using.
     */
    public final Reader getReader()
    {
        return in;
    }

    /**
     * Close the associated Reader
     */
     public final void closeReader() throws IOException
     {
        if (in != null) in.close();
     }

    /**
     * Pushes the current XMLTokenizer status on the stack, and then starts reading from the newly specified Reader.
     */
    public final void pushReader(BufferedReader in)
    {
        pushState();
        setReader(in);
        file = null;
        url = null;
    }

    /**
     * Pushes the current XMLTokenizer status on the stack, and then starts reading 
     * from the Reader specified in URL form. 
     * The URL specification can be relative, in which case the current base URL is taken into account, 
     * or it can be an absolute URL. 
     * The boolean value returned is true if the
     * reader for the specified url could be opened, otherwise false is returned.
     */
    public final void pushReader(String urlSpec) throws IOException
    {
        pushState();
        try
        {
            setURL(urlSpec);
            file = null;
        }
        catch (IOException ie)
        {
            popState();
            throw (ie);
        }
    }
    
    
    /**
     * Pushes the current XMLTokenizer status on the stack, 
     * then starts reading from the specified resource file.
     */
    public final void pushReader(String resourceDir, String fileName)
    {
        pushState();
        setResourceDir(resourceDir);
        setFileName(fileName);
        file = null;
        url = null;
        BufferedReader in = openResourceReader(); // might throw an XMLScanException
    }

    
    

    /**
     * Assuming that a previous call to pushReader has been made, this call will restore the previous reader, 
     * and the state of the XMLTokenizer to the state when the pushReader call was made. 
     * Note that popReader calls are implied (made automatically) when the popOnEndOfDocument mode is true.
     */
    public final void popReader()
    {
        popState();
       // showTokenizerState(" tokenizer state after popReader");
    }

    // mask values for including parts of error messages in XMLScanExceptions.
    public static final int ERRORTOKENLINE = 1;
    public static final int ERRORTOKENPOS = 2;
    public static final int ERRORLINE = 4;
    public static final int ERRORPOS = 8;
    public static final int ERRORFILE = 16;
    public static final int ERRORURL = 32;
    public static final int ERRORFULL = ERRORTOKENLINE | ERRORTOKENPOS | ERRORLINE | ERRORPOS | ERRORFILE | ERRORURL;
    public static final int ERRORFILELINE = ERRORLINE | ERRORFILE | ERRORURL;
    public static final int NOERRORPOSITION = 0;

    private int defaultScanExceptionMode = ERRORFULL;

    /**
     * determines what to include in XMLScanExceptions, generated by getXMLScanException(). ``mode'' must be an OR of a selection of the following
     * masks:
     * 
     * ERRORTOKENLINE (line of error token) ERRORTOKENPOS (starting position of the error token) ERRORLINE (line where error was detected) ERRORPOS
     * (character position where error was detected) ERRORFILE (file name, if available) ERRORURL (URL, if available, but not if file name has been
     * included already) The default mode is ERRORFULL, which is simply the ``or'' of all masks. Also available is ERRORFILELINE, which equals
     * ERRORFILE|ERRORURL|ERRORLINE, and NOERRORPOSITION, which is simply 0;
     */
    public void setXMLScanExceptionMode(int mode)
    {
        if (mode > ERRORFULL)
        {
            logger.error("XMLTokenizer: Illegal error mode");
        }
        else
        {
            defaultScanExceptionMode = mode;
        }
    }

    /**
     * returns an XMLScanException, containing the message String, but also including positional information, depending on available information, and
     * settings. In principle, the file or url (if available), the token line and character position, and the curent line and position are included.
     */
    public XMLScanException getXMLScanException(String message)
    {
        return getXMLScanException(message, defaultScanExceptionMode);
    }

    /**
     * returns an XMLScanException, containing the message String, but also including positional information, depending on available information, and
     * settings. In principle, the file or url (if available), the token line and character position, and the curent line and position are included.
     */
    public XMLScanException getXMLScanException(String message, int mode)
    {
        String fileName = (file != null) ? file.getName() : null;
        String urlString = (url != null) ? url.toString() : null;
        return new XMLScanException(getErrorMessage(message, mode), fileName, urlString, tokenLine, tokenCharPos, line, charPos);
    }

    /**
     * returns an error mesage String, containing the message String, but also including positional information, depending on available information,
     * and settings. In principle, the file or url (if available), the token line and character position, and the curent line and position are
     * included.
     */
    public String getErrorMessage(String message)
    {
        return getErrorMessage(message, defaultScanExceptionMode);
    }

    private static final int ERRORBUFSIZE = 40;

    /**
     * returns an error mesage String, containing the message String, but also including positional information, depending on available information,
     * and settings. In principle, the file or url (if available), the token line and character position, and the curent line and position are
     * included.
     */
    public String getErrorMessage(String message, int mode)
    {
        StringBuilder msg = new StringBuilder(ERRORBUFSIZE);
        if (message != null)
            msg.append(message);
        if ((mode & ERRORFILE) != 0 && file != null)
        {
            msg.append(", file: ");
            msg.append(file.getName());
        }
        else if ((mode & ERRORURL) != 0 && url != null)
        {
            msg.append(", URL: ");
            msg.append(url.toString());
        }
        if ((mode & ERRORFILE) != 0 && resourceRoot != null)
        {
            msg.append(", resourceRoot: ");
            msg.append(resourceRoot);
        }
//        if ((mode & ERRORFILE) != 0 && resourceFile != null)
//        {
//            msg.append(", resource file: ");
//            msg.append(resourceFile);
//        }
        if ((mode & ERRORTOKENLINE) != 0)
        {
            msg.append(", token line: ");
            msg.append(getTokenLine());
        }
        if ((mode & ERRORTOKENPOS) != 0)
        {
            msg.append(", position: ");
            msg.append(getTokenCharPos());
        }
        if ((mode & ERRORLINE) != 0)
        {
            msg.append(", error line: ");
            msg.append(getLine());
        }
        if ((mode & ERRORPOS) != 0)
        {
            msg.append(", position: ");
            msg.append(getCharPos());
        }
        return msg.toString();
    }

    /**
     * returns the current line number; line counts start at 1.
     */
    public final int getLine()
    {
        return line;
    }

    /**
     * returns the current character position within the current line. character counts start at 1 for the first character within the line. After a \n
     * character, the character position is 0.
     * 
     */
    public final int getCharPos()
    {
        return charPos;
    }

    /**
     * returns the starting line number of the start of the the current token. line counting starts at 1.
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

    /*
     * return the current token, i.e. the token returned by the last nextToken call
     */
    public final int currentToken()
    {
        return token;
    }

    /*
     * returns the current token, as String i.e. the token returned by the last nextToken call
     */
    public final String currentTokenString()
    {
        if (token == STAG || token == ETAG)
        {
            return tokenString(token) + " " + tagName;
        }
        else
        {
            return tokenString(token);
        }
    }

    /**
     * discards input until an STAG with specified tag name is reached, the end of document is reached, or an IOException is thrown. In the first
     * situation, true is returned, otherwise false.
     */
    public final boolean recoverAtSTag(String stag)
    {
        return recoverAtSTag(stag, DISCARDED_TOKEN_LIMIT);
    }

    /**
     * discards input until an STAG with specified tag name is reached, the end of document is reached, or an IOException is thrown. In the first
     * situation, true is returned, otherwise false. When showDiscardedTokens is true, discarded tokens are shown on the Console, up to a certain
     * limit ( DISCARDED_TOKEN_LIMIT)
     */
    public final boolean recoverAtSTag(String stag, int tokenLimit)
    {
        int skipCount = 0;
        try
        {
            while (!(atEndOfDocument() || atSTag(stag)))
            {
                nextToken();
                if (skipCount < tokenLimit)
                {
                    if (token == STAG || token == ETAG)
                    {
                        logger.warn("Skipping " + tokenString(token) + " token: " + tagName);
                    }
                    else
                    {
                        logger.warn("Skipping " + tokenString(token) + "token");
                    }
                }
                else if (skipCount == tokenLimit)
                {
                    logger.warn("Skipping tokens ....");
                }
                skipCount++;
            }
            return atSTag(stag);
        }
        catch (IOException e)
        {
            logger.error("Exception while recovering for " + stag + " STAG: " + e);
            return false;
        }
    }

    /**
     * discards input until an ETAG with specified tag name is reached, the end of document is reached, or an IOException is thrown. In the first
     * situation, the ETAG is consumed, and true is returned, otherwise false is returned.
     */
    public final boolean recoverAfterETag(String etag)
    {
        return recoverAfterETag(etag, DISCARDED_TOKEN_LIMIT);
    }

    /**
     * discards input until an ETAG with specified tag name is reached, the end of document is reached, or an IOException is thrown. In the first
     * situation, the ETAG is consumed, and true is returned, otherwise false is returned. When showDiscardedTokens is true, discarded tokens are
     * shown on the Console, up to a certain limit ( DISCARDED_TOKEN_LIMIT)
     */
    public final boolean recoverAfterETag(String etag, int tokenLimit)
    {
        int skipCount = 0;
        try
        {
            while (!(atEndOfDocument() || atETag(etag)))
            {
                nextToken();
                if (skipCount < tokenLimit)
                {
                    if (token == STAG || token == ETAG)
                    {
                        logger.warn("Skipping " + tokenString(token) + " token: " + tagName);
                    }
                    else
                    {
                        logger.warn("Skipping " + tokenString(token) + "token");
                    }
                }
                else if (skipCount == tokenLimit)
                {
                    logger.warn("Skipping tokens ....");
                }
                skipCount++;
            }
            if (atETag(etag))
            {
                takeETag();
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (IOException e)
        {
            logger.error("Exception while recovering for " + etag + " ETAG: " + e);
            return false;
        }
    }

    /**
     * assuming that we are at a STAG, skips the remainder of the current tag, up to and including the matching ETAG. The current token must be STAG,
     * OPENSTAG, or CLOSESTAG. The XML part that is skipped must be well formed, implying that STags and ETags should be properly matched. An
     * Exception is thrown if the ENDOFDOCUMENT is reached while skipping.
     */
    public final void skipTag() throws IOException
    {
        if (!atSTag())
        {
            throw getXMLScanException("The XMLTokenizer was not at an STAG at start of skipTag action");
        }
        String skipTagName = getTagName();

        int skipStackSize = tagStack.size();
        do
        {
            do
            { // skip tokens until ETAG is reached (or until an -illegal- ENDOFDOCUMENT is reached)
                nextToken();
                if (atEndOfDocument())
                {
                    logger.warn("ENDOFDOCUMENT reached while skipping tag: " + skipTagName);
                    token = ENDOFDOCUMENT;
                    return;
                }
            }
            while (!atETag());
        }
        while (tagStack.size() >= skipStackSize);
        tokenConsumed = true; // consume the last ETAG
    }

    /**
     * assuming that we are at an STag, gets the XML text until the corresponding closing ETag. An Exception is thrown if the ENDOFDOCUMENT is reached
     * while skipping.
     */
    public final String getXMLSection() throws IOException
    {
        if (!atSTag())
        {
            throw getXMLScanException("The XMLTokenizer was not at an STAG at start of getXMLSection action");
        }
        in.reset(); // we are about to begin the first character after the '<'. It will be seen as CHARDATA, i.e. will not be pushed again on the
                    // stack
        setSectionBuffering(true);
        clearSectionBuffer();
        sectionBuffer.append('<');
        // String sectionTagName = getTagName();
        int skipStackSize = tagStack.size();
        do
        {
            do
            { // skip tokens until ETAG is reached (or until an -illegal- ENDOFDOCUMENT is reached)
                nextToken();
                if (atEndOfDocument())
                {
                    throw new XMLScanException("ENDOFDOCUMENT reached");
                    // logger.warning("ENDOFDOCUMENT reached while extracting XML section for tag: " + sectionTagName);
                    // token = ENDOFDOCUMENT;
                    // return null;
                }
            }
            while (!atETag());
        }
        while (tagStack.size() >= skipStackSize); // || ! getTagName().equals(skipTagName) );
        tokenConsumed = true; // consume the last ETAG
        String result = getSectionBuffer();
        setSectionBuffering(false);
        return result;
    }

    /**
     * assuming that we are just beyond an STag, specified by means of the tag parameter, gets the XML text until the corresponding closing ETag. An
     * Exception is thrown if the ENDOFDOCUMENT is reached while skipping.
     * No white space is stripped on the start or end of the section.
     */
    public final String getXMLSectionContent() throws IOException
    {
      // Removed: we no longer strip the contents. 
//         first get rid of the remaining blank space on the current line. (But *not* the blank space of the next line)
//       
//        while (ci == ' ' || ci == '\t')
//        {
//            nextChar();
//        }
//        if (ci == '\r' || ci == '\n')
//            nextChar();
      
        
        setSectionBuffering(true);
        clearSectionBuffer();
        // String sectionTagName = topTag();
        int skipStackSize = tagStack.size(); // we may/must assume that tag is already on the stack
        do
        {
            do
            { // skip tokens until ETAG is reached (or until an -illegal- ENDOFDOCUMENT is reached)
                nextToken();
                if (atEndOfDocument())
                {
                    throw new XMLScanException("ENDOFDOCUMENT reached");
                    // logger.warning("ENDOFDOCUMENT reached while extracting XML section content " + sectionTagName);
                    // token = ENDOFDOCUMENT;
                    // return null;
                }
            }
            while (!atETag());
        }
        while (tagStack.size() >= skipStackSize); // || ! getTagName().equals(skipTagName) );
        String result = getStrippedSectionBuffer();
        setSectionBuffering(false);        
        return result;
    }

    /**
     * Called to move the tokenizer to a next token. Effect depends on current token and settings of several settings.
     * 
     */
    private int nextToken() throws IOException
    {
        try
        {
            while (true)
            {
//                if (debug) {
//                    //logger.warn("XMLTokenizer.nextToken tokenMode = " + tokenMode);
//                    showTokenizerState(" nextToken START "); 
//                }
                tokenConsumed = false;
                switch (tokenMode)
                {
                case CHARDATA_MODE:
                    parseCharData();
                    break;
                case PENDING_ETAG_MODE: // empty tag, return ETAG token.
                    token = ETAG;
                    tokenMode = CHARDATA_MODE;
                    // tagName is still valid
                    popTag(tagName);
                    break;
                case ENDOFDOCUMENT_MODE: // ENDOFDOCUMENT reached before
                    token = ENDOFDOCUMENT;
                    break;
                default:
                    throw getXMLScanException("unknown XMLTokenizer processing mode");
                }
                if (token == ENDOFDOCUMENT && popOnEndOfDocument && tokenizerStateStack.size() > 0)
                {
                    popState();
                }
                else
                {
//                    if (debug) {      showTokenizerState(" nextToken RETURN "); }
                    return token;
                }
            }
        }
        catch (RuntimeException re)
        { // including XMLScanExceptions
            token = ERRORTOKEN;
            throw re;
        }
    }

    /*
     * parses CHARDATA, and detects whether other lexical tokens are included within this CHARDATA.
     */
    private int parseCharData() throws IOException
    {

        while (ci != EOS)
        { // iterate until non-ignorable token is returned, or EOS is reached
         
//            if (debug)       {          showTokenizerState(" XMLTokenizer.parseCharDta, tokenizer state");            }
            if (ci == CONSUMED)
                nextParsedChar();
                
                
            while (isSpaceChar())
            {
                nextParsedChar();
            }
            setTokenPos();
            in.mark(4096);
            // use ci, not ch, for classification; ch could be '<' because of &lt; pattern in input stream)
            if (ci == '<')
            { // STAG, ETAG, PI, or decl

                int markup = parseMarkup();
                if (markup > 0)
                {
                    return markup;
                }
                else
                {
                    // continue with next iteration of "while (ci != EOS)" loop;
                    setTokenPos();
                }
            }
            else if (ci == EOS)
            { // trailing blank spaces
                token = ENDOFDOCUMENT;
                tokenMode = ENDOFDOCUMENT_MODE;
                setTokenPos();
                // if (checkWellformedness) {
                // checkEmptyTagStack();
                // }
                return token;
            }
            else
            { // proper CHARDATA, not ignorable
                clearBuffer(charDataBuffer);
                while (ci != '<' && ci != EOS)
                {
                    charDataBuffer.append(ch);
                    nextParsedChar();
                }
                token = CHARDATA;
                // tokenMode remains CHARDATA_MODE
                return token;
            }
        } // quit loop because of EOS char (immediately following previous token): end-of-data reached.
        token = ENDOFDOCUMENT;
        tokenMode = ENDOFDOCUMENT_MODE;
        setTokenPos();
        checkEmptyTagStack();
        return token;
    }

    /*
     * parses XML markup, where "markup" if everything starting with a '<' character. precondition: ci = '<', tokenMode = CHARDATA mode.
     */
    private int parseMarkup() throws IOException
    {
        setTokenPos();
        nextChar(); // expect a '!', a '?', a '/', or nameStartChar.
        if (ci == '!')
        { // declaration or comment or CDSECT
            return parseDeclaration();
        }
        else if (ci == '?')
        { // PI
            int piState = parsePI(); // returns -1 if PI's must be ignored.
            //showTokenizerState(" tokenizer state after parsePI");
            if (piState >= 0)
                return piState; // return PI, unless PI's are to be ignored.
            return -1; // ignorable PI.
        }
        else if (ci == '/')
        { // ETAG of the form </tagName>
            return parseETag();
        }
        else if (isNameStartChar())
        { // STAG of form <tagName .... >, possibly including attributes
            return parseSTag();
        }
        return -1;
    }

    /*
     * parse STags, either the whole STAG, or just the OPENSTAG part, depending on the STAG mode. pre: current ci following the '<' satisfies
     * isStartNameChar(), tokenMode = CHARDATA_MODE. When the STAG is terminated by means of the "/>" sequence, a pending ETAG is registered, by
     * setting tokenMode to PENDING_ETAG_MODE.
     */
    private int parseSTag() throws IOException
    {
        clearBuffer(tagNameBuffer);
        tagPrefix = null;
        // tagNamespace = defaultNamespace;
        namespaceStack.pushMark();
       // int namespaceDeclarationCount = 0; // number of namespace declarations for this STag.
        XMLNameSpace xmlnsNameSpace = null;
        attributes.clear();
        // attributeNamespaces.clear();

        while (isNameChar())
        {
            tagNameBuffer.append((char) ci);
            nextChar();
        }
        if (isNamespaceSepChar())
        {
            tagPrefix = tagNameBuffer.toString().intern();
            // tagNamespace = namespaceMap.get(tagPrefix); // could result in null namespace
            clearBuffer(tagNameBuffer);
            nextChar();
            while (isNameChar())
            {
                tagNameBuffer.append((char) ci);
                nextChar();
            }
        }
        tagName = tagNameBuffer.toString();

        skipSpaceChars();
        // parse attributes
        while (ci != '>' && ci != EOS)
        {
            if (ci == '/')
            { // empty content tag, set tokenMode for pending ETAG.
                nextChar();
                if (ci != '>')
                    throw getXMLScanException("\'>\' character after \'/\' expected instead of '" + (char) ci + "'");
                tokenMode = PENDING_ETAG_MODE;
            }
            else
            { // attribute
                if (!isNameStartChar())
                    throw getXMLScanException("XML attribute name expected");
                parseAttribute();
                if (attributePrefix != null && attributePrefix.equals("xmlns"))
                {
                    // xmlns declaration of form xmlns:ns="...."
                    String nameSpace = attributeValueBuffer.toString();
                    String namespacePrefix = attributeName;
                    XMLNameSpace ns = new XMLNameSpace(namespacePrefix, nameSpace);
                    namespaceStack.pushXMLNameSpace(ns);
                   // namespaceDeclarationCount++;
                    attributePrefixFixup(namespacePrefix, nameSpace);
                }
                else if (attributeName.equals("xmlns"))
                {
                    // xmlns declaration of form xmlns="..." (default namespace)
                    String nsp = attributeValueBuffer.toString().intern();
                    if (nsp == "")
                        nsp = null;
                    XMLNameSpace ns = new XMLNameSpace("", nsp);
                    namespaceStack.pushXMLNameSpace(ns);
                   // namespaceDeclarationCount++;
                    defaultNamespace = nsp;
                    // System.out.println("XMLTokenizer set defaultNamespace=\"" + defaultNamespace + "\"");

                }
                else
                { // no xmlns declaration, standard attribute
                    if (attributePrefix != null)
                    { // save namespace for this attribute
                        String attributeNameSpace = namespaceStack.getNameSpace(attributePrefix);
                        if (attributeNameSpace == null)
                        {
                            attributes.put(attributePrefix + ":" + attributeName, attributeValueBuffer.toString()); // temporarily use the prefix (to
                                                                                                                    // be replaced by real namespace
                                                                                                                    // during fixup)
                        }
                        else
                        {
                            attributes.put(attributeNameSpace + ":" + attributeName, attributeValueBuffer.toString()); // here we know the real
                                                                                                                       // namespace already, and
                                                                                                                       // prepend it
                        }
                    }
                    else
                    { // standard attribute, no namespace prefix
                        attributes.put(attributeName, attributeValueBuffer.toString());
                    }

                }
                skipSpaceChars();
            }
        }

        if (ci != '>')
            throw getXMLScanException("\'>\' expected at end of XML STAG");
        ci = CONSUMED;
        // setLogging(logMode);
        token = STAG;
        // System.out.println("Get namespace for prefix: " + tagPrefix);

        tagNamespace = (tagPrefix == null) ? defaultNamespace : namespaceStack.getNameSpace(tagPrefix);
        // System.out.println("tagNamespace = " + tagNamespace);
        //pushTag(tagName, namespaceDeclarationCount);
        pushTag(tagName);
        if (tokenMode != PENDING_ETAG_MODE)
            tokenMode = CHARDATA_MODE;
        // System.out.println("parseSTagTail, token = " + token);
        return token;
    }

    /*
     * When a names namespace declartion like xmlns:ns="...." is encountered, arributes, for this STag, encountered before the namespace declaration,
     * might have used the newly defined ns prefix. They have been inserted into the attributes HashMap, with that prefix which now has to be replaced
     * by the proper namespace
     */
    private void attributePrefixFixup(String nsPrefix, String ns)
    {
        Set<String> attributeNames = attributes.keySet();
        for (String attrName : attributeNames)
        {
            int colPos = attrName.indexOf(':');
            if (colPos >= 0)
            {
                String attrPrefix = attrName.substring(0, colPos);
                if (attrPrefix.equals(nsPrefix))
                {
                    String nameTail = attrName.substring(colPos);
                    String newName = ns + nameTail;
                    String attrValue = attributes.get(attrName);
                    attributes.remove(attrName);
                    attributes.put(newName, attrValue);
                }
            }
        }
    }

    private int parseETag() throws IOException
    {
        clearBuffer(tagNameBuffer);
        tagPrefix = null;
        tagNamespace = defaultNamespace;
        nextChar(); // consume '/' char. assume next char is a legal start of tag name.
        while (isNameChar())
        {
            tagNameBuffer.append((char) ci);
            nextChar();
        }
        if (isNamespaceSepChar())
        {
            tagPrefix = tagNameBuffer.toString().intern();
            tagNamespace = namespaceStack.getNameSpace(tagPrefix);
            clearBuffer(tagNameBuffer);
            nextChar();
            while (isNameChar())
            {
                tagNameBuffer.append((char) ci);
                nextChar();
            }
        }

        if (ci != '>')
            throw getXMLScanException("\'>\' character at end of ETAG expected, instead of '" + ((char) ci) + "'");
        ci = CONSUMED;
        token = ETAG;
        // tokenMode remains CHARDATA_MODE
        tagName = tagNameBuffer.toString();
        popTag(tagName);
        return token;
    }

    /*
     * parses a complete attribute-name-value pair. does not return a token, nor does it set the tokenMode. only the attributeName and the
     * attributeValueBuffer are set, and the attribute characters are consumed.
     */
    private void parseAttribute() throws IOException
    {
        clearBuffer(attributeNameBuffer);
        clearBuffer(attributeValueBuffer);
        attributePrefix = null;
        while (isNameChar())
        {
            attributeNameBuffer.append((char) ci);
            nextChar();
        }
        if (isNamespaceSepChar())
        {
            attributePrefix = attributeNameBuffer.toString().intern();
            clearBuffer(attributeNameBuffer);
            nextChar();
            while (isNameChar())
            {
                attributeNameBuffer.append((char) ci);
                nextChar();
            }
        }
        attributeName = attributeNameBuffer.toString();
        skipSpaceChars();
        if (ci != '=')
            throw getXMLScanException("\'=\' character expected in XML attribute instead of '" + (char) ci + "'");
        nextChar();
        parseString(attributeValueBuffer);
    }

    /*
     * parses strings of the form "..(chars except ")..." or '..(chars except ')...', possibly preceded by blank space. Note that such strings can
     * contain parsed chardata, so e.g. &quot is allowed inside a '...' type of string. The result is appended to buf. An XMLScanException is thrown
     * if EOS is reached. When no string was found, -1 is returned, else the length of the string is returned.
     */
    private int parseString(StringBuilder buf) throws IOException
    {

        // switch to parsed character data within the string
        // the end is indicated by ci == '"' or ci == '\''.
        // Note that ch can be '"' or '\'' if an entity reference of the &quot;
        // or &apos; is present in the input stream.
        skipSpaceChars();
        if (ci != '"' && ci != '\'')
            return -1;
        boolean aposmode = (ci == '\'');
        nextParsedChar();
        int len = 0;
        while ((aposmode || ci != '"') && (!aposmode || ci != '\'') && ci != EOS)
        {
            buf.append(ch);
            len++;
            nextParsedChar();
        }
        if (ci == EOS)
            throw getXMLScanException("missing \" or \': end-of-data reached");
        // else, if ci == '"' or ci == '\'',
        nextChar();
        return len;
    }

    /*
     * should take care of "declarations", including comment en doctypes. returns token > 0 if relevant token, or -1 if declaration must be ignored.
     * IMplicit assumption: after '<!' a valid known declaration type MUST follow.
     */
    private int parseDeclaration() throws IOException
    {
        nextChar(); // remove the '!' char
        if (ci == '-')
        { // comment
            return parseComment();
        }
        else if (ci == 'D')
        {
            return parseDoctype();
        }
        else if (ci == '[')
        { // CData: <![CDATA[ ... ]]>
            return parseCDSect();
        }
        else
        { // some other declaration
            throw getXMLScanException("Unknown declaration type");
        }
    }

    private static final int CDATABUFFERSIZE = 100;

    /*
     * parses (remainder of) a CDATA section. ND: At the moment, this method contains the implicit assumption that a valid declaration MUST follow
     * after '<!' assume that ci contains the first '[' char of the "<![CDATA[" opening sequence.
     */
    private int parseCDSect() throws IOException
    {
        if (nextChar() != 'C')
            throw getXMLScanException("Wrong format at CDATA section"); // start removing 'CDATA['
        if (nextChar() != 'D')
            throw getXMLScanException("Wrong format at CDATA section");
        if (nextChar() != 'A')
            throw getXMLScanException("Wrong format at CDATA section");
        if (nextChar() != 'T')
            throw getXMLScanException("Wrong format at CDATA section");
        if (nextChar() != 'A')
            throw getXMLScanException("Wrong format at CDATA section");
        if (nextChar() != '[')
            throw getXMLScanException("Wrong format at CDATA section");
        // clear buffer
        cDataBuffer = new StringBuilder(CDATABUFFERSIZE);
        // Start reading content until ]]>
        nextChar(); // read first CDATA char
        boolean endCData = false;
        while (!endCData)
        {
            if (ci != ']' && ci != EOS)
            {
                cDataBuffer.append((char) ci);
                nextChar();
            }
            else
            {
                if (ci != EOS) // if ci == EOS, the last clause will return end of CData
                    nextChar();
                if (ci != ']' && ci != EOS)
                {
                    cDataBuffer.append(']');
                    cDataBuffer.append((char) ci);
                    nextChar();
                }
                else
                {
                    if (ci != EOS) // if ci == EOS, the last clause will return end of CData
                        nextChar();
                    while (ci == ']' && ci != EOS)
                    {
                        cDataBuffer.append(']');
                        nextChar();
                    }
                    if (ci != '>' && ci != EOS)
                    {
                        cDataBuffer.append(']');
                        cDataBuffer.append(']');
                        cDataBuffer.append((char) ci);
                        nextChar();
                    }
                    else
                    {
                        if (ci == EOS)
                        {
                            throw getXMLScanException("Wrong format at CDATA section: unexpected EOS");
                        }
                        // end of CDATA
                        endCData = true;
                        ci = CONSUMED;
                        token = CDSECT;
                        return token;
                    }
                }
            }
        }
        throw getXMLScanException("'Impossible' situation in CDATA section: ran out of loops");
    }

    /*
     * parses (remainder of) a comment declaration. assume that ci contains the first '-' char of the "<!--" opening sequence.
     */
    private int parseComment() throws IOException
    {
        // System.out.println("comment");
        if (nextChar() != '-')
            return -1; // remove first '-', check for second '-'
        nextChar(); // read first comment char.
        // read comment until "--" is encountered.
        if (!skipComment)
            clearBuffer(commentDataBuffer);
        // System.out.println("First comment char:" + ((char) ci));
        while (ci != '-' && ci != EOS)
        { // read until second '-' encountered
            while (ci != '-' && ci != EOS)
            { // read until first '-' encountered
                // System.out.println("comment char:" + (char)ci);
                if (!skipComment)
                    commentDataBuffer.append((char) ci);
                nextChar();
            } // '-' encountered, potential end of comment
            nextChar(); // if it's a '-', it's the end of comment, else the '-' belongs to the commentDataBuffer
            if (!skipComment && ci != '-' && ci != EOS)
                commentDataBuffer.append('-');
        }
        if (ci == '-')
            nextChar();
        // end of comment reached, consume the expected '>' char, but retain a possible EOS
        if (ci == '>')
            ci = CONSUMED;
        else
        {
            throw getXMLScanException("'>' expected at end of XML comment instead of '" + (char) ci + "'");
        }
        if (skipComment)
        {
            // System.out.println("parseDeclaration: skip comment, return -1");
            return -1;
        }
        else
        {
            token = COMMENT;
            return token;
        }
    }

    /*
     * skips (remainder of) a DOCTYPE declaration. assume that ci contains the first 'D' char of the "<!DOCTYPE" opening sequence. skips upto and
     * including closing '>' char, taking nested "<..>" pairs into account. currently, cannot cope with internal comments or entity declarations that
     * contain improperly paired "," or ">" chars.
     */
    private void skipDoctype() throws IOException
    {
        int brackLevel = 1; // counting the '<' in the opening sequence "<!DOCTYPE"
        // read unparsed chardata until brackLevel reaches 0 or EOS is reached.
        while (brackLevel > 0 && ci != EOS)
        {
            nextChar();
            // System.out.println("brackLevel = " + brackLevel + " skip " + (char)ci);
            if (ci == '<')
                brackLevel++;
            if (ci == '>')
                brackLevel--;
        }
        if (ci == '>')
            ci = CONSUMED;

    }

    private static final int DOCTYPEBUFFERSIZE = 20;

    /*
     * parses (remainder of) a DOCTYPE declaration. assume that ci contains the first 'D' char of the "<!DOCTYPE" opening sequence.
     */
    private int parseDoctype() throws IOException
    {
        checkSequence("DOCTYPE");
        if (skipDoctype)
        {
            skipDoctype();
            return -1;
        }
        // System.out.println("doctype declaration");
        if (doctypeName == null)
        {
            buf = new StringBuilder(DOCTYPEBUFFERSIZE);
            // pubidLiteral = new StringBuilder
            // systemLiteral;
            // doctypeDataBuffer = new StringBuilder(50);
        }
        else
        {
            throw getXMLScanException("redeclaration of DOCTYPE not allowed");
        }
        skipSpaceChars();
        if (!isNameStartChar())
        {
            throw getXMLScanException("Illegal character at start of DOCTYPE name: " + (char) ci);
        }
        while (isNameChar())
        {
            buf.append((char) ci);
            nextChar();
        }
        doctypeName = buf.toString();
        skipSpaceChars();
        if (ci == 'S')
        { // SYSTEM ExternalId
            checkSequence("SYSTEM");
            clearBuffer(buf);
            parseString(buf);
            systemLiteral = buf.toString();
        }
        else if (ci == 'P')
        { // PUBLIC ExternalId
            checkSequence("PUBLIC");
            clearBuffer(buf);
            parseString(buf);
            pubidLiteral = buf.toString();
            clearBuffer(buf);
            parseString(buf);
            systemLiteral = buf.toString();
        }

        if (ci != '>' && ci == '[')
        {
            throw getXMLScanException("\"[....]\" inside DOCTYPE not supported");
        }
        skipSpaceChars();
        if (ci == '>')
        {
            ci = CONSUMED;
        }
        else
        {
            throw getXMLScanException("Unexpected character at end of DOCTYPE declaration");
        }
        token = DOCTYPE;
        return token;
    }

    /*
     * checks and consumes the sequence of characters seq assumes that ci is CONSUMED or else contains the first character of seq. Will leave the
     * first character after seq in ci.
     */
    private void checkSequence(String seq) throws IOException
    {
        if (ci == CONSUMED)
            nextChar();
        for (int i = 0; i < seq.length(); i++)
        {
            if (ci != seq.charAt(i))
                throw getXMLScanException(seq.charAt(i) + " character expected in " + seq + " instead of '" + (char) ci + "'");
            nextChar();
        }
    }

    /*
     * should take care of PI's (processing instructions. The PI section includes chars up to and including "?>" The text of the PI must not be
     * "parsed".
     */
    private int parsePI() throws IOException
    {
        // System.out.println("parsePI");
        nextChar(); // remove the '?' char.

        clearBuffer(piDataBuffer);
        while (ci != '>' && ci != EOS)
        { // read until "?>" or EOS is encountered
            while (ci != '?' && ci != EOS)
            {
                piDataBuffer.append((char) ci);
                nextChar();
            } // reached a '?' char, potential end of PI
            nextChar(); // if it's a '>' or EOS, it's the end of this PI, else the '?' belongs to the piDataBuffer
            if (ci != '>' && ci != EOS)
                piDataBuffer.append('?');
        }
        if (ci == '>')
            ci = CONSUMED;
            
       // System.out.println("=====> Pi " + piDataBuffer.toString());
        String piContents = piDataBuffer.toString().trim();
        if (piContents.startsWith("include ")) 
        {
        
            StringTokenizer st = new StringTokenizer(piContents, " =");
            String tk = st.nextToken(); // skip the "include" token
            String newResourcePath = null;
            String newResourceDir = resourceRoot;
            while (st.hasMoreTokens())
            {
                tk = st.nextToken();
                // System.out.println("===> Token: " + tk);
                if (tk.equals("file")) 
                {
                    newResourcePath = st.nextToken(); 
                    newResourcePath = newResourcePath.substring(1, newResourcePath.length()-1);
                    
                } 
                else if (tk.equals("resourcedir") || tk.equals("resources") ) 
                {
                    String resdir = st.nextToken();
                    newResourceDir = resdir.substring(1, resdir.length()-1);
                   
                }
                else 
                {
                    logger.warn("XML include, unrecognized token: " + tk);
                }
            }
            if (newResourcePath == null) {
                logger.error("XML include: no file specified (" + piContents + ")" );
            }
            else if (newResourceDir == null) 
            {
                logger.error("XML include: no resource dir(" + piContents + ")" );
            } 
            else
            {
                 pushReader(newResourceDir, newResourcePath);
                 tokenConsumed = false; // corrects the initState setting
                 setpopOnEndOfDocument(true);
            }
        }
        else if (piContents.startsWith("log") || piContents.startsWith("warn"))
        {
           String msg = piContents;
           logger.warn(msg);
        }
        else if (piContents.startsWith("print") || piContents.startsWith("info"))
        {
           String msg = piContents;
           logger.info(msg);
        }
        else if (piContents.startsWith("debug"))
        {
           setDebug(true);
        }   
        else if (piContents.startsWith("nodebug"))
        {
           setDebug(false);
        }   
        
        
        if (skipPI)
        {
            return -1;
        }
        else
        {
            token = PI;
            return token;
        }
    }

    /**
     * Used to set if PIDATA should be skipped.
     */
    public final boolean setSkipPI(boolean skipped)
    {
        boolean oldvalue = skipPI;
        skipPI = skipped;
        return oldvalue;
    }

    /**
     * Used to set if COMMENTS should be skipped.
     */
    public final boolean setSkipComment(boolean skipped)
    {
        boolean oldvalue = skipComment;
        skipComment = skipped;
        return oldvalue;
    }

    /**
     * Used to set if DOCTYPE should be skipped.
     */
    public final boolean setSkipDoctype(boolean skipped)
    {
        boolean oldvalue = skipDoctype;
        skipDoctype = skipped;
        return oldvalue;
    }

    /**
     * returns the current status of recognizeNamespace status.
     */
    public final boolean getRecognizeNamespaces()
    {
        return recognizeNamespaces;
    }

    /**
     * Sets the status of namespace recognition. When set to true (the default) then somens:sometag is split up in a namespace part somens and a tag
     * part sometag. If set to false, the : character is considered part of a (non-standard) tag somens:sometag. Returns the boolean value of the old
     * setting.
     */
    public final boolean setRecognizeNamespaces(boolean recnsp)
    {
        boolean oldvalue = recognizeNamespaces;
        recognizeNamespaces = recnsp;
        return oldvalue;
    }

    /*
     * Used to set logging mode
     */
    private void setSectionBuffering(boolean buffering)
    {
        sectionBuffering = buffering;
    }

    /*
    *
    */
    private void clearSectionBuffer()
    {
        sectionBuffer.delete(0, sectionBuffer.length());
    }

    /*
     * strips off the last </..> ETag from the current sectionBuffer contents, NOT including preceding space chars.
     */
    private String getStrippedSectionBuffer()
    {
        if (sectionBuffer == null)
            return "";
        if(sectionBuffer.length()==0)
            return "";
        int endPos = sectionBuffer.length() - 1;
        while (endPos >= 0 && sectionBuffer.charAt(endPos) != '<')
            endPos--;
        endPos--;
//        while (endPos >= 0 && sectionBuffer.charAt(endPos) == ' ')
//            endPos--;
        return sectionBuffer.substring(0, endPos + 1);
    }

    /*
     *
     */
    private String getSectionBuffer()
    {
        if (sectionBuffer == null)
            return "";
        return sectionBuffer.toString();
    }

    /* atX methods ***************************************************** */

    /**
     * Tests whether the scanner is positioned at an STAG or an OPENSTAG. (Note that the value of completeSTags determines whether the actual token is
     * an STAG or an OPENSTAG; also, the atOpenSTag() can be used to ensure that the token is OPENSTAG, rather than an STAG, if desired.)
     * 
     * @return true if the scanner's token is STAG or OPENSTAG.
     */
    public final boolean atSTag() throws IOException
    {
        if (tokenConsumed)
            nextToken();
            if (debug) {    showTokenizerState(" atSTag RETURN ");        }
        return token == STAG;
    }

    /**
     * Tests whether the scanner is positioned at an start tag with the given name. The actual token can be STAG or OPENSTAG.
     * 
     * @param tagName The element name to be tested
     * @return true if the scanner's token is at an start tag and the name in the tag is equal to tagName.
     */
    public final boolean atSTag(String tagName) throws IOException
    {
        return atSTag() && this.tagName.equals(tagName);
    }

    /**
     * Tests whether the scanner is positioned at an end tag.
     * 
     * @return true if the scanner's token is ETAG.
     */
    public final boolean atETag() throws IOException
    {
        // System.out.println("atETag test1, consumed = " + tokenConsumed + ",token = " + token + ", ci = " + ((char)ci));
        if (tokenConsumed)
            nextToken();
        // System.out.println("atETag test1, token = " + token);
        return token == ETAG;
    }

    /**
     * Tests whether the scanner is positioned at an end tag with the given name.
     * 
     * @param tName The element name to be tested
     * @return true if the scanner's token is at an end tag and the name in the tag is equal to tagName.
     */
    public final boolean atETag(String tName) throws IOException
    {
        // System.out.println("atETag test2, consumed = " + tokenConsumed + ",token = " + token + ", ci = " + ((char)ci));
        // System.out.println("expect ETAG: " + tName + ", found: " + tagName);
        return atETag() && tagName.equals(tName);
    }

    /**
     * Tests whether the scanner is positioned at a processing instruction.
     * 
     * @return true if the scanner's token is PI.
     */
    public final boolean atPI() throws IOException
    {
        // System.out.println("atPI test, consumed = " + tokenConsumed);
        if (tokenConsumed)
            nextToken();
        // System.out.println("atPi test, token = " + token);
        return token == PI;
    }

    /**
     * tests whether the scanner is positioned at a comment.
     * 
     * @return true if the scanner's token is COMMENT.
     */
    public final boolean atComment() throws IOException
    {
        // System.out.println("atComment(), ci =" + ci + "(" + (char)ci + ") tokenConsumed:" + tokenConsumed);
        if (tokenConsumed)
            nextToken();
        return token == COMMENT;
    }

    /**
     * tests whether the scanner is positioned at a doctype comment.
     * 
     * @return true if the scanner's token is DOCTYPE.
     */
    public final boolean atDoctype() throws IOException
    {
        if (tokenConsumed)
            nextToken();
        return token == DOCTYPE;
    }

    /**
     * tests whether the scanner is positioned at a CDATA section.
     * 
     * @return true if the scanner's token is CDATA.
     */
    public final boolean atCDSect() throws IOException
    {
        if (tokenConsumed)
            nextToken();
        return token == CDSECT;
    }

    /**
     * tests whether the scanner is positioned at a doctype comment. and that the doctype name equals name.
     */
    public final boolean atDoctype(String name) throws IOException
    {
        return atDoctype() && doctypeName.equals(name);
    }

    /**
     * tests whether the scanner is positioned at CHARDATA
     * 
     * @return true if the scanner's token is CHARDATA.
     */
    public final boolean atCharData() throws IOException
    {
        if (tokenConsumed)
            nextToken();
        return token == CHARDATA;
    }

    /**
     * Tests whether the scanner is positioned at the end of the document.
     * 
     * @return true if the scanner's token is ENDOFDOCUMENT.
     */
    public final boolean atEndOfDocument() throws IOException
    {
        if (tokenConsumed)
            nextToken();
        return token == ENDOFDOCUMENT;
    }

    /* getX methods ***************************************************** */

    /**
     * returns the current token, without consuming it (unlike nextToken) If the current token was consumed, nextToken is called first.
     * 
     * @return The current token
     */
    public final int getToken() throws IOException
    {
        if (tokenConsumed)
            nextToken();
        return token;
    }

    /**
     * returns the current token in String format, without consuming it. If the current token was consumed, nextToken is called first.
     * 
     * @return The current token as String
     */
    public final String getTokenString() throws IOException
    {
        if (tokenConsumed)
            nextToken();
        return tokenString(token);
    }

    /**
     * Returns the current start tag.
     * 
     * @return The name in the current start tag
     */
    public final String getTagName() throws IOException
    {
        if (!(atSTag() || atETag()))
        {
            throw getXMLScanException("The XMLTokenizer was not at an STAG or an ETAG");
        }
        return tagName;
    }

    /**
     * Returns the namespace of the current tag.
     * 
     * @return The namespace of the current start tag
     */
    public final String getNamespace() throws IOException
    {
        if (!(atSTag() || atETag()))
        {
            throw getXMLScanException("The XMLTokenizer was not at an STAG or an ETAG");
        }
        return tagNamespace;
    }

    /**
     * reads the current comment, without advancing the scanner to the next token.
     * 
     * @return The current comment
     */
    public final String getComment() throws IOException
    {
        if (!atComment())
        {
            throw getXMLScanException("The XMLTokenizer was not at a COMMENT token");
        }
        return commentDataBuffer.toString();
    }

    /**
     * returns the doctype name, or null if not defined.
     */
    public final String getDoctypeName()
    {
        return doctypeName;
    }

    /**
     * 
     */
    public final String getPubidLiteral()
    {
        return pubidLiteral;
    }

    /**
     * 
     */
    public final String getSystemLiteral()
    {
        return systemLiteral;
    }

    /**
     * reads the current PI, without advancing the scanner to the next token.
     * 
     * @return The current pi
     */
    public final String getPI() throws IOException
    {
        if (!atPI())
        {
            throw getXMLScanException("The XMLTokenizer was not at a PI token");
        }
        return piDataBuffer.toString();
    }

    /**
     * Reads the current CHARDATA.
     * 
     * @return current CHARDATA.
     */
    public final String getCharData() throws IOException
    {
        if (!atCharData())
        {
            throw getXMLScanException("The XMLTokenizer was not at an CHARDATA token");
        }
        return charDataBuffer.toString();
    }

    /**
     * Reads the current CHARDATA, or "" when not at CharData, but at an ETag
     * 
     * @return current CHARDATA or ""
     */
    public final String getOptionalCharData() throws IOException
    {
        if (atCharData())
        {
            return charDataBuffer.toString();

        }
        else if (atETag())
        {
            return "";
        }
        else
        {
            throw getXMLScanException("The XMLTokenizer was not at a CHARDATA position");
        }
    }

    /**
     * Reads the current CHARDATA, and trims away surrounding blank space.
     * 
     * @return current CHARDATA.
     */
    public final String getTrimmedCharData() throws IOException
    {
        return getCharData().trim();
    }

    /**
     * Reads the current CDATA.
     * 
     * @return current CDATA.
     */
    public final String getCDSect() throws IOException
    {
        if (!atCDSect())
        {
            throw getXMLScanException("The XMLTokenizer was not at an CDATA token");
        }
        String cData = cDataBuffer.toString();
        return cData;
    }

    /**
     * returns the attributes HashMap, which has properly defined name/value pairs iff the current token is eitherOpenSTag, or STAG.
     */
    public final HashMap<String, String> getAttributes() throws IOException
    {
        if (!atSTag())
        {
            throw getXMLScanException("The XMLTokenizer was not at an STAG");
        }
        return attributes;
    }

    public final String getAttribute(String attributeName) throws IOException
    {
        if (!atSTag())
        {
            throw getXMLScanException("The XMLTokenizer was not at an STAG");
        }
        return attributes.get(attributeName);
    }

    /**
     * returns an Iterator for the attributes HashMap, which has properly defined name/value pairs iff the current token is eitherOpenSTag, or STAG.
     * The Iterator elements are of type Map.Entry, which accessor methods getKey() and getValue()
     */
    public Iterator getAttributeIterator() throws IOException
    {
        if (!atSTag())
        {
            throw getXMLScanException("The XMLTokenizer was not at an STAG");
        }
        return attributes.entrySet().iterator(); // attrMap.entrySet().iterator()
    }

    // /**
    // * Returns the attributes namespaces HashMap, which has properly defined name/value pairs
    // * iff the current token is eitherOpenSTag, or STAG.
    // */
    // public final HashMap<String, String> getAttributeNamespaces() throws IOException {
    // if (! atSTag() ) {
    // throw getXMLScanException("The XMLTokenizer was not at an STAG");
    // }
    // return attributeNamespaces;
    // }
    //
    // /**
    // * Returns the namespace String for a specified attribute name. If the attribute is not present,
    // * or when it had no namespace prefix, or an unbound namespace prefix, a null String is returned.
    // * Note that attributes do not belong to default namespaces, so attributes without prefix always
    // * will return a null result.
    // */
    // public final String getAttributeNamespace(String attributeName) throws IOException {
    // if (! atSTag() ) {
    // throw getXMLScanException("The XMLTokenizer was not at an STAG");
    // }
    // return attributeNamespaces.get(attributeName);
    // }

    /* takeX methods ***************************************************** */

    /**
     * checks whether the current token is an STAG, and consumes it. When the current token is OPENSTAG, the remainder of the STAG will be read and
     * consumed. The tagName of the STAG is returned.
     */
    public final String takeSTag() throws IOException
    {
        if (!atSTag())
            throw getXMLScanException("The XMLTokenizer was not at an STAG token");
        String result = getTagName();
        tokenConsumed = true;
        return result;
    }

    /**
     * checks whether the current token is an STAG, and consumes it. When the current token is OPENSTAG, the remainder of the STAG will be read and
     * consumed. The tagName of the STAG is returned.
     */
    public final void takeSTag(String tagName) throws IOException
    {
        if (!atSTag())
            throw getXMLScanException("The XMLTokenizer was not at an STAG token");
        if (!getTagName().equals(tagName))
            throw getXMLScanException(tagName + " tag expected, found: " + getTagName());
        tokenConsumed = true;
    }

    public final String takeETag() throws IOException
    {
        // System.out.println("takeETag, token = " + token + ", consumed = " + tokenConsumed);
        String result = getTagName();
        // System.out.println("takeETag, tagName = " + result);
        if (!atETag())
            throw getXMLScanException("The XMLTokenizer was not at an ETAG token");
        tokenConsumed = true;
        return result;
    }

    public final void takeETag(String tagName) throws IOException
    {
        // System.out.println("takeETag, token = " + token + ", consumed = " + tokenConsumed);
        if (!atETag())
            throw getXMLScanException("The XMLTokenizer was not at an ETAG token");
        if (!(getTagName().equals(tagName)))
            throw getXMLScanException("ETAG " + tagName + " expected, but found : " + getTagName());
        tokenConsumed = true;
    }

    public final String takeCharData() throws IOException
    {
        String result = getCharData(); // will throw exception if not at char data
        tokenConsumed = true;
        return result;
    }

    public final String takeOptionalCharData() throws IOException
    {
        if (atCharData())
        {
            tokenConsumed = true; // co nsume the CHARDATA token
            return charDataBuffer.toString();
        }
        else if (atETag())
        {
            return ""; // return empty string, but do not consume the ETag token
        }
        else
        {
            throw getXMLScanException("The XMLTokenizer was not at a CHARDATA position");
        }
    }

    public final String takeTrimmedCharData() throws IOException
    {
        String result = getTrimmedCharData(); // will throw exception if not at char data
        tokenConsumed = true;
        return result;
    }

    public final String takeCDSect() throws IOException
    {
        String result = getCDSect(); // will throw exception if not at CDATA
        tokenConsumed = true;
        return result;
    }

    public final String takePI() throws IOException
    {
        String result = getPI(); // will throw exception if not at PI
        tokenConsumed = true;
        return result;
    }

    public final String takeComment() throws IOException
    {
        String result = getComment(); // will throw exception if not at comment
        tokenConsumed = true;
        return result;
    }

    /**
     * expects an XML element of the form &lt;tagName&gt; value &lt;/tagName&gt;, where value is PCData content. In particular, mixed content is not
     * allowed. The (trimmed) text content is returned.
     */
    public String takeTextElement(String tagName) throws IOException
    {
        takeSTag(tagName);
        String result = "";
        if (atCharData())
        {
            result = takeCharData();
        }
        takeETag(tagName);
        return result.trim();
    }

    /**
     * expects an XML element of the form &lt;tagName&gt; value &lt;/tagName&gt;, where value encodes an integer value. The parsed integer is
     * returned.
     */
    public int takeIntElement(String tagName) throws IOException
    {
        return Integer.parseInt(takeTextElement(tagName));
    }

    /**
     * expects an XML element of the form &lt;tagName&gt; value &lt;/tagName&gt;, where value encodes a long value. The parsed long is returned.
     */
    public long takeLongElement(String tagName) throws IOException
    {
        return Long.parseLong(takeTextElement(tagName));
    }

    /**
     * expects an XML element of the form &lt;tagName&gt; value &lt;/tagName&gt;, where value encodes a float value. The parsed float is returned.
     */
    public float takeFloatElement(String tagName) throws IOException
    {
        return Float.parseFloat(takeTextElement(tagName));
    }

    /**
     * expects an XML element of the form &lt;tagName&gt; value &lt;/tagName&gt;, where value encodes a double value. The parsed double is returned.
     */
    public double takeDoubleElement(String tagName) throws IOException
    {
        return Double.parseDouble(takeTextElement(tagName));
    }

    /**
     * expects an XML element of the form &lt;tagName attributes /&gt; The attributes are returned, in the form of a HashMap.
     */
    public HashMap<String, String> takeEmptyElement(String tagName) throws IOException
    {
        HashMap<String, String> result = getAttributes();
        takeSTag(tagName);
        takeETag(tagName);
        return result;
    }

    /**
     * reads one (unparsed) character from the input stream. this is a low-level method, that should be used only for exceptional situations for
     * instance, when embedded data that does not conform to XML standards has to be processed.
     */
    public final int read() throws IOException
    {
        if (ci == CONSUMED)
        {
            nextChar();
        }
        int result = ci;
        ci = CONSUMED;
        return result;
    }

    /**
     * reads unparsed characters from the input stream, and returns them as String. Exactly len characters will be read, including "blank space".
     */
    public final String takeString(int len) throws IOException
    {
        // System.out.println("takeString(" + len + "), ci =" + ci);
        if (len <= 0)
            return "";
        // len >= 1
        StringBuilder buf = new StringBuilder(len);
        // the first character is the current ci value, if not yet consumed,
        // else, the first character must be read.
        if (ci == CONSUMED)
        {
            nextChar();
        }
        buf.append((char) ci);
        // read remaining characters (start at i=1)
        for (int i = 1; i < len; i++)
        {
            nextChar();
            if (ci < 0)
            {
                throw getXMLScanException("takeString(" + len + ") : EOS while reading String character nr " + i);
            }
            buf.append((char) ci);
        }
        // last character read belong to String, so ci is consumed:
        ci = CONSUMED;
        return buf.toString();
    }

    /*
     * defines the characters that can be used inside names, like tag names and attribute names. Currently, (unicode) "CombiningChars" and "Extenders"
     * are not included. Name characters are: letters, digits, and the four characters '_', '-', '.', and ':'. Note ':' characters are allowed for
     * names that use name spaces. The test is carried out on the current character ci.
     */
    private boolean isNameChar()
    {
        return ('a' <= ci && ci <= 'z') || ('A' <= ci && ci <= 'Z') || ('0' <= ci && ci <= '9') || (ci == '_') || (ci == '-') || (ci == '.')
                || (!recognizeNamespaces && ci == ':');
    }

    private boolean isNamespaceSepChar()
    {
        return (ci == ':');
    }

    /*
     * defines the characters that can be used as <it>first</it> character of names. Characters allowed are: letters, and the two characters '_' and
     * ':'. The test is carried out on the current character ci.
     */
    private boolean isNameStartChar()
    {
        return ('a' <= ci && ci <= 'z') || ('A' <= ci && ci <= 'Z') || (ci == '_') || (!recognizeNamespaces && ci == ':');
    }

    /*
     * defines the characters that classify as "Space" in XML. The test is carried out on the current character ci.
     */
    private boolean isSpaceChar()
    {
        return (ci == ' ' || ci == '\n' || ci == '\t' || ci == '\r');
    }

    /*
     * skips "blank space" characters. i.e skips ' ', '\n', '\t', and '\r' characters.
     */
    private void skipSpaceChars() throws IOException
    {
        while (ci == ' ' || ci == '\n' || ci == '\t' || ci == '\r')
        {
            nextChar();
        }
    }

    /*
     * reads the ci character from the input stream, without further processing.
     */
    private int nextChar() throws IOException
    {
        ci = in.read();
        charPos++;
        if (sectionBuffering)
        {
            sectionBuffer.append((char) ci);
        }
        if (ci == '\n')
        {
            line++;
            charPos = 0;
        }
        return ci;
    }

    /*
     * reads the ci character from the input stream, translates entity references like &lt; and fills the character ch. ci contains the last raw
     * character from "in"; ch contains the last parsed character, so for instance, when the input contains &gt;, ci contains ';', and ch contains
     * '>'. When the input contains '>', both ci and ch contain '>'. This translation is required while reading CHARDATA and ATTRIBUTE Values. It is
     * not to be used while reading/parsing tags, attribute names, PI's, comments etc.
     */
    public final void nextParsedChar() throws IOException
    {
        nextChar();
        if (ci == '&')
        {
            nextChar();
            switch (ci)
            {
            case 'l':
            {
                if (nextChar() != 't' || (nextChar()) != ';')
                    throw getXMLScanException("error in \"&lt;\" reference (char:" + (char) ci + ") ");
                ch = '<';
                break;
            }
            case 'g':
            {
                if (nextChar() != 't' || nextChar() != ';')
                    throw getXMLScanException("error in \"&gt;\" reference(char:" + (char) ci + ") ");
                ch = '>';
                break;
            }
            case 'q':
            {
                if (nextChar() != 'u' || nextChar() != 'o' || nextChar() != 't' || nextChar() != ';')
                    throw getXMLScanException("error in \"&quot;\"  ");
                ch = '"';
                break;
            }
            case 'a':
            { // could be &amp; or &apos;
                nextChar();
                if (ci == 'm')
                {
                    if (nextChar() != 'p' || (nextChar()) != ';')
                        throw getXMLScanException("error in \"&amp;\" reference(char:" + (char) ci + ") ");
                    else
                        ch = '&';
                }
                else if (ci == 'p')
                {
                    if (nextChar() != 'o' || nextChar() != 's' || nextChar() != ';')
                        throw getXMLScanException("error in \"&apos;\" reference(char:" + (char) ci + ") ");
                    else
                        ch = '\'';
                }
                else
                {
                    throw getXMLScanException("error in \"$amp;\" or \"&apos;\" reference(char:" + (char) ci + ") ");
                }
                break;
            }
            default:
            {
                throw getXMLScanException("unexpected character after \'&\'(char:" + (char) ci + ") ");

            }
            }
        }
        else
        {
            ch = (char) ci;
        }
    }

    /*
     * freezes the current line and character position.
     */
    private void setTokenPos()
    {
        tokenLine = line;
        tokenCharPos = charPos;
        if (ci == CONSUMED)
            tokenCharPos++;
    }

    /* clears the charDataBuffer buffer etc */

    private void clearBuffer(StringBuilder b)
    {
        b.delete(0, b.length());
    }

    // private void pushTag(String tag) {
    // pushTag(tag, 0);
    // }

    /*
     * push a tag symbol on the tagStack.
     */
   // private void pushTag(String tag, int namespaceDeclarationCount)
    private void pushTag(String tag)
    {
        tagStack.add(tag);
       // 
       // xmlnsCountStack.add(namespaceDeclarationCount);
    }

    /* return the current top of the stack, or "" when empty stack */
    private String topTag()
    {
        if (tagStack.isEmpty())
        {
            return "";
        }
        else
        {
            return tagStack.get(tagStack.size() - 1);
        }
    }

    /*
     * pops a tag symbol from the tagStack. throws an XMLScanException if tag is not at the current top, or if the stack is empty.
     */
    private void popTag(String tag)
    {
        if (tagStack.isEmpty())
        {
            String msg = "XML document not wellformed: ETAG \"" + tag + "\" without corresponding STAG";
            throw getXMLScanException(msg);
        }
        int topIndex = tagStack.size() - 1;
        String top = tagStack.get(topIndex);

        //int namespaceDeclarationCount = xmlnsCountStack.get(topIndex);
        tagStack.remove(topIndex);
        namespaceStack.popMark();
        defaultNamespace = namespaceStack.getNameSpace(""); // re-establish default name spec, after (potentially) popping the current default
        
//        xmlnsCountStack.remove(topIndex);
//
//        for (int i = 0; i < namespaceDeclarationCount; i++)
//        {
//            XMLNameSpace ns = namespaceStack.popXMLNameSpace();
//            if (ns.getPrefix() == "")
//            { // default namespace
//                defaultNamespace = null;
//                // removeDefaultNamespace = true;
//            }
//        }
        if (!top.equals(tag))
            throw getXMLScanException("XML document not wellformed: \"/" + tag + "\" read, while expecting: /" + top);
    }

    /**
     * checks whether the tagStack is empty. If not, an XMLScanException is thrown.
     */
    private void checkEmptyTagStack()
    {
        int stackSize = tagStack.size();
        if (stackSize > 0)
        {
            throw getXMLScanException("XML document not wellformed: Open tag " + tagStack.get(stackSize - 1) + " at end of document");
        }
    }

    /**
     * sets the mode of popping the status stack when the ENDOFDOCUMENT token is encountered within the input stream: auto popping enabled will
     * silently suppress the End-Of-Document tokens until the stack is empty and the ENDOFDOCUMENT is reached
     */
    public final void setpopOnEndOfDocument(boolean mode)
    {
        popOnEndOfDocument = mode;
    }

    /**
     * returns the status of the &quot;popOnEndOfDocument&quot; setting
     */
    public final boolean getpopOnEndOfDocument()
    {
        return popOnEndOfDocument;
    }

    /**
     * returns a String representation for each integer that represents a lexical token, like the String "STAG" for the integer STAG constant etc.
     * Useful for printing tag names in log messages.
     */
    public static String tokenString(int token)
    {
        switch (token)
        {
        case NULLTOKEN:
            return "NULLTOKEN";
        case STAG:
            return "STAG";
        case ETAG:
            return "ETAG";
        case CHARDATA:
            return "CHARDATA";
        case CDSECT:
            return "CDSECT";
        case COMMENT:
            return "COMMENT";
        case PI:
            return "PI";
        case DECL:
            return "DECL";
        case DOCTYPE:
            return "DocType";
        case ENDOFDOCUMENT:
            return "ENDOFDOCUMENT";
        case ERRORTOKEN:
            return "XML Error";
        default:
            return "";
        }
    }

    private static boolean debug = false;

    /*
     * sets the debug mode; 
     */
    public static void setDebug(boolean mode)
    {
        debug = mode;
    }

    /**
     * sets the modes to their current default values.
     */
    public final void setDefaultModes()
    {
        skipDoctype = SKIPDOCTYPE;
        skipComment = SKIPCOMMENT;
        skipPI = SKIPPI;
        recognizeNamespaces = RECOGNIZENAMESPACES;
    }

    /* Possible token values: */
    public static final int NULLTOKEN = 0;
    public static final int STAG = 1; // a complete STAG, including attributes
    public static final int ETAG = 4; // a complete Etag: "</tagName>; (also generated for empty tags)
    public static final int CHARDATA = 8; // parsed character data
    public static final int CDSECT = 9; // unparsed character data: <![CData[ .... ]]>
    public static final int COMMENT = 10; // comment: <!-- ..... -->
    public static final int PI = 11; // processing instruction: <?.....?>
    public static final int DECL = 12; // other declarations,: <! ..... >
    public static final int DOCTYPE = 13; // document type declaration: <!DOCTYPE ... >
    public static final int ENDOFDOCUMENT = 14; // token returned when end of input stream has been reached.
    public static final int ERRORTOKEN = 15; // token returned when an RuntimeException occurs

    /*
     * Properties defining how the XMLTokenizer should handle whitespaces in character data "ignorable white space" is a sequence of white space in
     * between markup. if preserveIgnorableWS == true, such white space is considered CHARDATA, otherwise (the default) such white space is ignored.
     * White space around non-white content can be trimmed away, by setting trimWS to true.
     */
    // private boolean preserveIgnorableWS;
    // private boolean trimWS;

    /*
     * Properties defining what type of info should be skipped by scanner
     */
    private boolean skipDoctype;
    private boolean skipComment;
    private boolean skipPI;
    // private int trace = 0;

    /*
     * recognizeNamespace determines whether a tag like somens:sometag will be recognized as a namespace somens plus a tag sometag or, alternatively,
     * that is is regarded as a special tag that included a : character. The latter is not officially allowed by XML standards, and is only meant to
     * deal with namespaces in a simplified way.
     */
    private boolean recognizeNamespaces;

    /*
     * "in" is the current input stream, in Reader format. It might actually be a PushbackReader, which is denoted by the value of the boolean
     * "isPushbackReader". The amount of buffering for "in" is kept to a minimum, so as to allow other processes (i.e. outside this XMLTokenizer) to
     * share access to the "in" Reader. For STAG and ETAG tokens, only the characters belonging properly to the token are read from "in". For names
     * and for CHARDATA, the character immediately following the token is necesarily read also. It is possible to set "in" to a PushbackReader. In
     * this case, pushback() can be called, which will push back the last character read, or, after reading an OPENSTAG token, pushbackOpenSTag() will
     * push back the complete "<tagName " String, (including the character following the OPENSTAG).
     */
    private BufferedReader in; // the "current input stream", in Reader format.

    /*
     * url and/or file are the URL and/or File (if applicable) corresponding to the current Reader.
     */
    // protected String urlSpec;
    private URL url;
    private URL baseUrl;
    private File file;


    /*
     * The Resources root or baseDir and fileName.
     * resourceRoot != null implies baseDir == null, and vice versa. 
     * fileName == null (not used yet) could indicate a Reader or Stream not from file or URL 
     */
     private String resourceRoot = null; // was: "";
     private String baseDir = null; // alternative for resourceRoot
     private String fileName = null; // was: resourceFile = "";
    /*
     * ci is a one-place buffer, containing the "current" unparsed character. It has int type, rather than char type, since reading the input stream
     * yields ints, which can be negative: EOS (= -1) denotes end-of-stream reached, CONSUMED (=-2) means that the current ci value is considered
     * "consumed". When "consumed", the one place buffer is considered "empty", and a new value for ci should be read from "in" before further
     * processing. The "consumed" state allows for other processes than the process running this XMLTokenizer object to start reading from the same
     * Reader, without losing even a single character, since no character is buffered in the XMLTokenizer when ci is CONSUMED.
     */
    private int ci; // the "current character", in int format.

    /*
     * ch is the current parsed character. It is used inside CHARDATA sections, and is normally just ci converted to char type. "Parsed" implies that
     * entity references (like "&lt;") that occur in the "raw" input, will end up as a single ch value. For instance, the ci character sequence "&lt";
     * will be transformed into a '<' ch value).
     */
    private char ch; // the "current parsed character", in char format.
    private int line; // number of input lines read thus far, starting at line 1.
    private int charPos; // position of the current character.
    private int tokenLine; // starting line number for the current token.
    private int tokenCharPos; // starting character position for the current token.
    private int token; // the "current lexical token", like STAG etc.
    private StringBuilder sectionBuffer = new StringBuilder(SECTIONBUFSIZE); //
    private boolean sectionBuffering;
    // private boolean removeDefaultNamespace = false; // used to remove the default namespace upon the next ETag processing
    // private boolean etagFence;// if true, an ETAG with tagName equal to fenceTagName cannot be passed.
    // private String fenceTagName;// Tag that cannot be passed
    /*
     * unused private boolean blocking = true; // denotes whether the tokenizer should block if necessary // when the Reader is not ready.
     */

    /*
     * the current token can be "consumed", which means that getX and takeX methods will first read a new token, when they are called. However,
     * "consumed" does not mean that the input Reader has advanced as yet. This allows for other processes to read from the same Reader, without
     * "missing" input that is buffered by this XMLTokenizer.
     */

    private boolean tokenConsumed; // denotes whether the current token has been "consumed".
    private int tokenMode; // the "current (tag) mode ", indicates position within STags.

    /* buffers for collecting tag names, attribute names, attribute values, char data, comments, pi's */
    private String tagName; // tagNames and attributeNames are immediately converted to Strings.
    private String tagPrefix; // optional namespace label (i.e the prefix before the :) for tagName
    private String tagNamespace; // optional namespace for tagName
    private String defaultNamespace = null; // default namespace
    private String attributeName;
    private String attributePrefix; // optional namespace prefix for attributeName
    private String doctypeName; // name of DOCTYPE
    private String pubidLiteral; // public Id of DOCTYPE
    private String systemLiteral; // system id of DOCTYPE
    // attributeValues, charData, piData, commentData remains in StringBuilder form until retrieved
    // by a getX or takeX method.

    private static final int BUFSIZESMALL = 16;
    private static final int BUFSIZELARGE = 128;

    private StringBuilder tagNameBuffer = new StringBuilder(BUFSIZESMALL);
    // private StringBuilder namespaceBuffer = new StringBuilder(BUFSIZESMALL);
    private StringBuilder attributeNameBuffer = new StringBuilder(BUFSIZESMALL);
    private StringBuilder attributeValueBuffer = new StringBuilder(BUFSIZELARGE);
    private StringBuilder charDataBuffer = new StringBuilder(BUFSIZELARGE);
    private StringBuilder cDataBuffer = new StringBuilder(BUFSIZELARGE);
    private StringBuilder piDataBuffer = new StringBuilder(BUFSIZESMALL);
    private StringBuilder commentDataBuffer = new StringBuilder(BUFSIZELARGE);
    private StringBuilder buf;

    private static final int TAGSTACKSIZE = 32;
    private ArrayList<String> tagStack = new ArrayList<String>(TAGSTACKSIZE); // stack of tagnames, used to check wellformednes
    private XMLNameSpaceStack namespaceStack = new XMLNameSpaceStack(); // stack of currently declared namespaces, including default namespace
    
    //private ArrayList<XMLNameSpace> XMLNameSpaceDeclarations = new ArrayList<XMLNameSpace>(8); // temp buffer for new namespace declartions, before pushing on the namespaceStack
    
   // private ArrayList<Integer> xmlnsCountStack = new ArrayList<Integer>(); // counts of number of xmlns declarations per STag element on the stack

    private ArrayList<TokenizerState> tokenizerStateStack; // stack of XMLParserState objects
    private boolean popOnEndOfDocument = false; // indicates whether to (automatically) pop the status stack
    // when ENDOFDOCUMENT is encountered.
    // private ArrayList<Object> contextStack; // stack of context Objects

    /*
     * when completeSTags is set to true, the attributes HashMap is used to collect all attribute value pairs.
     */
    private java.util.HashMap<String, String> attributes = new java.util.LinkedHashMap<String, String>();

    /*
     * when completeSTags is set to true, the attributes HashMap is used to collect all attribute name spaces, if defined
     */
    // private java.util.HashMap<String, String> attributeNamespaces = new java.util.LinkedHashMap<String, String>();

    // /*
    // * The map from namespace labels to namespace strings
    // */
    // private java.util.HashMap<String, String> namespaceMap = new java.util.LinkedHashMap<String, String>();

    /*
     * The context attribute is not necessary for the functioning of the XMLTokenizer itself Rather, it is a generic Java HashMap that could be
     * something like a symbol table that needs to be passed on between a collection of methods for a recursive decent parser it is allocated only
     * when context methods are being used.
     */
    // private Object context;

    /* possible tokenMode values: */
    private static final int CHARDATA_MODE = 1; // "default mode", CHARDATA unless '<' or EOS
    // private static final int STAGTAIL_MODE = 2; // inside an STAG tail, expecting attributes or '>'
    // private static final int ATTRIBUTEVALUE_MODE = 3; // inside STAG, expecting "=attributeValue" part.
    private static final int PENDING_ETAG_MODE = 4; // at end of an "Empty Tag" ("<..../>"), with "pending ETAG token".
    private static final int ENDOFDOCUMENT_MODE = 5; // mode after reaching ENDOFDOCUMENT.

    public static final int EOS = -1; // end-of-stream char
    public static final int CONSUMED = -2; // ci has been consumed without actually reading next char from input stream.



    /*
     * push current XMLTokenizer status on the status stack
     */
    public final void pushState()
    {
        if (tokenizerStateStack == null)
        {
            tokenizerStateStack = new ArrayList<TokenizerState>();
        }
        TokenizerState currentState = new TokenizerState();
        currentState.copyState();
        tokenizerStateStack.add(currentState);
    }

    /*
     * pops the status stack, and restores the current XMLTokenizer state from the top stack element.
     */
    public final void popState()
    {
        int stackSize = tokenizerStateStack.size();
        TokenizerState savedState = tokenizerStateStack.get(stackSize - 1);
        tokenizerStateStack.remove(stackSize - 1);
        savedState.restoreState();
    }

    /*
     * prints the current contents of the stack on the Console
     */
    public final void showTokenizerStack()
    {
        showTokenizerStack("TokenizerStack:\n");
    }

    /*
     * prints the current contents of the stack on the Console
     */
    public final void showTokenizerStack(String message)
    {
        if (tokenizerStateStack == null)
        {
            logger.info("Null TokenizerStateStack");
        }
        else
        {
            logger.info(message + "\n" + tokenizerStateStack.toString());

        }
    }

    /*
     * prints the current contents of the stack on the Console
     */
    public final void showTokenizerState()
    {
        showTokenizerState("TokenizerState:\n");
    }

    /*
     * logs the current contents of the stack
     */
    public final void showTokenizerState(String message)
    {
        TokenizerState currentState = new TokenizerState();
        currentState.copyState();
        logger.warn(message + currentState);
    }

    /**
     * TokenizerState objects are used to save and restore the current "state" of this XMLTokenizer on the stack.
     */
    private class TokenizerState
    {
        private BufferedReader inState;
        private URL urlState;
        private File fileState;
        private String resourceRootState;
        private String baseDirState;
        private String fileNameState;
        private int ciState;
        private char chState;
        private int lineState;
        private int charPosState;
        private int tokenCharPosState;
        private int tokenState;
        private int tokenModeState;
        private boolean tokenConsumedState;
        private String tagNameState;
        private String namespaceState;
        private boolean popOnEndOfDocumentState;

        /* save current XMLTokenizer state in this TokenizerState Object */
        void copyState()
        {
            inState = in;
            urlState = url;
            fileState = file;
            resourceRootState = resourceRoot;
            baseDirState = baseDir;
            fileNameState = fileName;
            ciState = ci;
            chState = ch;
            lineState = line;
            charPosState = charPos;
            tokenCharPosState = tokenCharPos;
            tokenState = token;
            tokenModeState = tokenMode;
            tokenConsumedState = tokenConsumed;
            tagNameState = tagName;
            namespaceState = tagNamespace;
            popOnEndOfDocumentState = popOnEndOfDocument;
        }

        /* restore XMLTokenizer state from this TokenizerState Object */
        public void restoreState()
        {
            in = inState;
            url = urlState;
            file = fileState;
            resourceRoot = resourceRootState;
            baseDir = baseDirState;
            fileName = fileNameState;
            ci = ciState;
            ch = chState;
            line = lineState;
            charPos = charPosState;
            tokenCharPos = tokenCharPosState;
            token = tokenState;
            tokenMode = tokenModeState;
            tokenConsumed = tokenConsumedState;
            tagName = tagNameState;
            tagNamespace = namespaceState;
            popOnEndOfDocument = popOnEndOfDocumentState;
        }

        @Override
        public String toString()
        {
            StringBuilder buf = new StringBuilder("[");
            buf.append(" url: ");
            buf.append(urlState);
            buf.append(" file: ");
            buf.append(fileState);
            buf.append(" resourceRoot: ");
            buf.append(resourceRootState);
            buf.append(" baseDir: ");
            buf.append(baseDirState);
            buf.append(" fileName: ");
            buf.append(fileNameState);
            buf.append(" ch: ");
            buf.append(chState);
            buf.append(" ci: ");
            buf.append(ciState);
            buf.append(" line: ");
            buf.append(lineState);
            buf.append(" charPos: ");
            buf.append(charPosState);
            buf.append(" tokenCharPos: ");
            buf.append(tokenCharPosState);
            buf.append(" token: ");
            buf.append(tokenString(tokenState));
            buf.append(" tagName: ");
            buf.append(tagNameState);
            buf.append(" consumed: ");
            buf.append(tokenConsumedState);
            buf.append(" popEOD: ");
            buf.append(popOnEndOfDocumentState);
            buf.append(']');
            return buf.toString();
        }
    } // TokenizerState

}
