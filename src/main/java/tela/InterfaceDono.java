package tela;

import javax.swing.*;
import javax.swing.border.Border;
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
        contruirSidebar();

        setVisible(true);

    }


    @Override
    public void contruirSidebar() {
        List<Franquia> f = new ArrayList<>();
        JLabel lblTitulo = new JLabel("Menu Principal");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTitulo);
        sidebar.add(Box.createVerticalStrut(20));

        JButton btnFranquias = criarBotaoMenu("Gerenciar Franquias");
        sidebar.add(btnFranquias);

        sidebar.add(Box.createVerticalGlue());
        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);


        btnFranquias.addActionListener(e -> mostrarFranquias());
        btnSair.addActionListener(e -> {
            this.dispose();
            // Lógica para voltar ao Login
        });
    }


    private void mostrarFranquias() {
        // 1. Prepara o painel de conteúdo
        configurarPainelConteudo("Gerenciar Franquias"); // Limpa o painel e adiciona o título
        painelConteudo.setLayout(new BorderLayout(10, 10)); // Define o layout principal

        // 2. Cria o painel que conterá a lista de cards de franquias
        JPanel painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS)); // Um card embaixo do outro
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 3. Busca os dados e cria um card para cada franquia
        List<Franquia> listaDeFranquias = serviceManager.getServiceFranquia().listarFranquias();
        if (listaDeFranquias.isEmpty()) {
            painelListaCards.add(new JLabel("Nenhuma franquia cadastrada."));
        } else {
            for (Franquia franquia : listaDeFranquias) {
                painelListaCards.add(criarCardFranquia(franquia));
                painelListaCards.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento entre os cards
            }
        }

        // 4. Coloca a lista de cards dentro de uma barra de rolagem
        JScrollPane scrollPane = new JScrollPane(painelListaCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        painelConteudo.add(scrollPane, BorderLayout.CENTER); // Adiciona a área de rolagem no centro

        // 5. Cria o painel inferior para os botões de ação
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Alinha botões à direita
        JButton btnAdicionar = new JButton("Adicionar Franquia");
        JButton btnRemover = new JButton("Remover Franquia");

        painelBotoesAcao.add(btnAdicionar);
        painelBotoesAcao.add(btnRemover);

        painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH); // Adiciona o painel de botões na parte de baixo

        // Ações dos botões de Adicionar e Remover
        btnAdicionar.addActionListener(e -> acaoAdicionarFranquia());

        btnRemover.addActionListener(e -> {
            // Lógica para remover uma franquia selecionada
            JOptionPane.showMessageDialog(this, "Funcionalidade 'Remover' a ser implementada.");
        });

        // Revalida e redesenha o painel para exibir as alterações
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    /**
     * Cria um JPanel estilizado (card) para representar uma única franquia.
     *
     * @param franquia A franquia a ser exibida no card.
     * @return um JPanel configurado como um card.
     */
    private JPanel criarCardFranquia(Franquia franquia) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Altura fixa para os cards
        card.setBackground(new Color(245, 245, 245));

        // Borda estilizada para o card
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border bordaLinha = BorderFactory.createLineBorder(new Color(200, 200, 200));
        card.setBorder(BorderFactory.createCompoundBorder(bordaLinha, padding));

        // Nome da franquia
        JLabel lblNome = new JLabel(franquia.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.add(lblNome, BorderLayout.CENTER);

        // Botão para gerenciar a franquia específica
        JButton btnGerenciar = new JButton("Gerenciar");
        card.add(btnGerenciar, BorderLayout.EAST);

        // Ação do botão "Gerenciar"
        btnGerenciar.addActionListener(e -> {
            new InterfaceGerenciarLojas(serviceManager, franquia);
        });

        return card;
    }

    private void acaoAdicionarFranquia() {
        // Cria os campos de texto para o formulário
        JTextField txtNome = new JTextField();
        JTextField txtEndereco = new JTextField();
        JTextField txtTelefone = new JTextField();

        // Cria um painel para organizar os labels e os campos
        JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
        painelFormulario.add(new JLabel("Nome da Franquia:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Endereço:"));
        painelFormulario.add(txtEndereco);
        painelFormulario.add(new JLabel("Telefone:"));
        painelFormulario.add(txtTelefone);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Adicionar Nova Franquia",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String nome = txtNome.getText().trim();
            String endereco = txtEndereco.getText().trim();
            String telefone = txtTelefone.getText().trim();

            // Validação simples para garantir que os campos não estão vazios
            if (nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe a execução se a validação falhar
            }

            try {
                // Cria a nova franquia e a adiciona através do serviço
                Franquia novaFranquia = new Franquia(nome, endereco, telefone);
                serviceManager.getServiceFranquia().addFranquia(novaFranquia);

                JOptionPane.showMessageDialog(this, "Franquia '" + nome + "' adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // ATUALIZA A TELA para exibir a nova franquia
                mostrarFranquias();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar franquia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
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