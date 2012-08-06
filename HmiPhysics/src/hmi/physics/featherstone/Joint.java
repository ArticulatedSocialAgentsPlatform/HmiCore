package hmi.physics.featherstone;

public interface Joint
{
    /**
     * Calculates Xj, S, vj, cj on the basis of joint DoF q and their velocity qdot
     * @param Xj output: spatial joint transform by q 
     * @param S  output: joint motion subspace matrix (size 6xn)
     * @param vj output: joint spatial velocity       (size 6)
     * @param cj output: velocity dependent spatial acceleration of the joint (size 6)
     * @param q    input: joint DoF values  (typically size n)
     * @param qdot input: time derivative of joint DoF values (typically size n) 
     * @return number of DoF 
     */
    void jcalc(float Xj[], float S[], float vj[], float cj[],float q[], float qdot[]);
    
    int getQDimension();
    
    int getQDotDimension();
    
    int getSWidth();
}
