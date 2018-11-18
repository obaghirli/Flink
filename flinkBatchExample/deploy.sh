#!/usr/bin/env bash

# generate the jar file
cd flink-batch-csv-job
mvn clean package -DskipTests
cd ..

# build the flink image
chmod -R a+rwx build.sh
./build.sh --job-jar ./flink-batch-csv-job/target/original-flink-batch-csv-job-1.0-SNAPSHOT.jar --from-release --flink-version 1.2.1 --hadoop-version 2.7 --scala-version 2.10 --image-name flink-job

# start the container and submit the job
docker-compose up -d
