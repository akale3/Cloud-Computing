#!/bin/bash

## Download Spark Package and extract it to a local machine
wget www-eu.apache.org/dist/spark/spark-1.6.0/spark-1.6.0-bin-hadoop2.6.tgz
tar -xzvf spark-1.6.0-bin-hadoop2.6.tgz
cd spark-1.6.0-bin-hadoop2.6/ec2

## Export root key from amazon account and set it.
export AWS_ACCESS_KEY_ID="XXXX"
export AWS_SECRET_ACCESS_KEY="XXXX"

## Create a spark cluster
./spark-ec2 -k adityaSparkKey -i /home/aditya/Downloads/spark/adityaSparkKey.pem -s 1 -t c3.large --spot-price=0.02 launch spark_cluster
## For 16 nodes
## ./spark-ec2 -k adityaSparkKey -i /home/aditya/Downloads/spark/adityaSparkKey.pem -s 16 -t c3.large --spot-price=0.02 launch spark_cluster

## Logi to a Spark Cluster
./spark-ec2 -k adityaSparkKey.pem -i /home/aditya/Downloads/spark/adityaSparkKey.pem login spark_cluster


## Mount EBS Volume
lsblk
sudo file -s /dev/xvdb
sudo mkfs -t ext4 /dev/xvdb
sudo mkdir /aditya
sudo mount /dev/xvdb /aditya
sudo chmod 777 /aditya/

## Generate a 100GB file using gensort
cd aditya
wget http://www.ordinal.com/try.cgi/gensort-linux-1.5.tar.gz
tar -xzvf gensort-linux-1.5.tar.gz
cd 64
./gensort -a 1000000000 100GBFile

## Create a hdfs Directory on hadoop 
cd ephemeral-hdfs/bin
bin/hadoop fs -mkdir /hdfs_aditya
bin/hadoop fs -Ddfs.replication=1 -put /aditya/64/100GBFile /hdfs_aditya/
bin/hadoop dfs -ls /hdfs_aditya/

cd ~
cd spark/bin

## Create a Scala file and Run a scala program to sort a file.
## Create file : vi SparkSort.scala
## val starTime = System.currentTimeMillis()
## val inputfile = sc.textFile("hdfs:/aditya/100GBFile")
## val sort_file = inputfile.map(line => (line.take(10), line.drop(10)))
## val sort = sort_file.sortByKey()
## val lines = sort.map {case (key,value) => s"$key $value"}
## lines.saveAsTextFile("/aditya/output100GB")
## val endTime = System.currentTimeMillis()
## println ("Time Taken for Completion of Sorting is :" + (endTime - startTime) + "MilliSeconds")

./spark-shell -i /root/SparkSort.scala


## Get generated sorted file output from hdfs to mounted drive

bin/hadoop dfs -getmerge /hdfs_aditya/output100GB /aditya/sortedFile100GB

## validate using valsort
cd /aditya
./64/valsort /aditya/sortedFile100GB
head sortedFile100GB
tail sortedFile100GB


