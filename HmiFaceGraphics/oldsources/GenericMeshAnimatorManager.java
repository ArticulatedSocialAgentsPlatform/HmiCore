package hmi.animation;

import hmi.graphics.render.GenericMesh;
import java.util.HashMap;

/**
 *
 * @author N.A.Nijdam
 */
public class GenericMeshAnimatorManager {
    /* 
     * 
     */
    private static HashMap<GenericMesh, GenericMeshAnimator> genericMeshAnimators = new HashMap<GenericMesh, GenericMeshAnimator>();
    
    public static void put(GenericMeshAnimator genericMeshAnimator){
        genericMeshAnimators.put(genericMeshAnimator.getGenericMesh(), genericMeshAnimator);
    }
    
    public static GenericMeshAnimator get(GenericMesh genericMesh){
        return genericMeshAnimators.get(genericMesh);
    }
}
