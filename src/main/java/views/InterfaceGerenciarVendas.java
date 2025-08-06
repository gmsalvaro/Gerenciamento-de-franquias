package views;

import model.*;
import model.FormaDePagamento;
import Service.ServiceManager;
import Service.serviceEstoque;
import exception.persistencia.PersistenciaException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceGerenciarVendas extends JFrame {

    private final ServiceManager serviceManager;
    private final serviceEstoque estoqueService;
    private final Loja loja;
    private final Vendedor vendedor;
    private final Cliente cliente;

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabelaProdutos;
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloTabelaCarrinho;
    private JComboBox<FormaDePagamento> comboFormaPagamento;
    private JTextField txtQuantidade;
    private JLabel lblTotalCarrinho;

    private final Map<String, Integer> carrinhoDeCompras;
    private final Map<String, Produto> produtosDisponiveisMap;


    public InterfaceGerenciarVendas(ServiceManager serviceManager, Loja loja, Vendedor vendedor, Cliente cliente) throws PersistenciaException {
        super("Registrar Venda - Loja: " + loja.getNome() + " | Vendedor: " + vendedor.getNome());
        this.serviceManager = serviceManager;
        this.loja = loja;
        this.vendedor = vendedor;
        this.cliente = cliente;
        this.estoqueService = new serviceEstoque(loja, serviceManager);
        this.carrinhoDeCompras = new HashMap<>();
        this.produtosDisponiveisMap = new HashMap<>();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);

        initComponents();
        carregarProdutos();
        atualizarCarrinho();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelProdutos = new JPanel(new BorderLayout(5, 5));
        painelProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis"));
        String[] colunasProdutos = {"Nome", "Preço (R$)", "Estoque"};
        modeloTabelaProdutos = new DefaultTableModel(colunasProdutos, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(modeloTabelaProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelProdutos.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        JPanel painelAdicionar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelAdicionar.add(new JLabel("Qtd:"));
        txtQuantidade = new JTextField("1", 4);
        painelAdicionar.add(txtQuantidade);
        JButton btnAdicionarCarrinho = new JButton("Adicionar ao Carrinho ->");
        painelAdicionar.add(btnAdicionarCarrinho);
        painelProdutos.add(painelAdicionar, BorderLayout.SOUTH);

        JPanel painelCarrinho = new JPanel(new BorderLayout(5, 5));
        painelCarrinho.setBorder(BorderFactory.createTitledBorder("Carrinho de Compras"));
        String[] colunasCarrinho = {"Produto", "Qtd", "Subtotal (R$)"};
        modeloTabelaCarrinho = new DefaultTableModel(colunasCarrinho, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCarrinho = new JTable(modeloTabelaCarrinho);
        tabelaCarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelCarrinho.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);

        JPanel painelRemover = new JPanel(new BorderLayout());
        lblTotalCarrinho = new JLabel("Total: R$ 0.00");
        lblTotalCarrinho.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalCarrinho.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        JButton btnRemoverCarrinho = new JButton("Remover do Carrinho");
        painelRemover.add(lblTotalCarrinho, BorderLayout.WEST);
        painelRemover.add(btnRemoverCarrinho, BorderLayout.EAST);
        painelCarrinho.add(painelRemover, BorderLayout.SOUTH);

        JPanel painelBotoesFinais = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoesFinais.add(new JLabel("Forma de Pagamento:"));
        comboFormaPagamento = new JComboBox<>(FormaDePagamento.values());
        painelBotoesFinais.add(comboFormaPagamento);
        JButton btnFinalizarCompra = new JButton("Finalizar Compra");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoesFinais.add(btnFinalizarCompra);
        painelBotoesFinais.add(btnCancelar);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelProdutos, painelCarrinho);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
        add(painelBotoesFinais, BorderLayout.SOUTH);

        btnAdicionarCarrinho.addActionListener(e -> acaoAdicionarCarrinho());
        btnRemoverCarrinho.addActionListener(e -> acaoRemoverCarrinho());
        btnFinalizarCompra.addActionListener(e -> acaoFinalizarCompra());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void carregarProdutos() {
        modeloTabelaProdutos.setRowCount(0);
        produtosDisponiveisMap.clear();
        try {
            List<Produto> produtos = estoqueService.listarProdutosDisponiveis();
            for (Produto p : produtos) {
                modeloTabelaProdutos.addRow(new Object[]{p.getNome(), p.getPreco().toPlainString(), p.getEstoque()});
                produtosDisponiveisMap.put(p.getId(), p);
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCarrinho() {
        modeloTabelaCarrinho.setRowCount(0);
        BigDecimal totalGeral = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : carrinhoDeCompras.entrySet()) {
            String idProduto = entry.getKey();
            Integer quantidade = entry.getValue();
            Produto produto = produtosDisponiveisMap.get(idProduto);

            if (produto != null) {
                BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
                modeloTabelaCarrinho.addRow(new Object[]{produto.getNome(), quantidade, subtotal.toPlainString()});
                totalGeral = totalGeral.add(subtotal);
            }
        }
        lblTotalCarrinho.setText(String.format("Total: R$ %.2f", totalGeral));
    }

    private void acaoAdicionarCarrinho() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto da lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeProduto = (String) modeloTabelaProdutos.getValueAt(linhaSelecionada, 0);
        Produto produtoSelecionado = null;
        for(Produto p : produtosDisponiveisMap.values()){
            if(p.getNome().equals(nomeProduto)){
                produtoSelecionado = p;
                break;
            }
        }
        if(produtoSelecionado == null) return;

        int quantidade;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantidadeJaNoCarrinho = carrinhoDeCompras.getOrDefault(produtoSelecionado.getId(), 0);
        if (produtoSelecionado.getEstoque() < quantidade + quantidadeJaNoCarrinho) {
            JOptionPane.showMessageDialog(this, "Estoque insuficiente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        carrinhoDeCompras.put(produtoSelecionado.getId(), quantidadeJaNoCarrinho + quantidade);
        atualizarCarrinho();
    }

    private void acaoRemoverCarrinho() {
        int linhaSelecionada = tabelaCarrinho.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item no carrinho para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nomeProduto = (String) modeloTabelaCarrinho.getValueAt(linhaSelecionada, 0);

        produtosDisponiveisMap.values().stream()
                .filter(p -> p.getNome().equals(nomeProduto))
                .findFirst()
                .ifPresent(p -> carrinhoDeCompras.remove(p.getId()));

        atualizarCarrinho();
    }

    private void acaoFinalizarCompra() {
        if (carrinhoDeCompras.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Confirmar a finalização da compra?", "Finalizar Compra", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                FormaDePagamento pagamentoSelecionado = (FormaDePagamento) comboFormaPagamento.getSelectedItem();

                Pedido novoPedido = estoqueService.finalizarCompra(carrinhoDeCompras, this.vendedor, pagamentoSelecionado, cliente);

                JOptionPane.showMessageDialog(this, "Compra finalizada com sucesso! Pedido ID: " + novoPedido.getId(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                carrinhoDeCompras.clear();
                carregarProdutos();
                atualizarCarrinho();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao finalizar compra: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}