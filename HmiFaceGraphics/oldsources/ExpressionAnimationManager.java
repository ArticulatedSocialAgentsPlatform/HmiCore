package hmi.animation;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 */
public class ExpressionAnimationManager
{

    /* 
     * 
     */
    private static HashMap<MuscleSet, HashSet<ExpressionAnimation>> expressionAnimationSet = new HashMap<MuscleSet, HashSet<ExpressionAnimation>>();

    /**
     * 
     */
    public static void put(ExpressionAnimation expressionAnimation)
    {
        put(expressionAnimation.getMuscleSet(), expressionAnimation.getName(), expressionAnimation);
    }

    /**
     * 
     */
    public static void put(MuscleSet baseMuscleSet, String expressionAnimationId, ExpressionAnimation expressionAnimation)
    {
        if (!expressionAnimationSet.containsKey(baseMuscleSet))
            expressionAnimationSet.put(baseMuscleSet, new HashSet<ExpressionAnimation>());
        (expressionAnimationSet.get(baseMuscleSet)).add(expressionAnimation);
    }

    /**
     * 
     */
    public static HashSet<ExpressionAnimation> getSet(MuscleSet baseMuscleSet)
    {
        if (!expressionAnimationSet.containsKey(baseMuscleSet))
            expressionAnimationSet.put(baseMuscleSet, new HashSet<ExpressionAnimation>());
        return expressionAnimationSet.get(baseMuscleSet);
    }
}
