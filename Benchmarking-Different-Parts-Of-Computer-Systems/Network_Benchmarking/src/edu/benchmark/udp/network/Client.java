package edu.benchmark.udp.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Select Size of the Packets you want send using UDP: ");
			System.out.println("1. One Byte " + "\n" + "2. One Kilobyte " + "\n" + "3. 64 Kilobyte" + "\n" + "4. Exit");

			int packetChoice = Integer.parseInt(sc.nextLine());
			switch (packetChoice) {

			case 1:
				sendDataToServer(1);
				break;
			case 2:
				sendDataToServer(1000);
				break;
			case 3:
				sendDataToServer(64 * 1000);
				break;
			case 4:
				System.exit(0);
			default:
				System.out.println("Invalid Input");
			}
		}
	}

	private static void sendDataToServer(int packetSize) {

		int packetCount;
		int count = 0;
		Properties property = new Properties();

		try {
			property.load(new FileInputStream(new File("./resource/config.properties")));
			InetAddress ipAddress = InetAddress.getByName(property.getProperty("serverIp"));

			DatagramSocket clientSocket = new DatagramSocket();
			byte[] receiveData = new byte[64 * 1000];

			long startTime = System.currentTimeMillis();
			long timeDiff;
			double timeinSec;
			startTime = System.currentTimeMillis();

			byte[] bytePacket = new byte[packetSize];
			for (int i = 0; i < packetSize; i++) {
				bytePacket[i] = (byte) i;
			}
			DatagramPacket sendPacket = new DatagramPacket(bytePacket, bytePacket.length, ipAddress,
					Integer.parseInt(property.getProperty("serverPort")));
			DatagramPacket receiveServerResponse = null;

			packetCount = Integer.parseInt(property.getProperty("noOfPackets"));
			for (int i = 0; i < packetCount; i++) {
				clientSocket.send(sendPacket);
				count++;
				receiveServerResponse = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receiveServerResponse);
			}
			System.out.println(count + "Packets Sent to Server");
			System.out.println("Responses Received from Server");

			timeDiff = (System.currentTimeMillis() - startTime) / 2;
			timeinSec = new Double(timeDiff) / 1000;
			Double latency = new Double(timeDiff / (2 * packetCount));
			System.out.println("Latency in Milliseconds: " + latency + " MS");

			if (packetSize == 1) {
				System.out.println("Throughput : "
						+ new Double((2 * packetSize * packetCount * 8) / (timeinSec * 1000 * 1000)) + " Mb/s");
			}else if (packetSize == 1000) {
				System.out.println("Throughput : "
						+ new Double((2 * packetSize * packetCount * 8) / (timeinSec * 1000 * 1000)) + " Mb/s");
			}else if (packetSize == (64 * 1000)) {
				System.out.println("Throughput : "
						+ new Double((2 * packetSize * packetCount * 8) / (timeinSec * 1000 * 1000)) + " Mb/s");
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}
}