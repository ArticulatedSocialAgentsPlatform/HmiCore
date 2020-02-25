/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.testutil.rules;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Based on the standard Timeout Rule in JUnit, but provides a callback function that is to be used
 * to obtain extra progress information (in String form).
 * 
 * @author welberge
 */
public class TimeoutWithCallback implements MethodRule
{
    private final int timoutms;

    private TimeoutCallback timeoutCallback;

    /**
     * @param millis
     *            the millisecond timeout
     */
    public TimeoutWithCallback(int millis, TimeoutCallback callback)
    {
        timoutms = millis;
        timeoutCallback = callback;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target)
    {
        return new FailOnTimeoutWithCallback(base, timoutms, timeoutCallback);
    }

}
