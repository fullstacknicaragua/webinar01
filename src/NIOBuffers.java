/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smolina
 */
public class NIOBuffers {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            // TODO code application logic here            
            try (FileInputStream aFile = new FileInputStream("./nio-data.txt")) {
                ByteBuffer buffer = ByteBuffer.allocate(512);
                FileChannel inChannel = aFile.getChannel();
                WritableByteChannel outChannel = Channels.newChannel(System.out);
                buffer.clear();
                
                while(inChannel.read(buffer)>0){
					//System.out.println("\nBefore flip() " +buffer.toString());
					//limit is set to current position and position is set to zero
                    buffer.flip();                    
                    buffer.position(10);
                    //System.out.println("\nBefore rewind() " + buffer.toString());
                    buffer.rewind();
					//System.out.println("\nAfter rewind() " + buffer.toString());
                    //System.out.println("\nAfter flip() " +buffer.toString());
                    outChannel.write(buffer);
                    int pos = buffer.position();
                    buffer.compact();      
                    //System.out.println("\nAfter compact() " + buffer.toString()	);   
					//System.out.println((char)buffer.array()[pos-2]);
                }
                   
            }
        } catch (IOException ex) {
            Logger.getLogger(NIOBuffers.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

}
