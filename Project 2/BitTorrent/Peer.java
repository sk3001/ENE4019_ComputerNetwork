import java.io.File;
import java.util.ArrayList;

public class Peer {
	private String ipAddress;
	private int port;
	private boolean self;
	private File saveFolder;
	private File file;
	private String fileName;
	private FileWithMetaData fileData;
	private boolean seeder;
	
	
	public Peer(String ipAddress, int port) {
		this(ipAddress, port, false, null);
	}
	public Peer(String ipAddress, int port, boolean self, String fileName) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.setSelf(self);
		if(self) {
			this.saveFolder = new File(System.getProperty("user.dir") + File.separator + port);
			if(!saveFolder.exists()) {
				saveFolder.mkdirs();
			}
			this.fileName = fileName;
			this.file = new File(saveFolder, fileName);
			if(file.exists()) {
				seeder = true;
			}
			this.fileData = new FileWithMetaData(file);
		}
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public boolean isSelf() {
		return self;
	}
	public void setSelf(boolean self) {
		this.self = self;
	}
	public File getSaveFolder() {
		return saveFolder;
	}
	public void setSaveFolder(File saveFolder) {
		this.saveFolder = saveFolder;
	}
	
	public File getFile() {
		return file;
	}
	public String getFileName() {
		return fileName;
	}
	public boolean isSeeder() {
		return seeder;
	}
	public void setSeeder(boolean seeder) {
		this.seeder = seeder;
	}
	public FileWithMetaData getFileData() {
		return fileData;
	}
	
}
