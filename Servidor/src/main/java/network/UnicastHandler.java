package network;

import java.net.*;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.IvParameterSpec;
import models.AuctionData;
import models.ConnectionRequest;
import security.RSAMethods;
import org.json.JSONObject;
import security.AESMethods;
import security.KeyLogger;
import security.RegisteredUsers;

public class UnicastHandler implements Runnable {

    RegisteredUsers registeredUsers = new RegisteredUsers();
    private int serverPort;
    private DatagramSocket socket;
    private boolean running;
    private RSAMethods cryptoUtils;

    public UnicastHandler(int port) {
        this.serverPort = port;
    }

    @Override
    public void run() {
        
        while(true){
            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Servidor aguardando conexoes na porta " + serverPort + "...");

            // Aguardando por uma conexão do cliente
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado!");

            // Leitura da mensagem do cliente
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            // Lendo json
            String jsonRecebido = in.readLine();
            System.out.println("Payload recebido: ");
            
            // Processando o JSON recebido
            JSONObject recebidoJson = new JSONObject(jsonRecebido);
            String cpf = recebidoJson.getString("CPF");
            String plainText = recebidoJson.getString("text");
            String signature = recebidoJson.getString("signature");

            System.out.println("CPF: " + cpf);
            System.out.println("Texto: " + plainText);
            System.out.println("Assinatura: " + signature);

            System.out.println("----------------------------------");
            
            // Verificando assinatura
            PublicKey chavePublicaClient = registeredUsers.retornaPublicKey(cpf); //retorna a public key
            boolean atenticacao = RSAMethods.verificarAssinatura(plainText, signature, chavePublicaClient); //verifica
            System.out.println("A assinatura eh: "+atenticacao);
            
            if(atenticacao == true){
                
                System.out.println("Cifrando e assinando os dados...");
                System.out.println("");
                
                //Cifrando os dados a serem enviados com a chave simetrica e cifrando a chave cimetrica com a chave publica do cliente
                String chaveSimetricaCifrada = RSAMethods.cifrarComRSA(KeyLogger.ServerSimetricKeySTR, chavePublicaClient);
                IvParameterSpec iv = AESMethods.geraIvUsandoChaveAES(KeyLogger.ServerSimetricKey);
                String enderecoCifrado = AESMethods.cifraComAES(AuctionData.multicastAddress, KeyLogger.ServerSimetricKey, iv);
                
                String portaSTR =  String.valueOf(AuctionData.multicastPort);
                String portaCifrada = AESMethods.cifraComAES(portaSTR, KeyLogger.ServerSimetricKey, iv);
                
                //Assinando os dados cifrados
                String chaveSimetricaCifradaAss = RSAMethods.assinarMsg(chaveSimetricaCifrada, KeyLogger.ServerPrivateKey);
                String enderecoCifradoAss = RSAMethods.assinarMsg(enderecoCifrado, KeyLogger.ServerPrivateKey);
                String portaCifradaAss = RSAMethods.assinarMsg(portaCifrada, KeyLogger.ServerPrivateKey);
                
                System.out.println("Endereço cifrado: "+enderecoCifrado);
                System.out.println("Porta cifrada: "+portaCifrada);
                System.out.println("Chave Simetrica Cifrada: "+chaveSimetricaCifrada);
                
                System.out.println("");
                
                System.out.println("Endereço cifrado e ass: "+enderecoCifradoAss);
                System.out.println("Porta cifrada e ass: "+portaCifradaAss);
                System.out.println("Chave Simetrica Cifrada e ass: "+chaveSimetricaCifradaAss);
                
                System.out.println("----------------------------");
                
                //criando o payloud com os dados cifrados
                System.out.println("Criando payload...");
                
                JSONObject resposta = new JSONObject();
                resposta.put("Endereço", enderecoCifrado);
                resposta.put("Porta", portaCifrada);
                resposta.put("Chave Simetrica Cifrada", chaveSimetricaCifrada);
                
                resposta.put("EndereçoAss", enderecoCifradoAss);
                resposta.put("PortaAss", portaCifradaAss);
                resposta.put("Chave Simetrica CifradaAss", chaveSimetricaCifradaAss);
                
                System.out.println("Json a ser enviado para o cliente: "+resposta.toString());
                System.out.println("");
                
                //enviando para o cliente
                out.println(resposta.toString());
                
                System.out.println("Payload enviado!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(UnicastHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
}
