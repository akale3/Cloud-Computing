/**
  * Created by sabyasac on 3/18/16.
  */

import java.io.{File, BufferedWriter, FileWriter}

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import scala.collection.immutable.ListMap

object FileSort {

  def main(args: Array[String]) {
    val logFile = "spark-1.6.1-bin-hadoop2.6/input" // Should be some file on your system
    val conf = new SparkConf().setAppName("File Sort").set("spark.executor.memory", "2g")
    val sc = new SparkContext(conf)
    val file = new File("ouput")
    val bw = new BufferedWriter(new FileWriter(file))
    val data = sc.textFile(logFile)
    val splitData = data.flatMap(line => line.split("\n"))

    val arrayFromCollectedData = splitData.flatMap(line => Map(line.substring(0,10) -> line.substring(10,line.length)))
    val mapFromArray = arrayFromCollectedData.map(line => line._1 -> line._2)
    val sortedData = mapFromArray.sortBy(_._1)
    val collectedData = sortedData.collect()
    collectedData.foreach(line => bw.write(line._1 + line._2 + "\r\n"))

  }
}
