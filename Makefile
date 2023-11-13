all:
	mvn clean install

# You can pass in the replication factor for below command
run:
	java -cp target/shard-assigner-1.jar assigner.ShardAssigner -r 3 -n nodes.json -s shards.json

clean:
	mvn clean
	rm -r output/*