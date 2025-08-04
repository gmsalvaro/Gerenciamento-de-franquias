package tela;

import Model.*;
import Service.ServiceManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class InterfaceVendedor extends PainelPrincipal {
    private final ServiceManager serviceManager;
    private final Vendedor vendedorLogado;
    private final Loja lojaDoVendedor;
    private final GerenciaFluxoLogin fluxoLogin;

    // Componentes para a tela de "Meus Pedidos"
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;

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

        btnRegistrarVenda.addActionListener(e -> {
            try {
                new InterfaceGerenciarVendas(serviceManager, lojaDoVendedor, vendedorLogado);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de vendas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnMeusPedidos.addActionListener(e -> mostrarMeusPedidos(false));
        btnSair.addActionListener(e -> fluxoLogin.fazerLogout());
    }

    private void mostrarBoasVindas() {
        configurarPainelConteudo("Bem-vindo(a), " + vendedorLogado.getNome());
        painelConteudo.add(new JLabel("Utilize o menu à esquerda para iniciar.", SwingConstants.CENTER));
        painelConteudo.revalidate();
        painelConteudo.repaint();
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

        // Listener alterado: agora chama o novo método sem depender da seleção da tabela
        btnSolicitarAlteracao.addActionListener(e -> solicitarAlteracaoPedido());

        // Listener alterado: agora chama o novo método sem depender da seleção da tabela
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

    // Método modificado: agora utiliza um JComboBox para a seleção
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

    // Método modificado: agora utiliza um JComboBox para a seleção
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