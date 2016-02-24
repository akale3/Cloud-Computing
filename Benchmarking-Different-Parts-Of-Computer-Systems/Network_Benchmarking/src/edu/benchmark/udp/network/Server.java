package edu.benchmark.udp.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class Server {

	public static void main(String[] args) {
		Properties property = new Properties();
		
		try {
			property.load(new FileInputStream(new File("./resource/config.properties")));
			System.out.println("Server is up and ready to accept UDP packets");
			int noOfPackets = Integer.parseInt(property.getProperty("noOfPackets"));
			InetAddress ipAddress = null;
			int port;
			byte[] receiveData = new byte[64 * 1000];
			byte[] sendData; 
			DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
			DatagramPacket sendResponse = null;
			DatagramSocket datagramSocket = new DatagramSocket(Integer.parseInt(property.getProperty("serverPort")));
			while (true) {
				for (int i = 0; i < noOfPackets; i++) {
					datagramSocket.receive(receivedPacket);
					ipAddress = receivedPacket.getAddress();
					port = receivedPacket.getPort();
					sendData = receivedPacket.getData();
					sendResponse = new DatagramPacket(sendData, sendData.length, ipAddress, port);
					datagramSocket.send(sendResponse);
				}
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}