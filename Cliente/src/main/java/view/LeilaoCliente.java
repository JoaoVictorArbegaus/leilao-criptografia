/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import main.AuctionClient;
import models.AuctionData;
import network.MulticastClient;
import org.json.JSONException;
import org.json.JSONObject;
import security.AESMethods;
import security.KeyLogger;

/**
 *
 * @author JOAO
 */
public class LeilaoCliente extends javax.swing.JPanel {
    
    private MulticastClient multicastClient;
    private final String cpf;

     public LeilaoCliente(JFrame frame, String cpf) throws JSONException, NoSuchAlgorithmException {
        this.cpf = cpf;
        initComponents();
        cliente.setText(cpf);
        //Ao iniciar o painel ele começa a escutar
        iniciarEscutaMensagens();
        SolicitaDados();
    }

    private void iniciarEscutaMensagens() {
        //Separado em Thread para n dar conflito
        Thread receiverThread = new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    System.out.println("Aguardando mensagens do multicast...");
                    //Recebe o pacote vindo do multicast
                    MulticastClient.socket.receive(packet); 
                    System.out.println("Mensagem recebida");
                    
                    //Decifra a mensagem
                    String msgCifrada = new String(packet.getData(), 0, packet.getLength());
                    IvParameterSpec iv = AESMethods.gerarIvUsandoAESKey(KeyLogger.secretKey);//Usa a chave simetrica cadastrada no KeyLogger
                    String msgDecifrada = AESMethods.decifrarComAES(msgCifrada, KeyLogger.secretKey, iv);
                    
                    System.out.println("Mensagem Recebida: "+ msgDecifrada);
                    
                    JSONObject jsonRecebido = new JSONObject(msgDecifrada);
                    String remetente = jsonRecebido.getString("sender"); //String de quem mandou a mensagem
              
                    //Se a mensagem nao for do server ele n faz nada
                    //Pois no multicast ele recebe msg dele mesme e de outros clientes
                    if (remetente.equalsIgnoreCase("server")) {
                        
                        String tipoAcao = jsonRecebido.getString("action");
                        
                        //Se a ação foi receber detalhes do item
                        if ("itemDetails".equalsIgnoreCase(tipoAcao)) {

                            String nome = jsonRecebido.getString("name");
                            String desc = jsonRecebido.getString("description");
                            String preco = jsonRecebido.getString("currentPrice");
                            String lanceMin = jsonRecebido.getString("bidIncrement");
                            String tempo = jsonRecebido.getString("chronometer");
                            
                            //Seta os labels do painel com base nos campos do json
                            nome_item.setText(nome);
                            desc_item.setText(desc);
                            valor_item.setText(preco);
                            cronometro.setText(tempo);
                            minimoField.setText(lanceMin);
                            
                            if(jsonRecebido.has("CPF")){
                                    String CPF = jsonRecebido.getString("CPF");
                                    cpfBidder.setText(CPF);
                                }
                        }
                        //Se a ação foi o servidor declarando um ganhador 
                        if(tipoAcao.equalsIgnoreCase("declareWinner")){
                            String cpf = jsonRecebido.getString("CPF");
                            String name = jsonRecebido.getString("name");
                            String valor = jsonRecebido.getString("currentPrice");
                            JOptionPane.showMessageDialog(null, "O cliente " + cpf + " levou um(a) " + name + " por " + valor);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error in receiving messages: " + e.getMessage());
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LeilaoCliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(LeilaoCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        receiverThread.start();
    }

    //Envia uma solicitação de dados
    public static void SolicitaDados() throws JSONException, NoSuchAlgorithmException{
             try {
                JSONObject json = new JSONObject();
                //Json que contem apenas o cpf e a solicitação
                json.put("sender", "client");
                json.put("CPF", AuctionData.cpf);
                json.put("action", "getDetailsAboutItem");
                InetAddress group = InetAddress.getByName(AuctionData.multicastAddress);
                String msg = json.toString();
                IvParameterSpec iv = AESMethods.gerarIvUsandoAESKey(KeyLogger.secretKey);
                String msgCifrada = AESMethods.cifrarComAES(msg, KeyLogger.secretKey,iv);
                byte[] buffer = msgCifrada.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, AuctionData.multicastPort);
                //Envia a solicitação
                MulticastClient.socket.send(packet);
                System.out.println("enviou solicitação de entrada: " + msg);
                
            } catch (IOException e) {
                System.err.println("Error in sending messages: " + e.getMessage());
            }
        
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nome_item = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        desc_item = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        valor_item = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cronometro = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        LanceField = new javax.swing.JTextField();
        BotaoLance = new javax.swing.JButton();
        cronometro1 = new javax.swing.JLabel();
        minimoField = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cliente = new javax.swing.JLabel();
        cpfBidder = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        jLabel1.setText("Nome: ");

        nome_item.setText("nome_do_item");

        jLabel2.setText("Descricao:");

        desc_item.setText("desc_do_item");

        jLabel3.setText("Lance Atual:");

        valor_item.setText("valor_do_item");

        jLabel4.setText("Cronometro:");

        cronometro.setText("cronometro");

        jLabel5.setText("Seu Lance:");

        BotaoLance.setText("Enviar Lance");
        BotaoLance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotaoLanceActionPerformed(evt);
            }
        });

        cronometro1.setText("o lance minino é ");

        minimoField.setText("lance_minimo");

        jLabel6.setText("Cliente: ");

        cliente.setText("cliente");

        cpfBidder.setText("jLabel7");

        jLabel7.setText("Dono do lance:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(211, Short.MAX_VALUE)
                .addComponent(BotaoLance)
                .addGap(205, 205, 205))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nome_item))
                    .addComponent(LanceField, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cronometro1)
                        .addGap(18, 18, 18)
                        .addComponent(minimoField))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cronometro))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valor_item))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(desc_item)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cliente)
                    .addComponent(cpfBidder))
                .addGap(83, 83, 83))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nome_item)
                    .addComponent(jLabel6)
                    .addComponent(cliente))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(desc_item)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cpfBidder)
                            .addComponent(jLabel7))))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(valor_item))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cronometro))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cronometro1)
                    .addComponent(minimoField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LanceField, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(BotaoLance)
                .addContainerGap(18, Short.MAX_VALUE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BotaoLanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotaoLanceActionPerformed
        try {

                //Verifica se o campo de lance esta vazio
                if(LanceField.getText().equalsIgnoreCase("") || LanceField.getText().equals(null)){
                    System.out.println("Campo vazio");
                }else{
                    //Pega os valores dos campos
                    float minimo = Float.parseFloat(minimoField.getText());
                    float valorAtual = Float.parseFloat(valor_item.getText());;
                    float valor = Float.parseFloat(LanceField.getText());
                    //Se o valor do lance for menor que o valor minimo de lance nada acontece
                    if(valor < valorAtual+minimo){
                        System.out.println("valor menor");
                        
                        }else{
                        
                        JSONObject json = new JSONObject();
                        json.put("sender", "client");
                        json.put("CPF", AuctionData.cpf);
                        json.put("action", "makeABid");
                        json.put("value", valor);
                
                        InetAddress group = InetAddress.getByName(AuctionData.multicastAddress);
                        String msg = json.toString();
                        IvParameterSpec iv = AESMethods.gerarIvUsandoAESKey(KeyLogger.secretKey);
                        String msgCifradaSTR = AESMethods.cifrarComAES(msg, KeyLogger.secretKey,iv);
                
                        byte[] buffer = msgCifradaSTR.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, AuctionData.multicastPort);
                        
                        //Envia os dados do lance
                        MulticastClient.socket.send(packet);
                        System.out.println("Lance enviado ao multicast: " + msg);
                    }
                
                
                }
                    
                
            } catch (IOException e) {
                System.err.println("Error in sending messages: " + e.getMessage());
            } catch (JSONException ex) {
            Logger.getLogger(LeilaoCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LeilaoCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        
    }//GEN-LAST:event_BotaoLanceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotaoLance;
    private javax.swing.JTextField LanceField;
    private javax.swing.JLabel cliente;
    private javax.swing.JLabel cpfBidder;
    private javax.swing.JLabel cronometro;
    private javax.swing.JLabel cronometro1;
    private javax.swing.JLabel desc_item;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel minimoField;
    private javax.swing.JLabel nome_item;
    private javax.swing.JLabel valor_item;
    // End of variables declaration//GEN-END:variables
}
