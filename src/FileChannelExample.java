/**
 *
 * @author smolina
 * FileChannel + MappedByteBuffer
 */
import java.nio.channels.WritableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.Channels;
import java.nio.MappedByteBuffer;
import java.io.FileInputStream;
import java.io.IOException;

public class FileChannelExample{
	public static void main(String[] args){
		String filename = "nio-data.txt";		
		try{
			FileInputStream fis = new FileInputStream(filename);
			//Obtener un FileChannel a partir del FileInputStream
			try(FileChannel fChannel = fis.getChannel()){
				//Mapeamos el archivo en memoria
				//Se debe indicar el modo, y cuánto se mapea: position, size
				MappedByteBuffer buffer = fChannel.map(FileChannel.MapMode.READ_ONLY, 0,fChannel.size());
				//Obtenemos un canal de la salida estándar
				WritableByteChannel wbc = Channels.newChannel(System.out);
				//hay datos entre position y limit
				while(buffer.hasRemaining()){
					wbc.write(buffer);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
