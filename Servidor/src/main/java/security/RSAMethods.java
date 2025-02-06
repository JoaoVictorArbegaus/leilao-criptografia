
package security;

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
import javax.crypto.Cipher;

/**
 *
 * @author JOAO
 */
public class RSAMethods {
    
    public static KeyPair gerarParDeChavesRSA() throws NoSuchAlgorithmException {
        // Cria um gerador de chaves para o algoritmo RSA.
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        // Inicializa o gerador com tamanho de chave 2048 bits.
        keyGen.initialize(2048);
        // Gera e retorna o par de chaves.
        return keyGen.generateKeyPair();
    }

    public static String cifrarComRSA(String plainText, PublicKey publicKey) throws Exception {
        // Obtém uma instância do Cipher para o algoritmo RSA.
        Cipher cipher = Cipher.getInstance("RSA");
        // Inicializa o cipher no modo de cifragem usando a chave pública.
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // Cifra os bytes do texto em claro.
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        // Codifica os bytes cifrados em Base64 e retorna como String.
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decifrarComRSA(String cipherText, PrivateKey privateKey) throws Exception {
        // Decodifica o texto cifrado de Base64 para bytes.
        byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);
        // Obtém uma instância do Cipher para o algoritmo RSA.
        Cipher cipher = Cipher.getInstance("RSA");
        // Inicializa o cipher no modo de decifragem usando a chave privada.
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // Decifra os bytes cifrados.
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        // Converte os bytes decifrados para String e retorna.
        return new String(decryptedBytes);
    }
    
    public static String assinarMsg(String message, PrivateKey privateKey) throws Exception {
        // Gera o hash da mensagem usando SHA-256.
        byte[] messageHash = hashMessage(message);
        // Cria uma instância de Signature para o algoritmo SHA256withRSA.
        Signature signature = Signature.getInstance("SHA256withRSA");
        // Inicializa o objeto de assinatura para assinar, utilizando a chave privada.
        signature.initSign(privateKey);
        // Atualiza o objeto de assinatura com o hash da mensagem.
        signature.update(messageHash);
        // Gera a assinatura a partir do hash.
        messageHash = signature.sign();
        // Codifica a assinatura em Base64 para facilitar a transmissão e armazenamento.
        String signature64 = Base64.getEncoder().encodeToString(messageHash);
        return signature64;
    }

    public static boolean verificarAssinatura(String message, String signatureSTR, PublicKey publicKey) throws Exception {
        // Decodifica a assinatura de Base64 para bytes.
        byte[] signatureBytes = Base64.getDecoder().decode(signatureSTR);
        // Gera o hash da mensagem usando SHA-256.
        byte[] messageHash = hashMessage(message);

        // Cria uma instância de Signature para o algoritmo SHA256withRSA.
        Signature signature = Signature.getInstance("SHA256withRSA");
        // Inicializa o objeto de assinatura para verificação, utilizando a chave pública.
        signature.initVerify(publicKey);
        // Atualiza o objeto de assinatura com o hash da mensagem.
        signature.update(messageHash);
        // Verifica se a assinatura fornecida é válida para o hash e a chave pública.
        return signature.verify(signatureBytes);
    }

    public static String convertePublicKeyParaBase64String(PublicKey publicKey) {
        // Obtém os bytes codificados da chave pública.
        byte[] publicKeyBytes = publicKey.getEncoded();
        // Codifica os bytes em Base64 e retorna a String resultante.
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    public static String convertePrivateKeyParaBase64String(PrivateKey privateKey) {
        // Obtém os bytes codificados da chave privada.
        byte[] privateKeyBytes = privateKey.getEncoded();
        // Codifica os bytes em Base64 e retorna a String resultante.
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    public static PublicKey converteBase64StringParaPublicKey(String publicKeyString) {
        try {
            // Decodifica a String Base64 para obter os bytes da chave pública.
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            // Cria um especificador de chave no formato X509.
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            // Obtém uma instância do KeyFactory para o algoritmo RSA.
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // Gera e retorna a chave pública a partir do especificador.
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Retorna null caso ocorra algum erro.
        return null;
    }
 
    public static PrivateKey converteBase64StringParaPrivateKey(String privateKeyString) {
        try {
            // Decodifica a String Base64 para obter os bytes da chave privada.
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
            // Cria um especificador de chave no formato PKCS8.
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // Obtém uma instância do KeyFactory para o algoritmo RSA.
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // Gera e retorna a chave privada a partir do especificador.
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Retorna null caso ocorra algum erro.
        return null;
    }

    public static byte[] hashMessage(String message) throws NoSuchAlgorithmException {
        // Cria uma instância de MessageDigest para SHA-256.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // Calcula e retorna o hash da mensagem (em bytes).
        return digest.digest(message.getBytes());
    }

}