package hmi.facegraphics.deformers;

public interface SmoothDeformerServer extends DeformerServer
{
    void updateSmoothCenter(int smoothCenter);

    void updateSmoothSide(int smoothSide);

    void updateScalex(float scale);

    void updateScaley(float scale);

    void updateScalez(float scale);

    void updateUseVM(boolean value);

    void updateInvertVM(boolean value);

    int getSmoothCenter();

    int getSmoothSide();

    float getScalex();

    float getScaley();

    float getScalez();

    boolean getUseVM();

    boolean getInvertVM();
}
