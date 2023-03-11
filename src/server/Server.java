package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import services.ConsumptionServices;
import services.InvoiceServices;
import services.UserServices;

public class Server {

	private ServerSocket socketServer;
	private Socket clientSocket;
	private DatagramSocket datagramSocket;
	private DatagramPacket datagramPacket;
	private byte[] bufferPacket = new byte[1024];
	private UserServices userService = new UserServices();
	private ConsumptionServices consumptionService = new ConsumptionServices();
	private InvoiceServices invoiceService = new InvoiceServices();

	private void generateServer(int portServerSocket, int portDatagramSocket) throws IOException {

		socketServer = new ServerSocket(portServerSocket);
		datagramSocket = new DatagramSocket(portDatagramSocket);

	}

	private void generateAndStartThreadClient(Socket socketClient) {

		ThreadTcpClient threadTcpClient = new ThreadTcpClient(socketClient);
		new Thread(threadTcpClient).start();

	}

	private void execServer(int portServerSocket, int portDatagramSocket) throws IOException {

		boolean connection = true;

		generateServer(portServerSocket, portDatagramSocket);
		System.out.println("Server executado no IP:" + socketServer.getLocalPort());

		new Thread(() -> {

			while (connection) {

				try {
					datagramPacket = new DatagramPacket(bufferPacket, bufferPacket.length);
					datagramSocket.receive(datagramPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (datagramPacket.getPort() != -1 || datagramPacket.getAddress() != null) {

					ThreadUdpClient threadUdpClient = new ThreadUdpClient(datagramSocket, datagramPacket, bufferPacket);
					new Thread(threadUdpClient).start();

				}

			}

		}).start();

		while (connection) {

			clientSocket = socketServer.accept();
			System.out.println("Client connected");
			System.out.println(clientSocket.getInetAddress());
			generateAndStartThreadClient(clientSocket);

		}

		socketServer.close();

	}

	public static void main(String[] args) throws IOException {

		Server serverMain = new Server();
		serverMain.execServer(8000, 8100);

	}

}
