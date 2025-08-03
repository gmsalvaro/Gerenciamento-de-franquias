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

        // Botões para cada funcionalidade do Vendedor
        JButton btnRegistrarVenda = criarBotaoMenu("Registrar Nova Venda");
        JButton btnMeusPedidos = criarBotaoMenu("Meus Pedidos");

        sidebar.add(btnRegistrarVenda);
        sidebar.add(btnMeusPedidos);
        sidebar.add(Box.createVerticalGlue());
        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);

        // Ações dos botões
        btnRegistrarVenda.addActionListener(e -> {
            try {
                // Abre a tela de vendas, passando a loja e o vendedor logado
                new InterfaceGerenciarVendas(serviceManager, lojaDoVendedor, vendedorLogado);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de vendas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnMeusPedidos.addActionListener(e -> mostrarMeusPedidos(false));
        btnSair.addActionListener(e -> fluxoLogin.fazerLogout()); // Supondo que você use o fluxo de logout
    }

    private void mostrarBoasVindas() {
        configurarPainelConteudo("Bem-vindo(a), " + vendedorLogado.getNome());
        painelConteudo.add(new JLabel("Utilize o menu à esquerda para iniciar.", SwingConstants.CENTER));
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    /**
     * Constrói e exibe a tela com a lista de pedidos do vendedor logado.
     */
    private void mostrarMeusPedidos(boolean incluirConcluidos) {
        String titulo = incluirConcluidos ? "Histórico Completo de Pedidos" : "Meus Pedidos em Andamento";
        configurarPainelConteudo(titulo);
        painelConteudo.setLayout(new BorderLayout(10, 10));

        // --- Tabela de Pedidos (Painel Central) ---
        String[] colunas = {"ID do Pedido", "Data", "Valor Total (R$)", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPedidos = new JTable(modeloTabela);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabela = new JScrollPane(tabelaPedidos);
        painelConteudo.add(scrollTabela, BorderLayout.CENTER);

        // --- Painel de Botões (Inferior) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnVerDetalhes = new JButton("Ver Detalhes");
        JButton btnSolicitarAlteracao = new JButton("Solicitar Alteração");
        JButton btnSolicitarExclusao = new JButton("Solicitar Exclusão");

        // O vendedor só pode solicitar alteração/exclusão de pedidos em andamento
        btnSolicitarAlteracao.setEnabled(!incluirConcluidos);
        btnSolicitarExclusao.setEnabled(!incluirConcluidos);

        painelBotoes.add(btnVerDetalhes);
        painelBotoes.add(btnSolicitarAlteracao);
        painelBotoes.add(btnSolicitarExclusao);
        painelConteudo.add(painelBotoes, BorderLayout.SOUTH);

        // Ações (placeholders)
        btnVerDetalhes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade para ver detalhes em construção.", "Aviso", JOptionPane.INFORMATION_MESSAGE));
        btnSolicitarAlteracao.addActionListener(e -> JOptionPane.showMessageDialog(this, "Uma solicitação de alteração será enviada ao gerente.", "Aviso", JOptionPane.INFORMATION_MESSAGE));
        btnSolicitarExclusao.addActionListener(e -> JOptionPane.showMessageDialog(this, "Uma solicitação de exclusão será enviada ao gerente.", "Aviso", JOptionPane.INFORMATION_MESSAGE));

        // Chama o método para carregar os dados na tabela, passando o filtro
        carregarPedidosDoVendedor(incluirConcluidos);

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void carregarPedidosDoVendedor(boolean incluirConcluidos) {
        modeloTabela.setRowCount(0);
        try {
            // Usa o novo método de serviço que aceita o filtro
            List<Pedido> pedidos = serviceManager.getServicePedido().listarPorIdVendedor(vendedorLogado.getId(), incluirConcluidos);
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
}