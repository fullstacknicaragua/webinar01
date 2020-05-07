/**
 *
 * @author smolina
 */
 import java.nio.ByteBuffer;
 import java.nio.channels.Channels;
 import java.nio.channels.WritableByteChannel;
 import java.nio.channels.Selector;
 import java.nio.channels.ServerSocketChannel;
 import java.nio.channels.SocketChannel;
 import java.nio.channels.SelectionKey;
 import java.net.InetSocketAddress;
 import java.util.Set;
 import java.util.Iterator;
 import java.io.IOException;
 
 public class NonBlockingEchoServer{
	public static void main(String[] args){
		String host = "localhost";
		int port = 9000;
		final int BUFFERSIZE = 1024;
		InetSocketAddress isa = new InetSocketAddress(host, port);
		try{
			//Crear ServerSocketChannel
			ServerSocketChannel ssChannel = ServerSocketChannel.open();
			//Bind ServerSocket
			ssChannel.socket().bind(isa);
			//Configurar en modo no bloqueante
			ssChannel.configureBlocking(false);
			
			Selector selector = Selector.open();
			//registrar ServerSocketChannel con el selector
			ssChannel.register(selector, SelectionKey.OP_ACCEPT);
			//forever loop
			while(true){
				WritableByteChannel out = Channels.newChannel(System.out);
				//bloquear hasta que haya un conjunto de operaciones-ready 
				int readyChannels = selector.select();
				
				if(readyChannels == 0) continue;
				
				//Set ready-keys
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while(keyIterator.hasNext()){
					SelectionKey key = keyIterator.next();
					//Remover key
					keyIterator.remove();
					
					if(key.isAcceptable()){//Aceptar conexión
						ServerSocketChannel server = (ServerSocketChannel)key.channel();
						SocketChannel clientChannel = server.accept();
						System.out.println("Nuevo cliente (IP): "+ clientChannel.getRemoteAddress().toString());
						//configurar canal como no bloqueante
						clientChannel.configureBlocking(false);
						//registrarlo con el selector, interesado en READs
						clientChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));											
					}
					else if(key.isReadable()){
						//Obtener el canal que está listo para leer
						SocketChannel clientChannel = (SocketChannel)key.channel();
						//Obtener buffer asociado al canal
						ByteBuffer buffer = (ByteBuffer)key.attachment();
						//Leer del canal y guardar en el buffer
						clientChannel.read(buffer);
						key.interestOps(SelectionKey.OP_WRITE);
						String msg = new String(buffer.array(), "UTF-8");						
						System.out.print("Client sent: "+msg);
						
						
					}else if(key.isWritable()){
						//Obtener el canal que está listo para escribir 
						SocketChannel clientChannel = (SocketChannel) key.channel();
						ByteBuffer buffer = (ByteBuffer)key.attachment();
						//Poner el buffer en modo lectura
						buffer.flip();
						//Escribir en el canal el contenido que está en el buffer
						clientChannel.write(buffer);
						//Si quedaron datos en el buffer, compactarlo
						if(buffer.hasRemaining()) buffer.compact();
						//sino dejarlo listo para nueva escritura 
						else buffer.clear();
						key.interestOps(SelectionKey.OP_READ);
					}
				}
			}			
		}catch(IOException e){
		
		}
	}
 }
