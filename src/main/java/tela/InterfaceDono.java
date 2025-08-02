package tela;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import Model.Franquia;
import Model.Gerente;
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

        JButton btnGerentes = criarBotaoMenu("Administrar Gerentes");
        sidebar.add(btnGerentes);

        sidebar.add(Box.createVerticalGlue());
        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);


        btnFranquias.addActionListener(e -> mostrarFranquias());
        btnSair.addActionListener(e -> {
            this.dispose();
            // Lógica para voltar ao Login
        });


        btnGerentes.addActionListener(e -> mostrarGerentes());

    }


    private void mostrarGerentes() {
        configurarPainelConteudo("Administrar Gerentes");
        painelConteudo.setLayout(new BorderLayout(10, 10));

        // Painel para a lista de cards de gerentes
        JPanel painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS));
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Gerente> gerentes = serviceManager.getServiceUsuario().listarGerentes();

        if (gerentes.isEmpty()) {
            painelListaCards.add(new JLabel("Nenhum gerente cadastrado."));
        } else {
            for (Gerente gerente : gerentes) {
                painelListaCards.add(criarCardGerente(gerente));
                painelListaCards.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(painelListaCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        painelConteudo.add(scrollPane, BorderLayout.CENTER);

        // Painel inferior com os botões de ação
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAdicionar = new JButton("Adicionar Gerente");
        JButton btnRemover = new JButton("Remover Gerente");
        JButton btnRebaixar = new JButton("Rebaixar para Vendedor");

        painelBotoesAcao.add(btnAdicionar);
        painelBotoesAcao.add(btnRemover);
        painelBotoesAcao.add(btnRebaixar);

        painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH);

        // Adiciona as ações aos botões
        btnAdicionar.addActionListener(e -> acaoAdicionarGerente());
        btnRemover.addActionListener(e -> acaoRemoverGerente(gerentes));
        btnRebaixar.addActionListener(e -> acaoRebaixarGerente(gerentes));

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private JPanel criarCardGerente(Gerente gerente) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBackground(new Color(245, 245, 245));
        // ... (estilização do card)

        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
        painelInfo.setOpaque(false);

        painelInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel lblNome = new JLabel(gerente.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblNome);

        JLabel lblEmail = new JLabel(gerente.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblEmail);

        // Mostra a loja que ele gerencia
        String statusLoja = serviceManager.getServiceLoja().buscarStatusLojaPorGerente(gerente);
        String textoLoja = "Loja: " + (statusLoja.equals("Gerente disponível") ? "Nenhuma" : statusLoja);

        JLabel lblLoja = new JLabel(textoLoja);
        lblLoja.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoja.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblLoja);


        JLabel lblFranquiaGerente = new JLabel((serviceManager.getServiceFranquia().getFranquiaDoGerente(gerente, serviceManager.getServiceLoja())).getNome());
        lblFranquiaGerente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFranquiaGerente.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblFranquiaGerente);

        card.add(painelInfo, BorderLayout.CENTER);
        return card;
    }

    private void acaoAdicionarGerente() {
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtCpf = new JTextField();
        JPasswordField txtSenha = new JPasswordField();

        JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Email:"));
        painelFormulario.add(txtEmail);
        painelFormulario.add(new JLabel("CPF:"));
        painelFormulario.add(txtCpf);
        painelFormulario.add(new JLabel("Senha:"));
        painelFormulario.add(txtSenha);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Adicionar Novo Gerente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                Gerente novoGerente = new Gerente(txtNome.getText(), txtEmail.getText(), new String(txtSenha.getPassword()), txtCpf.getText());
                serviceManager.getServiceUsuario().addUsuario(novoGerente);
                JOptionPane.showMessageDialog(this, "Gerente adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarGerentes(); // Atualiza a tela
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar gerente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoRemoverGerente(List<Gerente> gerentes) {
        if (gerentes.isEmpty()) { /* ... (mesma lógica do acaoRemoverLoja) ... */ return; }
        JComboBox<Gerente> comboBox = new JComboBox<>(gerentes.toArray(new Gerente[0]));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Gerente) {
                    // Se o objeto na lista for um Gerente, define o texto do item para o nome dele
                    setText(((Gerente) value).getNome());
                }
                return this;
            }
        });

        // ... (configurar o renderer do comboBox)
        int resultado = JOptionPane.showConfirmDialog(this, comboBox, "Selecione o Gerente para Remover", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            // Lógica de confirmação e chamada ao serviceManager.getServiceUsuario().removeUsuario(...)
            // Depois, chamar mostrarGerentes() para atualizar a tela
        }
    }

    private void acaoRebaixarGerente(List<Gerente> gerentes) {
        if (gerentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há gerentes para rebaixar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JComboBox<Gerente> comboBox = new JComboBox<>(gerentes.toArray(new Gerente[0]));

        // --- AQUI ESTÁ A CORREÇÃO ---
        // Configura o renderer para exibir o nome do gerente em vez do "código"
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Gerente) {
                    // Se o objeto na lista for um Gerente, define o texto do item para o nome dele
                    setText(((Gerente) value).getNome());
                }
                return this;
            }
        });
        // --- FIM DA CORREÇÃO ---

        int resultado = JOptionPane.showConfirmDialog(this, comboBox, "Selecione o Gerente para Rebaixar", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            Gerente gerenteSelecionado = (Gerente) comboBox.getSelectedItem();
            if (gerenteSelecionado == null) return; // Garante que algo foi selecionado

            try {
                serviceManager.getServiceUsuario().rebaixarGerenteParaVendedor(gerenteSelecionado);
                JOptionPane.showMessageDialog(this, "Gerente rebaixado para Vendedor com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarGerentes(); // Atualiza a tela
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao rebaixar gerente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBackground(new Color(245, 245, 245));

        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border bordaLinha = BorderFactory.createLineBorder(new Color(200, 200, 200));
        card.setBorder(BorderFactory.createCompoundBorder(bordaLinha, padding));

        // --- AQUI ESTÁ A MUDANÇA ---

        // 1. Cria um painel para agrupar as informações da esquerda (nome e n° de lojas)
        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS)); // Layout vertical
        painelInfo.setOpaque(false); // Fundo transparente

        // 2. Adiciona o nome da franquia a este novo painel
        JLabel lblNome = new JLabel(franquia.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda
        painelInfo.add(lblNome);

        // 3. Adiciona o número de lojas a este novo painel, debaixo do nome
        String textoLojas = franquia.getIdLojas().size() + " loja(s) cadastrada(s)";
        JLabel labelLojas = new JLabel(textoLojas);
        labelLojas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLojas.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda
        painelInfo.add(labelLojas);

        // 4. Adiciona o painel de informações ao CENTRO do card principal
        card.add(painelInfo, BorderLayout.CENTER);

        // 5. O botão "Gerenciar" agora fica sozinho na região LESTE
        JButton btnGerenciar = new JButton("Gerenciar");
        btnGerenciar.addActionListener(e -> {
            new InterfaceGerenciarLojas(serviceManager, franquia);
        });

        // Para centralizar o botão verticalmente, o colocamos dentro de outro painel
        JPanel painelBotao = new JPanel(new GridBagLayout());
        painelBotao.setOpaque(false);
        painelBotao.add(btnGerenciar);
        card.add(painelBotao, BorderLayout.EAST);

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