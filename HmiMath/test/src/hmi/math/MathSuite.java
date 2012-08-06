/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmi.math;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author zwiers
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   hmi.math.CubicSplineTest.class,
   hmi.math.Mat3fTest.class,
   hmi.math.Mat4fTest.class,
   hmi.math.MatTest.class,
   hmi.math.NumMathTest.class,
   hmi.math.Quat4fTest.class,
   hmi.math.SpatialTransformTest.class,
   hmi.math.SpatialVecTest.class,
   hmi.math.TCBSplineTest.class,
   hmi.math.Vec3fTest.class,
   hmi.math.Vec4fTest.class
})
public class MathSuite {

}
