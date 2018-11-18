package org.example.flink

import org.apache.flink.api.scala._
import org.apache.flink.api.common.operators.Order
import org.apache.flink.core.fs.FileSystem.WriteMode

case class Itenary(date: Long, productId: Int, eventName: String, userId: Int)

object BatchJob {
  def main(args: Array[String]) {

    // define appConfig
    val dataPath = "/opt/data/case.csv"
    val outputPathCase_1 = "/opt/out/case_1.txt"
    val outputPathCase_2 = "/opt/out/case_2.txt"
    val outputPathCase_3 = "/opt/out/case_3.txt"
    val outputPathCase_4 = "/opt/out/case_4.txt"
    val outputPathCase_5 = "/opt/out/case_5.txt"

    // set up the batch execution environment
    val env = ExecutionEnvironment.getExecutionEnvironment

    // read a csv file and store it as a dataset
    val ds: DataSet[Itenary] = env.readCsvFile[Itenary](
      filePath = dataPath,
      lineDelimiter = "\n",
      fieldDelimiter = "|",
      ignoreFirstLine = true)

    // define filter functions
    def filterEventName(row: Itenary, predicate: String): Boolean = {
      row.eventName.equals(predicate)
    }

    def filterEventTypeCount(row: (Int, Int)): Boolean = {
      row._2.equals(4)
    }

    def filterUser(row: Itenary, predicate: Int): Boolean = {
      row.userId.equals(predicate)
    }

    /**
      * Case 1: Unique Product View counts by ProductId
      */

    val out_case_1 = ds
      .filter(row => filterEventName(row, "view"))
      .distinct("productId", "eventName", "userId")
      .map(x => (x.productId, 1))
      .groupBy(0)
      .sum(1)

    out_case_1
      .writeAsCsv(
        filePath = outputPathCase_1,
        fieldDelimiter = "|",
        rowDelimiter = "\n",
        writeMode = WriteMode.OVERWRITE)
      .setParallelism(1)


    /**
      * Case 2: Unique Event counts
      */

    val out_case_2 = ds
      .distinct("productId", "eventName", "userId")
      .map(x => (x.eventName, 1))
      .groupBy(0)
      .sum(1)

    out_case_2
      .writeAsCsv(
        filePath = outputPathCase_2,
        fieldDelimiter = "|",
        rowDelimiter = "\n",
        writeMode = WriteMode.OVERWRITE)
      .setParallelism(1)


    /**
      * Case 3: Top 5 Users who fulfilled all the events (view,add,remove,click)
      */

    // rank users by their total transaction counts
    // i.e. sum of all (view, add, click, remove) activities
    val sortedUsers = ds
      .map(x => (x.userId, 1))
      .groupBy(0)
      .sum(1)
      .sortPartition(1, Order.DESCENDING)
      .setParallelism(1)

    // find users who fulfilled all the events (view, add, click, remove)
    val usersWithAllEvents = ds
      .distinct("eventName", "userId")
      .map(x => (x.userId, 1))
      .groupBy(0)
      .sum(1)
      .filter(row => filterEventTypeCount(row))

    // join (1) and (2), and then sort by (2)
    val out_case_3 = usersWithAllEvents
      .join(sortedUsers).where(0).equalTo(0)
      .map(x =>(x._1._1,x._2._2 ))
      .sortPartition(1, Order.DESCENDING)
      .setParallelism(1)
      .first(5)
      .map(x => x._1)

    out_case_3
      .writeAsText(
        filePath = outputPathCase_3,
        writeMode = WriteMode.OVERWRITE)
      .setParallelism(1)


    /**
      * Case 4: All events of #UserId : 47
      */

    val out_case_4 = ds
      .filter(row => filterUser(row, 47))
      .map(x => (x.eventName, 1))
      .groupBy(0)
      .sum(1)

    out_case_4
      .writeAsCsv(
        filePath = outputPathCase_4,
        fieldDelimiter = "|",
        rowDelimiter = "\n",
        writeMode = WriteMode.OVERWRITE)
      .setParallelism(1)


    /**
      * Case 5: Product Views of #UserId : 47
      */

    val out_case_5 = ds
      .filter(row => filterUser(row, 47))
      .filter(row => filterEventName(row, "view"))
      .map(x => x.productId)

    out_case_5
      .writeAsText(
        filePath = outputPathCase_5,
        writeMode = WriteMode.OVERWRITE)
      .setParallelism(1)

    // execute program
    env.execute("Batch CSV Job")
  }
}