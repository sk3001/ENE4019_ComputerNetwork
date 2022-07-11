import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

public class BitTorrent {
	public static final int DEFAULT_BUFFER_SIZE = 10000;
	public static void main(String[] args) throws IOException{
		try {
			ArrayList<Peer> neighbor = new ArrayList<Peer>();
			
			LinkedBlockingQueue<Socket> friends = new LinkedBlockingQueue<Socket>(3);
			Peer self = parsingConfig(args[0], args[1], neighbor);
			
			//Server Thread
			new Thread(new ServerListeningJob(self)).start();
			
			if(!self.isSeeder())
				createClientThread(self, neighbor, friends);
		}catch(IOException e) {
			System.err.println("Can't Run Program!" + e.getMessage());
			throw e;
		}
	}
	private static void createClientThread(Peer self, ArrayList<Peer> neighbor, LinkedBlockingQueue<Socket> friends) {
		int i=0;
		while(true) {
			if(self.isSeeder()) break;
			String server = neighbor.get(i).getIpAddress();
			int port = neighbor.get(i++).getPort();
			if(i == (neighbor.size())) {
				i = 0;
			}
			try {
				Socket clientSocket = new Socket(server, port);
				clientSocket.setSoTimeout(5000);
				friends.put(clientSocket);
				new Thread(new FileReceiveJob(friends, self)).start();
			}catch (Exception e) {
				System.out.println(e.getMessage());
				continue;
			}
		}
	}
	
	private static Peer parsingConfig(String configFileName, String fileName, ArrayList<Peer> peer) throws IOException {
		int i = 0;
		Peer self = null;
		InputStream is = BitTorrent.class.getResourceAsStream(configFileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while(true) {
			String info = br.readLine();
			if(info == null) {
				break;
			}
			StringTokenizer st = new StringTokenizer(info, " ");
			String ip = st.nextToken();
			int port = Integer.parseInt(st.nextToken());
			if(i == 0) {
				self = new Peer(ip, port, true, fileName);
			}
			else {
				peer.add(new Peer(ip, port));
			}
			i++;
		}
		return self;
	}
}
