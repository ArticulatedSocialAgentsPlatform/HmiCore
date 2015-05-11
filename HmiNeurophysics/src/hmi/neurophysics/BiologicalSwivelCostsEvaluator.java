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
