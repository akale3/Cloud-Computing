#!/bin/bash

## Install JAVA and ANT and GCC
clear
#Java Installation
sudo apt-get update
sudo apt-add-repository ppa:webupd8team/java -y
sudo apt-get update
sudo apt-get install oracle-java8-installer -y
# Ant installation
sudo apt-get install ant -y
sudo apt-get update
# GCC installation
sudo apt-get install gcc -y
sudo apt-get update
clear

## Mount EBS Volume

lsblk
sudo file -s /dev/xvdb
sudo mkfs -t ext4 /dev/xvdb
sudo mkdir /aditya
sudo mount /dev/xvdb /aditya
sudo chmod 777 /aditya/

## Send code to newly created ebs volume
scp -i aditya1.pem -r SharedMemorySort/ ubuntu@5.34.175.147:/aditya/

## Generate 10GB file on EBS volume.
cd /aditya/SharedMemorySort/gensort-linux-1.5/64/
./gensort -a 100000000 10GBFile

## Edit configuration file in Shared Memory TeraSort.
## set source path to " /aditya/SharedMemorySort/gensort-linux-1.5/64/10GBFile"
## Change noOfThreads you want to execute ant run program
rm -r build
ant
