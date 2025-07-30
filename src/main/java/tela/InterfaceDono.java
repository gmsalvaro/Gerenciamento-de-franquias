package tela;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import Model.Franquia;
import Model.Usuario;
import Service.*;

public class InterfaceDono extends JFrame {
    private JPanel sidebar;
    private JPanel painelConteudo;

    public InterfaceDono(ServiceFranquia serviceFranquia, ServiceLoja serviceLoja, ServicePedido servicePedido, ServiceProduto serviceProduto, ServiceUsuario serviceUsuario, Usuario usuario) {
        super("Painel do Dono");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Cria sidebar (esquerda)
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(230, 230, 250));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        // Adiciona botões ou menus na sidebar
        sidebar.add(new JLabel("Menu"));
        sidebar.add(Box.createVerticalStrut(10));
        JButton btnFranquias = new JButton("Ver Franquias");
        sidebar.add(btnFranquias);
        JButton btnSair = new JButton("Sair");
        sidebar.add(btnSair);

        // Painel de conteúdo (direita)
        painelConteudo = new JPanel();
        painelConteudo.setLayout(new BorderLayout());
        //Pegar a lista de Franquias no Service -> Arquivo
        List<Franquia> franquias = serviceFranquia.listarFranquias();

        // Quando clicar em "Ver Franquias"
        btnFranquias.addActionListener(e -> mostrarFranquias(franquias));
        btnSair.addActionListener(e -> System.exit(0)); //Implementar para voltar para o login

        // Usa JSplitPane para dividir a tela
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, painelConteudo);
        splitPane.setDividerLocation(200); // Largura da sidebar
        splitPane.setEnabled(false); // Impede redimensionamento

        add(splitPane);
        setVisible(true);
    }

    private void mostrarFranquias(List<Franquia> franquias) {
        painelConteudo.removeAll();

        JPanel painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));

        for (Franquia f : franquias) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setBackground(Color.WHITE);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            // Painel de informações
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);
            info.add(new JLabel("Nome: " + f.getNome()));
            info.add(new JLabel("Endereço: " + f.getEndereco()));

            // Botão de ação
            JButton btnGerenciar = new JButton("Gerenciar");
            btnGerenciar.setBackground(Color.WHITE);
            btnGerenciar.addActionListener(e -> {
                //new InterfaceGerenciarLojas(serviceLoja, );
            });

            // Adiciona componentes ao card
            card.add(info, BorderLayout.CENTER);
            card.add(btnGerenciar, BorderLayout.EAST);

            painelLista.add(card);
            painelLista.add(Box.createVerticalStrut(10)); // Espaçamento
        }

        JScrollPane scrollPane = new JScrollPane(painelLista);
        painelConteudo.add(scrollPane, BorderLayout.CENTER);

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

}