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
package hmi.math;

import java.util.Random;

/**
 * @author welberge
 * Generates noise fluently interpolated through random points between some uppperlimit and lowerlimit 
 */
public class PerlinNoise 
{
  private static final int DEFAULT_NR_OF_POINTS=256;
  private float[] g;
  private static int randoms = 0;
  
  /**
   * Constructor, creates nr random points
   * @param nr number of random points to generate
   * @param lowerLimit lowest value for a random point
   * @param upperLimit highest value for a random point
   */
  public PerlinNoise(int nr, float lowerLimit, float upperLimit)
  {
    g=new float[nr];
    initialize(lowerLimit,upperLimit);
  }
  /**
   * Constructor, creates DEFAULT_NR_OF_POINTS random points
   * @param lowerLimit lowest value for a random point
   * @param upperLimit highest value for a random point
   */
  public PerlinNoise(float lowerLimit, float upperLimit)
  {
    g=new float[DEFAULT_NR_OF_POINTS];
    initialize(lowerLimit,upperLimit);
  }
  
  /**
   * Initialize random points
   * @param lowerLimit lowest value for a random point
   * @param upperLimit highest value for a random point
   */
  public void initialize(float lowerLimit, float upperLimit)
  {
    randoms++;
    Random r = new Random(System.currentTimeMillis()+randoms);
    double radius = upperLimit-lowerLimit;
    for(int i=0;i<g.length;i++)
    {
      g[i]=(float)(lowerLimit+r.nextDouble()*radius);
    }   
  }
  
  private float getG(int index)
  {
    return g[index%g.length];
  }
  
  private float getG(int index,int length)
  {
    int l = length;
    if(length>=g.length-1)l=g.length-1;
    return g[index%l];
  }
  /**
   * Get the noise value at time t
   * @param t time
   * @return the noise value
   */
  public float noise(float t)
  {
    int tInt = (int)t;
    int t1 = tInt-1;
    if(t1<0)
    {
      t1+=g.length;
    }
    return HermiteSpline.getValue(t-tInt,getG(t1),getG(tInt),getG(tInt+1),getG(tInt+2));
  }
  
  /**
   * Get the noise value at time t, loop after l random points
   * @param t time
   * @param l length
   * @return the noise value
   */
  public float noise(float t,int l)
  {
    int tInt = (int)t;
    int t_1 = tInt-1;
    if(t_1<0)
    {
      t_1+=l;
    }
    return HermiteSpline.getValue(t-tInt,getG(t_1),getG(tInt,l),getG(tInt+1,l),getG(tInt+2,l));
  }
}
