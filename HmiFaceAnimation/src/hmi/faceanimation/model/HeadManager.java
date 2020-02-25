/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
