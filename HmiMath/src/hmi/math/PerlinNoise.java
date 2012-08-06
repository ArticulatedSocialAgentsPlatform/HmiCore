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
