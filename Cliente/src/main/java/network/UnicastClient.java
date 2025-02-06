/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.json.JSONException;
import org.json.JSONObject;
import security.AESMethods;
import security.KeyLogger;
import security.RSAMethods;
import security.RegisteredUsers;

/**
 *
 * Classe responsável por estabelecer a comunicação unicast com o servidor do leilão.
 * Ela envia uma solicitação autenticada ao servidor, recebe a resposta criptografada,
 * verifica a assinatura digital do servidor e decifra os dados recebidos.
 *
 * @author JOAO
 */
public class UnicastClient {
    
    
    
    public static String conectaServerUnicast(String CPF, String textoEnvio, String assinatura, String serverAdress, int serverPort) throws JSONException, Exception {
        RegisteredUsers registeredUsers = new RegisteredUsers();

        try {
            // Criando o socket e conectando ao servidor no endereço e porta especificados.
            Socket socket = new Socket(serverAdress, serverPort);
            System.out.println("Conectado ao server!");

            // Criando os fluxos de entrada e saída para comunicação com o servidor.
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Criando o objeto JSON com as informações do cliente (CPF, texto e assinatura digital).
            System.out.println("Criando JSON");
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("CPF", CPF);
            jsonRequest.put("text", textoEnvio);
            jsonRequest.put("signature", assinatura);
            String json = jsonRequest.toString();

            // Enviando a requisição ao servidor via unicast.
            System.out.println("Enviando payload para o server...");
            out.println(json);
            System.out.println("Enviado!");
            System.out.println("-------------------------------------------");

            // Lendo a resposta do servidor.
            String responseJson = in.readLine();
            System.out.println("Resposta recebida do server!");
            
            // Processando a resposta JSON recebida.
            JSONObject jsonRequest2 = new JSONObject(responseJson);
            System.out.println("Json recebido!");
            
            // Extraindo os dados cifrados e suas respectivas assinaturas.
            String ednCifrado = jsonRequest2.getString("Endereço");
            String portaCifrada = jsonRequest2.getString("Porta");
            String chaveCifrada = jsonRequest2.getString("Chave Simetrica Cifrada");
            
            String ednCifradoAss = jsonRequest2.getString("EndereçoAss");
            String portaCifradaAss = jsonRequest2.getString("PortaAss");
            String chaveCifradaAss = jsonRequest2.getString("Chave Simetrica CifradaAss");            
            
            // Exibindo o JSON recebido para debug.
            String jsonString2 = jsonRequest2.toString();
            System.out.println("");
            System.out.println("Json que chegou no cliente: "+jsonString2);
            System.out.println("");

            // Verificando a assinatura digital do servidor para garantir a autenticidade dos dados recebidos.
            System.out.println("Verificando a assinatura do server...");
            boolean authentication = RSAMethods.verificaAssinatura(ednCifrado, ednCifradoAss, KeyLogger.ServerPublicKey);
            System.out.println("A assinatura do endereço eh: "+authentication);
            
            boolean authentication2 = RSAMethods.verificaAssinatura(portaCifrada, portaCifradaAss, KeyLogger.ServerPublicKey);
            System.out.println("A assinatura da porta eh: "+authentication2);
            
            boolean authentication3 = RSAMethods.verificaAssinatura(chaveCifrada, chaveCifradaAss, KeyLogger.ServerPublicKey);
            System.out.println("A assinatura da chave eh: "+authentication3);
            
            // Se todas as assinaturas forem válidas, os dados podem ser decifrados.
            if(authentication && authentication2 && authentication3) {
                System.out.println("Decifrando...");

                // Obtendo a chave privada do cliente a partir do CPF.
                PrivateKey clientPrivateKey = registeredUsers.returnPrivateKey(CPF);
            
                // Decifrando a chave simétrica enviada pelo servidor.
                String chaveDecifrado = RSAMethods.decfiraComRSA(chaveCifrada, clientPrivateKey);
               
                // Criando o objeto `SecretKey` a partir da chave simétrica decifrada.
                SecretKey chaveSimetrica = AESMethods.converteBase64ParaSecretKey(chaveDecifrado);
                
                // Gerando um vetor de inicialização (IV) baseado na chave simétrica.
                IvParameterSpec iv = AESMethods.gerarIvUsandoAESKey(chaveSimetrica);
                
                // Decifrando os dados do servidor (endereço multicast e porta) usando a chave simétrica.
                String ednDecifrado = AESMethods.decifrarComAES(ednCifrado, chaveSimetrica, iv);
                String portaDecifrado = AESMethods.decifrarComAES(portaCifrada, chaveSimetrica, iv);
                
                // Exibindo os valores decifrados.
                System.out.println("Endereço Decifrado: "+ednDecifrado);
                System.out.println("Porta Decifrada: "+portaDecifrado);
                System.out.println("Chave simetrica Decifrada: "+chaveDecifrado);
                
                // Criando um objeto JSON com os dados decifrados para retorno.
                JSONObject resposta = new JSONObject();
                resposta.put("Endereço", ednDecifrado);
                resposta.put("Porta", portaDecifrado);
                resposta.put("Chave Simetrica Decifrada", chaveDecifrado);
                
                String jsonDecifrado = resposta.toString();
                
                // Fechando os fluxos de comunicação e o socket.
                out.close();
                in.close();
                socket.close();
                
                // Retornando os dados decifrados.
                return jsonDecifrado;
            }
        } catch (JsonProcessingException e) {
            // Capturando e exibindo erros relacionados à manipulação de JSON.
            System.out.println("Erro no JSON");
            e.printStackTrace();
        } catch (IOException e) {
            // Capturando e exibindo erros relacionados à comunicação com o servidor.
            System.out.println("Erro: o servidor ainda não está rodando.");
            // e.printStackTrace();
        }
        
        // Em caso de erro, retorna `null`.
        System.out.println("Erro ao conectar ao servidor.");
        return null;
    }


 
}
