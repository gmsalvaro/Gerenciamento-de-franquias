package views;

import model.*;
import service.ServiceManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class InterfaceVendedor extends PainelPrincipal {
    private final ServiceManager serviceManager;
    private final Vendedor vendedorLogado;
    private final Loja lojaDoVendedor;
    private final GerenciaFluxoLogin fluxoLogin;
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;

    private CardLayout cardLayout;
    private JComboBox<Cliente> comboClientesExistentes;
    private JTextField txtNomeNovoCliente, txtCpfNovoCliente;
    private JRadioButton radioClienteExistente, radioNovoCliente;
    private JPanel painelCards;

    private static final String CARD_EXISTENTE = "Card com clientes existentes";
    private static final String CARD_NOVO = "Card para novo cliente";

    public InterfaceVendedor(ServiceManager serviceManager, Vendedor vendedorLogado, GerenciaFluxoLogin fluxoLogin) {
        super("Painel do Vendedor - " + vendedorLogado.getNome());
        this.serviceManager = serviceManager;
        this.vendedorLogado = vendedorLogado;
        this.lojaDoVendedor = serviceManager.getServiceLoja().buscarLojaPorUsuario(vendedorLogado).orElse(null);
        this.fluxoLogin = fluxoLogin;

        if (lojaDoVendedor == null) {
            JOptionPane.showMessageDialog(null, "ERRO: Você não está designado a nenhuma loja.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contruirSidebar();
        mostrarBoasVindas();
        setVisible(true);
    }

    @Override
    public void contruirSidebar() {
        sidebar.setBackground(new Color(240, 240, 240));
        JLabel lblTituloSidebar = new JLabel(lojaDoVendedor.getNome());
        lblTituloSidebar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloSidebar.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTituloSidebar);
        sidebar.add(Box.createVerticalStrut(30));

        JButton btnRegistrarVenda = criarBotaoMenu("Registrar Nova Venda");
        JButton btnMeusPedidos = criarBotaoMenu("Meus Pedidos");

        sidebar.add(btnRegistrarVenda);
        sidebar.add(btnMeusPedidos);
        sidebar.add(Box.createVerticalGlue());
        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);

        btnRegistrarVenda.addActionListener(e -> mostrarSelecaoCliente());
        btnMeusPedidos.addActionListener(e -> mostrarMeusPedidos(false));
        btnSair.addActionListener(e -> fluxoLogin.fazerLogout());
    }

    private void mostrarBoasVindas() {
        configurarPainelConteudo("Bem-vindo(a), " + vendedorLogado.getNome());
        painelConteudo.add(new JLabel("Utilize o menu à esquerda para iniciar.", SwingConstants.CENTER));
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }


    private void mostrarSelecaoCliente() {
        configurarPainelConteudo("Passo 1: Identificar o Cliente");
        painelConteudo.setLayout(new BorderLayout(10, 10));

        // 1. Painel Superior com os botões de rádio
        radioClienteExistente = new JRadioButton("Selecionar Cliente Existente", true);
        radioNovoCliente = new JRadioButton("Cadastrar Novo Cliente");
        ButtonGroup grupoRadios = new ButtonGroup();
        grupoRadios.add(radioClienteExistente);
        grupoRadios.add(radioNovoCliente);

        JPanel painelRadios = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelRadios.add(radioClienteExistente);
        painelRadios.add(radioNovoCliente);
        painelConteudo.add(painelRadios, BorderLayout.NORTH);

        // 2. Painel Central com CardLayout
        cardLayout = new CardLayout();
        painelCards = new JPanel(cardLayout);

        // --- Card 1: Selecionar Cliente Existente ---
        JPanel painelExistente = new JPanel(new BorderLayout(10,10));
        painelExistente.setBorder(new EmptyBorder(15, 10, 15, 10));

        JPanel painelConteudoExistente = new JPanel();
        painelConteudoExistente.setLayout(new BoxLayout(painelConteudoExistente, BoxLayout.Y_AXIS));
        comboClientesExistentes = new JComboBox<>();
        carregarClientesNoComboBox();

        JLabel instrucao = new JLabel("Selecione um cliente na lista:");
        instrucao.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboClientesExistentes.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboClientesExistentes.setMaximumSize(new Dimension(Integer.MAX_VALUE,comboClientesExistentes.getPreferredSize().height));

        painelConteudoExistente.add(instrucao);
        painelConteudoExistente.add(Box.createRigidArea(new Dimension(0, 5)));
        painelConteudoExistente.add(comboClientesExistentes);

        painelExistente.add(painelConteudoExistente, BorderLayout.NORTH);

        // --- Card 2: Cadastrar Novo Cliente ---
        JPanel painelNovo = new JPanel(new GridBagLayout());
        painelNovo.setBorder(BorderFactory.createTitledBorder("Dados do Novo Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        painelNovo.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNomeNovoCliente = new JTextField(20);
        painelNovo.add(txtNomeNovoCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelNovo.add(new JLabel("CPF:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtCpfNovoCliente = new JTextField(20);
        painelNovo.add(txtCpfNovoCliente, gbc);

        // Adiciona os cards ao painel principal
        painelCards.add(painelExistente, CARD_EXISTENTE);
        painelCards.add(painelNovo, CARD_NOVO);
        painelConteudo.add(painelCards, BorderLayout.CENTER);

        // 3. Painel Inferior com o botão de continuar
        JPanel painelAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnContinuar = new JButton("Continuar para a Venda ->");
        painelAcao.add(btnContinuar);
        painelConteudo.add(painelAcao, BorderLayout.SOUTH);

        // Action Listeners para trocar os cards
        radioClienteExistente.addActionListener(e -> cardLayout.show(painelCards, CARD_EXISTENTE));
        radioNovoCliente.addActionListener(e -> cardLayout.show(painelCards, CARD_NOVO));
        btnContinuar.addActionListener(e -> acaoContinuarParaVenda());

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }


    private void carregarClientesNoComboBox() {
        try {
            List<Cliente> clientes = serviceManager.getServiceCliente().listarTodos();
            comboClientesExistentes.removeAllItems();
            if (clientes.isEmpty()) {
                // Adiciona um item informativo se não houver clientes
                radioNovoCliente.setSelected(true);
                cardLayout.show(painelCards, CARD_NOVO);
            } else {
                for (Cliente c : clientes) {
                    comboClientesExistentes.addItem(c);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoContinuarParaVenda() {
        Cliente clienteSelecionado = null;

        if (radioClienteExistente.isSelected()) {
            if (comboClientesExistentes.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Nenhum cliente selecionado. Por favor, cadastre um novo cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            clienteSelecionado = (Cliente) comboClientesExistentes.getSelectedItem();
        } else { // Novo cliente
            String nome = txtNomeNovoCliente.getText().trim();
            String cpf = txtCpfNovoCliente.getText().trim();
            try {
                Cliente novoCliente = new Cliente(nome, cpf);
                serviceManager.getServiceCliente().adicionar(novoCliente);
                clienteSelecionado = novoCliente;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente: " + ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (clienteSelecionado != null) {
            try {
                new InterfaceGerenciarVendas(serviceManager, lojaDoVendedor, vendedorLogado, clienteSelecionado).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de vendas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void mostrarMeusPedidos(boolean incluirConcluidos) {
        String titulo = incluirConcluidos ? "Histórico Completo de Pedidos" : "Meus Pedidos em Andamento";
        configurarPainelConteudo(titulo);
        painelConteudo.setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID do Pedido", "Data", "Valor Total (R$)", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPedidos = new JTable(modeloTabela);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabela = new JScrollPane(tabelaPedidos);
        painelConteudo.add(scrollTabela, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnVerDetalhes = new JButton("Ver Detalhes");
        JButton btnSolicitarAlteracao = new JButton("Solicitar Alteração");
        JButton btnSolicitarExclusao = new JButton("Solicitar Exclusão");

        btnSolicitarAlteracao.setEnabled(!incluirConcluidos);
        btnSolicitarExclusao.setEnabled(!incluirConcluidos);

        painelBotoes.add(btnVerDetalhes);
        painelBotoes.add(btnSolicitarAlteracao);
        painelBotoes.add(btnSolicitarExclusao);
        painelConteudo.add(painelBotoes, BorderLayout.SOUTH);

        btnVerDetalhes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade para ver detalhes em construção.", "Aviso", JOptionPane.INFORMATION_MESSAGE));

        btnSolicitarAlteracao.addActionListener(e -> solicitarAlteracaoPedido());

        btnSolicitarExclusao.addActionListener(e -> solicitarCancelamentoPedido());

        carregarPedidosDoVendedor(incluirConcluidos);

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void carregarPedidosDoVendedor(boolean incluirConcluidos) {
        modeloTabela.setRowCount(0);
        try {
            List<Pedido> pedidos = serviceManager.getServicePedido().listarPorVendedor(vendedorLogado.getId(), incluirConcluidos);
            SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Pedido p : pedidos) {
                modeloTabela.addRow(new Object[]{
                        p.getId(),
                        formatadorData.format(p.getDataPedido()),
                        p.getPrecoTotal().toPlainString(),
                        p.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void solicitarAlteracaoPedido() {
        List<Pedido> pedidos;
        try {
            pedidos = serviceManager.getServicePedido().listarPorVendedor(vendedorLogado.getId(), false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há pedidos em andamento para solicitar alteração.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Pedido> comboPedidos = new JComboBox<>(pedidos.toArray(new Pedido[0]));
        JTextArea areaJustificativa = new JTextArea(5, 20);
        areaJustificativa.setLineWrap(true);
        areaJustificativa.setWrapStyleWord(true);
        JScrollPane scrollJustificativa = new JScrollPane(areaJustificativa);

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Selecione o Pedido:"));
        painel.add(comboPedidos);
        painel.add(new JLabel("Justificativa:"));
        painel.add(scrollJustificativa);

        int result = JOptionPane.showConfirmDialog(this, painel, "Solicitar Alteração de Pedido",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Pedido pedidoSelecionado = (Pedido) comboPedidos.getSelectedItem();
            String justificativa = areaJustificativa.getText();

            if (justificativa.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A justificativa não pode ser vazia.", "Justificativa Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (pedidoSelecionado.getStatus() == StatusPedido.ANALISE_ALTERACAO || pedidoSelecionado.getStatus() == StatusPedido.ANALISE_CANCELAMENTO) {
                    JOptionPane.showMessageDialog(this, "Este pedido já possui uma solicitação de alteração ou cancelamento em análise.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                pedidoSelecionado.setStatus(StatusPedido.ANALISE_ALTERACAO);
                pedidoSelecionado.setJustificativa(justificativa);
                serviceManager.getServicePedido().atualizar(pedidoSelecionado);
                JOptionPane.showMessageDialog(this, "Solicitação de alteração do pedido " + pedidoSelecionado.getId() + " enviada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidosDoVendedor(false);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar a solicitação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void solicitarCancelamentoPedido() {
        List<Pedido> pedidos;
        try {
            pedidos = serviceManager.getServicePedido().listarPorVendedor(vendedorLogado.getId(), false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há pedidos em andamento para solicitar cancelamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Pedido> comboPedidos = new JComboBox<>(pedidos.toArray(new Pedido[0]));
        JTextArea areaJustificativa = new JTextArea(5, 20);
        areaJustificativa.setLineWrap(true);
        areaJustificativa.setWrapStyleWord(true);
        JScrollPane scrollJustificativa = new JScrollPane(areaJustificativa);

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Selecione o Pedido:"));
        painel.add(comboPedidos);
        painel.add(new JLabel("Justificativa:"));
        painel.add(scrollJustificativa);

        int result = JOptionPane.showConfirmDialog(this, painel, "Solicitar Cancelamento de Pedido",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Pedido pedidoSelecionado = (Pedido) comboPedidos.getSelectedItem();
            String justificativa = areaJustificativa.getText();

            if (justificativa.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A justificativa não pode ser vazia.", "Justificativa Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja solicitar o cancelamento do pedido " + pedidoSelecionado.getId() + "?", "Confirmação de Cancelamento", JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                try {
                    if (pedidoSelecionado.getStatus() == StatusPedido.ANALISE_ALTERACAO || pedidoSelecionado.getStatus() == StatusPedido.ANALISE_CANCELAMENTO) {
                        JOptionPane.showMessageDialog(this, "Este pedido já possui uma solicitação de alteração ou cancelamento em análise.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    pedidoSelecionado.setStatus(StatusPedido.ANALISE_CANCELAMENTO);
                    pedidoSelecionado.setJustificativa(justificativa);
                    serviceManager.getServicePedido().atualizar(pedidoSelecionado);
                    JOptionPane.showMessageDialog(this, "Solicitação de cancelamento do pedido " + pedidoSelecionado.getId() + " enviada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarPedidosDoVendedor(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao enviar a solicitação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}