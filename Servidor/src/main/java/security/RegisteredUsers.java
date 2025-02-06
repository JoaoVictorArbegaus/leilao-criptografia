
package security;

import java.security.PublicKey;
import java.util.HashMap;

/**
 *
 * @author JOAO
 * Classe com as chaves publicas dos clientes previamente cadastrados
 */
public class RegisteredUsers {
    
    public static HashMap<String, String> dados = new HashMap<>();
    
    public RegisteredUsers(){
                dados.put("80009209999","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt088Zbo2wmHnWLpiHkN4ng/xapmKrIZVuCfuRcgSLyhrlxZEyKp2hMPImm5Q35yA1Wqagv8VNT4q4hgC43vDxNfPuem0ugz9GsqxkFaxL9USctene3UnUagM2fhZBPtHj7Xo0nzQM+uxDyNIpXuqbS0NeEFMa/ho8x5aV9IU/VI3M8EIGL9ekAYUL1NNvzWIB1kCRVrzH5z+D6OsjkVao4TDF4ly+m3jYTCJ+VLnFMb8inAjNoVflf/vIrEEq667geiYj56Dukd9aN52Hwu551J/lGhuOOjT0aQdes79jPY1t/276gY9/kD/qxcgWyNZqH3Fk3vczi+alJvzd8T5SwIDAQAB");
                dados.put("80009209992","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxoYCINQ1oTRSSQ0Eo2Cthd9KaqCa/8thX3w2jkG7CYgSK539CmU58se7TQXGlHRnc5VD3wABQuawr1pIqtjizGnGMCvTHly1WxsFhTII95GSkB0DyVEXZU3R2GgyMANpcx9naQ1YkmFK5mNzv59c45ogMsgqZ2oioE10SHY/HcdMaWa2372qN4Cb7xMVldHQ5USh/r5yZHT1p5/mjpkPOS+xohVZD3xiQqBBP0MDWd4H4cjWm94LUEwPKw4RgWNi3cT1OHW74GQKzJnm/J3Z3KZj07XEMtBNoprwJ8447baJW1lSLrJOwoSWxcHumoQxd9m2tXrpcOukSi2QNfw4bQIDAQAB");
                dados.put("80009209993","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA10poDEU3wyqyCOCVbUlB/QsITo55r2CvMQ4bAUqjCb0HS8PTYSPlWAEdRHwjIl7P1e8m61WvXoSnXJWA6BaByAFVm2rBeNpy1q99gewvz/jl3m/42g/h4I4ofpyW7a8rMidvLBtwj9fGgY5uzdYbiXLcSY7VCxisPRjjZ997ULnXO0jzlrJmjbI7bkk/c3Tvoi9P8prHPdihDVqH4llAurVawBxhmcRv+7bK36ujXqNNHJ+QUi8gqlZaM5KS9Gdw1GFIjQSx4Nd9yVUCG0v9Snkj3ZYF4m3SJkRUF2d2yo8btk4StQJBPtmm8jfAypbCfVCNIJaEoN92Lw5OOVqsWwIDAQAB");
    }
 
    public String retornaPublicKeySTR(String CPF){
        if(dados.containsKey(CPF)){
            return dados.get(CPF);
        }
        return null;
    }
    
    public PublicKey retornaPublicKey(String CPF){
        if(dados.containsKey(CPF)){
            PublicKey clientPublicKey = RSAMethods.converteBase64StringParaPublicKey(dados.get(CPF)); //tranforma a string em uma chave
            return clientPublicKey;
        }
        return null;
    }
}
