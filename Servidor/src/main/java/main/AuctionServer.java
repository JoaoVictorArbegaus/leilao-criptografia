package main;

import javax.swing.JFrame;
import models.AuctionData;
import network.UnicastHandler;
import network.MulticastHandler;
import views.LeilaoView;

public class AuctionServer {

    private UnicastHandler unicastHandler;
    private MulticastHandler multicastHandler;

    public AuctionServer() {
        try {
            setupConnections();
            System.out.println("Servidor iniciado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupConnections() {
        try {
            // Inicializa o handler unicast para comunicação direta com os clientes
            unicastHandler = new UnicastHandler(5000); // Porta 5000 para unicast
            new Thread(unicastHandler).start();

            // Inicializa o handler multicast para o grupo do leilão
            multicastHandler = new MulticastHandler(AuctionData.multicastAddress, AuctionData.multicastPort); // Grupo multicast e porta
            if (multicastHandler.joinGroup()) { // Garante que o cliente entrou no grupo
                        System.out.println("Multicast criado e conectado com sucesso!");
                                    JFrame frame = new JFrame("Leilão Virtual");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(450, 450); // Define o tamanho 
            frame.setContentPane(new LeilaoView(frame)); // Passa o frame para o LoginPanel
            frame.setVisible(true);
            }

            System.out.println("Conexoes configuradas: Unicast na porta 5000");
        } catch (Exception e) {
            System.err.println("Erro ao configurar conexoes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AuctionServer();
    }
}
