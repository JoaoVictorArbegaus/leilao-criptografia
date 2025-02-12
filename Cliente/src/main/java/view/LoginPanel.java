package view;

import network.UnicastClient;
import security.RSAMethods;

import javax.swing.*;
import java.awt.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import models.AuctionData;
import network.MulticastClient;
import org.json.JSONObject;
import security.AESMethods;
import security.KeyLogger;
import security.RegisteredUsers;

public class LoginPanel extends javax.swing.JPanel {

    RegisteredUsers registeredUsers = new RegisteredUsers();
    private UnicastClient unicastClient;
    private MulticastClient multicastClient;
    private PrivateKey privateKey;
    public JFrame frame;
    public LoginPanel(JFrame frame) {

        this.frame = frame;
        initComponents();
        
        
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        CpfField = new javax.swing.JTextField();
        BotaoEnviar = new javax.swing.JButton();

        jLabel1.setText("Insira seu CPF");

        CpfField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CpfFieldActionPerformed(evt);
            }
        });

        BotaoEnviar.setText("Solicitar Aceso");
        BotaoEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotaoEnviarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CpfField, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(BotaoEnviar)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CpfField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addComponent(BotaoEnviar)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void CpfFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CpfFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CpfFieldActionPerformed

    private void BotaoEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotaoEnviarActionPerformed
        String cpf = CpfField.getText().trim();

        //Checa se o campo de CPF esta vazio ou nao ao clicar o botao
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(null, "CPF nao pode estar vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
   
        
        try {
            //Seta o cpf da classe auction data com base no campo preenchido
            AuctionData.cpf = cpf;
            
            //Cria o texto a ser enviado ao Server
            String textoEnvio = "CONECTION_REQUEST"; 
            
            //Pega a chave Privada do cliente com base no cpf inserido (PrivateKey e em String)
            PrivateKey keyPrivadaClient = registeredUsers.returnPrivateKey(cpf);
            String keyPrivadaClientSTR = registeredUsers.returnPrivateKeySTR(cpf);
            System.out.println("Chave Privada do cliente: "+keyPrivadaClientSTR);
            
            
            //String da msg assinada
            String assinatura = RSAMethods.assinarMsg(textoEnvio, keyPrivadaClient); 
            System.out.println("Assinatura em string: "+assinatura);
            System.out.println("Tentando conexao...");
            System.out.println("---------------------------------------------");
   
            //Chama função que retorna um json com os dados do multicast
            String resposta = UnicastClient.conectaServerUnicast(cpf, textoEnvio, assinatura, "192.168.0.186", 12345);
            
            //Transforma em json os dados retornados da função
            JSONObject jsonDados = new JSONObject(resposta);
            
            String end = jsonDados.getString("Endereço");
            String porta = jsonDados.getString("Porta");
            String chave = jsonDados.getString("Chave Simetrica Decifrada");
            int portaInt = Integer.parseInt(porta);
            
            //Seta os dados da classe auction data e keyloggers com base nos campos do json
            AuctionData.multicastAddress = end;
            AuctionData.multicastPort = portaInt;
            KeyLogger.ChaveSimetricaSTR = chave;
            KeyLogger.secretKey = AESMethods.converteBase64ParaSecretKey(chave);
            
            JOptionPane.showMessageDialog(null, "Resposta do servidor: " + resposta, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // Transição para a próxima tela se a resposta for válida
                if (jsonDados.has("Endereço") && jsonDados.has("Porta")) {

                        // Fecha a janela atual
                        frame.dispose(); 
                        System.out.println("Frame fechado.");

                        // Criar nova janela para o leilão
                        JFrame novoFrame = new JFrame("Tela do Leilão");
                        novoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        novoFrame.setSize(600, 400);
                        novoFrame.setContentPane(new LeilaoCliente(novoFrame, cpf));
                        novoFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Erro ao entrar no grupo multicast.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
//    }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao enviar solicitação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

    }//GEN-LAST:event_BotaoEnviarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotaoEnviar;
    private javax.swing.JTextField CpfField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
