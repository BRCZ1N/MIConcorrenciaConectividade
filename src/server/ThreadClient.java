package server;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import apirest.ProtocolHttp;
import apirest.RequestHttp;

public class ThreadClient implements Runnable {

	private Socket socket;
	private String connection;
	private RequestHttp req;

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

			System.out.println("Conexão com a porta:" + socket.getPort() + " encerrada");

		} catch (IOException e) {

			e.printStackTrace();
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

	}

	public boolean isEmptyBuffer(InputStream input) throws IOException {

		if (input.available() == 0) {

			return true;

		}

		return false;

	}

}
