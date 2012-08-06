/**
 * 
 */

package hmi.animation;

import java.util.ArrayList;

/**
 *
 * @author N.A.Nijdam
 */
public class FacialMuscles {
    private float vertexArray[];
    private ArrayList<Muscle> muscles = new ArrayList<Muscle>();

    public ArrayList<Muscle> getMuscles() {
        return muscles;
    }
    
    public void init()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    /**************
     * Get/Set methods
     **************/   

    
    public void setMuscles(ArrayList<Muscle> muscles) {
        this.muscles = muscles;
    }

    public float[] getVertexArray() {
        return vertexArray;
    }

    public void setVertexArray(float[] vertexArray) {
        this.vertexArray = vertexArray;
    }    
    
}
