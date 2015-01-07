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
package hmi.faceanimation.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles the simple task of putting a head to sleep and of waking it up again when needed.
 * 
 * @author ronald
 */
public class HeadManager
{
    private BufferedReader br;
    private Head head;

    public HeadManager(BufferedReader br)
    {
        this.br = br;
    }

    public void setHead(Head head)
    {
        this.head = head;
    }

    public Head readXMLFile()
    {
        // FIXME
        if (head == null || br == null)
            return null;

        try
        {
            head.readXML(br);
            br.close();
        }
        catch (IOException ex)
        {
            System.err.println("Error reading: " + ex.getMessage());
        }

        return head;
    }

    public void writeXMLFile(File file)
    {
        try
        {
            PrintWriter pw = new PrintWriter(file);
            head.writeXML(pw);
            pw.close();
        }
        catch (IOException ex)
        {
            System.err.println("Error writing " + file + ": " + ex.getMessage());
        }
    }
}
