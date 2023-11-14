all:
	mvn clean install

# You can pass in the replication factor in below command
run:
	java -cp target/shard-assigner-1.jar assigner.ShardAssigner -r 1 -n nodes.json -s shards.json

clean:
	mvn clean
