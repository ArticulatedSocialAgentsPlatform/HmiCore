package hmi.tts;

import lombok.Data;

@Data
public class Prosody
{
    private final double[] f0;
    private final double[] rmsEnergy;
    private final double frameDuration;
}
