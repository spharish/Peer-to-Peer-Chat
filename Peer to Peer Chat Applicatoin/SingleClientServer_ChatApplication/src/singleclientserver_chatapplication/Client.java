/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package singleclientserver_chatapplication;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import sun.misc.*;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author spharish
 */
 class AESencrp_Client {
    
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

public class Client {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 5555);
        System.out.println("Connecting to server");
        SendThread send = new SendThread(s);
        ReceiveThread rec = new ReceiveThread(s);
        Thread thread1 = new Thread(send);
        Thread thread2 = new Thread(rec);
        thread1.start();
        thread2.start();    
        
    }
    
}

class SendThread implements Runnable {
    Socket s;
    Scanner sc = new Scanner(System.in);
    public SendThread(Socket send_socket) {
        this.s = send_socket;
    }
    @Override
    public void run() {
             DataOutputStream out = null;
            try {
               out= new DataOutputStream(this.s.getOutputStream());       
                while (true) {            
                    String msg = sc.nextLine(); 
                     String crypt =  AESencrp_Client.encrypt(msg);
                    
                     out.writeUTF(crypt);      
                    if (msg.equals("bye"))
                        break;
                    }
            }catch(Exception e){}
    }
}

class ReceiveThread implements Runnable {
    Socket s;
    String msg;
    //Scanner sc = new Scanner(System.in);
    public ReceiveThread(Socket rec_socket) {
        this.s = rec_socket;
    }
    
    public void run() {
        DataInputStream in = null;
        try {
            in = new DataInputStream(this.s.getInputStream());
             while (true) {            
                String crypt = in.readUTF();
                msg = AESencrp_Client.decrypt(crypt);
                System.out.println("Server: "+msg);
                if (msg.equals("bye")) {
                    break;
                }
            }
        }catch (Exception e){}
    } 
}

