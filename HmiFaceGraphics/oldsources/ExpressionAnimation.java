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
/**
 * \file ExpressionAnimation.java
 * \author N.A.Nijdam
 * \version 1.0
 * \date 26-11-2006
 */
package hmi.animation;

import java.util.TreeMap;

/**
 * \brief extended Expression type for animated expressions Maintains a list of expressions with each expression a time offset
 */
public class ExpressionAnimation extends Expression
{

    private TreeMap<Long, Expression> expressionTree = new TreeMap<Long, Expression>();
    private Expression expressions[] = null;
    private long[] timing;
    private long time = 0;

    /**
     * \brief the default constructor \param name the name for the animation \param avatar the avatar.
     */
    public ExpressionAnimation(String name, MuscleSet muscleSet)
    {
        super(name, muscleSet);
        updateList();
    }

    /**
     * \brief add an expression at a time offset \param expression the expression \param time the time offset
     */
    public void addExpression(Expression expression, long time)
    {
        if (muscleSettings.length == 0)
        {
            muscleSettings = new float[expression.getMuscleSet().getMuscleSettingsCount()];
        }
        if (time < 0)
        {
            if (timing.length != 0)
            {
                time = timing[timing.length - 1] + 1;
            }
            else
            {
                time = 0;
            }
        }
        System.out.println("Adding " + expression + " at " + time);

        expressionTree.put(time, expression);
        updateList();
    }

    /**
     * \brief remove an expression from the animation
     */
    public void removeExpression(int index)
    {
        expressionTree.remove(timing[index]);
        updateList();
    }

    /**
     * \brief update the arrays of expressions and times
     */
    public void updateList()
    {
        expressions = new Expression[expressionTree.size()];
        timing = new long[expressionTree.size()];
        int cnt = 0;

        for (long i : expressionTree.keySet())
        {
            timing[cnt++] = i;
        }
        cnt = 0;
        for (Expression muscles : expressionTree.values())
        {
            expressions[cnt++] = muscles;
        }
    }

    @Override
    public float[] getMuscleSettings()
    {
        updateMusclesSettings();
        return muscleSettings;
    }

    public void updateMusclesSettings()
    {
        updateMusclesSettings(time);
    }

    /**
     * \brief get a certain muscle settings by the time offset \param time the time offset
     */
    public void updateMusclesSettings(long time)
    {
        Expression start = null;
        Expression end = null;
        long startTime = 0;
        long endTime = 0;

        for (int i = 0; i < this.expressions.length; i++)
        {
            startTime = this.timing[i];
            if (time >= startTime)
            {
                start = this.expressions[i];
                if (i + 1 == this.expressions.length)
                {
                    System.arraycopy(start.getMuscleSettings(), 0, muscleSettings, 0, muscleSettings.length);
                    return;
                }
                end = this.expressions[i + 1];
                endTime = this.timing[i + 1];
                if (endTime > time)
                {
                    break;
                }
            }
        }
        if (start == null || end == null)
        {
            return;
        }
        interpolate(start.getMuscleSettings(), end.getMuscleSettings(), (time - startTime) * (1.0f / (endTime - startTime)));
    }

    /**
     * \brief check if the time offset is free \param time the time offset
     */
    public boolean isValidTime(long time)
    {
        return !this.expressionTree.containsKey(time);
    }

    /**
     * \brief change the time offset for an expression \param index the index of the expression \param time the new time offset
     */
    public void setTiming(int index, long time)
    {
        Expression expression = expressions[index];
        expressionTree.remove(timing[index]);
        addExpression(expression, time);
    }

    /**
     * \brief interpolate between two expressions \param start the start expression \param end the end expression \param progress the interpolation
     * offset (range from 0.0 - 1.0)
     */
    public void interpolate(float start[], float end[], float progress)
    {
        for (int i = 0; i < this.muscleSettings.length; i++)
        {
            this.muscleSettings[i] = start[i] + (end[i] - start[i]) * progress;
        }
    }

    /**
     * \brief return the time for an expression \param index the index for the expression \return the time offset
     */
    public long getTime(int index)
    {
        if (index > this.expressions.length)
        {
            return -1;
        }
        return this.timing[index];
    }

    /**
     * \brief get the animation time
     */
    public long getTime()
    {
        return time;
    }

    /**
     * \brief set the animation time \param time the animation time offset
     */
    public void setTime(long time)
    {
        this.time = time;
    }

    /**
     * \brief get the expressions with time offsets \return treemap <time, expression>
     */
    public TreeMap<Long, Expression> getExpressionTree()
    {
        return expressionTree;
    }

    /**
     * \brief get array of expressions
     */
    public Expression[] getExpressions()
    {
        return expressions;
    }

    /**
     * \brief get expression by name
     */
    public Expression getExpression(String name)
    {
        for (Expression expression : expressions)
        {
            if (name.equals(expression.getName()))
            {
                return expression;
            }
        }
        return null;
    }

    /**
     * \brief get time offset array.
     */
    public long[] getTiming()
    {
        return timing;
    }

    public long getLargestTime()
    {
        if (timing.length == 0)
        {
            return 0;
        }
        return timing[timing.length - 1];
    }
}
