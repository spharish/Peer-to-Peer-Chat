/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiclientchatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author spharish
 */
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import sun.misc.*;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;


 
 class AESencrp_Server {
    
     private static final String ALGO = "AES";
    private static final byte[] keyValue = 
        new byte[] { 'm', 'y', 's', 'e', 'c', 'r', 'e',
't', 's', 'p', 'h','a', 'r', 'i', 's', 'h' };

public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
}

}
public class Server {
    public static int count = 0;
    public static Socket[] array = new Socket[20];
   
    public static void main(String[] args) throws IOException {
         ServerSocket s_socket = new ServerSocket(5555);
         System.out.println("Server is starting");
         while (true) {
             Socket client = s_socket.accept();
             count++;
             array[count-1] = client;
             System.out.println("Client "+count+" connected to server");
             ReceivingThread rec = new ReceivingThread(client);
             Thread t = new Thread(rec);
             t.start();       
        }
    }
}

class ReceivingThread implements Runnable {
    Socket s;
    String msg;
    //Scanner sc = new Scanner(System.in);
    public ReceivingThread(Socket rec_socket) {
        this.s = rec_socket;
    }
    
    public void run() {
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            in = new DataInputStream(this.s.getInputStream());
             while (true) {            
                String crypted = in.readUTF();
                msg = AESencrp_Server.decrypt(crypted);
                int i = 0, id = 0;
                for (i = 0; i < Server.count; i++) {
                    if(Server.array[i] == this.s) {
                        id = i+1;
                        break;
                    }
                }
                System.out.println("Client "+id+": "+msg);
                for (i = 0; i < Server.count; i++) {
                    if (Server.array[i] != this.s) {
                        Socket sp = Server.array[i];
                        out = new DataOutputStream(sp.getOutputStream());
                        String send = "Client ";
                        send += id;
                        send += ": ";
                        send += msg;
                        String encrypt = AESencrp_Server.encrypt(send);
                        out.writeUTF(encrypt);
                        
                    }
                }
                
                if (msg.equals("bye")) {
                    break;
                }
            }
        }catch (Exception e){}
    } 
    
}

