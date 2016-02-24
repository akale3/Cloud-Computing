package edu.benchmark.tcp.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

public class Server {

	public static void main(String argv[]) throws Exception {

		Properties property = new Properties();
		property.load(new FileInputStream(new File("./resource/config.properties")));
		try {
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(property.getProperty("serverPort")));
			System.out.println("Server is up and ready to accept TCP packets");
			int noOfPackets = Integer.parseInt(property.getProperty("noOfPackets"));
			
			Socket socket;
			Thread thread = null;
			ServerImpl serverImpl = new ServerImpl();
			while (true) {
				socket = serverSocket.accept();
				// Creating new Thread each time and processing differently for concurrent clients.
				serverImpl.setNoOfPackets(noOfPackets);
				serverImpl.setSocket(socket);
				thread = new Thread(serverImpl);
				thread.start();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
