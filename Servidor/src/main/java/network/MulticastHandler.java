package network;

import java.net.*;
import java.io.*;
import javax.crypto.spec.IvParameterSpec;
import models.AuctionData;
import org.json.JSONObject;
import security.AESMethods;
import security.KeyLogger;


/**
 * Classe responsável por gerenciar a conexão do server ao grupo multicast.
 * @author JOAO
 */
public class MulticastHandler {
    
    private String multicastAddress;
    private int port;
    public static MulticastSocket socket;
    private InetAddress group;

    public MulticastHandler(String multicastAddress, int port) {

        try {
            this.port = AuctionData.multicastPort;
            this.group = InetAddress.getByName(AuctionData.multicastAddress);
            this.socket = new MulticastSocket(AuctionData.multicastPort);

            // Cliente entra no grupo multicast
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public boolean joinGroup() {
        try {
            socket.joinGroup(group);
            System.out.println("Cliente entrou no grupo multicast: " + group.getHostAddress() + " na porta " + port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void close() {
        try {
            socket.leaveGroup(group);
            socket.close();
            System.out.println("Cliente saiu do grupo multicast.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
