package assigner;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

class ShardAssigner{
	
	public static void main(String[] args) {
		final Map<String, Object> arguments = getArgs(args);
		System.out.println(arguments);
		final List<Shard> shards = readInputFromFile((String)arguments.get("s"), new TypeReference<List<Shard>>(){});
		final List<Node> nodes = readInputFromFile((String)arguments.get("n"), new TypeReference<List<Node>>(){});
		final BalanceShardsAllocator allocator = new BalanceShardsAllocator();
		final List<Shard> unassignedShards = allocator.allocate(shards, nodes, (int)arguments.get("r"), 1.f, 1.f, 1e-8f);
		final List<Assignment> assignments = getAssignments(nodes, true, true);
		final List<Assignment> primaryAssignments = getAssignments(nodes, true, false);
		final List<Assignment> replicaAssignments = getAssignments(nodes, false, false);
		writeOutputsToFile(unassignedShards, "output/unassigned_shards.json");
		writeOutputsToFile(assignments, "output/assignments.json");
		writeOutputsToFile(primaryAssignments, "output/primary_assignments.json");
		writeOutputsToFile(replicaAssignments, "output/replica_assignments.json");
	}

	private static Map<String, Object> getArgs(String[] args) {
		final Map<String, Object> arguments = new HashMap<>();
		for(int i = 0;i < args.length;i+=2) {
			switch(args[i].substring(1, args[i].length())) {
				case "r":
					arguments.put("r", Integer.parseInt(args[i + 1]));
					break;
				case "n":
					arguments.put("n", args[i + 1]);
					break;
				case "s":
					arguments.put("s", args[i + 1]);
					break;
			}
		}
		return arguments;
	}

	public static List<Assignment> getAssignments(final List<Node> nodes, final boolean isPrimary, final boolean ignore) {
		final List<Assignment> assignments = new ArrayList<>();
		for(Node node : nodes) {
			for(Shard shard : node.getAllocatedShards()) {
				if(ignore || shard.isPrimary() == isPrimary) {
					assignments.add(new Assignment(node.getId(), shard.getIndex(), shard.getShardName()));
				}
			}
		}
		return assignments;
	}

	private static void writeOutputsToFile(final Object object, final String path) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			String str = mapper.writeValueAsString(object);
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(str);
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static <T> List<T> readInputFromFile(final String path, final TypeReference<List<T>> typeReference) {
		List<T> input = null;
		try {
			final ClassLoader classLoader = ShardAssigner.class.getClassLoader();

			InputStream is = classLoader.getResourceAsStream(path);
			String responseStr = IOUtils.toString(is);

			input = new ObjectMapper().readValue(responseStr, typeReference);
		} catch (IOException e) {
			System.out.println(e);
		}
		return input;
	}
}