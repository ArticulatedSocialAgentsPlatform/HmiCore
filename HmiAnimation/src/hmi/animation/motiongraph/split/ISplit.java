/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmi.animation.motiongraph.split;

import hmi.animation.SkeletonInterpolator;
import java.util.List;

/**
 *
 * @author yannick-broeker
 */
public interface ISplit {
    
    List<SkeletonInterpolator> split(SkeletonInterpolator skeletonInterpolator);
    
}
