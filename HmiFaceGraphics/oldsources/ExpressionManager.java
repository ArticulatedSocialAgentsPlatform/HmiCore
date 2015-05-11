/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
