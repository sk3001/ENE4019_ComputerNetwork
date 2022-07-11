import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class FileWithMetaData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -833224890876957902L;
	private transient File file;
	private String originalFileName;
	private long size;
	private String fileID;
	private String seperator;
	private FileChunk[] fileChunks;
	private boolean complete;

	public FileWithMetaData(File file) {
		super();
		this.file = file;
		this.originalFileName = file.getName();
		if(file != null && file.exists()) {
			this.size = file.length();
			fileChunks = new FileChunk[(int) Math.ceil((double) size/BitTorrent.DEFAULT_BUFFER_SIZE)];
			this.fileID = UUID.randomUUID().toString();
			for(int i=0;i<fileChunks.length;i++) {
				fileChunks[i] = new FileChunk(fileID, i, i*BitTorrent.DEFAULT_BUFFER_SIZE);
			}
			complete = true;
		}
		this.seperator = "===";
	}

	public File getFile() {
		return file;
	}
	
	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public String getFileID() {
		return fileID;
	}
	
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public byte[] getSeperator() {
		return seperator.getBytes();
	}
	
	public synchronized FileChunk[] getFileChunks() {
		return fileChunks;
	}
	public synchronized void setFileChunks(FileChunk[] fileChunks) {
		this.fileChunks = fileChunks;
	}
	

	public synchronized boolean addFileChunk(FileChunk fileChunk) {
		if(fileChunks[fileChunk.getChunkID()]==null) {
			fileChunks[fileChunk.getChunkID()] = fileChunk;
		}else {
			System.out.println(fileChunk + " is dumped");
		}
		boolean complete = fileChunks!=null;
		if(complete) {
			for(int i=0; i<fileChunks.length; i++) {
				complete = complete && fileChunks[i]!=null;
				if(!complete) break;
			}
		}
		this.complete = complete;
		return complete;		
	}
	
	public boolean isComplete() {
		return this.complete;
	}
	
	public int fileProgress() {
		int count = 0;
		for(int i=0; i<fileChunks.length; i++) {
			if(fileChunks[i]!=null) {
				count++;
			}
		}
		return (int) (((double) count/fileChunks.length) * 100);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileID == null) ? 0 : fileID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileWithMetaData other = (FileWithMetaData) obj;
		if (fileID == null) {
			if (other.fileID != null)
				return false;
		} else if (!fileID.equals(other.fileID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileWithMetaData [originalFileName=" + originalFileName + ", size=" + size + ", fileID=" + fileID + "]";
	}
	
}
