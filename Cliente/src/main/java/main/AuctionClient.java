
package main;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.swing.*;
import security.RSAMethods;
import view.LoginPanel;

/**
 *
 * @author JOAO
 */


public class AuctionClient {


    public static void main(String[] args){
        
        try {
            // Inicializa a tela de login (LoginPanel)
            JFrame frame = new JFrame("Leilão Virtual");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(450, 200); // Define o tamanho 
            frame.setContentPane(new LoginPanel(frame)); // Passa o frame para o LoginPanel
            frame.setVisible(true);

            System.out.println("Cliente iniciado e aguardando CPF para conexao...");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
     
        
    }
    
    
}


