
package security;

import java.security.PublicKey;
import javax.crypto.SecretKey;

/**
 *
 * @author JOAO
 */

//Classe para armazenar a chave publica do servidor
public class KeyLogger {
    
    //chave publica e privada do servidor em string
    public static final String ServerPublicKeySTR = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv1cAvOU3pUtO6bbLzaZQT5Uivy6VlP6B8OnzzkI8b5k0hQUhm10G6rTIHokwmMB/rmg1mG+83Je2qTnaTr3IrukpmKJwUOZWyfm7++dggkdLUnv3XphpyY/qoEjkO5Pd8wQV5sKOKlJwLVniiM8V6IIR0ZqYLgqWpaSyfb+iduBYLuHpys1Xpkhomxog9VEqnD03vF6GCFUEMsSPMwZQdabSq6aVr6lqBPjlHfQmg2pE9ZwXx2DYp6eKYzc3q857b7yvwuH1UohjpLUhOsKVfHivbKKlnHe04XLDULtcTLxQ0bMP5VnslEj7sxttWSxEfTWEANpvTnMkiPIStzpp+wIDAQAB";
    
    //chave publica e privada do servidor
    public static final PublicKey ServerPublicKey = RSAMethods.converteBase64StringParaPublicKey(ServerPublicKeySTR);
    
    public static SecretKey secretKey;
    public static String ChaveSimetricaSTR;
    
    
}
