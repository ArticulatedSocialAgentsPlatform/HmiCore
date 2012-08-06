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
