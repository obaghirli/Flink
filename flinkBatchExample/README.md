### Flink Case Study: Batch

 - read CSV file
 - transform
 - write 

### Cases
- Case:1 - Unique Product View counts by ProductId
- Case:2 - Unique Event counts
- Case:3 - Top 5 Users who fulfilled all the events (view,add,remove,click)
- Case:4 - All events of #UserId : 47
- Case:5 - Product Views of #UserId : 47

### Constraints
- All calculations should be completed in single batch
- All outputs should be saved as txt files and '|' seperated
- Use Apache Flink and deployed to local Docker container

### Project Structure
- input data: ./data/case.csv
- jar file: ./flink-batch-csv-job/target/original-flink-batch-csv-job-1.0-SNAPSHOT.jar
- output: ./out

### Requirements
- Java 8.x
- Maven 3.0.4 (or higher)
- docker/docker-compose

### Run
Run the deploy.sh script from the 'flinkBatchExample' directory as:
```sh
$ cd flinkBatchExample
$ sudo sh deploy.sh
```

### Output
Please check the ```./out``` directory for the computation results
