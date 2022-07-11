import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class FileReceiveJob implements Runnable{
	public static final int DEFAULT_BUFFER_SIZE = 10000;
	private Peer self;
	private LinkedBlockingQueue<Socket> friends;
	
	public FileReceiveJob(LinkedBlockingQueue<Socket> friends, Peer self) {
		super();
		this.friends = friends;
		this.self = self;
	}

	private int[] getEmptyChunkIDs(FileChunk[] origin, FileChunk[] myChunk) {
		int[] emptyIdx = new int[origin.length];
		int count = 0;
		
		for(int i=0;i<myChunk.length;i++) {
			if(myChunk[i] == null && origin[i] != null) {
				emptyIdx[count++] = i;
			}
		}
		int[] result = new int[count];
		System.arraycopy(emptyIdx, 0, result, 0, count);
		return result;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		InputStream is = null;
		OutputStream os = null;
		RandomAccessFile raFile = null;
		Socket clientSocket = null;
		try {
			clientSocket = friends.take();
			is = clientSocket.getInputStream();
			os = clientSocket.getOutputStream();
	        
			FileWithMetaData fileData = (FileWithMetaData) IOUtils.readObjectFromInputStream(is);
			String fileName = fileData.getOriginalFileName();
			if(fileName.equals(self.getFileName())) {
				FileChunk[] myFileChunks = self.getFileData().getFileChunks();
				FileChunk[] origin = fileData.getFileChunks();
				
				if(myFileChunks == null && origin != null) {
					myFileChunks = new FileChunk[origin.length];
					self.getFileData().setSize(fileData.getSize());
					self.getFileData().setFileID(fileData.getFileID());
					self.getFileData().setFileChunks(myFileChunks);
				}
				
				int[] emptyIdx = getEmptyChunkIDs(origin, myFileChunks);
				if(emptyIdx.length == 0) {
					return;
				}
				
				
				FileChunk[] requestChunks = new FileChunk[emptyIdx.length>3?3:emptyIdx.length];
				for(int i=0; i<requestChunks.length; i++) {
					FileChunk requestChunk = origin[emptyIdx[i]];
					requestChunks[i] = requestChunk;
				}
				IOUtils.writeObjectOutputStream(requestChunks, os);
				
				System.out.println("File Chunk Requests Send" + Arrays.toString(requestChunks));
//				 // client idle time
		        try {
		        	Thread.sleep(1000);
		        }catch (InterruptedException e) {}
				
				File saveFile = self.getFile();
				
				if(!saveFile.exists()) {
					saveFile.createNewFile();
					raFile = new RandomAccessFile(saveFile, "rw");
					raFile.setLength(fileData.getSize());
				}else {
					raFile = new RandomAccessFile(saveFile, "rw");
				}
				
				for(int i = 0; i<requestChunks.length; i++) {
					long end = System.currentTimeMillis();
					if(end - start > 10000)
						break;
					receiveChunk(clientSocket, requestChunks[i], is, raFile, origin);
				}
			}
		}catch(SocketTimeoutException e) {
			try {
				clientSocket.close();
			}catch (IOException e1) {
				e1.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(is != null) is.close();
				if(os!=null) os.close();
				if(raFile!=null) raFile.close();
//				clientSocket.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receiveChunk(Socket clientSocket, FileChunk requestChunk, InputStream is,  RandomAccessFile raFile, FileChunk[] origin) throws IOException {
		double sTime = System.currentTimeMillis();
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int readBytes = is.read(buffer);
		raFile.seek(requestChunk.getPosition());
		raFile.write(buffer, 0, readBytes);
		boolean complete = self.getFileData().addFileChunk(requestChunk);
		
		double eTime = System.currentTimeMillis();
		double dTime = (eTime - sTime)/1000;
		System.out.println("FileChunk("+ requestChunk.getChunkID() +"/"+origin.length+") Download (" + self.getFileData().fileProgress() +"%)Completed From "+ clientSocket.getPort());
		System.out.println("Time: " + dTime + " second(s)");
		if(complete) {
			System.out.println("File download complete!");
			self.setSeeder(true);
			return;
		}
		
	}
}