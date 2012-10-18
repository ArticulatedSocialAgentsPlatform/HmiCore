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
}
