package hmi.animation.motiongraph;

import hmi.animation.SkeletonInterpolator;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

public class RotationVelocityChecker implements TransitionChecker
{
    private final float epsilonRotation;
    private final float epsilonVelocity;

    public RotationVelocityChecker(float epsilonRotation, float epsilonVelocity)
    {
        this.epsilonRotation = epsilonRotation;
        this.epsilonVelocity = epsilonVelocity;
    }

    private float[] getAngularVelocity(SkeletonInterpolator ski, int frame)
    {
        float[] config,qrate;
        float[] avel = new float[ski.getConfigSize()/4*3];
        
        if(frame+1<ski.size())
        {
            config = ski.getConfig(frame);
            qrate = ski.getConfig(frame+1);
        }
        else if(frame-1>=0)
        {
            config = ski.getConfig(frame-1);
            qrate = ski.getConfig(frame);
        }
        else
        {
            return avel;
        }
        
        Vec3f.sub(qrate, config);
        for(int i=0;i<ski.getConfigSize()/4;i++)
        {
            Quat4f.setAngularVelocityFromQuat4f(avel, i*3, config, i*4, qrate, i*4);            
        }
        return avel;
    }

    @Override
    public boolean allowTransition(SkeletonInterpolator skiIn, SkeletonInterpolator skiOut, int frameIn, int frameOut)
    {
        float configIn[] = skiIn.getConfig(frameIn);
        float configOut[] = skiOut.getConfig(frameOut);
        for (int i = 0; i < skiIn.getConfigSize() / 4; i++)
        {
            if (!Quat4f.epsilonRotationEquivalent(configIn, i*4, configOut, i*4, epsilonRotation))
            {
                return false;
            }
        }

        float[]avelIn = getAngularVelocity(skiIn,frameIn);
        float[]avelOut=getAngularVelocity(skiOut,frameOut);
        for (int i = 0; i < skiIn.getConfigSize() / 4; i++)
        {
            if(!Vec3f.epsilonEquals(avelIn, i, avelOut, i, epsilonVelocity))
            {
                return false;
            }
        }
        return true;        
    }

}
