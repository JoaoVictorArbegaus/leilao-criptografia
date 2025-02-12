
package views;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import models.AuctionData;
import network.MulticastHandler;
import static network.MulticastHandler.socket;
import org.json.JSONException;
import org.json.JSONObject;
import security.AESMethods;
import security.KeyLogger;

/**
 *
 * @author JOAO
 */
public class LeilaoView extends javax.swing.JPanel {
    
    private Thread timerThread;
    private MulticastHandler multicastHandler;
    
    public LeilaoView(JFrame frame) throws IOException {
        initComponents();
        conectaMulticast();
        startReceivingMessages();
    }
    
    
    private void conectaMulticast() throws UnknownHostException, IOException{
        
        InetAddress multicastGtoup = InetAddress.getByName(AuctionData.multicastAddress);
        AuctionData.multicastSocket = new MulticastSocket(AuctionData.multicastPort);
        AuctionData.multicastSocket.joinGroup(multicastGtoup);
        System.out.println("Conectado ao grupo multicast");
    }
    
/**
 * Inicia uma thread para receber e processar mensagens do grupo multicast.
 * Essa thread aguarda mensagens, decifra usando AES e processa a ação contida no JSON.
 */
    private void startReceivingMessages() {
    // Criação e inicialização de uma thread para receber as mensagens de forma assíncrona.
    Thread receiverThread = new Thread(() -> {
        try {
            // Buffer para armazenar os dados recebidos (1024 bytes).
            byte[] buffer = new byte[1024];

                // Loop infinito para manter a thread sempre aguardando novas mensagens.
            while (true) {
                // Cria um pacote DatagramPacket para receber os dados.
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("Aguardando mensagens do multicast...");

                // Recebe o pacote através do socket multicast.
                AuctionData.multicastSocket.receive(packet);
                System.out.println("Mensagem recebida");
                    
                // Converte os dados do pacote para uma String, considerando o tamanho real da mensagem.
                String msgRecebidaCifrada = new String(packet.getData(), 0, packet.getLength());

                // Gera o vetor de inicialização (IV) usando a chave simétrica do servidor.
                IvParameterSpec iv = AESMethods.geraIvUsandoChaveAES(KeyLogger.ServerSimetricKey);
                    
                // Decifra a mensagem recebida utilizando AES com a chave simétrica e o IV gerado.
                String msgRecebida = AESMethods.decifraComAES(msgRecebidaCifrada, KeyLogger.ServerSimetricKey, iv);

                // Converte a mensagem decifrada para um objeto JSON para facilitar o processamento.
                JSONObject jsonRecebido = new JSONObject(msgRecebida);

                // Extrai informações do JSON: identificador do remetente e a ação solicitada.
                String remetente = jsonRecebido.getString("sender");
                String acaoDoUser = jsonRecebido.getString("action");
                System.out.println("ACAO DO PAYLOAD RECEBIDO: " + acaoDoUser);

                // Verifica se o remetente é o "client" (caso a mensagem seja originada de um cliente)
                    if (remetente.equalsIgnoreCase("client")) {
                        System.out.println("IF CLIENTE ENTROU");

                        // Verifica se a ação solicitada é "getDetailsAboutItem" (requisitar detalhes do item)
                        if (acaoDoUser.equalsIgnoreCase("getDetailsAboutItem")) {
                            System.out.println("Entrou no get details");
                            
                            // Verifica se o campo de nome (NomeField) não está vazio ou nulo,
                            // o que indica que o leilão já começou.
                            if(NomeField.getText().equalsIgnoreCase("") || NomeField.getText().equals(null)){
                                System.out.println("O leilao ainda n comecou");
                            }else{
                                
                                // Cria um novo objeto JSON para enviar os detalhes do item.s
                                JSONObject sendedjson = new JSONObject();

                                // Obtém os detalhes do item a partir dos campos da interface.
                                String nome = NomeField.getText();
                                String desc = DescField.getText();
                                Float lanceMin = Float.parseFloat(minimoField.getText());
                                int tempo = Integer.parseInt(CronoField.getText());
                                Float preco = Float.parseFloat(ValorField.getText());
                                
                                // Popula o JSON com os dados do item e a ação "itemDetails".
                                sendedjson.put("sender", "server");
                                sendedjson.put("action", "itemDetails");
                                sendedjson.put("name", nome);
                                sendedjson.put("description", desc);
                                sendedjson.put("currentPrice", preco);
                                sendedjson.put("bidIncrement", lanceMin);
                                sendedjson.put("chronometer", tempo);
                                
                                System.out.println("json que o server vai enviar: " + sendedjson);
                                
                                // Define o grupo multicast com base no endereço definido em AuctionData.
                                InetAddress group = InetAddress.getByName(AuctionData.multicastAddress);

                                // Converte o JSON para String.
                                String message = sendedjson.toString();

                                // Criptografa a mensagem utilizando AES com a chave simétrica do servidor e o IV.
                                String encodedMessage = AESMethods.cifraComAES(message, KeyLogger.ServerSimetricKey, iv);
                                System.out.println("Json a ser enviado: " + encodedMessage);

                                // Prepara o pacote a ser enviado com os dados criptografados.
                                byte[] buffer2 = encodedMessage.getBytes();
                                DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, group, AuctionData.multicastPort);

                                // Envia o pacote multicast.
                                AuctionData.multicastSocket.send(packet2);
                                System.out.println("Mensagem enviada!");                          
                            }

                        }
    
                        // Processamento de ação para realizar um lance
                        if (acaoDoUser.equalsIgnoreCase("makeABid")) {
                            Float preco = Float.parseFloat(ValorField.getText());
                            JSONObject jsonResposta = new JSONObject();

                            // Obtém o novo valor do lance a partir do JSON recebido.
                            float novoPreco = jsonRecebido.getFloat("value");

                            // Se o novo lance for maior que o lance atual, atualiza os dados.
                            if (novoPreco > preco) {
                                preco = jsonRecebido.getFloat("value");
                                ValorLance.setText(String.valueOf(preco));
                                String CPF = jsonRecebido.getString("CPF");
                                DonoLance.setText(CPF);

                                // Obtém novamente os dados do item da interface.
                                String nome = NomeField.getText();
                                String desc = DescField.getText();
                                Float lanceMin = Float.parseFloat(minimoField.getText());
                                int tempo = Integer.parseInt(CronoField.getText());
                                
                                // Prepara o JSON de resposta com os detalhes atualizados do item.
                                jsonResposta.put("sender", "server");
                                jsonResposta.put("action", "itemDetails");
                                jsonResposta.put("name", nome);
                                jsonResposta.put("description", desc);
                                jsonResposta.put("currentPrice", preco);
                                jsonResposta.put("bidIncrement", lanceMin);
                                jsonResposta.put("chronometer", tempo);
                                jsonResposta.put("CPF", CPF);
                                
                                System.out.println("json que o server vai enviar: " + jsonResposta);
                                
                                // Define o grupo multicast para envio.
                                InetAddress group = InetAddress.getByName(AuctionData.multicastAddress);
            
                                // Converte o JSON para String.
                                String message = jsonResposta.toString();
            
                                // Criptografa a mensagem utilizando a chave simétrica e o IV.
                                String encodedMessage = AESMethods.cifraComAES(message, KeyLogger.ServerSimetricKey, iv);
            
                                System.out.println("Json a ser enviado: "+encodedMessage);

                                byte[] buffer2 = encodedMessage.getBytes();
                                DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, group, AuctionData.multicastPort);
                                AuctionData.multicastSocket.send(packet2); 
                                System.out.println("Mensagem enviada! ");
                                
                                // Inicia um timer ou outra ação relacionada à atualização dos dados do lance.
                                timer(jsonResposta);

                            }
                        }
                    }

                }
            } catch (IOException e) {
                System.err.println("Error in receiving messages: " + e.getMessage());
            }
        });
        receiverThread.start();
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        BotaoLance = new javax.swing.JButton();
        NomeField = new javax.swing.JTextField();
        DescField = new javax.swing.JTextField();
        ValorField = new javax.swing.JTextField();
        CronoField = new javax.swing.JTextField();
        ValorLance = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        DonoLance = new javax.swing.JLabel();
        minimoField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        jLabel1.setText("Nome: ");

        jLabel2.setText("Descricao:");

        jLabel3.setText("Lance Inicial:");

        jLabel4.setText("Tempo de Lance:");

        jLabel5.setText("Maior lance atual:");

        BotaoLance.setText("Começar Leilão");
        BotaoLance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotaoLanceActionPerformed(evt);
            }
        });

        NomeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NomeFieldActionPerformed(evt);
            }
        });

        DescField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescFieldActionPerformed(evt);
            }
        });

        ValorLance.setText("valor_lance_atual");

        jLabel7.setText("Cliente dono do lance:");

        DonoLance.setText("dono_lance");

        jLabel6.setText("Lance minimo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(208, 208, 208)
                .addComponent(BotaoLance)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minimoField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DonoLance, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(247, 247, 247))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CronoField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ValorField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(NomeField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ValorLance, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DescField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(NomeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(DescField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ValorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(CronoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(minimoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(ValorLance))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(DonoLance))
                .addGap(18, 18, 18)
                .addComponent(BotaoLance)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    //Pega os dados do painel e adiciona em um json e envia ao multicast
    private void BotaoLanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotaoLanceActionPerformed
        try {
        JSONObject dados = new JSONObject();
        
        String nome = NomeField.getText();
        String desc = DescField.getText();
        Float preco = Float.parseFloat(ValorField.getText());
        Float lanceMin = Float.parseFloat(minimoField.getText());
        int tempo = Integer.parseInt(CronoField.getText());
        
        dados.put("sender", "server");
        dados.put("action", "itemDetails");
        dados.put("name", nome);
        dados.put("description", desc);
        dados.put("currentPrice", preco);
        dados.put("bidIncrement", lanceMin);
        dados.put("chronometer", tempo);

        System.out.println("JSON a ser enviado: "+dados);
        
        AuctionData.itemName = nome;
        AuctionData.itemDescription = desc;
        AuctionData.currentPrice = preco;
        AuctionData.bidIncrement = lanceMin;
        AuctionData.chronometer = tempo;
        
        InetAddress group = InetAddress.getByName(AuctionData.multicastAddress);
            
        String message = dados.toString();
            
        IvParameterSpec iv = AESMethods.geraIvUsandoChaveAES(KeyLogger.ServerSimetricKey);
            
        String encodedMessage = AESMethods.cifraComAES(message, KeyLogger.ServerSimetricKey, iv);
            
        System.out.println("Json a ser enviado: "+encodedMessage);

        byte[] buffer = encodedMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, AuctionData.multicastPort);
        AuctionData.multicastSocket.send(packet);
        System.out.println("Mensagem enviada! ");
        timer(dados);
        
        } catch (IOException e) {
                System.err.println("Error in sending messages: " + e.getMessage());
            } 
        
        NomeField.setEditable(false);
        DescField.setEditable(false);
        ValorField.setEditable(false);
        CronoField.setEditable(false);
        minimoField.setEditable(false);
        BotaoLance.setEnabled(false);
        
    }//GEN-LAST:event_BotaoLanceActionPerformed

    private void NomeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NomeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NomeFieldActionPerformed

    private void DescFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DescFieldActionPerformed
    
    public void timer(JSONObject json) {
        // Interrompe a thread anterior, se estiver ativa
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
        
        // Cria e inicia uma nova thread
        timerThread = new Thread(() -> {
            try {
                //Pega o tempo do cronometro com base no field do painel
                String tempo = CronoField.getText();
                int temp = Integer.parseInt(tempo);
                
                while (temp > 0) {
                    Thread.sleep(1000); // Aguarda 1 segundo
                    temp--;
                    String tempSTR = String.valueOf(temp);
                    CronoField.setText(tempSTR);
                }
                //Quando acaba o tempo cria um json com os dados do vencedor e envia ao multicast
                JSONObject receivedJson = json;
                
                String nome = receivedJson.getString("name");
                String desc = receivedJson.getString("description");
                Float preco = receivedJson.getFloat("currentPrice");
                Float lanceMin = receivedJson.getFloat("bidIncrement");
                int crono = receivedJson.getInt("chronometer");
                
                //Se tem cpf no json entao alguem fez um lance e logo há um vencedor 
                if(receivedJson.has("CPF")){
                    String CPF = receivedJson.getString("CPF");
                    
                    JSONObject sendedjson = new JSONObject();
                
                    sendedjson.put("sender", "server");
                    sendedjson.put("action", "declareWinner");
                    sendedjson.put("CPF", CPF);
                    sendedjson.put("currentPrice", preco);
                    sendedjson.put("name", nome);
                                
                    InetAddress group = InetAddress.getByName(AuctionData.multicastAddress);
            
                    String message = sendedjson.toString();
            
                    System.out.println("json que o server vai enviar: " + sendedjson);
                    IvParameterSpec iv = AESMethods.geraIvUsandoChaveAES(KeyLogger.ServerSimetricKey);
                    String encodedMessage = AESMethods.cifraComAES(message, KeyLogger.ServerSimetricKey, iv);
            
                    System.out.println("Json a ser enviado: "+encodedMessage);

                    byte[] buffer = encodedMessage.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, AuctionData.multicastPort);
                    AuctionData.multicastSocket.send(packet);
                    System.out.println("Mensagem enviada! ");
                    
                    //Reseta os dados do painel do leilao
                    NomeField.setEditable(true);
                    DescField.setEditable(true);
                    ValorField.setEditable(true);
                    CronoField.setEditable(true);
                    minimoField.setEditable(true);
                    BotaoLance.setEnabled(true);
                    NomeField.setText("");
                    DescField.setText("");
                    ValorField.setText("");
                    CronoField.setText("");
                    minimoField.setText("");
                    ValorLance.setText("");
                    DonoLance.setText("");
                    
                    //Cria e envia um json vazio ao membros do multicast para resetarem seus dados
                    JSONObject jsonEviar = new JSONObject();
                    
                    jsonEviar.put("sender", "server");
                    jsonEviar.put("action", "itemDetails");
                    jsonEviar.put("name", "");
                    jsonEviar.put("description", "");
                    jsonEviar.put("currentPrice", "");
                    jsonEviar.put("bidIncrement", "");
                    jsonEviar.put("chronometer", "");
                    jsonEviar.put("CPF", "");
                    
                    InetAddress group2 = InetAddress.getByName(AuctionData.multicastAddress);
            
                    String message2 = jsonEviar.toString();
                    
                    System.out.println("Json a ser enviado: "+message2);
            
                    String encodedMessage2 = AESMethods.cifraComAES(message2, KeyLogger.ServerSimetricKey, iv);
            
                    System.out.println("Json a ser enviado: "+encodedMessage2);

                    byte[] buffer3 = encodedMessage2.getBytes();
                    DatagramPacket packet3 = new DatagramPacket(buffer3, buffer3.length, group2, AuctionData.multicastPort);
                    AuctionData.multicastSocket.send(packet3); // Sends the message
                    System.out.println("Mensagem enviada! ");
                    
                }else{
                    System.out.println("Ninguem fez nenhum lance");
                    
                    NomeField.setEditable(true);
                    DescField.setEditable(true);
                    ValorField.setEditable(true);
                    CronoField.setEditable(true);
                    minimoField.setEditable(true);
                    BotaoLance.setEnabled(true);
                    
                    NomeField.setText("");
                    DescField.setText("");
                    ValorField.setText("");
                    CronoField.setText("");
                    minimoField.setText("");
                    ValorLance.setText("");
                    DonoLance.setText("");
                    
                    JSONObject jsonEviar = new JSONObject();
                    
                    jsonEviar.put("sender", "server");
                    jsonEviar.put("action", "itemDetails");
                    jsonEviar.put("name", "");
                    jsonEviar.put("description", "");
                    jsonEviar.put("currentPrice", "");
                    jsonEviar.put("bidIncrement", "");
                    jsonEviar.put("chronometer", "");
                    jsonEviar.put("CPF", "");
                    
                    InetAddress group2 = InetAddress.getByName(AuctionData.multicastAddress);
            
                    String message2 = jsonEviar.toString();
                    
                    System.out.println("Json a ser enviado: "+message2);
            
                    IvParameterSpec iv = AESMethods.geraIvUsandoChaveAES(KeyLogger.ServerSimetricKey);
                    String encodedMessage2 = AESMethods.cifraComAES(message2, KeyLogger.ServerSimetricKey, iv);
            
                    System.out.println("Json a ser enviado: "+encodedMessage2);

                    byte[] buffer3 = encodedMessage2.getBytes();
                    DatagramPacket packet3 = new DatagramPacket(buffer3, buffer3.length, group2, AuctionData.multicastPort);
                    AuctionData.multicastSocket.send(packet3); // Sends the message
                    System.out.println("Mensagem enviada! ");
                }
            } catch (InterruptedException e) {
                // Interrompe a thread sem erros
                Thread.currentThread().interrupt();
            } catch (IOException ex) {
                Logger.getLogger(LeilaoView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        timerThread.start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotaoLance;
    private javax.swing.JTextField CronoField;
    private javax.swing.JTextField DescField;
    private javax.swing.JLabel DonoLance;
    private javax.swing.JTextField NomeField;
    private javax.swing.JTextField ValorField;
    private javax.swing.JLabel ValorLance;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField minimoField;
    // End of variables declaration//GEN-END:variables
}
