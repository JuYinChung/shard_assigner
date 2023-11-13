import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BalanceShardsAllocator {

    public void allocate(final List<Shard> shards, final List<Node> nodes, final int repFactor) {
        final WeightFunction weightFunction = new WeightFunction(
                1.f,
                1.f,
                1e-8f
        );
        final Balancer balancer = new Balancer(weightFunction, shards, nodes, repFactor);
        balancer.allocateUnassigned();
        balancer.printAllWeights();
    }

    private static class WeightFunction {

        private final float theta0;
        private final float theta1;
        private final float theta2;

        WeightFunction(float indexBalance, float shardBalance, float diskUsageBalance) {
            float sum = indexBalance + shardBalance + diskUsageBalance;
            if (sum <= 0.0f) {
                throw new IllegalArgumentException("Balance factors must sum to a value > 0 but was: " + sum);
            }
            theta0 = shardBalance / sum;
            theta1 = indexBalance / sum;
            theta2 = diskUsageBalance / sum;
        }

        float weight(Balancer balancer, Node node, String index) {
            final float weightShard = node.getAllocatedShards().size() - balancer.avgShardsPerNode();
            final float weightIndex = node.getAllocatedShardCount(index) - balancer.avgShardsPerNode(index);
            final float diskUsage = (float) (node.diskUsageInBytes() - balancer.avgDiskUsageInBytesPerNode());
            float res = (theta0 * weightShard) + (theta1 * weightIndex) + (theta2 * diskUsage);
            //System.out.println("WeightShard: " + weightShard + " weightIndex: " + weightIndex + " diskUsage: " + diskUsage);
            return res;
        }
    }

    public static class Balancer {
        private final WeightFunction weight;
        private final List<Shard> shards;
        private final List<Node> nodes;
        private final int repFactor;
        private final float avgShardsPerNode;
        private final double avgDiskUsageInBytesPerNode;

        private Balancer(WeightFunction weight, final List<Shard> shards, final List<Node> nodes, final int repFactor) {
            this.weight = weight;
            avgShardsPerNode = ((float) shards.size()) / nodes.size();
            avgDiskUsageInBytesPerNode =
                    ((double) shards.stream()
                    .mapToLong(i -> i.getSize()).sum() / nodes.size());
            this.shards = shards;
            this.nodes = nodes;
            this.repFactor = repFactor;
        }

        public void allocateUnassigned() {
            for(final Shard primary : shards) {
                decideAllocateUnassigned(primary, false);
            }

            System.out.println("Finished allocating primary\n\n");
            for(int i = 0;i < repFactor;i++) {
                for(final Shard primary : shards) {
                    final Shard replica = primary.clone();
                    replica.setPrimary(false);
                    decideAllocateUnassigned(replica, false);
                }
            }
        }

        private void decideAllocateUnassigned (final Shard shard, final boolean ignore) {
            float minWeight = Float.POSITIVE_INFINITY;
            Node minNode = null;
            for (Node node : nodes) {
                if (!ignore && node.containsShard(shard.getIndex(), shard.getShardId())) {
                    // decision is NO without needing to check anything further, so short circuit
                    continue;
                }

                // simulate weight if we would add shard to node
                float currentWeight = weight.weight(this, node, shard.getIndex());
                // moving the shard would not improve the balance, and we are not in explain mode, so short circuit
                if (currentWeight > minWeight) {
                    continue;
                }
                final boolean updateMinNode;
                System.out.println("NodeId:" + node.getId() + " currentW: " + currentWeight + " minW: " + minWeight);
                if (currentWeight == minWeight) {
                    /*  we have an equal weight tie breaking:
                     *  1. if one decision is YES prefer it
                     *  2. prefer the node that holds the primary for this index with the next id in the ring ie.
                     *  for the 3 shards 2 replica case we try to build up:
                     *    1 2 0
                     *    2 0 1
                     *    0 1 2
                     *  such that if we need to tie-break we try to prefer the node holding a shard with the minimal id greater
                     *  than the id of the shard we need to assign. This works find when new indices are created since
                     *  primaries are added first and we only add one shard set a time in this algorithm.
                     */
                    final int repId = shard.getShardId();
                    final int nodeHigh = node.highestPrimary(shard.getIndex());
                    final int minNodeHigh = minNode.highestPrimary(shard.getIndex());
                    updateMinNode = ((((nodeHigh > repId && minNodeHigh > repId)
                            || (nodeHigh < repId && minNodeHigh < repId))
                            && (nodeHigh < minNodeHigh))
                            || (nodeHigh > repId && minNodeHigh < repId));
                } else {
                    updateMinNode = true;
                }
                if (updateMinNode) {
                    minNode = node;
                    minWeight = currentWeight;
                }

            }

            if(minNode != null) {
                // decision made, allocate node
                minNode.getAllocatedShards().add(shard);
                System.out.println("Allocated shard " + shard + " to " + minNode.getId() + "\n");
            } else {
                System.out.println("No decision. Reallocate " + shard + "\n");
                decideAllocateUnassigned(shard, true);
            }

        }

        public void printAllWeights() {
            Set<String> indices = shards.stream().map(s -> s.getIndex()).collect(Collectors.toSet());
            for(String index : indices) {
                for(Node node : nodes) {
                    System.out.println("Index: "+ index + " Node: " + node.getId() + " Weight: " + weight.weight(this, node, index));
                }
            }
            System.out.println("");
        }

        public float avgShardsPerNode() {
            return avgShardsPerNode;
        }

        public double avgDiskUsageInBytesPerNode() {
            return avgDiskUsageInBytesPerNode;
        }

        public float avgShardsPerNode(String index) {
            return ((float) shards.stream()
                    .filter(s -> s.getIndex().equals(index)).count()) / nodes.size();
        }
    }
}
