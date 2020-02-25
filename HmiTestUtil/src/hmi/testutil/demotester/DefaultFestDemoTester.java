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
package hmi.testutil.demotester;

import java.awt.Frame;

import javax.swing.JFrame;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.security.NoExitSecurityManagerInstaller;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Utility methods and default BeforeClass/AfterClass for setting up a test case for a demo with a Frame/JFrame.
 * By default this checks if all Swing stuff is ran in the EDT.<br>
 * Usage:<br>
 * 1. Create/grab the JFrame of your application<br>
 * 2. Create a framefixture for it using createFrameFixture<br>
 * 3. Do some stuff with the frame in your demo<br>
 * 4. Close the framefixture with its close method<br>
 * @author hvanwelbergen
 *
 */
public abstract class DefaultFestDemoTester
{
    private static NoExitSecurityManagerInstaller noExitSecurityManagerInstaller;

    @BeforeClass
    public static void setUpOnce()
    {
        FailOnThreadViolationRepaintManager.install();

        // Hooks up system.exit to an exception
        noExitSecurityManagerInstaller = NoExitSecurityManagerInstaller.installNoExitSecurityManager();
    }

    @AfterClass
    public static void tearDownOnce()
    {
        noExitSecurityManagerInstaller.uninstall();
    }

    public JFrame createJFrame()
    {
        JFrame testFrame = GuiActionRunner.execute(new GuiQuery<JFrame>()
        {
            protected JFrame executeInEDT()
            {
                return new JFrame();
            }
        });
        return testFrame;
    }

    public FrameFixture createFrameFixture(Frame jf)
    {
        return new FrameFixture(jf);
    }
}
