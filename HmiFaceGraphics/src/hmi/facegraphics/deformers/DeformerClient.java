package hmi.facegraphics.deformers;

public interface DeformerClient
{
    void updateSize(float size);

    void updateValue(int value);

    void setDisplacement(float[] npos);
}
