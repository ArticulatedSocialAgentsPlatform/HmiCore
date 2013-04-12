package hmi.testutil.demotester;

import javax.swing.JButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.FrameFixture;

/**
 * Some utitilities to to make Fest testing easier.
 * @author hvanwelbergen
 *
 */
public final class FestUtils
{
    private static GenericTypeMatcher<JButton> getButtonMatcher(final String id)
    {
        GenericTypeMatcher<JButton> buttonMatcher = new GenericTypeMatcher<JButton>(JButton.class){
            protected boolean isMatching(JButton jb)
            {
                return id.equals(jb.getText());
            }
        };
        return buttonMatcher;
    }
    
    
    public static void clickButton(String name, FrameFixture window)
    {
        window.button(getButtonMatcher(name)).click();
    }
}
