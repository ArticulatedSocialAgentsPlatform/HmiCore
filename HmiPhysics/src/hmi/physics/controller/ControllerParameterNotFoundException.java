package hmi.physics.controller;

public class ControllerParameterNotFoundException extends ControllerParameterException
{
    private static final long serialVersionUID = 1L;
    private final String paramId;
    
    public String getParamId()
    {
        return paramId;
    }
    
    public ControllerParameterNotFoundException(String param)
    {
        super("ParameterNotFound: "+ param);
        this.paramId = param;
    }
    
    public ControllerParameterNotFoundException(String message, String param)
    {
        super(message);
        this.paramId = param;
    }
}
