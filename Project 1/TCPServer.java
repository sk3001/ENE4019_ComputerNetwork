import java.io.*;
import java.net.*;

public class TCPServer {
	public static final int DEFAULT_BUFFER_SIZE = 10000;
	public static void main(String[] args){
		int port = Integer.parseInt(args[0]);
		String fileName = args[1];

		File file = new File(fileName);
		if(!file.exists()) {
			System.out.println("No File Detected.");
			System.exit(0);
		}
		
		long fileSize = file.length();
		long totalReadBytes = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int readBytes;
		double sTime = 0;
		
		try {
			ServerSocket welcomeSocket = new ServerSocket(port);
			System.out.println("Connecting to Port: " + port + "....");
			Socket connectionSocket = welcomeSocket.accept();
			FileInputStream fis = new FileInputStream(file);

			sTime = System.currentTimeMillis();
            OutputStream os = connectionSocket.getOutputStream();
            while ((readBytes = fis.read(buffer)) > 0) {
                os.write(buffer, 0, readBytes);
                totalReadBytes += readBytes;
                System.out.println("Progressing: " + totalReadBytes + "/" + fileSize + " Byte(s) ("+ (totalReadBytes * 100 / fileSize) + " %)");
            }
             
            System.out.println("File transfer completed.");
            fis.close();
            os.close();
            welcomeSocket.close();
            
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		double eTime = System.currentTimeMillis();
		double dTime = (eTime - sTime)/1000;
		double transferSpeed = (fileSize / 1000)/dTime;
		
		System.out.println("Time: " + dTime + " second(s)");
		System.out.println("Average transfer speed: " + transferSpeed + " KB/s");
		
	}
}
