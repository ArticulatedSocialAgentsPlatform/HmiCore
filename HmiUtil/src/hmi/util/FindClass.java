package hmi.util;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableSet;

/**
 * Finds classes in the classpath
 * Based on FindClass, which was released under LGPL
 * <p>
 * See http://www.nsydenham.net/java/FindClass/FindClass.shtml for details.
 * </p>
 * @author welberge
 */
@Slf4j
public class FindClass
{
    private List<String> locations = new ArrayList<String>();
    private List<Class<?>> matches = new ArrayList<Class<?>>();
    private String selectedPackageName = null;
    private boolean recurse = true;
    private boolean matchArchives = false;
    private final static String fs = System.getProperty("path.separator");
    private final static String fileSep = System.getProperty("file.separator");
    public final static String version = "1.2";

    // constants
    private final static String JAR = "jar";
    private final static String ZIP = "zip";

    /** Filters for jar, zip and class files plus directories */
    private static class JavaFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            if (file.isDirectory()) return true;
            else
            {
                String lName = file.getName().toLowerCase();
                if (lName.endsWith(JAR) || lName.endsWith(ZIP))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /** Iterate through locations looking for classes */
    public ImmutableSet<Class<?>> findClasses()
    {
        // add system classpath
        if (locations.size() == 0)
        {
            addLocations(System.getProperty("java.class.path"));
        }

        if (locations.size() > 0)
        {
            searchFiles();
        }
        return ImmutableSet.copyOf(matches);
    }

    /**
     * Iterate through locations looking for classes
     * that have a certain package
     */
    public ImmutableSet<Class<?>> findClasses(String packageName)
    {
        this.selectedPackageName = packageName;
        ImmutableSet<Class<?>> allClasses = findClasses();
        return ImmutableSet.copyOf(allClasses);
    }

    /** Search for files in the path */
    private void searchFiles()
    {
        String location;
        Iterator<String> lit;

        lit = locations.iterator();
        while (lit.hasNext())
        {
            location = lit.next();

            if (location.toLowerCase().endsWith(JAR) || location.toLowerCase().endsWith(ZIP))
            {
                findInArchive(location);
            }
            else
            {
                findInDirectory(location);
            }
        }

    }

    /**
     * See if the file exists in the specified JAR or ZIP file
     * @param fileName name of the file to look for
     * @param archive name of the archive to examine
     */
    private void findInArchive(String archive)
    {
        ZipEntry ze;
        JarFile jf;

        // use a JarFile because it can also read zips
        try
        {
            jf = new JarFile(archive);
        }
        catch (IOException e)
        {
            return;
        }

        Enumeration<JarEntry> enu = jf.entries();
        while (enu.hasMoreElements())
        {
            ze = enu.nextElement();

            if (ze.getName().endsWith("class"))
            {
                String className = ze.getName();
                className = className.substring(0, className.length() - 6);
                className = className.replace('/', fileSep.charAt(0));
                Class<?> c = transformToClass(className);

                if (c != null)
                {
                    matches.add(c);
                }
            }
        }
        try
        {
            jf.close();
        }
        catch (IOException e)
        {
            log.warn("Cannot close jar ", e);
        }
    }

    private Class<?> transformToClass(String fileName)
    {
        // input is a string containing the full filename for a classfile.
        // output is a corresponding Class object.
        // packagename is determined by trying to recognize the longest
        // package-name postfix to the path.
        // String fileName = (String)o;
        // System.out.println("Filename: " + fileName);
        String className = fileName.substring(fileName.lastIndexOf(fileSep) + 1);
        // System.out.println("classname: " + className);
        String packageName = fileName.substring(0, fileName.lastIndexOf(fileSep) + 1);
        packageName = packageName.replace(fileSep.charAt(0), '.');
        while (packageName.endsWith("."))
        {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        if (selectedPackageName != null)
        {
            if (!packageName.equals(selectedPackageName)) return null;
        }
        // System.out.println("Initial packagename: " + packageName);
        Class<?> result = null;
        while (result == null)
        {
            String fullName = className;
            if (!packageName.equals(""))
            {
                fullName = packageName + "." + fullName;
            }
            // System.out.println("trying fullname: " + fullName);
            try
            {
                result = Class.forName(fullName);
                break; // succesful: then return this class
            }
            catch (Exception ex)
            {
                // System.out.println("----------error creating class object. info:---------");
                // System.out.println(ex);
                // ex.printStackTrace();
                // System.out.println("----------end info-----------------------------------");
            }
            catch (Error ex)
            {
                // System.out.println("----------error creating class object. info:---------");
                // System.out.println(ex);
                // ex.printStackTrace();
                // System.out.println("----------end info-----------------------------------");
            }
            if ((packageName.indexOf(".") < 0) || (packageName.indexOf(".") == packageName.length() - 1))
            {
                packageName = "";
                // System.out.println("no packageName: " + className);
                try
                {
                    result = Class.forName(className);
                    break; // succesful: then return this class
                }
                catch (Exception ex)
                {
                    // System.out.println("----------***error creating class object. info:---------");
                    // System.out.println(ex);
                    // ex.printStackTrace();
                    // System.out.println("----------end info-----------------------------------");
                }
                catch (Error ex)
                {
                    // System.out.println("----------error creating class object. info:---------");
                    // System.out.println(ex);
                    // ex.printStackTrace();
                    // System.out.println("----------end info-----------------------------------");
                }
                break; // nothing more to guess, no package... probably an inaccessible class? log deze classes maar eens
            }
            packageName = packageName.substring(packageName.indexOf(".") + 1);
            // System.out.println("trying packageName: " + packageName);
        }
        return result;
    }

    private void findInDirectory(String dir)
    {
        findInDirectory(dir, dir);
    }

    /**
     * See if the file exists in the specified directory
     * @param fileName name of the file to look for
     * @parm dir name of the directory to search
     */
    private void findInDirectory(String baseDir, String dir)
    {
        File dirFile = new File(dir);
        File dirEntries[];
        File dirEntry;
        String name;
        
        // check directory is valid
        if (!dirFile.exists())
        {
            return;
        }
        else if (!dirFile.isDirectory())
        {
            return;
        }

        if (matchArchives)
        {
            dirEntries = dirFile.listFiles(new JavaFilter());
        }
        else
        {
            dirEntries = dirFile.listFiles();
        }

        for (int i = 0; i < dirEntries.length; i++)
        {
            dirEntry = dirEntries[i];
            name = dirEntry.getName();

            if (recurse && dirEntry.isDirectory())
            {
                findInDirectory(baseDir, dirEntry.getAbsolutePath());
            }
            else if (name.endsWith(JAR) || name.endsWith(ZIP))
            {
                findInArchive(dirEntry.getAbsolutePath());
            }
            else if (name.endsWith("class"))
            {
                String className = dirEntry.getPath().substring(baseDir.length() + 1);
                className = className.substring(0, className.length() - 6);
                // System.out.println(className);
                Class<?> c = transformToClass(className);
                if (c != null)
                {
                    matches.add(c);
                }
            }
        }
    }

    /**
     * Add entries
     * @param filePath the path to search
     */
    private void addLocations(String filePath)
    {
        StringTokenizer st = new StringTokenizer(filePath, fs);
        while (st.hasMoreTokens())
        {
            locations.add(st.nextToken());
        }
    }

    /**
     * If set to true, recurse into directories
     * @param recurse
     */
    public void setRecurse(boolean recurse)
    {
        this.recurse = recurse;
    }

    public void setArchiveMatch(boolean archiveMatch)
    {
        this.matchArchives = archiveMatch;
    }

    /** Print a help message */
    private static void printHelp()
    {
        printAbout();

        System.out.println("java nrs.findclass.FindClass <options> <classes>");
        System.out.println("Do _not_ specify packages.");
        System.out.println("If no classpath or directory is specified the system classpath will be searched.");
        System.out.println("Multiple files can be searched for at the same time.");
        System.out.println("    -a      print info about this program");
        System.out.println("    -c <classpath>  add specified classpath to system classpath");
        System.out.println("    -d <path>   search only in specified path(s)");
        System.out.println("    -f <file>   log output to file");
        System.out.println("    -h      this help message");
        System.out.println("    -i      ignore case (default: no)");
        System.out.println("    -j      only search Java archives (jar, zip) (default: no)");
        System.out.println("    -m      match word (default: no)");
        System.out.println("    -q      only print when a match is found\n");
        System.exit(0);
    }

    /** Print some info about the utility */
    private static void printAbout()
    {
        System.out.println("FindClass " + version);
        System.out.println("Searches a classpath for the specified file(s).");
        System.out.println("This utility is distributed under the terms of the GPL and comes with absolutely no warranty");
        System.out.println("Homepage: http://www.nsydenham.net/java/FindClass/FindClass.shtml");
        System.out.println("Copyright (C) 2002-2004 Nick Sydenham <nsydenham@yahoo.co.uk>\n");
    }

    /** Start the utility */
    public static void main(String[] args)
    {
        String arg;

        FindClass find = new FindClass();

        // parse the command line arguments
        try
        {
            for (int i = 0; i < args.length; i++)
            {
                arg = args[i];
                if (arg.equalsIgnoreCase("-a"))
                {
                    printAbout();
                    System.exit(0);
                }
                else if (arg.equals("-c"))
                {
                    // add locations in class path
                    find.addLocations(System.getProperty("java.class.path"));
                    // add extra locations
                    find.addLocations(args[++i]);
                }
                else if (arg.equals("-d"))
                {
                    // search only in specifed locations
                    find.addLocations(args[++i]);
                }
                else if (arg.equalsIgnoreCase("-h"))
                {
                    printHelp();
                }
                else if (arg.equalsIgnoreCase("-j"))
                {
                    find.setArchiveMatch(true);
                }
                else if (arg.equalsIgnoreCase("-r"))
                {
                    find.setRecurse(true);
                }
            }
        }
        catch (IndexOutOfBoundsException iex)
        {
            System.out.println("Invalid arguments");
            printHelp();
        }

        find.findClasses();
    }
}
