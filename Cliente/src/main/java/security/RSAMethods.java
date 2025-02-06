package security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author JOAO
 */
public class RSAMethods {

// Método para gerar um par de chaves RSA
public static KeyPair geradorParChavesRSA(){
    try {
        // Cria uma instância de KeyPairGenerator para o algoritmo RSA
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA"); 
        
        // Inicializa o KeyPairGenerator com um tamanho de chave de 2048 bits (tamanho recomendado para RSA)
        keyGen.initialize(2048); 
        
        // Gera e retorna o par de chaves (privada e pública)
        return keyGen.generateKeyPair(); 
    } catch (NoSuchAlgorithmException ex) {
        // Em caso de erro na criação do KeyPairGenerator (por exemplo, algoritmo não encontrado), registra o erro
        Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    // Retorna null caso ocorra algum erro durante a geração das chaves
    return null;
}



// Método para criptografar um texto usando a chave pública RSA
public static String cifraComRSA(String textoPlano, PublicKey publicKey){
    try {
        // Cria uma instância do Cipher com o algoritmo RSA
        Cipher cipher = Cipher.getInstance("RSA"); 
        
        // Inicializa o Cipher para modo de criptografia (ENCRYPT_MODE) usando a chave pública fornecida
        cipher.init(Cipher.ENCRYPT_MODE, publicKey); 
        
        // Criptografa os dados (texto simples) e obtém os bytes criptografados
        byte[] encryptedBytes = cipher.doFinal(textoPlano.getBytes()); 
        
        // Codifica os bytes criptografados para uma string Base64 para facilitar a transmissão ou armazenamento
        return Base64.getEncoder().encodeToString(encryptedBytes); 
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
        // Caso ocorra um erro em qualquer uma das etapas (algoritmo, padding, chave inválida, etc.), registra o erro
        Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    // Retorna null se houver algum erro durante o processo de criptografia
    return null;
}


// Método para descriptografar um texto criptografado com a chave privada RSA
public static String decfiraComRSA(String txtCifrado, PrivateKey privateKey){
    try {
        // Decodifica o texto criptografado de Base64 para um vetor de bytes
        byte[] encryptedBytes = Base64.getDecoder().decode(txtCifrado); 
        
        // Cria uma instância do Cipher com o algoritmo RSA
        Cipher cipher = Cipher.getInstance("RSA"); 
        
        // Inicializa o Cipher para o modo de descriptografia (DECRYPT_MODE) usando a chave privada fornecida
        cipher.init(Cipher.DECRYPT_MODE, privateKey); 
        
        // Descriptografa os dados criptografados, transformando-os novamente em texto simples
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        // Retorna o texto simples (depois de convertido novamente para String)
        return new String(decryptedBytes);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
        // Caso ocorra um erro durante o processo de descriptografia, registra o erro
        Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    // Retorna null caso ocorra algum erro durante o processo de descriptografia
    return null;
}



    // Método para assinar uma mensagem usando a chave privada RSA
    public static String assinarMsg(String msg, PrivateKey privateKey) throws Exception {
    //Hash da mensagem usando o algoritmo SHA-256
    byte[] messageHash = hashMessage(msg);  // Gera um resumo (hash) da mensagem

    //Inicializa o objeto de assinatura com o algoritmo "SHA256withRSA" 
    // O algoritmo SHA-256 com RSA é usado para assinar a mensagem.
    Signature signature = Signature.getInstance("SHA256withRSA"); 

    //Assina o hash da mensagem com a chave privada
    signature.initSign(privateKey); // Inicializa a assinatura com a chave privada
    signature.update(messageHash); // Atualiza a assinatura com o hash da mensagem
    messageHash = signature.sign(); // Gera a assinatura digital com o hash da mensagem

    // Converte a assinatura para uma string codificada em Base64
    String signature64 = Base64.getEncoder().encodeToString(messageHash); 

    // Retorna a assinatura digital como uma string codificada em Base64
    return signature64; 
    }



// Método para verificar a assinatura digital de uma mensagem usando a chave pública RSA
public static boolean verificaAssinatura(String msg, String assinaturaSTR, PublicKey publicKey) throws Exception {
    
    //Decodifica a assinatura de Base64 para um vetor de bytes
    byte[] signatureBytes = Base64.getDecoder().decode(assinaturaSTR); // A assinatura é fornecida como uma string Base64
    
    //Gera o hash da mensagem usando o mesmo algoritmo de hash usado na assinatura (SHA-256)
    byte[] messageHash = hashMessage(msg); // Calcula o hash da mensagem

    //Inicializa o objeto de assinatura para verificar a assinatura
    Signature signature = Signature.getInstance("SHA256withRSA"); // Utiliza SHA-256 com RSA para verificar a assinatura

    //Inicializa a verificação com a chave pública
    signature.initVerify(publicKey); // Inicializa a assinatura para verificação usando a chave pública
    
    //Atualiza o objeto de assinatura com o hash da mensagem
    signature.update(messageHash); // Atualiza a assinatura com o hash da mensagem

    //Verifica se a assinatura fornecida é válida para o hash da mensagem
    return signature.verify(signatureBytes); // Retorna true se a assinatura for válida, false caso contrário
}


// Método para converter uma chave pública para uma string codificada em Base64
public static String convertePublicKeyParaBase64String(PublicKey publicKey) {
    //Obtém a forma codificada da chave pública (em formato de bytes)
    byte[] publicKeyBytes = publicKey.getEncoded(); // Retorna a chave pública em uma representação binária (byte array)

    //Codifica o vetor de bytes da chave pública em uma string Base64
    return Base64.getEncoder().encodeToString(publicKeyBytes); // Converte os bytes para uma string Base64 e retorna
}



    // Método para converter uma chave privada para uma string codificada em Base64
    public static String convertePrivateKeyParaBase64String(PrivateKey privateKey) {
    //Obtém a forma codificada da chave privada (em formato de bytes)
    byte[] privateKeyBytes = privateKey.getEncoded(); // Retorna a chave privada em uma representação binária (byte array)

    //Codifica o vetor de bytes da chave privada em uma string Base64
    return Base64.getEncoder().encodeToString(privateKeyBytes); // Converte os bytes para uma string Base64 e retorna
}


// Método para converter uma string codificada em Base64 para uma chave pública RSA
public static PublicKey converteBase64StringParaPublicKey (String publicKeyString){
    try {
        //Decodifica a string Base64 para um vetor de bytes (representação binária da chave pública)
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString); 
        
        // Cria um objeto X509EncodedKeySpec a partir dos bytes da chave pública
        // X.509 é um padrão comum para a representação de chaves públicas
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes); 
        
        //Cria uma instância de KeyFactory para o algoritmo RSA
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
        
        //Gera a chave pública a partir da chave especificada em X509EncodedKeySpec
        return keyFactory.generatePublic(keySpec); // Retorna a chave pública gerada
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        // Em caso de erro, registra a exceção
        Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
    }
    // Caso ocorra um erro, retorna null
    return null;
}

    

// Método para converter uma string codificada em Base64 para uma chave privada RSA
public static PrivateKey converteBase64StringParaPrivateKey (String privateKeyString){
    try {
        //Decodifica a string Base64 para um vetor de bytes (representação binária da chave privada)
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString); 
        
        //Cria um objeto PKCS8EncodedKeySpec a partir dos bytes da chave privada
        // PKCS#8 é o padrão para a codificação de chaves privadas
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes); 
        
        // Cria uma instância de KeyFactory para o algoritmo RSA
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
        
        // Gera a chave privada a partir da chave especificada em PKCS8EncodedKeySpec
        return keyFactory.generatePrivate(keySpec); // Retorna a chave privada gerada
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        // Em caso de erro, registra a exceção
        Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
    }
    // Caso ocorra um erro, retorna null
    return null;
}



    public static byte[] hashMessage(String message) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Initialize SHA-256 algorithm
        return digest.digest(message.getBytes()); // Returns the hash of the message
    }
    
}
