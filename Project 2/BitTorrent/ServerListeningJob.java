import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ServerListeningJob implements Runnable{
	
	private Peer self;
	
	public ServerListeningJob(Peer self) {
		super();
		this.self = self;
	}

	@Override
	public void run() {
		try {
			ServerSocket welcomeSocket = new ServerSocket(self.getPort());
			welcomeSocket.setSoTimeout(5000);
			while(true) {
				if(self.getFileData().getFileChunks()==null) continue;
				System.out.println("Waiting for Request in Port: " + self.getPort() + "....");
				try {	
					Socket connectionSocket = welcomeSocket.accept();
					new Thread(new FileServeJob(connectionSocket, self.getFileData())).start();
				}catch(SocketTimeoutException e) {
				}
			}
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
