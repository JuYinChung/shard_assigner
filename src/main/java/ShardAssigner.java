//package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

class ShardAssigner{
	
	public static void main(String[] args) {

		List<Shard> shards = getShards();
		List<Node> nodes = getNodes();
		BalanceShardsAllocator allocator = new BalanceShardsAllocator();
		allocator.allocate(shards, nodes, 3);
		System.out.println(nodes);

	}

	public static List<Shard> getShards() {

		List<Shard> shards = null;
		try {
			final ClassLoader classLoader = ShardAssigner.class.getClassLoader();

			InputStream is = classLoader.getResourceAsStream("shards.json");
			String responseStr = IOUtils.toString(is);

			shards = new ObjectMapper().readValue(responseStr, new TypeReference<List<Shard>>(){});
		} catch (IOException e) {
			System.out.println(e);
		}
		return shards;
	}

	public static List<Node> getNodes() {

		List<Node> nodes = null;
		try {
			final ClassLoader classLoader = ShardAssigner.class.getClassLoader();

			InputStream is = classLoader.getResourceAsStream("nodes.json");
			String responseStr = IOUtils.toString(is);

			nodes = new ObjectMapper().readValue(responseStr, new TypeReference<List<Node>>(){});
		} catch (IOException e) {
			System.out.println(e);
		}
		return nodes;
	}
}