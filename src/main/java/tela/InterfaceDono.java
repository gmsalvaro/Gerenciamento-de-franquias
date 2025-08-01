package tela;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import Model.Franquia;
import Model.Usuario;
import Service.*;

public class InterfaceDono extends PainelPrincipal {
    private ServiceManager serviceManager;

    public InterfaceDono(ServiceManager serviceManager, Usuario usuario) {
        super("Painel do Dono - " + usuario.getNome());
        this.serviceManager = serviceManager;
        setVisible(true);

    }


    @Override
    public void contruirSidebar() {
        List<Franquia> f = new ArrayList<>();
        sidebar.add(new JLabel("Menu Principal"));
        sidebar.add(Box.createVerticalStrut(20));

        JButton btnFranquias = criarBotaoMenu("Gerenciar Franquias");
        sidebar.add(btnFranquias);

        sidebar.add(Box.createVerticalGlue());
        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);


        btnFranquias.addActionListener(e -> mostrarFranquias(f));
        btnSair.addActionListener(e -> {
            this.dispose();
            // Lógica para voltar ao Login
        });
    }


    private void mostrarFranquias(List<Franquia> franquias) {
        configurarPainelConteudo("Gerenciar Franquias");
        List<Franquia> Franquias = serviceManager.getServiceFranquia().listarFranquias();

        JPanel painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));

        for (Franquia f : franquias) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            // ... (resto da lógica para criar o card da franquia)
            card.add(new JLabel("  " + f.getNome()));
            painelLista.add(card);
            painelLista.add(Box.createVerticalStrut(5));
        }


    }
}










//        super("Painel do Dono");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(800, 600);
//        setLocationRelativeTo(null);
//
//        // Cria sidebar (esquerda)
//        sidebar = new JPanel();
//        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
//        sidebar.setBackground(new Color(230, 230, 250));
//        sidebar.setPreferredSize(new Dimension(200, getHeight()));
//
//        // Adiciona botões ou menus na sidebar
//        sidebar.add(new JLabel("Menu"));
//        sidebar.add(Box.createVerticalStrut(10));
//        JButton btnFranquias = new JButton("Ver Franquias");
//        sidebar.add(btnFranquias);
//        JButton btnSair = new JButton("Sair");
//        sidebar.add(btnSair);
//
//        // Painel de conteúdo (direita)
//        painelConteudo = new JPanel();
//        painelConteudo.setLayout(new BorderLayout());
//        //Pegar a lista de Franquias no Service -> Arquivo
//        List<Franquia> franquias = serviceFranquia.listarFranquias();
//
//        // Quando clicar em "Ver Franquias"
//        btnFranquias.addActionListener(e -> mostrarFranquias(franquias));
//        btnSair.addActionListener(e -> System.exit(0)); //Implementar para voltar para o login
//
//        // Usa JSplitPane para dividir a tela
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, painelConteudo);
//        splitPane.setDividerLocation(200); // Largura da sidebar
//        splitPane.setEnabled(false); // Impede redimensionamento
//
//        add(splitPane);
//        setVisible(true);