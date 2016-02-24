package edu.benchmark.tcp.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerImpl implements Runnable {

	private int noOfPackets;
	private Socket socket;

	public int getNoOfPackets() {
		return noOfPackets;
	}

	public void setNoOfPackets(int noOfPackets) {
		this.noOfPackets = noOfPackets;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

			Object readByte = 0;

			for (int i = 0; i < noOfPackets; i++) {
				//Reading packets from Client
				readByte = inputStream.readObject();
				// Sending Acknowledgment to a Client
				outputStream.writeObject(readByte);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
