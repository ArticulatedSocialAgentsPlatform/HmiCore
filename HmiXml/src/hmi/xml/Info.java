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

package hmi.xml; // change this line for other packages


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
