package hmi.facegraphics.deformers;

public interface SmoothDeformerClient extends DeformerClient
{
    void updateSmoothCenter(int smoothCenter);

    void updateSmoothSide(int smoothSide);

    void updateScalex(float scale);

    void updateScaley(float scale);

    void updateScalez(float scale);

    void updateUseVM(boolean value);

    void updateInvertVM(boolean value);
}
