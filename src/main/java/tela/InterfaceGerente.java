package tela;


import Model.*;
import Service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.* ;
import java.util.ArrayList;
import java.util.List;


public class InterfaceGerente extends PainelPrincipal {
     private final ServiceManager serviceManager;
     private final Gerente gerente;

     public InterfaceGerente(ServiceManager serviceManager, Gerente gerente) {
          super("Painel Gerente - " + gerente.getNome());
          this.serviceManager = serviceManager;
          this.gerente = gerente;

          setVisible(true);
          mostrarEstoqueDaLoja();
     }


     private void mostrarEstoqueDaLoja() {
          configurarPainelConteudo("Estoque da Loja");
          List<Produto> produtos = serviceManager.getServiceProduto().listarPorIDLoja(gerente.getIdloja());
          JPanel painelLista = new JPanel();
          painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));

          for (Produto p : produtos) {
               JPanel card = new JPanel(new BorderLayout(10, 10));
               card.setBorder(BorderFactory.createCompoundBorder(
                       BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                       new EmptyBorder(10, 10, 10, 10)));


               card.add(new JLabel(p.getNome()), BorderLayout.CENTER);
               card.add(new JLabel("Qtd: " + p.getEstoque()), BorderLayout.EAST);
               painelLista.add(card);
               painelLista.add(Box.createVerticalStrut(5));
          }

     }

     private void mostrarPedidosDaLoja() {
          configurarPainelConteudo("Pedidos da Loja");
          painelConteudo.add(new JLabel("Funcionalidade de pedidos em construção."));
          painelConteudo.revalidate();
          painelConteudo.repaint();
     }

     private void mostrarVendedoresDaLoja() {
          configurarPainelConteudo("Vendedores da Loja");
          var loja = serviceManager.getServiceLoja().getLojaById(gerente.getIdloja());
          if (loja == null) {
               painelConteudo.add(new JLabel("Erro: Loja não encontrada."));
               return;
          }
          List<Usuario> vendedores = serviceManager.getServiceUsuario().getUsuariosPorLoja(loja);
          JPanel painelLista = new JPanel();
          painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));

          for (Usuario v : vendedores) {
               JPanel card = new JPanel(new BorderLayout(10, 10));
               card.setBorder(BorderFactory.createCompoundBorder(
                       BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                       new EmptyBorder(10, 10, 10, 10)
               ));

               card.add(new JLabel(v.getNome()), BorderLayout.CENTER);
               painelLista.add(card);
               painelLista.add(Box.createVerticalStrut(5));
          }

     }

     @Override
     public void contruirSidebar() {
          sidebar.add(new JLabel("Menu do Gerente"));
          sidebar.add(Box.createVerticalStrut(20));

          JButton botaoEstoque = criarBotaoMenu("Estoque");
          JButton botaoPedidos = criarBotaoMenu("Pedidos");
          JButton botaoVendedores = criarBotaoMenu("Vendedores");
          JButton botaoSair = criarBotaoMenu("Sair");

          sidebar.add(botaoEstoque);
          sidebar.add(Box.createVerticalStrut(10));
          sidebar.add(botaoPedidos);
          sidebar.add(Box.createVerticalStrut(10));
          sidebar.add(botaoVendedores);
          sidebar.add(Box.createVerticalGlue());
          sidebar.add(botaoSair);


          botaoEstoque.addActionListener(e -> mostrarEstoqueDaLoja());
          botaoPedidos.addActionListener(e -> mostrarPedidosDaLoja());
          botaoVendedores.addActionListener(e -> mostrarVendedoresDaLoja());
          botaoSair.addActionListener(e -> {
               this.dispose();

          });
     }
}


//JFrame janelaGerente = new JFrame();
//         janelaGerente.setTitle("Interface Gerente");
//         janelaGerente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         janelaGerente.setVisible(true);
//         janelaGerente.setLayout(null);
//
//         janelaGerente.setBounds(850, 250, 1280, 720);
//
//
////Label's
//JLabel userGerente = new JLabel("Usuario: " + "111.333.666-77");
//         userGerente.setBounds(100,50,200,50);
//         janelaGerente.add(userGerente);
//
//JLabel senhaGerente = new JLabel("senha: " + "********");
//         senhaGerente.setBounds(300,50,200,50);
//         janelaGerente.add(senhaGerente);
