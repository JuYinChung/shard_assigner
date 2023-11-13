import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

class ShardAssigner{
	
	public static void main(String[] args) {
		int repFactor = 0;
		try {
			repFactor = Integer.parseInt(args[0]);
		} catch (final Exception e) {
			System.out.println(e);
		}
		List<Shard> shards = readShardsFromFile();
		List<Node> nodes = readNodesFromFile();
		BalanceShardsAllocator allocator = new BalanceShardsAllocator();
		allocator.allocate(shards, nodes, repFactor);
		List<Assignment> assignments = new ArrayList<>();
		for(Node node : nodes) {
			for(Shard shard : node.getAllocatedShards()) {
				assignments.add(new Assignment(node.getId(), shard.getIndex(), shard.getShardName()));
			}
		}
		System.out.println(assignments);
		writeAssignmentsToFile(assignments);
	}

	public static void writeAssignmentsToFile(final List<Assignment> assignments) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			String str = mapper.writeValueAsString(assignments);
			BufferedWriter writer = new BufferedWriter(new FileWriter("output/assignments.json"));
			writer.write(str);
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static List<Shard> readShardsFromFile() {
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

	public static List<Node> readNodesFromFile() {
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