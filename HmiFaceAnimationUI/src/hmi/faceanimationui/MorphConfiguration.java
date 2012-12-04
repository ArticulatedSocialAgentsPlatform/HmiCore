package hmi.faceanimationui;

import lombok.Data;

/**
 * Morph name + value [-1,1]
 * @author hvanwelbergen
 *
 */
@Data
public class MorphConfiguration
{
    private final String name;
    private final float value;
}
