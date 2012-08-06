package hmi.physics.controller;

/**
 * Generic exception for all parameter failures
 * @author welberge
 *
 */
public class ControllerParameterException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ControllerParameterException()
    {
        super();
    }
    
    public ControllerParameterException(String message)
    {
        super(message);
    }
}
