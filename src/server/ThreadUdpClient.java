package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import resources.Consumption;
import services.ConsumptionServices;
import services.UserServices;

public class ThreadUdpClient implements Runnable {

	private DatagramSocket socket;
	private String connection;

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public ThreadUdpClient(DatagramSocket socket) {

		this.socket = socket;
		this.connection = (socket.getInetAddress() + ":" + socket.getPort());

	}

	@Override
	public void run() {

		while (true) {

			byte[] dataPacket = new byte[1024];
			DatagramPacket packet = new DatagramPacket(dataPacket, dataPacket.length, socket.getInetAddress(),socket.getPort());
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String message = new String(packet.getData(), StandardCharsets.UTF_8);
			Pattern pattern = Pattern.compile("(\\d+)-(\\d+.\\d+)-(\\d{2})/(\\d{2})/(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})");
			Matcher matcher = pattern.matcher(message);

			if (matcher.matches()) {
				
				String[] messageConsumption = message.split("-");
				Consumption consumption = new Consumption(Double.parseDouble(messageConsumption[1]),messageConsumption[2]);
				ConsumptionServices.addConsumption(messageConsumption[0], consumption);
			
			} else {

				String[] messageCredentials = message.split(":");
				UserServices.authenticateClient(messageCredentials[0], messageCredentials[1]);

			}

		}

	}

}