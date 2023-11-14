package assigner;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class Node{
    @JsonProperty("id")
    private String id;
    @JsonProperty("total_space")
    private long totalSpace;
    @JsonProperty("used_space")
    private long usedSpace;
    private List<Shard> allocatedShards = new ArrayList<>();

    public Node() {
    }

    public Node(String id, long totalSpace, long usedSpace) {
        this.id = id;
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public List<Shard> getAllocatedShards() {
        return allocatedShards;
    }

    public void setAllocatedShards(List<Shard> allocatedShards) {
        this.allocatedShards = allocatedShards;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", totalSpace=" + totalSpace +
                ", usedSpace=" + usedSpace +
                ", allocatedShards=" + allocatedShards +
                '}';
    }

    public boolean containsShard(final String index, final int shardId) {
        return allocatedShards.stream()
                .filter(s -> s.getIndex().equals(index))
                .filter(s -> s.getShardId() == shardId)
                .count() >= 1;
    }

    // get max shard id for all allocated primary shards for given index
    public int highestPrimary(String index) {
        return allocatedShards.stream()
                .filter(s -> s.getIndex().equals(index))
                .filter(s -> s.isPrimary())
                .mapToInt(s -> s.getShardId()).max().orElse(-1);

    }

    public long getAllocatedShardCount(final String index) {
        return allocatedShards.stream()
                .filter(s -> s.getIndex().equals(index))
                .count();
    }

    public long diskUsageInBytes() {
        // The total disk usage in node is the existing usage (usedSpace) + sum of allocated shard size
        return usedSpace +
                allocatedShards.stream()
                        .mapToLong(i -> i.getSize()).sum();
    }
}