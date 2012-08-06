package hmi.facegraphics.deformers;

public interface EaseDeformerClient extends DeformerClient
{
    void updateEase(int ease);

    void updateScalex(float scale);

    void updateScaley(float scale);

    void updateScalez(float scale);

    void updateUseVM(boolean value);

    void updateInvertVM(boolean value);
}
