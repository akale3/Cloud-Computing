#!/bin/sh

clear
#Java Installation
sudo apt-get update
sudo apt-add-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
# Ant installation
sudo apt-get install ant
sudo apt-get update
# GCC installation
sudo apt-get install gcc
sudo apt-get update
clear

## Install cluster ssh
## Prerequisites
##vInstall openSSH on your Remote systems

sudo apt-get install openssh-server

# Install cluster SSH

sudo apt-get install clusterssh

sudo nano /etc/clusters

## do entries of ip to create cluster
## clusters = testcluster
## testcluster = ubuntu@552.34.231.223 ubuntu@52.34.231.239

## Run this command to start a cluster
cssh -l ubuntu -o "-i aditya.pem" testcluster


## create different taskid with different sleep values in Queing service.
vi ./resources/config.properties
## change tasknumbers and sleep time.
rm ./resources/workloadFile
java -cp ./build/jar/QueingService.jar  edu.utility.TaskCreation

# Run Client and workers with different threads on local machine.
java -cp ./build/jar/QueingService.jar  edu.executor.ExecuteTask –s LOCAL t 1 -w ./resources/workloadFile



