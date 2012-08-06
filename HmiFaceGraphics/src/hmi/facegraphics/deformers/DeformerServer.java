package hmi.facegraphics.deformers;

public interface DeformerServer
{
    void updateSize(float size);

    void updateValue(int value);

    float getSize();

    int getValue();
}
