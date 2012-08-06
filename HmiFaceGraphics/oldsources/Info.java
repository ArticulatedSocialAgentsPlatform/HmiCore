/**
 * @(#) Info.java
 * @version 1.0   3/09/2007
 * @author Job Zwiers
 */

package hmi.animation; // change this line for other packages

import javax.swing.JOptionPane;

/**
 * The Info class is intended to be used as "Main class" when the package is jarred. Running java -jar <packageJarFile> will print some package
 * information. Note that some of this information is only available from the Manifest.mf file, that is included in the jar file, and not when running
 * directly from compiled classes.
 */
public class Info
{

    private static Package pack = new Info().getClass().getPackage();
    public static String packageName = pack.getName();

    /**
     * Yields a String containing manifest file info. When not running from a jar file, only the package name is included.
     */
    public static String manifestInfo()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Package: ");
        buf.append(packageName);
        buf.append("\n");
        if (pack.getSpecificationTitle() != null)
        {
            buf.append("Specification-Title: " + pack.getSpecificationTitle() + "\n");
        }
        if (pack.getSpecificationVersion() != null)
        {
            buf.append("Specification-Version: " + pack.getSpecificationVersion() + "\n");
        }
        if (pack.getSpecificationVendor() != null)
        {
            buf.append("Specification-Vendor: " + pack.getSpecificationVendor() + "\n");
        }
        if (pack.getImplementationTitle() != null)
        {
            buf.append("Implementation-Title: " + pack.getImplementationTitle() + "\n");
        }
        if (pack.getImplementationVersion() != null)
        {
            buf.append("Implementation-Version: " + pack.getImplementationVersion() + "\n");
        }
        if (pack.getImplementationVendor() != null)
        {
            buf.append("Implementation-Vendor: " + pack.getImplementationVendor() + "\n");
        }
        return buf.toString();
    }

    /**
     * Checks whether the current specification version meets the specified required version; if not, a RuntimeException is thrown. No check is
     * performed when manifest info is not available.
     */
    public static void requireVersion(String requiredVersion)
    {
        if (pack.getSpecificationVersion() == null)
            return; // no check possible, so assume ok
        if (pack.isCompatibleWith(requiredVersion))
            return;
        String msg = "Package " + packageName + " Version " + pack.getSpecificationVersion() + " does not meet the required version "
                + requiredVersion;
        JOptionPane.showMessageDialog(null, msg, "Package Info", JOptionPane.PLAIN_MESSAGE);
        throw new RuntimeException(msg);
    }

    /**
     * Returns the specification version from the Manifest.mf file, if available, or else an empty String.
     */
    public static String getVersion()
    {
        String result = pack.getSpecificationVersion();
        return (result == null) ? "" : result;
    }

    /**
     * checks whether the package specification version is compatible with a certain desired version. &quot;isCompatibleWith(desiredversion)&quot;
     * return true iff the desiredVersion is smaller or equal than the package specification version, where "smaller than" is determined by the
     * lexicographic order on major, minor, and micro version numbers. (Missing numbers are considered to be 0). For instance, when the package
     * specification version would be 2.1, then some examples of compatible desired versions are: 1, 1.0, 1.6, 2.0.4, 2.1, 2.1.0, whereas desired
     * versions like 2.2, 3.0, or 2.1.1 would not be compatible.
     */
    public static boolean isCompatibleWith(String desiredVersion)
    {
        String specificationVersion = pack.getSpecificationVersion();
        if (specificationVersion == null)
            return true; // no spec version available, so assume ok
        String[] desiredNums = desiredVersion.split("[.]");
        String[] specificationNums = specificationVersion.split("[.]");
        int desired, specified;
        try
        {
            for (int vn = 0; vn < desiredNums.length; vn++)
            {
                // System.out.println("  desired num " + vn + ": " + desiredNums[vn] + " specification num: " + specificationNums[vn]);
                desired = Integer.valueOf(desiredNums[vn]);
                if (vn < specificationNums.length)
                {
                    specified = Integer.valueOf(specificationNums[vn]);
                }
                else
                {
                    specified = 0;
                }

                if (desired < specified)
                    return true;
                if (desired > specified)
                    return false;
            }
            return true;
        }
        catch (NumberFormatException e)
        {
            System.out.println(packageName + ".Info.isCompatibelWith method: illegal version numbers: " + desiredVersion + " / "
                    + specificationVersion);
            return false;
        }
    }

    /*
     * Show some package information
     */
    public static void main(String[] arg)
    {
        JOptionPane.showMessageDialog(null, Info.manifestInfo(), "Package Info", JOptionPane.PLAIN_MESSAGE);
    }

}
