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
