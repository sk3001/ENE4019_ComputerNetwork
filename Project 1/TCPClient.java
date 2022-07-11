import java.io.*;
import java.net.*;

public class TCPClient {
	public static final int DEFAULT_BUFFER_SIZE = 10000;
	public static void main(String[] args){
		String server = args[0];
		int port = Integer.parseInt(args[1]);
		String fileName = args[2];
		
		try {
			Socket clientSocket = new Socket(server, port);
			
			FileOutputStream fos = new FileOutputStream(fileName);
			InputStream is = clientSocket.getInputStream();
			
			double sTime = System.currentTimeMillis();
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int readBytes;
			
			while((readBytes = is.read(buffer)) != -1) {
				fos.write(buffer, 0, readBytes);
			}
			double eTime = System.currentTimeMillis();
			double dTime = (eTime - sTime)/1000;
			
			System.out.println("File Download Completed");
			System.out.println("Time: " + dTime + " second(s)");
			
			is.close();
			fos.close();
			clientSocket.close();
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
