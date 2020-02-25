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
package hmi.neurophysics;

/**
 * Energy model / biological constraints: The shoulder joint limits the swivel angle to an interval
 * of about 180 degree. Whereas, the tended thrift energy for each pos, specified by the swivel angle
 * from this interval, can be modeled as a Gaussian pdf.
 * 
 * @author Amir Sadeghi (original C++ version)
 * @author hvanwelbergen (Java translation)
 */
public class BiologicalSwivelCostsEvaluator
{
    private final double minFeasibleElbowSwivel;
    private final double maxFeasibleElbowSwivel;
    private final double sigmaOfGaussianCostsDistribution;
    private final double freedomOfTheGaussianMean;
    private final double angleStepToComputeCosts = 0.1;
    private final boolean considerBiologicalCosts = true;
    private final boolean considerDeviationCosts = true;
    
    public BiologicalSwivelCostsEvaluator(double minFeasibleElbowSwivel, double maxFeasibleElbowSwivel,
            double freedomOfTheGaussianMean)
    {
        this(minFeasibleElbowSwivel, maxFeasibleElbowSwivel, 0.45, freedomOfTheGaussianMean);
    }
    
    public BiologicalSwivelCostsEvaluator(double minFeasibleElbowSwivel, double maxFeasibleElbowSwivel,
            double sigmaOfGaussianCostsDistribution, double freedomOfTheGaussianMean)
    {
        this.minFeasibleElbowSwivel = minFeasibleElbowSwivel;
        this.maxFeasibleElbowSwivel = maxFeasibleElbowSwivel;
        this.sigmaOfGaussianCostsDistribution = sigmaOfGaussianCostsDistribution;
        this.freedomOfTheGaussianMean = freedomOfTheGaussianMean;
    }

    public double getBiologicalCostsOfElbowSwivel(double swivel)
    {
        double mu = ((minFeasibleElbowSwivel + maxFeasibleElbowSwivel) / 2) + freedomOfTheGaussianMean;
        return 1.0 - (1.0 / Math.sqrt(2 * Math.PI * Math.pow(sigmaOfGaussianCostsDistribution, 2)))
                * Math.exp(-Math.pow(swivel - mu, 2) / (2 * Math.pow(sigmaOfGaussianCostsDistribution, 2)));
    }
    
    public double getSwivelAngleWithMinCost(double formerSwivel)
    {
        double swivel = 0;
        double minCost = -1;
        for (double a = minFeasibleElbowSwivel; a <= maxFeasibleElbowSwivel; a = a + angleStepToComputeCosts)
        {
            double sumOfCosts = 0.0;
            if (considerBiologicalCosts)
            {
                double c =  getBiologicalCostsOfElbowSwivel(a);
                sumOfCosts += c;                
            }
            if (considerDeviationCosts)     // && formerSwivel!=-99)=> what does that do???
            {
                double maxDeviation = Math.abs(maxFeasibleElbowSwivel - minFeasibleElbowSwivel);
                double d = Math.abs(formerSwivel - a)/maxDeviation;                
                sumOfCosts += d;
            }
            /* TODO
            if (considerSelfCollision)
            {
                aceVec3 elbowPos = srs->getElbowPosForSwivel(a, shoulderTransform);
                bool hasCollision = selfCollisionDetector->elbowHasCollision(elbowPos, left_right);
                if (hasCollision)
                {
                    sumOfCosts += 1.0;
                    cache_lastSelfCollisionCosts[left_right][a] = 1.0;
                }
                else {
                    foundASwivelWithoutSelfCollision = true;
                    cache_lastSelfCollisionCosts[left_right][a] = 0.0;
                }
            }
            */
            double cost = sumOfCosts/3;

            if (minCost==-1 || cost<minCost)
            {
                minCost = cost;
                swivel = a;
            }
        }
        
        return swivel;
    }
}
