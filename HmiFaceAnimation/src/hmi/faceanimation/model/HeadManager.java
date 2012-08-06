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

    /*
     * @Deprecated public void serializeHead() { try { FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream out = new
     * ObjectOutputStream(fos); out.writeObject(head); fos.close(); out.close(); } catch (IOException ex) { System.err.println("Error writing " + file
     * + ": " + ex.getMessage()); } }
     * 
     * @Deprecated public Head unserializeHead() { try { FileInputStream fis = new FileInputStream(file); ObjectInputStream in = new
     * ObjectInputStream(fis); head = (Head) in.readObject(); fis.close(); in.close(); } catch (IOException ex) { System.err.println("Error reading "
     * + file + ": " + ex.getMessage()); } catch (ClassNotFoundException e) { e.printStackTrace(); }
     * 
     * return head; }
     * 
     * @Deprecated public Head readFile() { try { FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr);
     * head.parseContent(br); fr.close(); } catch (IOException ex) { System.err.println("Error reading " + file + ": " + ex.getMessage()); }
     * 
     * return head; }
     * 
     * @Deprecated public void writeFile() { try { FileWriter fw = new FileWriter(file); StringBuilder sb = new StringBuilder();
     * head.appendContent(sb, 0); fw.write(sb.toString()); fw.close(); } catch (IOException ex) { System.err.println("Error writing " + file + ": " +
     * ex.getMessage()); } }
     */

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
