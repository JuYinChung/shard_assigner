package assigner;

import java.util.*;
class TestHelper {
    public static List<Shard> getShards() {
        return new ArrayList<>();
    }

    public static List<Node> getStandardNodes() {
        final List<Node> nodes = new ArrayList<>();
        nodes.add(new Node("NodeA", (long)1e9, (long)1e5));
        nodes.add(new Node("NodeB", (long)1e9, (long)1e5));
        nodes.add(new Node("NodeC", (long)1e9, (long)1e5));
        return nodes;
    }

    public static List<Node> getNodesWithDifferentDisk() {
        final List<Node> nodes = new ArrayList<>();
        nodes.add(new Node("NodeA", (long)1e9, (long)1e8));
        nodes.add(new Node("NodeB", (long)1e9, (long)1e8));
        nodes.add(new Node("NodeC", (long)1e9, (long)1e1));
        return nodes;
    }

    public static List<Node> getNodesNoSpace() {
        final List<Node> nodes = new ArrayList<>();
        nodes.add(new Node("NodeA", (long)1e9, (long)1e9));
        nodes.add(new Node("NodeB", (long)1e9, (long)1e9));
        nodes.add(new Node("NodeC", (long)1e9, (long)1e9));
        return nodes;
    }

    public static List<Shard> getStandardShards() {
        final List<Shard> shards = new ArrayList<>();
        shards.add(new Shard("Collection0", "Shard1", (long)1e4));
        shards.add(new Shard("Collection0", "Shard2", (long)1e4));
        shards.add(new Shard("Collection0", "Shard3", (long)1e4));
        return shards;
    }
    public static List<Shard> getShardsWithDifferentSize() {
        final List<Shard> shards = new ArrayList<>();
        shards.add(new Shard("Collection0", "Shard1", (long)1e8));
        shards.add(new Shard("Collection0", "Shard2", (long)1e8));
        shards.add(new Shard("Collection0", "Shard3", (long)1e8));
        shards.add(new Shard("Collection0", "Shard4", (long)1e1));
        return shards;
    }

    public static List<Node> getNodesWithLimitedSpace() {
        final List<Node> nodes = new ArrayList<>();
        nodes.add(new Node("NodeA", (long)1e9, (long)1e8 * 8));
        nodes.add(new Node("NodeB", (long)1e9, (long)1e8 * 8));
        return nodes;
    }

    public static List<Shard> get2IndexShards() {
        final List<Shard> shards = new ArrayList<>();
        shards.add(new Shard("Collection0", "Shard1", (long)1e8));
        shards.add(new Shard("Collection0", "Shard2", (long)1e8));
        shards.add(new Shard("Collection1", "Shard1", (long)1e8));
        shards.add(new Shard("Collection1", "Shard2", (long)1e8));
        return shards;
    }

    public static List<Shard> getMultipleIndex() {
        final List<Shard> shards = new ArrayList<>();
        shards.add(new Shard("Collection0", "Shard1", (long)1e5));
        shards.add(new Shard("Collection1", "Shard1", (long)1e5));
        return shards;
    }
}