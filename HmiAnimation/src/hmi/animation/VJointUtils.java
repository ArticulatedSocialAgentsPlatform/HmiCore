package hmi.animation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

/**
 * Utilities for VJoints
 * @author hvanwelbergen
 * 
 */
public final class VJointUtils
{
    private VJointUtils()
    {
    }

    public static class VJointInCollectionPredicate implements Predicate<VJoint>
    {
        private final Collection<VJoint> jointCol;

        public VJointInCollectionPredicate(Collection<VJoint> collection)
        {
            this.jointCol = collection;
        }

        @Override
        public boolean apply(VJoint vj)
        {
            for (VJoint joint : jointCol)
            {
                if (joint.equivId(vj)) return true;
            }
            return false;
        }
    }

    /**
     * Get a collection of joints that intersects vj1 and vj2
     */
    public static Collection<VJoint> intersection(Collection<VJoint> vj1, Collection<VJoint> vj2)
    {
        List<VJoint> vjOut = new ArrayList<VJoint>(vj1);
        return Collections2.filter(vjOut, new VJointInCollectionPredicate(vj2));
    }

    /**
     * Create a set of sid strings from a collection of VJoints     
     */
    public static Set<String> transformToSidSet(Collection<VJoint> joints)
    {
        Collection<String> j = Collections2.transform(joints, new Function<VJoint, String>()
        {
            @Override
            public String apply(VJoint joint)
            {
                return joint.getSid();
            }
        });
        return ImmutableSet.copyOf(j);
    }
}
