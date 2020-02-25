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
package hmi.physics.featherstone;

public interface Joint
{
    /**
     * Calculates Xj, S, vj, cj on the basis of joint DoF q and their velocity qdot
     * @param Xj output: spatial joint transform by q 
     * @param S  output: joint motion subspace matrix (size 6xn)
     * @param vj output: joint spatial velocity       (size 6)
     * @param cj output: velocity dependent spatial acceleration of the joint (size 6)
     * @param q    input: joint DoF values  (typically size n)
     * @param qdot input: time derivative of joint DoF values (typically size n) 
     * @return number of DoF 
     */
    void jcalc(float Xj[], float S[], float vj[], float cj[],float q[], float qdot[]);
    
    int getQDimension();
    
    int getQDotDimension();
    
    int getSWidth();
}
