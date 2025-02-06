package models;
import org.json.JSONObject;

/**
 * @author JOAO
 */
public class ConnectionRequest {

    private String cpf;
    private String message;
    private String singnature;

    // Construtor
    public ConnectionRequest(String cpf, String message, String encryptedHash) {
        this.cpf = cpf;
        this.message = message;
        this.singnature = encryptedHash;
    }

    // Método para converter o objeto ConnectionRequest para JSON
    public String toJSON() {
        JSONObject json = new JSONObject();
        json.put("cpf", this.cpf);
        json.put("message", this.message);
        json.put("assinatura", this.singnature);
        
        return json.toString();
    }

    // Método para converter um JSON para um objeto ConnectionRequest
    public static ConnectionRequest fromJSON(JSONObject json) {
        String cpf = json.getString("cpf");
        String message = json.getString("message");
        String encryptedHash = json.getString("encryptedHash");
        return new ConnectionRequest(cpf, message, encryptedHash);
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEncryptedHash() {
        return singnature;
    }

    public void setEncryptedHash(String encryptedHash) {
        this.singnature = encryptedHash;
    }


}
