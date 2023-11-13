all:
	mvn clean install

run:
	java -cp target/shard-assigner-1.jar ShardAssigner

clean:
	mvn clean
	rm -r output/*