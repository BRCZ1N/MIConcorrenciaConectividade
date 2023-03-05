package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import apirest.ProtocolHttp;
import apirest.RequestHttp;

public class ThreadClient implements Runnable {

	private Socket socket;
	private String connection;

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public ThreadClient(Socket socket) {

		this.socket = socket;
		this.connection = (socket.getInetAddress() + ":" + socket.getPort());

	}

	@Override
	public void run() {

		RequestHttp req;

		try {

			while (!isEmptyBuffer(socket.getInputStream())) {

				req = ProtocolHttp.readRequest(socket.getInputStream());

			}

		} catch (IOException e) {

			System.out.println("Conexao encerrada:"+ socket.getLocalPort());
		}

	}

	public boolean isEmptyBuffer(InputStream input) throws IOException {
		
	
		if (input.available() > 0) {

			return false;

		}

		return true;

	}

}
