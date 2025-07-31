package tela;


import Model.Gerente ;
import Model.Pedido;
import Service.*;

import javax.swing.*;
import java.awt.* ;
import java.util.List;



public class InterfaceGerente extends JFrame {
     private JPanel sidebar;
     private JPanel painelGerente;
     private JPanel painelConteudo;


     private ServiceProduto serviceProduto;
     private ServicePedido servicePedido;
     private ServiceUsuario serviceUsuario;

     private Gerente gerente;

     public InterfaceGerente(ServiceProduto serviceProduto,ServicePedido servicePedido, ServiceUsuario serviceUsuario, Gerente gerente)
     {
          super("Painel do Gerente" + gerente.getNome() );
          this.serviceProduto = serviceProduto;
          this.servicePedido = servicePedido;
          this.serviceUsuario = serviceUsuario;
          this.gerente = gerente;

          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          setSize(800,600);
          setLocationRelativeTo(null);

          sidebar = new JPanel();
          sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
          sidebar.setBackground(new Color(240,240,240));
          sidebar.setPreferredSize(new Dimension(200,getHeight()));

          JLabel menuLabel = new JLabel("Menu do Gerente");
          menuLabel.setFont(new Font("Segoe UI",Font.BOLD,16));
          sidebar.add(menuLabel);
          sidebar.add(Box.createVerticalStrut(20));

          JButton botaoEstoque  = new JButton("Gerenciar Estoque");
          botaoEstoque.setAlignmentX(Component.CENTER_ALIGNMENT);
          sidebar.add(botaoEstoque);
          sidebar.add(Box.createVerticalStrut(10));

          JButton botaoPedidos  = new JButton("Ver Pedidos");
          botaoPedidos.setAlignmentX(Component.CENTER_ALIGNMENT);
          sidebar.add(botaoPedidos);
          sidebar.add(Box.createVerticalStrut(10));

          sidebar.add(new JSeparator()) ;
          sidebar.add(Box.createVerticalStrut(10));

          JButton botaoSair  = new JButton("Sair");
          botaoSair.setAlignmentX(Component.CENTER_ALIGNMENT);
          sidebar.add(botaoSair);

          painelConteudo = new JPanel() ;
          painelConteudo.setLayout(new BorderLayout());

          ///Acoes dos botoes, tem q implementar ainda

          //botaoEstoque.addActionListener(e -> {mostrarEstoqueDaLoja()}) ;
          //botaoPedidos.addActionListener(e -> {mostrarPedidosDaLoja()}) ;
          //botaoSair.addActionListener(e -> {System.exit(0);});


          JSplitPane splitPane = new JSplitPane();
          splitPane.setDividerSize(200);
          splitPane.setEnabled(false);

          add(splitPane);
          setVisible(true);







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
