package hmi.animationui;

import lombok.Data;

@Data
public class JointRotationConfiguration
{
    private final String jointName;
    private final float q[];
    private final float rpyDeg[];
}
