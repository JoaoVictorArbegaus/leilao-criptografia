
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
 * Classe com os metodos AES usados na criptografia
 */
public class AESMethods {
    
    
    //Gera uma chave simétrica AES de 128 bits.
    public static SecretKey gerarChaveAES() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES"); // Obtém um gerador de chaves AES
        keyGen.init(128); // Define o tamanho da chave para 128 bits
        return keyGen.generateKey(); // Retorna a chave gerada
    }

    //Converte uma chave AES representada como String Base64 para um objeto SecretKey.
    public static SecretKey converteBase64ParaSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey); // Decodifica a chave Base64 para bytes
        return new SecretKeySpec(decodedKey, "AES"); // Retorna a chave como SecretKeySpec (AES)
    }

    //Converte um objeto SecretKey AES para sua representação em Base64.
    public static String converteAESKeyParaBase64(SecretKey key) {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded()); // Codifica a chave para Base64
        return encodedKey; // Retorna a chave como string Base64
    }

    //Gera um vetor de inicialização (IV) de 16 bytes a partir da chave AES
    //O IV é gerado aplicando um hash SHA-256 sobre a chave e pegando os primeiros 16 bytes.
    public static IvParameterSpec gerarIvUsandoAESKey(SecretKey key) throws NoSuchAlgorithmException {
        byte[] keyBytes = key.getEncoded(); // Obtém os bytes da chave AES
        MessageDigest md = MessageDigest.getInstance("SHA-256"); // Cria um hash SHA-256
        byte[] iv = md.digest(keyBytes); // Gera o hash da chave
        byte[] iv16 = new byte[16]; // Cria um array de 16 bytes para o IV
        System.arraycopy(iv, 0, iv16, 0, 16); // Copia os primeiros 16 bytes do hash para o IV
        return new IvParameterSpec(iv16); // Retorna o IV gerado
    }

    //Cifra um texto usando AES no modo CBC com preenchimento PKCS5Padding.
    public static String cifrarComAES(String plainText, SecretKey key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Configura o modo AES/CBC/PKCS5Padding
            cipher.init(Cipher.ENCRYPT_MODE, key, iv); // Inicializa o cifrador no modo ENCRYPT
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes()); // Cifra o texto
            return Base64.getEncoder().encodeToString(encryptedBytes); // Retorna o texto cifrado em Base64
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | 
                 BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex); // Registra erros
        }
        return null; // Retorna null em caso de erro
    }

    //Decifra um texto cifrado usando AES no modo CBC com preenchimento PKCS5Padding.
    public static String decifrarComAES(String encryptedText, SecretKey key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Configura o modo AES/CBC/PKCS5Padding
            cipher.init(Cipher.DECRYPT_MODE, key, iv); // Inicializa o cifrador no modo DECRYPT
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText)); // Decifra o texto
            return new String(decryptedBytes); // Retorna o texto decifrado
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | 
                 BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex); // Registra erros
        }
        return null; // Retorna null em caso de erro
    }
    
}
