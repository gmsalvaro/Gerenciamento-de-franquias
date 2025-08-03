package tela;

import Model.Loja;
import Model.Pedido;
import Model.Produto;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class InterfaceGerenciarPedidos extends JFrame {

    private JList<Pedido> listaPedidos;
    private DefaultListModel<Pedido> listModel;

    private JLabel lblIdPedido;
    private JLabel lblDataPedido;
    private JComboBox<String> cmbStatus;
    private JTable tabelaProdutosPedido;
    private DefaultTableModel modeloTabelaProdutos;

    private JButton btnAtualizarStatus;
    private JButton btnFechar;

    private ServiceManager serviceManager;
    private Loja lojaAssociada;

    public InterfaceGerenciarPedidos(ServiceManager serviceManager, Loja loja) {
        super("Gerenciar Vendas - " + loja.getNome());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        this.serviceManager = serviceManager;
        this.lojaAssociada = loja;

        initComponents();
        carregarPedidos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Painel da Lista de Pedidos ---
        listModel = new DefaultListModel<>();
        listaPedidos = new JList<>(listModel);
        listaPedidos.setCellRenderer(new DefaultListCellRenderer() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pedido) {
                    Pedido pedido = (Pedido) value;
                    String texto = "Pedido #" + pedido.getId().substring(0, 8) + " - " + dateFormat.format(pedido.getDataPedido()) + " (" + pedido.getStatus() + ")";
                    setText(texto);
                }
                return this;
            }
        });
        listaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listaPedidos);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Pedidos da Loja"));

        listaPedidos.addListSelectionListener(this::selecaoPedidoMudou);

        // --- Painel de Detalhes do Pedido ---
        JPanel painelDetalhes = new JPanel(new BorderLayout());
        painelDetalhes.setBorder(BorderFactory.createTitledBorder("Detalhes do Pedido"));

        JPanel painelInfo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painelInfo.add(new JLabel("ID do Pedido:"), gbc);
        gbc.gridx = 1; lblIdPedido = new JLabel("-"); painelInfo.add(lblIdPedido, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelInfo.add(new JLabel("Data:"), gbc);
        gbc.gridx = 1; lblDataPedido = new JLabel("-"); painelInfo.add(lblDataPedido, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painelInfo.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(new String[]{"Pendente", "Concluido", "Cancelado"});
        painelInfo.add(cmbStatus, gbc);

        // Tabela de produtos do pedido
        String[] colunas = {"ID Produto", "Nome", "Preço Unit.", "Quantidade"};
        modeloTabelaProdutos = new DefaultTableModel(colunas, 0);
        tabelaProdutosPedido = new JTable(modeloTabelaProdutos);
        JScrollPane scrollTabela = new JScrollPane(tabelaProdutosPedido);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Itens do Pedido"));

        JPanel painelBotoesDetalhes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAtualizarStatus = new JButton("Atualizar Status");
        btnFechar = new JButton("Fechar");
        painelBotoesDetalhes.add(btnAtualizarStatus);
        painelBotoesDetalhes.add(btnFechar);

        painelDetalhes.add(painelInfo, BorderLayout.NORTH);
        painelDetalhes.add(scrollTabela, BorderLayout.CENTER);
        painelDetalhes.add(painelBotoesDetalhes, BorderLayout.SOUTH);

        // --- Adiciona os painéis ao JFrame ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, painelDetalhes);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        btnAtualizarStatus.addActionListener(e -> acaoAtualizarStatus());
        btnFechar.addActionListener(e -> dispose());

        limparDetalhes();
        habilitarDetalhes(false);
    }

    private void carregarPedidos() {
        listModel.clear();
        try {
            List<Pedido> pedidos = serviceManager.getServicePedido().listarPorIDLoja(lojaAssociada.getId());
            for (Pedido p : pedidos) {
                listModel.addElement(p);
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecaoPedidoMudou(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Pedido selecionado = listaPedidos.getSelectedValue();
            if (selecionado != null) {
                exibirDetalhesPedido(selecionado);
                habilitarDetalhes(true);
            } else {
                limparDetalhes();
                habilitarDetalhes(false);
            }
        }
    }

    private void exibirDetalhesPedido(Pedido pedido) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        lblIdPedido.setText(pedido.getId());
        lblDataPedido.setText(dateFormat.format(pedido.getDataPedido()));
        cmbStatus.setSelectedItem(pedido.getStatus());

        modeloTabelaProdutos.setRowCount(0);
        try {
            Map<String, Integer> produtosNoPedido = pedido.getProdutosNoPedido();
            for (Map.Entry<String, Integer> entry : produtosNoPedido.entrySet()) {
                String idProduto = entry.getKey();
                Integer quantidade = entry.getValue();

                Produto produto = serviceManager.getServiceProduto().getProdutoById(idProduto);

                if (produto != null) {
                    modeloTabelaProdutos.addRow(new Object[]{
                            produto.getId(),
                            produto.getNome(),
                            String.format("%.2f", produto.getPreco()),
                            quantidade
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos do pedido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoAtualizarStatus() {
        Pedido selecionado = listaPedidos.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //String novoStatus = (String) cmbStatus.getSelectedItem(); ARRUMAR !!!
        //selecionado.setStatus(novoStatus);

        try {
            serviceManager.getServicePedido().atualizarPedido(selecionado);
            JOptionPane.showMessageDialog(this, "Status do pedido atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            listaPedidos.repaint(); // Atualiza a exibição na lista
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status do pedido: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparDetalhes() {
        lblIdPedido.setText("-");
        lblDataPedido.setText("-");
        cmbStatus.setSelectedItem("Pendente");
        modeloTabelaProdutos.setRowCount(0);
    }

    private void habilitarDetalhes(boolean habilitar) {
        cmbStatus.setEnabled(habilitar);
        btnAtualizarStatus.setEnabled(habilitar);
    }
}