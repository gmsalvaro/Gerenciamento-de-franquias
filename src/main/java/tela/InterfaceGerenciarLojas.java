package tela;

import Model.Franquia;
import Model.Gerente;
import Model.Loja;
import Service.CriaGerente;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;

// Passo 1: Fazer esta classe estender a mesma base da InterfaceDono
public class InterfaceGerenciarLojas extends PainelPrincipal {

    private final ServiceManager serviceManager;
    private final Franquia franquia; // A franquia que estamos gerenciando

    public InterfaceGerenciarLojas(ServiceManager serviceManager, Franquia franquia) {
        super("Gerenciando Lojas da Franquia: " + franquia.getNome());
        this.serviceManager = serviceManager;
        this.franquia = franquia;
        contruirSidebar();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
        mostrarLojas();
        setVisible(true);
    }

    @Override
    public void contruirSidebar() {
        // Passo 2: Alterar a sidebar para o padrão desejado (branca e com botão Voltar)
        sidebar.setBackground(Color.WHITE); // Fundo branco

        JLabel lblTituloSidebar = new JLabel(franquia.getNome());
        lblTituloSidebar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloSidebar.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTituloSidebar);
        sidebar.add(Box.createVerticalStrut(30));

        JButton btnVerLojas = criarBotaoMenu("Ver Lojas");
        sidebar.add(btnVerLojas);

        JButton btnDesempenho = criarBotaoMenu("Desempenho da Franquia");
        sidebar.add(btnDesempenho);

        sidebar.add(Box.createVerticalGlue()); // Empurra o botão para baixo

        JButton btnVoltar = criarBotaoMenu("Voltar");
        sidebar.add(btnVoltar);

        // Ação do botão Voltar: fecha a janela atual, retornando à anterior

        btnVerLojas.addActionListener(e -> mostrarLojas());
        btnDesempenho.addActionListener(e -> mostrarDesempenhoFranquia());
        btnVoltar.addActionListener(e -> this.dispose());
    }


    private void mostrarDesempenhoFranquia() {
        configurarPainelConteudo("Desempenho Financeiro: " + franquia.getNome());
        painelConteudo.setLayout(new BorderLayout());

        JPanel painelMetricas = new JPanel();
        // Usando GridBagLayout para um alinhamento mais limpo
        painelMetricas.setLayout(new GridBagLayout());
        painelMetricas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // --- Métricas Financeiras ---
        // TODO: A lógica de cálculo deve vir dos seus serviços.
        // Por enquanto, usaremos valores de exemplo.

        // Faturamento Bruto Total
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTituloFaturamento = new JLabel("Faturamento Bruto Total:");
        lblTituloFaturamento.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloFaturamento, gbc);

        gbc.gridx = 1;
        // double faturamentoTotal = serviceManager.getServiceRelatorio().calcularFaturamentoTotal(franquia);
        JLabel lblValorFaturamento = new JLabel(String.format("R$ %.2f", 31501.60));
        lblValorFaturamento.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorFaturamento, gbc);

        // Número Total de Pedidos
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTituloPedidos = new JLabel("Número Total de Pedidos:");
        lblTituloPedidos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloPedidos, gbc);

        gbc.gridx = 1;
        // int totalPedidos = serviceManager.getServiceRelatorio().contarPedidosTotais(franquia);
        JLabel lblValorPedidos = new JLabel("0");
        lblValorPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorPedidos, gbc);

        // Ticket Médio
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblTituloTicket = new JLabel("Ticket Médio:");
        lblTituloTicket.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloTicket, gbc);

        gbc.gridx = 1;
        // double ticketMedio = faturamentoTotal / totalPedidos;
        JLabel lblValorTicket = new JLabel("R$ 0,00");
        lblValorTicket.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorTicket, gbc);

        painelConteudo.add(painelMetricas, BorderLayout.NORTH);

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void mostrarLojas() {
        // Passo 3: Reestruturar o painel de conteúdo com BorderLayout
        configurarPainelConteudo("Lojas da Franquia"); // Limpa o painel e adiciona o título
        painelConteudo.setLayout(new BorderLayout(10, 10));

        // Painel para os cards com rolagem
        JPanel painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS));
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Loja> lojas = serviceManager.getServiceLoja().listarPorIDFranquia(franquia.getId());

        if (lojas.isEmpty()) {
            painelListaCards.add(new JLabel("Nenhuma loja cadastrada para esta franquia."));
        } else {
            for (Loja loja : lojas) {
                painelListaCards.add(criarCardLoja(loja));
                painelListaCards.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(painelListaCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        painelConteudo.add(scrollPane, BorderLayout.CENTER);

        // Painel inferior com os botões de ação globais
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAdicionar = new JButton("Adicionar Loja");
        JButton btnRemover = new JButton("Remover Loja");
        painelAcoes.add(btnAdicionar);
        painelAcoes.add(btnRemover);

        painelConteudo.add(painelAcoes, BorderLayout.SOUTH);

        // Adiciona as funcionalidades aos novos botões
        btnAdicionar.addActionListener(e -> acaoAdicionarLoja());
        btnRemover.addActionListener(e -> acaoRemoverLoja(lojas));

        // Atualiza a tela
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }


    private JPanel criarCardLoja(Loja loja) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(250, 250, 250));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // --- Painel de informações da loja (com as alterações) ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Nome da loja
        JLabel nomeLoja = new JLabel(loja.getNome());
        nomeLoja.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nomeLoja.setAlignmentX(Component.LEFT_ALIGNMENT); // Garante alinhamento à esquerda
        infoPanel.add(nomeLoja);
        infoPanel.add(Box.createVerticalStrut(5));

        // Endereço
        JLabel enderecoLoja = new JLabel("<html><b>Endereço:</b> " + loja.getEndereco() + "</html>");
        enderecoLoja.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        enderecoLoja.setAlignmentX(Component.LEFT_ALIGNMENT); // Garante alinhamento à esquerda
        infoPanel.add(enderecoLoja);

      //  infoPanel.add(Box.createVerticalStrut(15));

        // Faturamento
        double faturamentoExemplo = 15750.80;
        JLabel labelFaturamento = new JLabel(String.format("<html><b>Faturamento:</b> R$ %.2f</html>", faturamentoExemplo));
        labelFaturamento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelFaturamento.setAlignmentX(Component.LEFT_ALIGNMENT); // Garante alinhamento à esquerda
        infoPanel.add(labelFaturamento);

        // Pedidos Realizados (LINHA CORRIGIDA)
        int numeroPedidos = (loja.getIdPedidos() != null) ? loja.getIdPedidos().size() : 0;
        JLabel labelNumeroPedidos = new JLabel(String.format("<html><b>Pedidos Realizados:</b> %d</html>", numeroPedidos));
        labelNumeroPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelNumeroPedidos.setAlignmentX(Component.LEFT_ALIGNMENT); // Garante alinhamento à esquerda
        infoPanel.add(labelNumeroPedidos);

        String nomeGerente = serviceManager.getServiceLoja().getNomeGerenteDaLoja(loja, serviceManager);
        char iconeGerente;
        String statusGerente;

        if(nomeGerente != null){
            iconeGerente = '✅';
            statusGerente = nomeGerente;
        } else{
            iconeGerente = '❌';
            statusGerente = "Loja sem gerente!";
        }

        JLabel labelTemGerente = new JLabel("<html><b>Gerente:</b> " +iconeGerente +" "+ statusGerente +"</html>");
        labelTemGerente.setFont(getFonteDeEmoji(labelTemGerente.getFont()));
        labelTemGerente.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(labelTemGerente);

        // Painel de botões de ação específicos do card
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new BoxLayout(botoesPanel, BoxLayout.Y_AXIS));
        botoesPanel.setOpaque(false);

        JButton btnGerenciarUsuarios = new JButton("Gerenciar Usuários");
        btnGerenciarUsuarios.addActionListener(e -> new InterfaceGerenciarUsuario(loja, serviceManager, franquia).setVisible(true));
        botoesPanel.add(btnGerenciarUsuarios);

        JButton btnDesignarGerente = new JButton("Designar Gerente");
        btnDesignarGerente.addActionListener(e -> acaoDesignarGerente(loja)); // Chama o novo metodo de ação
        botoesPanel.add(btnDesignarGerente);

        JButton btnFinanceiro = new JButton("Financeiro");
        btnFinanceiro.addActionListener(e -> JOptionPane.showMessageDialog(this, "Função em desenvolvimento", "Aviso", JOptionPane.WARNING_MESSAGE));
        botoesPanel.add(btnFinanceiro);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(botoesPanel, BorderLayout.EAST);

        return card;
    }


    private Font getFonteDeEmoji(Font fonteBase) {
        String os = System.getProperty("os.name").toLowerCase();
        String nomeFonte;

        if (os.contains("win")) {
            nomeFonte = "Segoe UI Emoji";
        } else if (os.contains("mac")) {
            nomeFonte = "Apple Color Emoji";
        } else {
            // Assume Linux ou outro Unix-like como padrão
            nomeFonte = "Noto Color Emoji";
        }

        return new Font(nomeFonte, fonteBase.getStyle(), fonteBase.getSize());
    }

    private void acaoDesignarGerente(Loja loja) {
        // Busca a lista de gerentes disponíveis (não os que já têm loja)
        List<Gerente> gerentesDisponiveis = serviceManager.getServiceUsuario().listarGerentesDisponiveis(serviceManager.getServiceLoja());

        // --- Montagem do Painel para o Diálogo ---
        JPanel painelDialogo = new JPanel(new BorderLayout(5, 5));
        painelDialogo.add(new JLabel("Selecione um gerente disponível ou crie um novo:"), BorderLayout.NORTH);

        JComboBox<Gerente> comboBoxGerentes = new JComboBox<>(gerentesDisponiveis.toArray(new Gerente[0]));

        // Se não houver gerentes disponíveis, desabilita a combobox
        if (gerentesDisponiveis.isEmpty()) {
            comboBoxGerentes.setEnabled(false);
        }

        // Configura o renderer para exibir o nome
        comboBoxGerentes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Gerente) {
                    setText(((Gerente) value).getNome());
                }
                return this;
            }
        });
        painelDialogo.add(comboBoxGerentes, BorderLayout.CENTER);

        // --- Criação do Diálogo com Botões Customizados ---
        String[] options = {"Designar Selecionado", "Criar Novo Gerente", "Cancelar"};

        int resultado = JOptionPane.showOptionDialog(
                this,                             // Janela pai
                painelDialogo,                    // Painel com a JComboBox
                "Designar Gerente para a Loja",   // Título
                JOptionPane.DEFAULT_OPTION,       // Tipo de opção
                JOptionPane.QUESTION_MESSAGE,     // Ícone
                null,                             // Ícone customizado (nenhum)
                options,                          // Os textos dos botões
                options[0]                        // Botão padrão
        );

        Gerente gerenteParaDesignar = null;

        // --- Lógica para tratar a escolha do usuário ---
        if (resultado == 0) { // Botão "Designar Selecionado"
            if (comboBoxGerentes.isEnabled()) {
                gerenteParaDesignar = (Gerente) comboBoxGerentes.getSelectedItem();
                if (gerenteParaDesignar == null) {
                    JOptionPane.showMessageDialog(this, "Nenhum gerente foi selecionado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Não há gerentes disponíveis para selecionar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

        } else if (resultado == 1) { // Botão "Criar Novo Gerente"
            // Chama a NOVA versão do método, passando a loja de destino
            gerenteParaDesignar = CriaGerente.criarNovoGerente(this, serviceManager, loja);

            // Como a vinculação já foi feita dentro do helper, não precisamos mais do bloco 'if (gerenteParaDesignar != null)'
            // Apenas atualizamos a tela
            mostrarLojas();
            return; // Encerra o método aqui, pois o fluxo já foi concluído.

        } else { // Usuário fechou ou cancelou
            return;
        }

// O bloco de código abaixo será executado apenas se o usuário escolher "Designar Selecionado"
        if (gerenteParaDesignar != null) {
            try {
                serviceManager.getServiceLoja().designarGerenteParaLoja(gerenteParaDesignar, loja, serviceManager.getServiceUsuario());
                JOptionPane.showMessageDialog(this, "Gerente '" + gerenteParaDesignar.getNome() + "' designado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarLojas(); // Atualiza a tela
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Erro ao designar gerente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoAdicionarLoja() {
        // A lógica é a mesma que implementamos para "Adicionar Franquia"
        JTextField txtNome = new JTextField();
        JTextField txtEndereco = new JTextField();

        JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
        painelFormulario.add(new JLabel("Nome da Loja:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Endereço:"));
        painelFormulario.add(txtEndereco);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Adicionar Nova Loja",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String nome = txtNome.getText().trim();
            String endereco = txtEndereco.getText().trim();

            if (nome.isEmpty() || endereco.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e Endereço são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }


            Gerente gerenteDesignado = null;
            while (gerenteDesignado == null) {
                List<Gerente> gerentesDisponiveis = serviceManager.getServiceUsuario().listarGerentesDisponiveis(serviceManager.getServiceLoja());

                // Opções para o diálogo: a lista de gerentes + a opção de criar um novo
                Object[] opcoes;
                if (gerentesDisponiveis.isEmpty()) {
                    opcoes = new Object[]{"Criar Novo Gerente"};
                } else {
                    opcoes = new Object[gerentesDisponiveis.size() + 1];
                    for (int i = 0; i < gerentesDisponiveis.size(); i++) {
                        opcoes[i] = gerentesDisponiveis.get(i).getNome();
                    }
                    opcoes[gerentesDisponiveis.size()] = "Criar Novo Gerente";
                }

                String escolha = (String) JOptionPane.showInputDialog(this,
                        "Selecione um gerente responsável para a nova loja:",
                        "Designar Gerente (Etapa 2 de 2)",
                        JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

                if (escolha == null) {
                    return; // Usuário cancelou
                }

                if (escolha.equals("Criar Novo Gerente")) {
                    // Reutiliza a lógica de adicionar gerente que já existe na InterfaceDono
                    // (Poderia ser refatorado para um metodo auxiliar estático no futuro)
                    gerenteDesignado = acaoCriarNovoGerente();
                } else {
                    // Encontra o gerente selecionado na lista
                    for (Gerente g : gerentesDisponiveis) {
                        if (g.getNome().equals(escolha)) {
                            gerenteDesignado = g;
                            break;
                        }
                    }
                }
            }

            try {
                Loja novaLoja = new Loja(nome, endereco, franquia.getId());
                serviceManager.getServiceLoja().addLoja(novaLoja, franquia, gerenteDesignado);
                serviceManager.getServiceFranquia().atualizar(franquia); // Atualiza a franquia para salvar a nova associação de loja

                JOptionPane.showMessageDialog(this, "Loja adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarLojas(); // Atualiza a tela
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar loja: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Gerente acaoCriarNovoGerente() {
        return CriaGerente.criarNovoGerente(this, serviceManager);
    }

    private void acaoRemoverLoja(List<Loja> lojas) {
        if (lojas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há lojas para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Usa um JComboBox para que o usuário escolha qual loja remover
        JComboBox<Loja> comboBoxLojas = new JComboBox<>(lojas.toArray(new Loja[0]));
        comboBoxLojas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Loja) {
                    setText(((Loja) value).getNome());
                }
                return this;
            }
        });

        int resultado = JOptionPane.showConfirmDialog(this, comboBoxLojas, "Selecione a Loja para Remover",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            Loja lojaSelecionada = (Loja) comboBoxLojas.getSelectedItem();
            if (lojaSelecionada == null) return;

            int confirmacaoFinal = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover a loja '" + lojaSelecionada.getNome() + "'?\nEsta ação não pode ser desfeita.",
                    "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirmacaoFinal == JOptionPane.YES_OPTION) {
                try {
                    serviceManager.getServiceLoja().removerLoja(lojaSelecionada, serviceManager.getServiceFranquia()); // Remove a loja
                    serviceManager.getServiceFranquia().atualizar(franquia); // Salva a mudança na franquia

                    JOptionPane.showMessageDialog(this, "Loja removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    mostrarLojas(); // Atualiza a tela
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao remover loja: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}