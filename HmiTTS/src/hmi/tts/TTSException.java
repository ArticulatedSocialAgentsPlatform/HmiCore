package hmi.tts;

/**
 * Wraps the different exceptions that may occur during TTS in different TTSGenerator implementations
 * @author hvanwelbergen
 *
 */
public class TTSException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TTSException(String message, Exception ex)
    {
        super(message, ex);        
    }
}
