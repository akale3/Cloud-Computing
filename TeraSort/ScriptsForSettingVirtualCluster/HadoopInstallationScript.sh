#!/bin/bash

## Send Hadoop MapReduce Sorting Jar and all updated Configuration xml files to an instance and connect to ana instance.
scp -i aditya1.pem -r MapReduceSort ubuntu@ec2-52-37-161-38.us-west-2.compute.amazonaws.com:./
ssh -i aditya1.pem ubuntu@ec2-52-37-161-38.us-west-2.compute.amazonaws.com -y

## Java Installation
sudo apt-get update
sudo apt-add-repository ppa:webupd8team/java -y
sudo apt-get update
sudo apt-get install oracle-java8-installer -y

## Ant installation
sudo apt-get install ant -y
sudo apt-get update

## GCC installation
sudo apt-get install gcc -y
sudo apt-get update -y

## Mount EBS Volume
lsblk
sudo file -s /dev/xvdb
sudo mkfs -t ext4 /dev/xvdb
sudo mkdir /aditya
sudo mount /dev/xvdb /aditya
sudo chmod 777 /aditya/

## Hadoop Installation
wget http://www-us.apache.org/dist/hadoop/common/hadoop-2.7.2/hadoop-2.7.2.tar.gz
tar -xzvf hadoop-2.7.2.tar.gz
chmod 777 hadoop-2.7.2
chmod 777 MapReduceSort
sudo apt-get install ssh -y

## Change configuration files.
## change core-site.xml, hadoop-env.sh, hdfs-site.xml, yarn-site.xml and mapred-site.xml in hadoop-2.7.2/etc/hadoop/ folder.
cp MapReduceSort/UpdatedFiles/core-site.xml hadoop-2.7.2/etc/hadoop/
cp MapReduceSort/UpdatedFiles/hadoop-env.sh hadoop-2.7.2/etc/hadoop/
cp MapReduceSort/UpdatedFiles/hdfs-site.xml hadoop-2.7.2/etc/hadoop/
cp MapReduceSort/UpdatedFiles/mapred-site.xml hadoop-2.7.2/etc/hadoop/
cp MapReduceSort/UpdatedFiles/yarn-site.xml hadoop-2.7.2/etc/hadoop/
cp MapReduceSort/UpdatedFiles/slaves hadoop-2.7.2/etc/hadoop/

## create image of master and launch slave instances
## edit slaves in /etc/hadoop/ and /etc/hosts file in master and slaves instances

## master node: slaves file : public dns of master and all the slaves
##		hosts file : private_ip and public_dns of master and all the slaves

## slave node : slaves file : public dns of master and that slave
##		hosts file : private_ip and public_dns of master and that slave

## Repeat below 6 steps on all nodes.
eval "$(ssh-agent)"
cd ~
mv MapReduceSort/aditya1.pem ./
chmod 600 aditya1.pem
ssh-add aditya1.pem
ssh-keygen  -t rsa
ssh-copy-id -i ~/.ssh/id_rsa.pub ubuntu@ec2-52-37-161-38.us-west-2.compute.amazonaws.com
chmod 0600 ~/.ssh/authorized_keys

## Start hadoop.
cd hadoop-2.7.2/
bin/hadoop namenode -format
ssh localhost 
cd hadoop-2.7.2/sbin/
./start-dfs.sh        
./start-yarn.sh

jps
## Your Hadoop configuration is done. 

## Run all below command from main hadoop folder "hadoop-2.7.2". 
## command for creating Hadoop directory
cd..
bin/hadoop fs -mkdir /hdfs_aditya

## Generate file using gensort and Copy file to hadoop location. 
mv /home/ubuntu/MapReduceSort/gensort-linux-1.5/ /aditya/
cd /aditya/gensort-linux-1.5/64/
./gensort -a 1000000000 100GBFile
cd ~
cd hadoop-2.7.2
bin/hadoop dfs -copyFromLocal /aditya/gensort-linux-1.5/64/100GBFile /hdfs_aditya
bin/hadoop dfs -ls /hdfs_aditya/
bin/hadoop dfs -rm -rf /hdfs_aditya/output

## Run Mapreduce Jar
bin/hadoop jar /home/ubuntu/MapReduceSort/MapReduceSort/build/jar/MapReduceTeraSort.jar /hdfs_aditya/100GBFile /hdfs_aditya/output >&log.out&

#Command to get the file from hadoop file system to our instance EC2.
bin/hadoop dfs -get /hdfs_aditya/output/part-r-00000 /aditya/

## Check file using valsort
cd /aditya/gensort-linux-1.5/64/
./valsort /aditya/part-r-00000
head part-r-00000
tail part-r-00000
