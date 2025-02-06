
package security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class AESMethods {

    public static SecretKey geraChaveAES(){
        try {
            // Cria um gerador de chaves para o algoritmo AES.
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            // Inicializa o gerador com tamanho de chave 128 bits.
            keyGen.init(128);
            // Retorna a chave AES gerada.
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            // Registra o erro caso o algoritmo AES não esteja disponível.
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static SecretKey converteBase64ParaSecretKey(String encodedKey) {
        // Decodifica a string Base64 para obter os bytes da chave.
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // Cria e retorna um objeto SecretKeySpec para o algoritmo AES com os bytes decodificados.
        return new SecretKeySpec(decodedKey, "AES");
    }

    public static String converteAESKeyParaBase64(SecretKey key) {
        // Obtém os bytes da chave e codifica-os em Base64.
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        return encodedKey;
    }

    public static IvParameterSpec geraIvUsandoChaveAES(SecretKey key) {
        try {
            // Obtém os bytes da chave AES.
            byte[] keyBytes = key.getEncoded();
            // Cria um MessageDigest para SHA-256.
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Gera o hash SHA-256 dos bytes da chave.
            byte[] iv = md.digest(keyBytes);
            
            // Cria um array de 16 bytes para armazenar o IV.
            byte[] iv16 = new byte[16];
            // Copia os 16 primeiros bytes do hash para o vetor de inicialização.
            System.arraycopy(iv, 0, iv16, 0, 16);
            
            // Retorna o IV encapsulado em um objeto IvParameterSpec.
            return new IvParameterSpec(iv16);
        } catch (NoSuchAlgorithmException ex) {
            // Registra o erro caso o algoritmo SHA-256 não esteja disponível.
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String cifraComAES(String plainText, SecretKey key, IvParameterSpec iv) {
        try {
            // Obtém uma instância do Cipher configurada para AES/CBC/PKCS5Padding.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // Inicializa o cipher no modo de cifragem com a chave e IV fornecidos.
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            // Cifra os bytes do texto em claro.
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            // Codifica os bytes cifrados em Base64 e retorna a String resultante.
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            // Registra o erro ocorrido durante o processo de cifragem.
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String decifraComAES(String encryptedText, SecretKey key, IvParameterSpec iv) {
        try {
            // Obtém uma instância do Cipher configurada para AES/CBC/PKCS5Padding.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // Inicializa o cipher no modo de decifragem com a chave e IV fornecidos.
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            // Decodifica a mensagem cifrada de Base64 para bytes.
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            // Converte os bytes decifrados para String e retorna o resultado.
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            // Registra o erro ocorrido durante o processo de decifragem.
            Logger.getLogger(RSAMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
