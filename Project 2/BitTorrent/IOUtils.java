import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class IOUtils {
	public static void writeObjectOutputStream(Object obj, OutputStream os) throws IOException {
		if(!(obj instanceof Serializable)) {
			throw new IllegalArgumentException("Can't Serializable");
		}
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(obj);
		oos.flush();
	}
	public static Object readObjectFromInputStream(InputStream is) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(is);
		try {
			return ois.readObject();
		}catch (Exception e) {
			throw new IOException(e);
		}
	}
}
