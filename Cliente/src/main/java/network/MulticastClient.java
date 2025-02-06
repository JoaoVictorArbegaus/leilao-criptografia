
package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.IvParameterSpec;
import models.AuctionData;
import org.json.JSONObject;
import security.AESMethods;
import security.KeyLogger;
import view.LeilaoCliente;

/**
 * Classe responsável por gerenciar a conexão de um cliente a um grupo multicast.
 * Essa classe possibilita que o cliente entre em um grupo multicast para receber
 * mensagens e, posteriormente, saia do grupo quando não for mais necessário.
 * @author JOAO
 */
public class MulticastClient {
    public static MulticastSocket socket;
    private InetAddress group;
    private int port;

    public MulticastClient(String multicastAddress, int port) {
        try {
            // Atribui a porta definida na classe AuctionData à variável local.
            this.port = AuctionData.multicastPort;
            // Obtém o objeto InetAddress a partir do endereço multicast definido em AuctionData.
            this.group = InetAddress.getByName(AuctionData.multicastAddress);
            // Cria o socket multicast utilizando a porta definida em AuctionData.
            this.socket = new MulticastSocket(AuctionData.multicastPort);
        } catch (IOException e) {
            // Em caso de erro, imprime a stack trace para depuração.
            e.printStackTrace();
        }
    }

    public boolean joinGroup() {
        try {
            // Solicita a adesão do socket ao grupo multicast.
            socket.joinGroup(group);
            System.out.println("Cliente entrou no grupo multicast: " + group.getHostAddress() + " na porta " + port);
            return true;
        } catch (IOException e) {
            // Em caso de erro, imprime a stack trace e retorna false.
            e.printStackTrace();
            return false;
        }
    }


    public void close() {
        try {
            // Solicita a saída do grupo multicast.
            socket.leaveGroup(group);
            // Fecha o socket multicast.
            socket.close();
            System.out.println("Cliente saiu do grupo multicast.");
        } catch (IOException e) {
            // Em caso de erro, imprime a stack trace.
            e.printStackTrace();
        }
    }
}





