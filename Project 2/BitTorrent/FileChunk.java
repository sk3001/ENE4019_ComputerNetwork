import java.io.Serializable;

/**
 * Use for FileChunkRequest & FileServe
 */
public class FileChunk implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3174388060593307671L;
	private String fileID;
	private int chunkID;
	private transient byte[] chunk;
	private long position;
	
	public FileChunk(String fileID, int chunkID, byte[] chunk) {
		super();
		this.fileID = fileID;
		this.chunk = chunk;
		this.setChunkID(chunkID);
	}

	public FileChunk(String fileID, int chunkID, long position) {
		super();
		this.fileID = fileID;
		this.position = position;
		this.setChunkID(chunkID);
	}

	public String getFileID() {
		return fileID;
	}

	public byte[] getChunk() {
		return chunk;
	}

	public void setChunk(byte[] chunk) {
		this.chunk = chunk;
	}
	
	public long getPosition() {
		return position;
	}

	public int getChunkID() {
		return chunkID;
	}

	public void setChunkID(int chunkID) {
		this.chunkID = chunkID;
	}

	@Override
	public String toString() {
		return "FileChunk [chunkID=" + chunkID + "]";
	}
	
	
}
