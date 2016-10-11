package HelloWorld;


import java.util.Scanner;

import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 * An example implementation that uses the template-system. 
 * @author Mark ter Maat
 *
 */
public class HelloWorld
{
    public HelloWorld()
    {
        DefaultRecord is = new DefaultRecord();
        is.set("userturn", new DefaultRecord());

        TemplateController controller = new TemplateController();
        controller.processTemplateFile("HelloWorld.xml");
        controller.addFunction(new HelloWorldFunctions());

        String userText = "";
        Scanner in = new Scanner(System.in);
        while( !userText.equals("quit") && !userText.equals("exit") ) {
            System.out.println("You can start speaking.");
            System.out.print(">> ");
            userText = in.nextLine();
            if( userText.toLowerCase().contains("hi") || userText.toLowerCase().contains("hello") ) {
                is.getRecord("userturn").set("intention", "greeting");
            } else if( userText.toLowerCase().contains(" goodbye ") || userText.toLowerCase().contains("bye") ) {
                is.getRecord("userturn").set("intention", "ending");
            } else {
                is.getRecord("userturn").set("intention", "Unknown");
            }
            is.getRecord("userturn").set("text", userText);
            controller.checkTemplates(is);
        }
    }

    public static void main( String args[] )
    {
        new HelloWorld();
    }
}
