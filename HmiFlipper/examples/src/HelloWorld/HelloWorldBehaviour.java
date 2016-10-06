package HelloWorld;

import java.util.ArrayList;
import hmi.flipper.behaviourselection.behaviours.BehaviourClass;

/**
 * Another example of an implementation of BehaviourClass
 * 
 * @author Mark ter Maat
 *
 */
public class HelloWorldBehaviour implements BehaviourClass
{
    public void execute( ArrayList<String> argNames, ArrayList<String> argValues )
    {
        System.out.println("Agent: " + argValues.get(0));
    }

    public void prepare( ArrayList<String> argNames, ArrayList<String> argValues )
    {

    }
}
