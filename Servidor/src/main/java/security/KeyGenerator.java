
package security;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;

/**
 * Classe usada para gerar as chaves usadas no codigo
 * @author JOAO
 * 
 */
public class KeyGenerator {
//        public static void main(String[] args) throws Exception {
//        generator();
//   }  
    
    public static void generator() throws NoSuchAlgorithmException, Exception {
        //gerador de chaves publica e privada
        KeyPair ParChaves = RSAMethods.gerarParDeChavesRSA();
        
        PublicKey keyPublica = ParChaves.getPublic();
        PrivateKey keyPrivada = ParChaves.getPrivate();
        
        String keyPublicaSTR = RSAMethods.convertePublicKeyParaBase64String(keyPublica);
        String keyPrivadaSTR = RSAMethods.convertePrivateKeyParaBase64String(keyPrivada);
        
        System.out.println("Chave Publica: ");
        System.out.println(keyPublicaSTR);
        
        System.out.println("Chave Privada: ");
        System.out.println(keyPrivadaSTR);
        
        //gerador de chave simetrica
        SecretKey key = AESMethods.geraChaveAES();
        String secretKeySTR = AESMethods.converteAESKeyParaBase64(key);
        
        System.out.println("Chave Simetrica: ");
        System.out.println(secretKeySTR);
        

    }
    

    
//server: 
//
//Chave Publica: 
//MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv1cAvOU3pUtO6bbLzaZQT5Uivy6VlP6B8OnzzkI8b5k0hQUhm10G6rTIHokwmMB/rmg1mG+83Je2qTnaTr3IrukpmKJwUOZWyfm7++dggkdLUnv3XphpyY/qoEjkO5Pd8wQV5sKOKlJwLVniiM8V6IIR0ZqYLgqWpaSyfb+iduBYLuHpys1Xpkhomxog9VEqnD03vF6GCFUEMsSPMwZQdabSq6aVr6lqBPjlHfQmg2pE9ZwXx2DYp6eKYzc3q857b7yvwuH1UohjpLUhOsKVfHivbKKlnHe04XLDULtcTLxQ0bMP5VnslEj7sxttWSxEfTWEANpvTnMkiPIStzpp+wIDAQAB
//
//Chave Privada:
//MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC/VwC85TelS07ptsvNplBPlSK/LpWU/oHw6fPOQjxvmTSFBSGbXQbqtMgeiTCYwH+uaDWYb7zcl7apOdpOvciu6SmYonBQ5lbJ+bv752CCR0tSe/demGnJj+qgSOQ7k93zBBXmwo4qUnAtWeKIzxXoghHRmpguCpalpLJ9v6J24Fgu4enKzVemSGibGiD1USqcPTe8XoYIVQQyxI8zBlB1ptKrppWvqWoE+OUd9CaDakT1nBfHYNinp4pjNzerzntvvK/C4fVSiGOktSE6wpV8eK9soqWcd7ThcsNQu1xMvFDRsw/lWeyUSPuzG21ZLER9NYQA2m9OcySI8hK3Omn7AgMBAAECggEAPjFTZhLDUZD253SpLgEfHZ8GGkpUCIfpSJtiyeoxpryPm6UdVViZhPSaD4LcLupzFgd9VD0NkE4n6z2IvgtmDaRwBtLV2xXjo+buuWRttCrkSr1MnIzB5ehHhS3sMYlQnUr4q3cVqp/9ImCTB5D4OAj/LbgoOt7qNJEiC0mczzJwA9BRsZWLMW1USVx2CqnXQAzJ/ybWzgiDqU3quiCnvXtHOz6p6d8c7kZeOv+yX5j6M/KB+77T5M3zK9wodM9NsgCeyGkqpDbeGbyZrqhxZWMG8aAcmHYZXDpxPw0KOJRQzsFbCUv8tvL1puxyoFH9lygJaVLRttZtHT281VsOsQKBgQDG3u2hEi3oz9j63qWuvLpGkVjUtE5uTPJJRJmWteYSlsp7RFUlBftDQDvMIUgvdmQ7AUGt93w+Eby2GF15VtW0lK3fVHjtm1tHwIdHnfApFhPAffEjDFiVaMIj8mZxtFaOUSXOZBdzhZtOPmLtAgkYjgn+MY1hS0ipvaCJ/Eh5hwKBgQD2Tj5kDOx7FX9kO+GMscbdxUkR9gzlgwHMq6rQZbkVcQf7IDLs5RVNIt6Mdc9/K9g64Rek40ecDmJVRKIriWVKQ9kV+iDKmeg4UVzxMzu+19X3pj0CHIcHmbeAwOvLczuDWTpieTg1rNWqQrngcsVEStIzYFns2QfF9OOq96jY7QKBgQCMQC3gGV0NQoAotJquN+U63UiDgcGKYnPAnkOfGRtv4q5b7p4JpUVFcWh6lsI8zsbzIfi7Ar25XHL1DQvnBgli+DQDO6SpFZZzbOE67bSwlSRK7ccAy7UxcZbxGQOMjv04ExyRiN9NFl2n+bF0qqnVil6byUCG7DwLkXNFvQkzwwKBgDFfU9BYatrtX0DnY/oA9N0lm9UW3lTvYt/1FWN60JQEuVXUiYDFFvUEZVoAj5xtXIXI8yVhTcxxcbKn9F4Hx58QZKasIgCaw1rurFPJMlO6DjD7SMVwlHyoiokrotiAe9CDhVmR9PuFbrsXbw0X1IUw/lvAF6aLhqxev3hY9qatAoGATRepxUjCGEnO/E9iX1ZUplwSYOvWs4fcMVZVeK3PaD67qMPFhPjK2jOAHjixLUSPzqhmgfl4MOt3ON6X1Ryl2xbx0T7xZzeBDyJStsW18QyjBgbeX8haX1nrknj87SJsGROH5Nc8om1tUAZu9RJuokvv9VxmjtJcLOmTnm3+Cls=
//
//Chave Simetrica:
//T/o3eaaQA9pqprNvXlue8Q==
}
