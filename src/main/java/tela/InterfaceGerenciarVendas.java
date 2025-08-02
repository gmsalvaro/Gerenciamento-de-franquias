package tela;

import Model.Loja;
import Model.Pedido;
import Model.Produto;
import Service.ServiceManager;
import Service.serviceEstoque;
import Model.formaPagamento;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceGerenciarVendas extends JFrame {

    private ServiceManager serviceManager;
    private serviceEstoque estoqueService;
    private Loja lojaSelecionada;

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabelaProdutos;
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloTabelaCarrinho;

    private JTextField txtQuantidade;
    private JButton btnAdicionarCarrinho;
    private JButton btnRemoverCarrinho;
    private JButton btnFinalizarCompra;
    private JButton btnCancelar;

    // --- NOVO: Componente para seleção da forma de pagamento ---
    private JComboBox<formaPagamento> comboFormaPagamento;
    // -------------------------------------------------------------

    // Map para armazenar os itens no carrinho: ID_Produto -> Quantidade
    private Map<String, Integer> carrinhoDeCompras;
    // Map auxiliar para acesso rápido a produtos pelo ID (do estoque)
    private Map<String, Produto> produtosDisponiveisMap;

    public InterfaceGerenciarVendas(ServiceManager serviceManager, Loja loja) throws PersistenciaException {
        super("Finalizar Compra - " + loja.getNome());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        this.serviceManager = serviceManager;
        this.lojaSelecionada = loja;
        this.estoqueService = new serviceEstoque(loja, serviceManager);
        this.carrinhoDeCompras = new HashMap<>();
        this.produtosDisponiveisMap = new HashMap<>();

        initComponents();
        carregarProdutos();
        atualizarCarrinho();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Painel de Produtos Disponíveis ---
        JPanel painelProdutos = new JPanel(new BorderLayout());
        painelProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis"));
        String[] colunasProdutos = {"ID", "Nome", "Preço", "Estoque"};
        modeloTabelaProdutos = new DefaultTableModel(colunasProdutos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloTabelaProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        painelProdutos.add(scrollProdutos, BorderLayout.CENTER);

        // --- Painel de Ação para Adicionar ao Carrinho ---
        JPanel painelAdicionar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelAdicionar.add(new JLabel("Quantidade:"));
        txtQuantidade = new JTextField("1", 5);
        painelAdicionar.add(txtQuantidade);
        btnAdicionarCarrinho = new JButton("Adicionar ao Carrinho");
        painelAdicionar.add(btnAdicionarCarrinho);
        painelProdutos.add(painelAdicionar, BorderLayout.SOUTH);

        // --- Painel do Carrinho de Compras ---
        JPanel painelCarrinho = new JPanel(new BorderLayout());
        painelCarrinho.setBorder(BorderFactory.createTitledBorder("Carrinho de Compras"));
        String[] colunasCarrinho = {"ID Produto", "Nome", "Quantidade", "Preço Unit.", "Subtotal"};
        modeloTabelaCarrinho = new DefaultTableModel(colunasCarrinho, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaCarrinho = new JTable(modeloTabelaCarrinho);
        tabelaCarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);
        painelCarrinho.add(scrollCarrinho, BorderLayout.CENTER);

        // --- Painel de Ação para Remover do Carrinho ---
        JPanel painelRemover = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRemoverCarrinho = new JButton("Remover do Carrinho");
        painelRemover.add(btnRemoverCarrinho);
        painelCarrinho.add(painelRemover, BorderLayout.SOUTH);


        // --- Painel de Botões Inferiores (com seleção de pagamento) ---
        JPanel painelBotoesFinais = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // --- NOVO: Adicionar a seleção da forma de pagamento ---
        painelBotoesFinais.add(new JLabel("Forma de Pagamento:"));
        comboFormaPagamento = new JComboBox<>(formaPagamento.values()); // Popula o JComboBox com os valores do enum
        painelBotoesFinais.add(comboFormaPagamento);
        // -------------------------------------------------------------

        btnFinalizarCompra = new JButton("Finalizar Compra");
        btnCancelar = new JButton("Cancelar");
        painelBotoesFinais.add(btnFinalizarCompra);
        painelBotoesFinais.add(btnCancelar);

        // --- Adicionar ao Layout Principal ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelProdutos, painelCarrinho);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
        add(painelBotoesFinais, BorderLayout.SOUTH);

        // --- Action Listeners ---
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
            if (produtos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há produtos cadastrados para esta loja.", "Produtos", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Produto p : produtos) {
                modeloTabelaProdutos.addRow(new Object[]{p.getId(), p.getNome(), String.format("%.2f", p.getPreco()), p.getEstoque()});
                produtosDisponiveisMap.put(p.getId(), p);
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCarrinho() {
        modeloTabelaCarrinho.setRowCount(0);
        double totalGeral = 0.0;

        for (Map.Entry<String, Integer> entry : carrinhoDeCompras.entrySet()) {
            String idProduto = entry.getKey();
            Integer quantidadeNoCarrinho = entry.getValue();
            Produto produto = produtosDisponiveisMap.get(idProduto);

            if (produto != null) {
                double subtotal = produto.getPreco() * quantidadeNoCarrinho;
                modeloTabelaCarrinho.addRow(new Object[]{
                        produto.getId(),
                        produto.getNome(),
                        quantidadeNoCarrinho,
                        String.format("%.2f", produto.getPreco()),
                        String.format("%.2f", subtotal)
                });
                totalGeral += subtotal;
            }
        }
        modeloTabelaCarrinho.addRow(new Object[]{"", "", "", "Total:", String.format("%.2f", totalGeral)});
    }

    private void acaoAdicionarCarrinho() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na lista de disponíveis.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idProduto = (String) modeloTabelaProdutos.getValueAt(linhaSelecionada, 0);
        Produto produto = produtosDisponiveisMap.get(idProduto);

        int quantidade;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser um número positivo.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida. Digite um número inteiro.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (produto.getEstoque() < quantidade) {
            JOptionPane.showMessageDialog(this, "Quantidade desejada (" + quantidade + ") excede o estoque disponível (" + produto.getEstoque() + ") para " + produto.getNome() + ".", "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        carrinhoDeCompras.put(idProduto, carrinhoDeCompras.getOrDefault(idProduto, 0) + quantidade);
        atualizarCarrinho();
        txtQuantidade.setText("1");
    }

    private void acaoRemoverCarrinho() {
        int linhaSelecionada = tabelaCarrinho.getSelectedRow();
        if (linhaSelecionada == -1 || linhaSelecionada == modeloTabelaCarrinho.getRowCount() - 1) {
            JOptionPane.showMessageDialog(this, "Selecione um item no carrinho para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idProduto = (String) modeloTabelaCarrinho.getValueAt(linhaSelecionada, 0);
        carrinhoDeCompras.remove(idProduto);
        atualizarCarrinho();
    }

    private void acaoFinalizarCompra() {
        if (carrinhoDeCompras.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio. Adicione produtos antes de finalizar a compra.", "Carrinho Vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirmar a finalização da compra?", "Finalizar Compra", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // --- NOVO: Obter a forma de pagamento selecionada ---
                formaPagamento pagamentoSelecionado = (formaPagamento) comboFormaPagamento.getSelectedItem();
                // -------------------------------------------------------------

                // --- MODIFICADO: Passar a forma de pagamento para o método ---
                Pedido novoPedido = estoqueService.finalizarCompra(carrinhoDeCompras, pagamentoSelecionado);
                // -------------------------------------------------------------

                JOptionPane.showMessageDialog(this, "Compra finalizada com sucesso! Pedido ID: " + novoPedido.getId(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                carrinhoDeCompras.clear();
                carregarProdutos();
                atualizarCarrinho();

            } catch ( PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Erro ao finalizar compra: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}