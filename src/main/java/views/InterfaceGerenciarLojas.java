package views;

import exception.autenticacao.UsuarioInvalidoException;
import model.Franquia;
import model.Gerente;
import model.Loja;
import utils.CriaGerente;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class InterfaceGerenciarLojas extends PainelPrincipal {

    private final ServiceManager serviceManager;
    private final Franquia franquia;

    public InterfaceGerenciarLojas(ServiceManager serviceManager, Franquia franquia) {
        super("Gerenciando Lojas da Franquia: " + franquia.getNome());
        this.serviceManager = serviceManager;
        this.franquia = franquia;
        contruirSidebar();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mostrarLojas();
        setVisible(true);
    }

    @Override
    public void contruirSidebar() {
        sidebar.setBackground(Color.WHITE);

        JLabel lblTituloSidebar = new JLabel(franquia.getNome());
        lblTituloSidebar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloSidebar.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTituloSidebar);
        sidebar.add(Box.createVerticalStrut(30));

        JButton btnVerLojas = criarBotaoMenu("Ver Lojas");
        sidebar.add(btnVerLojas);

        JButton btnDesempenho = criarBotaoMenu("Desempenho da Franquia");
        sidebar.add(btnDesempenho);

        sidebar.add(Box.createVerticalGlue());

        JButton btnVoltar = criarBotaoMenu("Voltar");
        sidebar.add(btnVoltar);

        btnVerLojas.addActionListener(e -> mostrarLojas());
        btnDesempenho.addActionListener(e -> mostrarDesempenhoFranquia());
        btnVoltar.addActionListener(e -> this.dispose());
    }


    private void mostrarDesempenhoFranquia() {
        configurarPainelConteudo("Desempenho Financeiro: " + franquia.getNome());
        painelConteudo.setLayout(new BorderLayout());

        JPanel painelMetricas = new JPanel();
        painelMetricas.setLayout(new GridBagLayout());
        painelMetricas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        BigDecimal faturamentoTotal = serviceManager.getServiceRelatorio().calcularFaturamentoFranquia(franquia);
        int totalPedidos = serviceManager.getServiceRelatorio().contarPedidosTotaisFranquia(franquia);

        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (totalPedidos > 0) {
            ticketMedio = faturamentoTotal.divide(new BigDecimal(totalPedidos), 2, java.math.RoundingMode.HALF_UP);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTituloFaturamento = new JLabel("Faturamento Bruto Total:");
        lblTituloFaturamento.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloFaturamento, gbc);

        gbc.gridx = 1;

        JLabel lblValorFaturamento = new JLabel(String.format("R$ %.2f", faturamentoTotal));
        lblValorFaturamento.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorFaturamento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTituloPedidos = new JLabel("Número Total de Pedidos:");
        lblTituloPedidos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloPedidos, gbc);

        gbc.gridx = 1;
        JLabel lblValorPedidos = new JLabel(String.valueOf(totalPedidos));
        lblValorPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorPedidos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblTituloTicket = new JLabel("Ticket Médio:");
        lblTituloTicket.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloTicket, gbc);

        gbc.gridx = 1;


        JLabel lblValorTicket = new JLabel(String.format("R$ %.2f", ticketMedio));
        lblValorTicket.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorTicket, gbc);

        painelConteudo.add(painelMetricas, BorderLayout.NORTH);

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void mostrarLojas() {
        configurarPainelConteudo("Lojas da Franquia");
        painelConteudo.setLayout(new BorderLayout(10, 10));

        JPanel painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS));
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Loja> lojas = serviceManager.getServiceLoja().listarPorFranquia(franquia.getId());

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

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAdicionar = new JButton("Adicionar Loja");
        JButton btnRemover = new JButton("Remover Loja");
        painelAcoes.add(btnAdicionar);
        painelAcoes.add(btnRemover);

        painelConteudo.add(painelAcoes, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> acaoAdicionarLoja());
        btnRemover.addActionListener(e -> acaoRemoverLoja(lojas));

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

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nomeLoja = new JLabel(loja.getNome());
        nomeLoja.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nomeLoja.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nomeLoja);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel enderecoLoja = new JLabel("<html><b>Endereço:</b> " + loja.getEndereco() + "</html>");
        enderecoLoja.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        enderecoLoja.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(enderecoLoja);

        BigDecimal faturamentoLoja = serviceManager.getServiceRelatorio().calcularFaturamentoLoja(loja);
        JLabel labelFaturamento = new JLabel(String.format("<html><b>Faturamento:</b> R$ %.2f</html>", faturamentoLoja));
        labelFaturamento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelFaturamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(labelFaturamento);


        int numeroPedidos = (loja.getIdPedidos() != null) ? loja.getIdPedidos().size() : 0;
        JLabel labelNumeroPedidos = new JLabel(String.format("<html><b>Pedidos Realizados:</b> %d</html>", numeroPedidos));
        labelNumeroPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelNumeroPedidos.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new GridLayout(0, 1, 0, 5));
        botoesPanel.setOpaque(false);

        JButton btnGerenciarUsuarios = new JButton("Gerenciar Usuários");
        btnGerenciarUsuarios.addActionListener(e -> new InterfaceGerenciarUsuario(loja, serviceManager, franquia).setVisible(true));

        JButton btnDesignarGerente = new JButton("Designar Gerente");
        btnDesignarGerente.addActionListener(e -> acaoDesignarGerente(loja));

        JButton btnVerDesempenho = new JButton("Ver Desempenho");
        btnVerDesempenho.addActionListener(e -> acaoVerDesempenhoLoja(faturamentoLoja, numeroPedidos));

        botoesPanel.add(btnGerenciarUsuarios);
        botoesPanel.add(btnDesignarGerente);
        botoesPanel.add(btnVerDesempenho);

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
            nomeFonte = "Noto Color Emoji";
        }

        return new Font(nomeFonte, fonteBase.getStyle(), fonteBase.getSize());
    }


    private void acaoVerDesempenhoLoja(BigDecimal  faturamentoLoja, Integer numeroPedidos){
        configurarPainelConteudo("Desempenho Financeiro da loja: " + franquia.getNome());
        painelConteudo.setLayout(new BorderLayout());

        JPanel painelMetricas = new JPanel();
        painelMetricas.setLayout(new GridBagLayout());
        painelMetricas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTituloFaturamento = new JLabel("Faturamento Bruto Total:");
        lblTituloFaturamento.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloFaturamento, gbc);

        gbc.gridx = 1;


        BigDecimal faturamentoTotal = faturamentoLoja;

        JLabel lblValorFaturamento = new JLabel(String.format("R$ %.2f", faturamentoTotal));
        lblValorFaturamento.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorFaturamento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTituloPedidos = new JLabel("Número Total de Pedidos:");
        lblTituloPedidos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloPedidos, gbc);

        gbc.gridx = 1;
        int totalPedidos = numeroPedidos;


        JLabel lblValorPedidos = new JLabel(String.valueOf(totalPedidos));
        lblValorPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorPedidos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblTituloTicket = new JLabel("Ticket Médio:");
        lblTituloTicket.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblTituloTicket, gbc);

        gbc.gridx = 1;

        BigDecimal ticketMedioLoja = BigDecimal.ZERO;
        if(totalPedidos>0)
            ticketMedioLoja = faturamentoTotal.divide(new BigDecimal(totalPedidos), 2, BigDecimal.ROUND_HALF_UP);

        JLabel lblValorTicket = new JLabel(String.format("R$ " + ticketMedioLoja));
        lblValorTicket.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelMetricas.add(lblValorTicket, gbc);

        painelConteudo.add(painelMetricas, BorderLayout.NORTH);

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void acaoDesignarGerente(Loja loja) {
        List<Gerente> gerentesDisponiveis = serviceManager.getServiceUsuario().listarGerentesDisponiveis(serviceManager.getServiceLoja());

        JPanel painelDialogo = new JPanel(new BorderLayout(5, 5));
        painelDialogo.add(new JLabel("Selecione um gerente disponível ou crie um novo:"), BorderLayout.NORTH);

        JComboBox<Gerente> comboBoxGerentes = new JComboBox<>(gerentesDisponiveis.toArray(new Gerente[0]));

        if (gerentesDisponiveis.isEmpty()) {
            comboBoxGerentes.setEnabled(false);
        }

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

        String[] options = {"Designar Selecionado", "Criar Novo Gerente", "Cancelar"};

        int resultado = JOptionPane.showOptionDialog(
                this,
                painelDialogo,
                "Designar Gerente para a Loja",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        Gerente gerenteParaDesignar = null;

        if (resultado == 0) {
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

        } else if (resultado == 1) {
            gerenteParaDesignar = CriaGerente.criarNovoGerente(this, serviceManager, loja);

            mostrarLojas();
            return;

        } else {
            return;
        }

        if (gerenteParaDesignar != null) {
            try {
                serviceManager.getServiceLoja().designarGerenteParaLoja(gerenteParaDesignar, loja, serviceManager.getServiceUsuario());
                JOptionPane.showMessageDialog(this, "Gerente '" + gerenteParaDesignar.getNome() + "' designado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarLojas();
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Erro ao designar gerente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            catch(UsuarioInvalidoException e){
                JOptionPane.showMessageDialog(this, "Erro ao obter o gerente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoAdicionarLoja() {
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
                    return;
                }

                if (escolha.equals("Criar Novo Gerente")) {
                    gerenteDesignado = acaoCriarGerenteSemLoja();
                } else {
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
                serviceManager.getServiceLoja().adicionar(novaLoja, franquia, gerenteDesignado);
                serviceManager.getServiceFranquia().atualizar(franquia);

                JOptionPane.showMessageDialog(this, "Loja adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarLojas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar loja: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Gerente acaoCriarNovoGerente() {
        return CriaGerente.criarNovoGerente(this, serviceManager);
    }

    private Gerente acaoCriarGerenteSemLoja(){
        return CriaGerente.criaGerenteSemLoja(this, serviceManager);
    }



    private void acaoRemoverLoja(List<Loja> lojas) {
        if (lojas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há lojas para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
                    serviceManager.getServiceLoja().remover(lojaSelecionada, serviceManager);
                    serviceManager.getServiceFranquia().atualizar(franquia);

                    JOptionPane.showMessageDialog(this, "Loja removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    mostrarLojas();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao remover loja: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}