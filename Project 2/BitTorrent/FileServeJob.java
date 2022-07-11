import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class FileServeJob implements Runnable{
	public static final int DEFAULT_BUFFER_SIZE = 10000;
	private Socket socket;
	private File file;
	private FileWithMetaData fileData;
	private long fileSize;

	public FileServeJob(Socket socket, FileWithMetaData fileData) {
		super();
		this.socket = socket;
		this.file = fileData.getFile();
		this.fileData = fileData;
		this.fileSize = fileData.getSize();
	}

	@Override
	public void run() {
		OutputStream os = null;
		InputStream is = null;
		RandomAccessFile raFile = null;
		try {
	        os = socket.getOutputStream();
	        is = socket.getInputStream();
	        IOUtils.writeObjectOutputStream(fileData, os);
	        System.out.println(fileData +" sent to " + socket.getPort());
	        
	        FileChunk[] chunkRequests = null;
	        while(true) {
	        	// server idle time
	        	try {
	        		Thread.sleep(1000);
	        		chunkRequests = (FileChunk[]) IOUtils.readObjectFromInputStream(is);
	        		break;
	        	}catch (Exception e) {
	        		continue;
	        	}
	        }
	        if(chunkRequests==null || chunkRequests.length == 0) return;
	        
	        raFile = new RandomAccessFile(file, "r");
	        for(int i = 0; i<chunkRequests.length; i++) {
	        	sendChunkRequest(chunkRequests[i], raFile, os);
	        }
//	        
	   	}catch(IOException e) {
	   		System.err.println(e.getMessage());
//			throw new RuntimeException(e);
		}finally {
			try {
				if(raFile!=null)
					raFile.close();
				if(os != null)
					os.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendChunkRequest(FileChunk chunkRequest, RandomAccessFile raFile, OutputStream os) throws IOException {
        int chunkID = chunkRequest.getChunkID();
        
        long startPoint = fileData.getFileChunks()[chunkID].getPosition();
        raFile.seek(startPoint);

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int readBytes = raFile.read(buffer);
    	byte[] chunk = new byte[readBytes];
    	System.arraycopy(buffer, 0, chunk, 0, readBytes);
        os.write(chunk);
        os.flush();
        System.out.println("Client: " + socket.getInetAddress()+":"+socket.getPort()  + " connected.");
        System.out.println("Progressing: [" + chunkID +  "/" + fileData.getFileChunks().length + "]"  + readBytes + "/" + fileSize + " Byte(s)");
         
        System.out.println("File transfer completed.");
	}
}
   