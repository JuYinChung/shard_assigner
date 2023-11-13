all:
	mvn clean install

run:
	java -cp target/shard-assigner-1.jar ShardAssigner 0

clean:
	mvn clean
	rm -r output/*