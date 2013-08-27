package hmi.animationui;

import hmi.util.InfoUtils;

import javax.swing.JOptionPane;

/**
 * The Info class is intended to be used as "Main class" when the package is
 * jarred. Running java -jar <packageJarFile> will print some package
 * information. Note that some of this information is only available from the
 * Manifest.mf file, that is included in the jar file, and not when running
 * directly from compiled classes.
 */
public final class Info
{
    private Info()
    {
    }

    /**
     * Show some package information
     */
    public static void main(String[] arg)
    {
        JOptionPane.showMessageDialog(null, InfoUtils.manifestInfo(Info.class.getPackage()), "Package Info", JOptionPane.PLAIN_MESSAGE);
    }
}

