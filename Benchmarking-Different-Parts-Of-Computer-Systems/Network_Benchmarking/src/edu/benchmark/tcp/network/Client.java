package edu.benchmark.tcp.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Select Size of the Packets you want send using TCP: ");
			System.out.println(
					"1. One Byte " + "\n" + "2. One Kilobyte " + "\n" + "3. SixtyFour Kilobyte" + "\n" + "4. Exit");

			int packetSize = sc.nextInt();
			switch (packetSize) {

			case 1:
				startClient(1);
				break;
			case 2:
				startClient(1000);
				break;
			case 3:
				startClient(64 * 1000);
				break;
			case 4:
				System.exit(0);
			default:
				System.out.println("Invalid Input");
			}
		}

	}

	/**
	 * @param packetSize
	 *            takes packet size as 1 Byte, 1KByte, 64KByte. This method
	 *            sends packets to a server.
	 */
	private static void startClient(int packetSize) {

		Properties property = new Properties();
		int packetCount;
		Socket clientSocket;
		try {
			property.load(new FileInputStream(new File("./resource/config.properties")));
			clientSocket = new Socket(property.getProperty("serverIp"),
					Integer.parseInt(property.getProperty("serverPort")));
			packetCount = Integer.parseInt(property.getProperty("noOfPackets"));
			ObjectOutputStream dataOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream dataInputStream = new ObjectInputStream(clientSocket.getInputStream());
			long startTime;
			long timeDiff;
			double timeinSec;
			startTime = System.currentTimeMillis();

			byte[] packet = new byte[packetSize];
			for (int i = 0; i < packetSize; i++) {
				packet[i] = (byte) i;
			}
			for (int i = 0; i < packetCount; i++) {
				dataOutputStream.writeObject(packet);
				dataInputStream.readObject();
			}

			timeDiff = (System.currentTimeMillis() - startTime);
			clientSocket.close();
			timeinSec = new Double(timeDiff) / 1000;
			Double latency = new Double(timeDiff / (2 * packetCount));
			System.out.println("Latency in Milliseconds: " + latency + " MS");

			if (packetSize == 1) {
				System.out.println("Throughput : "
						+ new Double((2 * packetSize * packetCount * 8) / (timeinSec * 1000 * 1000)) + " Mb/s");
			} else if (packetSize == 1000) {
				System.out.println("Throughput : "
						+ new Double((2 * packetSize * packetCount * 8) / (timeinSec * 1000 * 1000)) + " Mb/s");
			} else if (packetSize == (64 * 1000)) {
				System.out.println("Throughput : "
						+ new Double((2 * packetSize * packetCount * 8) / (timeinSec * 1000 * 1000)) + " Mb/s");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
