package hmi.util;

/**
 * Generic duplicate ID exception 
 * @author Dennis Reidsma
 */
public class DuplicateIdException extends RuntimeException
{
    private static final long serialVersionUID = -8534635217080796975L;
    private final String id;

    public String getId()
    {
        return id;
    }
    
    public DuplicateIdException(String id, String message)
    {
		super(message);
        this.id = id;
    }
    
}
