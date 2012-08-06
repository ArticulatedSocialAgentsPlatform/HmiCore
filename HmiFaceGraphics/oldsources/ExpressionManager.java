package hmi.animation;

import java.util.HashMap;
import java.util.HashSet;

public class ExpressionManager
{

    /*
     * a base muscle set has a set of expression settings <base muscle set, <expression name, expression settings>> an expression is a clone of the
     * muscle set with its own muscle values
     */
    private static HashMap<MuscleSet, HashSet<Expression>> expressionSet = new HashMap<MuscleSet, HashSet<Expression>>();

    /**
     * \brief put a muscles settings object in the list of loaded muscle settings
     */
    public static void put(Expression expression)
    {
        put(expression.getMuscleSet(), expression.getName(), expression);
    }

    /**
     * \brief put a muscles settings object in the list of loaded muscle settings
     */
    public static void put(MuscleSet baseMuscleSet, String expressionId, Expression expression)
    {
        if (!expressionSet.containsKey(baseMuscleSet))
            expressionSet.put(baseMuscleSet, new HashSet<Expression>());
        (expressionSet.get(baseMuscleSet)).add(expression);
    }

    public static HashSet<Expression> getSet(MuscleSet baseMuscleSet)
    {
        if (!expressionSet.containsKey(baseMuscleSet))
            expressionSet.put(baseMuscleSet, new HashSet<Expression>());
        return expressionSet.get(baseMuscleSet);
    }

    public static Expression get(MuscleSet baseMuscleSet, String name)
    {
        HashSet<Expression> expressions = getSet(baseMuscleSet);
        for (Expression expression : expressions)
            if (expression.getName().equals(name))
            {
                return expression;
            }
        return null;
    }
}
