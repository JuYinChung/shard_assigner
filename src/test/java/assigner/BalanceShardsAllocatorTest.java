package assigner;

import org.junit.Before;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.util.stream.Collectors;

public class BalanceShardsAllocatorTest {

    BalanceShardsAllocator allocator;
    ShardAssigner assigner;
    @Before
    public void setUp() {
        allocator = new BalanceShardsAllocator();
        assigner = new ShardAssigner();
    }

    @Test
    public void testNoAvailSpace() {
        final List<Node> nodes = TestHelper.getNodesNoSpace();
        final List<Shard> shards = TestHelper.getStandardShards();
        final List<Shard> unassigned = allocator.allocate(shards, nodes, 0, 1.f, 1.f, 1e-8f);
        final List<Assignment> assignments = assigner.getAssignments(nodes, true, true);
        final List<Assignment> primaryAssignments = assigner.getAssignments(nodes, true, false);
        final List<Assignment> replicaAssignments = assigner.getAssignments(nodes, false, false);
        assertEquals(0, assignments.size());
        assertEquals(0, primaryAssignments.size());
        assertEquals(0, replicaAssignments.size());
        assertEquals(shards.size(), unassigned.size());
    }

    // Test with one node with much less usage to see if shards will be balanced allocated
    // Even if one of the nodes has little usage, our algo won't be biased because we still need to consider shard count and index count at the same time
    @Test
    public void test1Index3Shard2RepWithDifferentDisk() {
        int rep = 2;
        verifySameIndex(TestHelper.getNodesWithDifferentDisk(), TestHelper.getStandardShards(), rep);
    }

    // Same test case as test1Index3Shard3RepWithDifferentDisk, but we emphasize disk usage more by adding the disk balance
    // Then our algo will bias the node with much less usage
    @Test
    public void test1Index3Shard2RepWithDifferentDiskBiasedDisk() {
        int rep = 2;
        final List<Node> nodes = TestHelper.getNodesWithDifferentDisk();
        final List<Shard> shards = TestHelper.getStandardShards();
        final List<Shard> unassigned = allocator.allocate(shards, nodes, rep, 1.f, 1.f, 1e-5f);
        final List<Assignment> assignments = assigner.getAssignments(nodes, true, true);
        final List<Assignment> primaryAssignments = assigner.getAssignments(nodes, true, false);
        final List<Assignment> replicaAssignments = assigner.getAssignments(nodes, false, false);
        assertEquals(shards.size() * (rep + 1), assignments.size());
        assertEquals(shards.size(), primaryAssignments.size());
        assertEquals(shards.size() * rep, replicaAssignments.size());
        assertEquals(0, unassigned.size());
        Node biasedNode = null;
        final List<Node> unbiasedNodes = new ArrayList<>();
        for(Node node : nodes) {
            if(node.getId().equals("NodeC")) {
                biasedNode = node;
            } else {
                unbiasedNodes.add(node);
            }
        }
        assertTrue(biasedNode.getAllocatedShards().size() >= unbiasedNodes.get(0).getAllocatedShards().size());
        assertTrue(biasedNode.getAllocatedShards().size() >= unbiasedNodes.get(1).getAllocatedShards().size());
    }

    @Test
    public void test1Index3Shard2Rep() {
        int rep = 2;
        verifySameIndex(TestHelper.getStandardNodes(), TestHelper.getStandardShards(), rep);
    }

    @Test
    public void test1Index3Shard1Rep() {
        int rep = 1;
        verifySameIndex(TestHelper.getStandardNodes(), TestHelper.getStandardShards(), rep);
    }

    @Test
    public void test1Index3Shard0Rep() {
        int rep = 0;
        verifySameIndex(TestHelper.getStandardNodes(), TestHelper.getStandardShards(), rep);
    }

    // Make sure disk usage are balanced. We have 2 big shard size Shard1, Shard2 and 2 small shard size Shard3, Shard4 assigned to 2 nodes
    // To be balanced, Shard1 should be with shard3, shard2 should be with shard 4
    @Test
    public void test1Index2Shard0RepWithDifferentShardSize() {
        int rep = 0;
        final List<Node> nodes = TestHelper.getStandardNodes();
        nodes.remove(nodes.size() - 1);
        final List<Shard> shards = TestHelper.getShardsWithDifferentSize();
        final List<Shard> unassigned = allocator.allocate(shards, nodes, rep, 1.f, 1.f, 1e-8f);
        final List<Assignment> assignments = assigner.getAssignments(nodes, true, true);
        final List<Assignment> primaryAssignments = assigner.getAssignments(nodes, true, false);
        final List<Assignment> replicaAssignments = assigner.getAssignments(nodes, false, false);
        assertEquals(shards.size() * (rep + 1), assignments.size());
        assertEquals(shards.size(), primaryAssignments.size());
        assertEquals(shards.size() * rep, replicaAssignments.size());
        assertEquals(0, unassigned.size());
        for(Node node : nodes) {
            assertEquals(2, node.getAllocatedShards().size());
        }
        Set<String> nodeSet = assignments.stream()
                .filter(a -> a.getShardName().equals("Shard1") || a.getShardName().equals("Shard3"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        assertEquals(1, nodeSet.size());
        nodeSet = assignments.stream()
                .filter(a -> a.getShardName().equals("Shard2") || a.getShardName().equals("Shard4"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        assertEquals(1, nodeSet.size());
    }

    // same index should be allocated to different nodes
    @Test
    public void test2Index3Node1RepBalanced() {
        int rep = 2;
        final List<Node> nodes = TestHelper.getStandardNodes();
        final List<Shard> shards = TestHelper.getMultipleIndex();
        final List<Shard> unassigned = allocator.allocate(shards, nodes, rep, 1.f, 1.f, 1e-8f);
        final List<Assignment> assignments = assigner.getAssignments(nodes, true, true);
        final List<Assignment> primaryAssignments = assigner.getAssignments(nodes, true, false);
        final List<Assignment> replicaAssignments = assigner.getAssignments(nodes, false, false);
        assertEquals(shards.size() * (rep + 1), assignments.size());
        assertEquals(shards.size(), primaryAssignments.size());
        assertEquals(shards.size() * rep, replicaAssignments.size());
        assertEquals(0, unassigned.size());
        Set<String> indexList = assignments.stream()
                .filter(a -> a.getId().equals("NodeA"))
                .map(a -> a.getIndex())
                .collect(Collectors.toSet());
        assertEquals(2, indexList.size());
        indexList = assignments.stream()
                .filter(a -> a.getId().equals("NodeB"))
                .map(a -> a.getIndex())
                .collect(Collectors.toSet());
        assertEquals(2, indexList.size());
        indexList = assignments.stream()
                .filter(a -> a.getId().equals("NodeC"))
                .map(a -> a.getIndex())
                .collect(Collectors.toSet());
        assertEquals(2, indexList.size());
    }

    // 2 shards for the same index should be assigned first
    @Test
    public void test2Index2ShardAvailEnoughFor2() {
        final List<Node> nodes = TestHelper.getNodesWithLimitedSpace();
        final List<Shard> shards = TestHelper.get2IndexShards();
        final List<Shard> unassigned = allocator.allocate(shards, nodes, 0, 1.f, 1.f, 1e-8f);
        final List<Assignment> assignments = assigner.getAssignments(nodes, true, true);
        final List<Assignment> primaryAssignments = assigner.getAssignments(nodes, true, false);
        final List<Assignment> replicaAssignments = assigner.getAssignments(nodes, false, false);
        assertEquals(2, assignments.size());
        assertEquals(2, primaryAssignments.size());
        assertEquals(0, replicaAssignments.size());
        assertEquals(2, unassigned.size());
    }


    private void verifySameIndex(final List<Node> nodes, final List<Shard> shards, final int rep) {
        final List<Shard> unassigned = allocator.allocate(shards, nodes, rep, 1.f, 1.f, 1e-8f);
        final List<Assignment> assignments = assigner.getAssignments(nodes, true, true);
        final List<Assignment> primaryAssignments = assigner.getAssignments(nodes, true, false);
        final List<Assignment> replicaAssignments = assigner.getAssignments(nodes, false, false);
        assertEquals(shards.size() * (rep + 1), assignments.size());
        assertEquals(shards.size(), primaryAssignments.size());
        assertEquals(shards.size() * rep, replicaAssignments.size());
        assertEquals(0, unassigned.size());
        for(Node node : nodes) {
            assertEquals(rep + 1, node.getAllocatedShards().size());
        }

        // test if primary and replicas are balanced allocated
        Set<String> nodeSet = assignments.stream()
                .filter(a -> a.getShardName().equals("Shard1"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        assertEquals(rep + 1 > nodes.size() ? nodes.size() : rep + 1, nodeSet.size());
        nodeSet = assignments.stream()
                .filter(a -> a.getShardName().equals("Shard2"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        assertEquals(rep + 1 > nodes.size() ? nodes.size() : rep + 1, nodeSet.size());
        nodeSet = assignments.stream()
                .filter(a -> a.getShardName().equals("Shard3"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        assertEquals(rep + 1 > nodes.size() ? nodes.size() : rep + 1, nodeSet.size());
        // test replicas are on different nodes than the primary
        Set<String>  rNodeSet = replicaAssignments.stream()
                .filter(a -> a.getShardName().equals("Shard1"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        List<String> pNodeList = primaryAssignments.stream()
                .filter(a -> a.getShardName().equals("Shard1"))
                .map(a -> a.getId())
                .collect(Collectors.toList());
        assertFalse(rNodeSet.contains(pNodeList.get(0)));
        rNodeSet = replicaAssignments.stream()
                .filter(a -> a.getShardName().equals("Shard2"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        pNodeList = primaryAssignments.stream()
                .filter(a -> a.getShardName().equals("Shard2"))
                .map(a -> a.getId())
                .collect(Collectors.toList());
        assertFalse(rNodeSet.contains(pNodeList.get(0)));
        rNodeSet = replicaAssignments.stream()
                .filter(a -> a.getShardName().equals("Shard3"))
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        pNodeList = primaryAssignments.stream()
                .filter(a -> a.getShardName().equals("Shard3"))
                .map(a -> a.getId())
                .collect(Collectors.toList());
        assertFalse(rNodeSet.contains(pNodeList.get(0)));
    }
}
