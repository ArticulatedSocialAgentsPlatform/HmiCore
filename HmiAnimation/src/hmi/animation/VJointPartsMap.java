package hmi.animation;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Helper class to more efficiently obtain parts from a VJoint tree.
 * @author hvanwelbergen
 */
public class VJointPartsMap
{
    private Map<String,VJoint> jointMap;
    
    public void VJointsPartsMap(VJoint vjRoot)
    {
        Map<String,VJoint> m = new HashMap<String,VJoint>();
        m.put(vjRoot.getSid(),vjRoot);
        for(VJoint vj: vjRoot.getParts())
        {
            if(vj.getSid() == null)
            {
                throw new IllegalArgumentException("Part with "+vj+" has null sid. Root joint: "+vjRoot);
            }
            m.put(vj.getSid(),vj);
        }
        jointMap = ImmutableMap.copyOf(m);
    }
    
    public VJoint get(String sid)
    {
        return jointMap.get(sid);
    }
}
