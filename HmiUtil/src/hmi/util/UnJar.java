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

package hmi.util;

import javax.swing.*;
import java.io.*;
import java.util.jar.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.text.*;
import java.net.*;

/**
 * UnJar extracts files from a jar file
 * 
 * @author Job Zwiers
 */
public final class UnJar
{

    /*
     * Disable UnJar Object creation
     */
    private UnJar()
    {
    }

    /**
     * Return a String with the codesouce location of the specified Class. This could denote some jar file, or some directory like ..../classes
     */
    public static String getJarForClass(Class<?> cl)
    {
        try
        {
            URI jarUri = cl.getProtectionDomain().getCodeSource().getLocation().toURI();
            return jarUri.toString();
        }
        catch (URISyntaxException e)
        {
            hmi.util.Console.println("Unjar: " + e);
            return "";
        }
    }

    /**
     * Extracts files, assuming that this UnJar class is running from the same jar file that contains the files to be extracted. The regular
     * expression specifies which files to extract.
     */
    public static List<String> autoExtract(String regex, String extractDirectory, boolean listOnly, boolean overwrite)
    {
        return extract(hmi.util.UnJar.class, regex, extractDirectory, listOnly, overwrite);
    }

    // /**
    // * Extracts files with names that end with &quot;.fileType&quot;,
    // * from a named jar file.
    // */
    // public static List<String> extractType(String jar, String fileType, String dir) {
    // return extract(jar, ".*\\." + fileType, dir);
    // }

    /**
     * Like extract(jar, regex, extractDirectory) where the jar file is located by means of UnJar.getJarFromClass(cl)
     */
    public static List<String> extract(Class<?> cl, String regex, String extractDirectory, boolean listOnly, boolean overwrite)
    {
        return extract(getJarForClass(cl), regex, extractDirectory, listOnly, overwrite);
    }

    /**
     * Extracts files with names that match the specified regular expression from a named jar file. Extracted files are placed in the specified
     * directory. The latter must exist already. If extracted files have some non-trivial path, subdirectories will be created as necessary. files are
     * not automaticaly overwritten.
     */
    public static List<String> extract(String jar, String regex, String extractDirectory)
    {
        return extract(jar, regex, extractDirectory, false, false);
    }

    /**
     * Lists files with names that match the specified regular expression from a named jar file. The extractDirectory path is prepended, just like it
     * is done for the UnJar.extract operations. Note that <em>all</em> matching files are included, like the result for an extract operation with
     * overwrite equal to true.
     */
    public static List<String> list(String jar, String regex, String extractDirectory)
    {
        return extract(jar, regex, extractDirectory, true, true);
    }

    /**
     * Extracts files with names that match the specified regular expression from a named jar file. Extracted files are placed in the specified
     * directory. The latter must exist already. If extracted files have some non-trivial path, subdirectories will be created as necessary.
     */
    public static List<String> extract(String jar, String regex, String extractDirectory, boolean listOnly, boolean overwrite)
    {
        File jarFile = null;
        if (jar.startsWith("file:"))
        { // assume it's a file URI or file URL
            try
            {
                URI jarUri = new URI(jar);
                jarFile = new File(jarUri);
            }
            catch (URISyntaxException e)
            {
                throw new RuntimeException("Unjar: " + e);
            }
        }
        else
        { // assume it's a local file name
            jarFile = new File(jar);
        }
        File dir = new File(extractDirectory);
        if (!dir.exists() || !dir.isDirectory())
        {
            throw new RuntimeException("Unjar: Cannot find directory " + extractDirectory);
        }
        return extract(jarFile, regex, dir, listOnly, overwrite);
    }

    /**
     * Extracts files with names that match the specified regular expression from a jar File, and puts the result in a specified directory. The latter
     * must exist already. If extracted files have some non-trivial path, subdirectories will be created as necessary.
     */
    public static List<String> extract(File jarfile, String regex, File extractDir)
    {
        return extract(jarfile, regex, extractDir, false, false);
    }

    private static final int DIALOGWIDTH = 400;
    private static final int DIALOGHEIGHT = 200;

    /**
     * Extracts files with names that match the specified regular expression from a jar File, and puts the result in a specified directory. The latter
     * must exist already. If extracted files have some non-trivial path, subdirectories will be created as necessary. When listOnly is true, files
     * are added to the List returned, but no actual file extraction is done. When overwrite is true, files are unconditionally extracted (or listed).
     * When overwrite is false, then existing files are overwritten only when they differ in size and/or timestamp. Moreover, a popup panel asks for
     * permission to overwrite in these cases.
     */
    public static List<String> extract(File jarfile, String regex, File extractDir, boolean listOnly, boolean overwrite)
    {
        // hmi.util.Console.println("extracting " + jarfile + " to " + extractDir);
        ArrayList<String> result = new ArrayList<String>();
        JarFile jarFile = null;
        try
        {
            jarFile = new JarFile(jarfile);
        }
        catch (IOException e)
        {
            hmi.util.Console.println("UnJar: " + e);
            return result;
        }
        String pex = ".*" + regex;
        // hmi.util.Console.println("regex=" + pex);
        Enumeration<JarEntry> enu = jarFile.entries();
        while (enu.hasMoreElements())
        {
            JarEntry je = enu.nextElement();
            String jeName = je.getName();
            // Console.println("jar file Element: " + jeName);
            if (jeName.matches(pex))
            {
                // hmi.util.Console.println("match! " + jeName);
                File jeFile = new File(extractDir, jeName);
                if ((!overwrite) && jeFile.exists())
                {
                    // hmi.util.Console.println("file exists!");
                    long jeTime = je.getTime();
                    Date jeDate = new Date(jeTime);
                    long fileTime = jeFile.lastModified();
                    Date fileDate = new Date(fileTime);
                    long jeSize = je.getSize();
                    long fileSize = jeFile.length();
                    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
                    String fileDateString = dateFormat.format(fileDate);
                    String jeDateString = dateFormat.format(jeDate);
                    String optionMessage = "Existing " + jeName + " from    " + fileDateString + "\nReplacement file from " + jeDateString;
                    if (jeTime != fileTime || jeSize != fileSize)
                    {
                        String[] options = new String[] { "Yes", "No", "Cancel" };
                        JOptionPane jpane = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null,
                                options, null);
                        JDialog dialog = jpane.createDialog(null, null);
                        dialog.setSize(DIALOGWIDTH, DIALOGHEIGHT);
                        if (jeTime < fileTime)
                        {
                            String msg = optionMessage + "\nReplace existing file with older version?";
                            jpane.setMessage(msg);
                            jpane.setInitialValue("No");
                        }
                        else if (jeTime > fileTime)
                        {
                            String msg = optionMessage + "\nReplace  existing file with newer version?";
                            jpane.setMessage(msg);
                            jpane.setInitialValue("Yes");
                        }
                        else
                        {
                            String msg = "Existing file has size " + fileSize + "\n Replacement file has size " + jeSize
                                    + "\n Replace existing file?";
                            jpane.setMessage(msg);
                            jpane.setInitialValue("Yes");
                        }
                        dialog.setVisible(true);
                        String selectedValue = (String) jpane.getValue();
                        if (selectedValue == null)
                        {
                            return result;
                        }
                        else if (selectedValue.equals("Yes"))
                        {
                            if (!listOnly) extractJarEntry(jarFile, je, jeFile);
                            result.add(jeFile.toString());
                        }
                        else if (selectedValue.equals("Cancel"))
                        {
                            return result;
                        }
                        // else {
                        // //Console.println("No");
                        // }
                    }
                    // else {
                    // //Console.println("Existing file, with identical size and date, not replaced");
                    // }
                }
                else
                { // file does not yet exist
                  // hmi.util.Console.println("Extract new file: " + jeName);
                    if (!listOnly) extractJarEntry(jarFile, je, jeFile);
                    result.add(jeFile.toString());
                }
            }
        }
        return result;
    }

    private static final int BUFSIZE = 1024;

    /* extracts the file */
    private static void extractJarEntry(JarFile jarFile, JarEntry jarEntry, File outputFile)
    {
        try
        {
            // outputFile.deleteOnExit();
            // hmi.util.Console.println("extractJarEntry from file: " + jarFile.getName() + " , entry = " + jarEntry.getName() + " output = " +
            // outputFile);

            // hmi.util.Console.println("open inputstream");
            InputStream ips = new BufferedInputStream(jarFile.getInputStream(jarEntry));
            String parentDir = outputFile.getParent();
            // hmi.util.Console.println("open outputstream, parent = " + parentDir);

            File parDir = new File(parentDir);
            // hmi.util.Console.println("mkdirs...");
            if (!parDir.mkdirs())
            {
                System.out.println("UnJar.extractJarEntry: could not create parent directory for " + outputFile.getName());
            }

            OutputStream ops = new BufferedOutputStream(new FileOutputStream(outputFile));
            byte[] buf = new byte[BUFSIZE];
            int nbytes = ips.read(buf);
            while (nbytes > 0)
            {
                ops.write(buf, 0, nbytes);
                nbytes = ips.read(buf);
            }
            ops.flush();
            ops.close();
            ips.close();
            if (!outputFile.setLastModified(jarEntry.getTime()))
            {
                System.out.println("UnJar.extractJarEntry: could not preserve timestamp for " + outputFile.getName());
            }
            // Console.println("extracted ...");
        }
        catch (IOException e)
        {
            Console.println("UnJar - Failed to extract " + jarEntry.getName() + ": " + e);
        }
    }

    public static String getCodeSourceLocation()
    {
        try
        {
            URI jarUri = hmi.util.UnJar.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            // hmi.util.Console.println("UnJar jarUri: " + jarUri);
            return jarUri.toString();
        }
        catch (URISyntaxException e)
        {
            hmi.util.Console.println("Unjar: " + e);
            return "--";
        }
    }

    /**
    * 
    */
    public static void main(String[] arg)
    {
        // extractType("jogldll.jar", "dll");
        // String tmpdir = System.getProperty("java.io.tmpdir");
        // hmi.util.Console.println("tmpdir = " + tmpdir);
        // extractType("DLL.jar", "dll", tmpdir);
        try
        {
            URI jarUri = hmi.util.UnJar.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            hmi.util.Console.println("UnJar jarUri: " + jarUri);
            // extract(jarUri, regex, extractDirectory);
        }
        catch (URISyntaxException e)
        {
            hmi.util.Console.println("Unjar: " + e);

        }
    }
}
