package hmi.facegraphics.deformers;

public interface EaseDeformerServer extends DeformerServer
{
    void updateEase(int ease);

    void updateScalex(float scale);

    void updateScaley(float scale);

    void updateScalez(float scale);

    void updateUseVM(boolean value);

    void updateInvertVM(boolean value);

    int getEase();

    float getScalex();

    float getScaley();

    float getScalez();

    boolean getUseVM();

    boolean getInvertVM();
}
