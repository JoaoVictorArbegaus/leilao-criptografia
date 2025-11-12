# Leilão Multicast com Criptografia

Trabalho acadêmico desenvolvido como parte da disciplina de Segurança Computacional.  
O projeto implementa um **sistema de leilão eletrônico multicast**, com foco em **sigilo, integridade e autenticação** através de técnicas de **criptografia assimétrica** e **envelopamento digital**.

---

## 🧠 Objetivo

O objetivo do projeto é demonstrar o uso de **criptografia e comunicação segura em redes** para resolver o problema de **transmissão simultânea (multicast)** em um ambiente de leilão, garantindo que:

- As propostas enviadas pelos clientes sejam **confidenciais**;  
- A integridade dos dados seja preservada;  
- A autenticação entre os participantes seja verificada;  
- O processo de abertura e verificação dos lances ocorra de forma **segura e auditável**.

---

## ⚙️ Tecnologias e Conceitos

- **Java 17+**  
- **Sockets Multicast (UDP)**  
- **Criptografia Assimétrica (RSA)**  
- **Criptografia Simétrica (AES)**  
- **Envelopamento Digital** (chave simétrica cifrada com chave pública)  
- **Assinaturas Digitais**  
- **Serialização de Objetos e Comunicação de Mensagens**  

