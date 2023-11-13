package assigner;

import org.junit.Before;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
public class BalanceShardsAllocatorTest {

    BalanceShardsAllocator allocator;
    ShardAssigner assigner;
    @Before
    public void setUp() {
        allocator = new BalanceShardsAllocator();
        assigner = new ShardAssigner();
    }

    @Test
    public void test2Index2ShardAvailEnoughFor2() {
        // 2 shards for one index should be assigned, the other index should be totally unassigned
    }

    @Test
    public void testNoAvailSpace() {

    }

    @Test
    public void test1Index3Shard3Rep() {

    }

    @Test
    public void test1Index3Shard2Rep() {

    }

    @Test
    public void test1Index3Shard1Rep() {

    }

    @Test
    public void test1Index3Shard0Rep() {
//        List<Node> nodes = TestHelper.getNodes();
//        allocator.allocate(TestHelper.getShards(), TestHelper.getNodes(), 0, 1.f, 1.f, 1e-8f);
//        List<Assignment> assignments = assigner.getAssignments(nodes);
//        assertEquals(0, assignments.size());
    }

}
