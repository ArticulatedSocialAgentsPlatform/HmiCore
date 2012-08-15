package hmi.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for the collectionutils
 * @author hvanwelbergen
 *
 */
public class CollectionUtilsTest
{
    @Test
    public void testEnsureSizeFromEmptyList()
    {
        List<Double> list = new ArrayList<>();
        CollectionUtils.ensureSize(list, 10);
        assertEquals(10, list.size());
    }
}
