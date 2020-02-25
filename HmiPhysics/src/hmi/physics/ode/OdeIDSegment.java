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
package hmi.physics.ode;

import hmi.physics.Mass;
import hmi.physics.inversedynamics.IDSegment;

/**
 * IDSegment that makes use of Ode mass functionality to calculate certain mass properties
 * Ode should be initialized before this is class used  
 * @author welberge
 */
public class OdeIDSegment extends IDSegment 
{
    /**
     * Creates a Mass object, used for 
     * @return null, unimplemented
     */
    @Override
    public Mass createMass()
    {
        return new OdeMass();
    }
}
