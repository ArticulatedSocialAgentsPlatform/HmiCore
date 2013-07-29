package hmi.animation;

/**
 * DOF for the hand as used in the HandAnimator
 * @author Herwin
 *
 */
public class HandDOF
{
    public double PIPPinkyFlexion;
    public double PIPRingFlexion;
    public double PIPMiddleFlexion;
    public double PIPIndexFlexion;
    public double IPThumbFlexion;
    
    public double MCPPinkyFlexion;
    public double MCPRingFlexion;
    public double MCPMiddleFlexion;
    public double MCPIndexFlexion;
    public double MCPThumbFlexion;
    
    public double MCPPinkyAbduction;
    public double MCPRingAbduction;
    public double MCPIndexAbduction;
    
    public double TMCFlexion;
    public double TMCAbduction;
    
    public double getPIPFlexion(String joint)
    {
        switch (joint)
        {
        case "r_pinky": 
        case "l_pinky": return PIPPinkyFlexion;
        case "r_ring":
        case "l_ring": return PIPRingFlexion;
        case "r_middle":
        case "l_middle": return PIPMiddleFlexion;
        case "r_index":
        case "l_index": return PIPIndexFlexion;
        default: throw new IllegalArgumentException(joint + " is not a valid PIP joint");
        }
    }
    
    public double getMCPFlexion(String joint)
    {
        switch (joint)
        {
        case "r_pinky": 
        case "l_pinky": return MCPPinkyFlexion;
        case "r_ring":
        case "l_ring": return MCPRingFlexion;
        case "r_middle":
        case "l_middle": return MCPMiddleFlexion;
        case "r_index":
        case "l_index": return MCPIndexFlexion;
        default: throw new IllegalArgumentException(joint + " is not a valid MCP joint");
        }
    }
    
    public double getMCPAbduction(String joint)
    {
        switch (joint)
        {
        case "r_pinky": 
        case "l_pinky": return MCPPinkyAbduction;
        case "r_ring":
        case "l_ring": return MCPRingAbduction;
        case "r_middle":
        case "l_middle": return 0;
        case "r_index":
        case "l_index": return MCPIndexAbduction;
        default: throw new IllegalArgumentException(joint + " is not a valid MCP joint");
        }
    }
}
