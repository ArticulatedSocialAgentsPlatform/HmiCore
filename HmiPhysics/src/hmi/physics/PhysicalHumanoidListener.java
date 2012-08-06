package hmi.physics;

/** Listener callbacks for physical humanoids, e.g. when they are enabled / disabled */
public interface PhysicalHumanoidListener
{
    void physicalHumanEnabled(PhysicalHumanoid ph, boolean enabled);
}
