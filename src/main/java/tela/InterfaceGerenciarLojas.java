package tela;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import Model.Franquia;
import Model.Loja;
import Service.ServiceLoja;
import Service.ServiceManager;
import Service.ServicePedido;
import Service.ServiceProduto;
import Service.ServiceUsuario;
import exception.persistencia.PersistenciaException;

public class InterfaceGerenciarLojas extends JFrame {
    private JPanel sidebar;
    private JPanel painelConteudo;

    private ServiceManager serviceManager;
    private Franquia franquia;

    public InterfaceGerenciarLojas(ServiceManager serviceManager, Franquia franquia) {
        super("Painel do Dono");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        this.serviceManager = serviceManager;
        this.franquia = franquia;

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 44, 52));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel lblTituloSidebar = new JLabel("<html><font color='white'><b>Menu Principal</b></font></html>");
        lblTituloSidebar.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloSidebar.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTituloSidebar);
        sidebar.add(Box.createVerticalStrut(30));

        JButton btnVerLojas = new JButton("Ver Lojas");
        btnVerLojas.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerLojas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnVerLojas.setBackground(new Color(75, 110, 175));
        btnVerLojas.setForeground(Color.WHITE);
        btnVerLojas.setFont(new Font("Arial", Font.BOLD, 14));
        btnVerLojas.setFocusPainted(false);
        btnVerLojas.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JButton btnSair = new JButton("Sair");
        btnSair.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSair.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSair.setBackground(new Color(200, 70, 70));
        btnSair.setForeground(Color.WHITE);
        btnSair.setFont(new Font("Arial", Font.BOLD, 14));
        btnSair.setFocusPainted(false);
        btnSair.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        sidebar.add(btnVerLojas);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnSair);
        sidebar.add(Box.createVerticalStrut(10));

        painelConteudo = new JPanel();
        painelConteudo.setLayout(new BorderLayout());
        painelConteudo.setBackground(new Color(240, 242, 245));

        btnVerLojas.addActionListener(e -> mostrarLojas());
        btnSair.addActionListener(e -> System.exit(0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, painelConteudo);
        splitPane.setDividerLocation(220);
        splitPane.setEnabled(false);

        add(splitPane);
        setVisible(true);

        mostrarLojas();
    }

    private void mostrarLojas() {
        painelConteudo.removeAll();
        List<Loja> lojas = serviceManager.getServiceLoja().listarPorIDFranquia(franquia.getId());

        JPanel painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelLista.setBackground(new Color(255, 255, 255));

        if (lojas.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.add(new JLabel("Nenhuma loja encontrada para exibir."));
            painelLista.add(emptyPanel);
        } else {
            for (Loja lojaAtual : lojas) {
                JPanel card = new JPanel(new BorderLayout(10, 0));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                card.setBackground(new Color(250, 250, 250));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
                card.setMinimumSize(new Dimension(300, 150));

                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setOpaque(false);
                infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel nomeLoja = new JLabel("<html><b>Nome:</b> " + lojaAtual.getNome() + "</html>");
                nomeLoja.setFont(new Font("Arial", Font.BOLD, 16));
                JLabel enderecoLoja = new JLabel("<html><b>Endereço:</b> " + lojaAtual.getEndereco() + "</html>");
                enderecoLoja.setFont(new Font("Arial", Font.PLAIN, 12));

                // Novas informações de desempenho
                int numeroPedidos = (lojaAtual.getIdPedidos() != null) ? lojaAtual.getIdPedidos().size() : 0;
                JLabel labelNumeroPedidos = new JLabel("<html><b>Número de Pedidos:</b> " + numeroPedidos + "</html>");
                labelNumeroPedidos.setFont(new Font("Arial", Font.PLAIN, 12));

                JLabel labelArrecadacao = new JLabel("<html><b>Arrecadação Total:</b> A calcular...</html>"); // criar função depois no Service !
                labelArrecadacao.setFont(new Font("Arial", Font.PLAIN, 12));


                infoPanel.add(nomeLoja);
                infoPanel.add(Box.createVerticalStrut(5));
                infoPanel.add(enderecoLoja);
                infoPanel.add(Box.createVerticalStrut(10));
                infoPanel.add(labelNumeroPedidos);
                infoPanel.add(Box.createVerticalStrut(5));
                infoPanel.add(labelArrecadacao);
                infoPanel.add(Box.createVerticalGlue());

                JPanel botoesPanel = new JPanel();
                botoesPanel.setLayout(new BoxLayout(botoesPanel, BoxLayout.Y_AXIS));
                botoesPanel.setOpaque(false);
                botoesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));

                JButton btnGerenciarUsuarios = new JButton("Gerenciar Usuários");
                btnGerenciarUsuarios.setPreferredSize(new Dimension(160, 30));
                btnGerenciarUsuarios.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnGerenciarUsuarios.addActionListener(e -> {
                    new InterfaceGerenciarUsuario(lojaAtual, serviceManager, franquia).setVisible(true);
                });


                JButton btnGerenciarProdutos = new JButton("Gerenciar Produtos");
                btnGerenciarProdutos.setPreferredSize(new Dimension(160, 30));
                btnGerenciarProdutos.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnGerenciarProdutos.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "Funcionalidade de Gerenciar Produtos em construção.", "Em Breve", JOptionPane.INFORMATION_MESSAGE);
                });

                JButton btnGerenciarPedidos = new JButton("Gerenciar Pedidos");
                btnGerenciarPedidos.setPreferredSize(new Dimension(160, 30));
                btnGerenciarPedidos.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnGerenciarPedidos.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "Funcionalidade de Gerenciar Pedidos em construção.", "Em Breve", JOptionPane.INFORMATION_MESSAGE);
                });

                botoesPanel.add(btnGerenciarUsuarios);
                botoesPanel.add(Box.createVerticalStrut(5));
                botoesPanel.add(btnGerenciarProdutos);
                botoesPanel.add(Box.createVerticalStrut(5));
                botoesPanel.add(btnGerenciarPedidos);

                card.add(infoPanel, BorderLayout.CENTER);
                card.add(botoesPanel, BorderLayout.EAST);

                painelLista.add(card);
                painelLista.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scrollPane = new JScrollPane(painelLista);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        painelLista.add(Box.createVerticalGlue());

        painelConteudo.add(scrollPane, BorderLayout.CENTER);

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
}