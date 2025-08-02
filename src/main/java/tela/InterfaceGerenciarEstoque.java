package tela;

import Model.Loja;
import Model.Produto;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InterfaceGerenciarEstoque extends JFrame {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome;
    private JTextField txtPreco;
    private JTextField txtEstoque;
    private JLabel lblIdProduto;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnRemover;
    private JButton btnFechar;

    private ServiceManager serviceManager;
    private Loja lojaAssociada;

    public InterfaceGerenciarEstoque(ServiceManager serviceManager, Loja loja) throws PersistenciaException {
        super("Gerenciar Produtos - " + loja.getNome());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        this.serviceManager = serviceManager;
        this.lojaAssociada = loja;

        initComponents();
        carregarProdutos();
        limparCampos();
        habilitarCampos(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Painel da Lista de Produtos ---
        String[] colunas = {"ID", "Nome", "Preço", "Estoque"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede que as células sejam editadas diretamente
            }
        };
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Produtos da Loja"));

        tabelaProdutos.getSelectionModel().addListSelectionListener(this::selecaoProdutoMudou);

        // --- Painel de Formulário e Detalhes ---
        JPanel painelDetalhes = new JPanel(new BorderLayout());
        painelDetalhes.setBorder(BorderFactory.createTitledBorder("Detalhes do Produto"));

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painelFormulario.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; lblIdProduto = new JLabel("-"); painelFormulario.add(lblIdProduto, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; txtNome = new JTextField(20); painelFormulario.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Preço:"), gbc);
        gbc.gridx = 1; txtPreco = new JTextField(20); painelFormulario.add(txtPreco, gbc);

        gbc.gridx = 0; gbc.gridy = 3; painelFormulario.add(new JLabel("Estoque:"), gbc);
        gbc.gridx = 1; txtEstoque = new JTextField(20); painelFormulario.add(txtEstoque, gbc);

        painelDetalhes.add(painelFormulario, BorderLayout.NORTH);

        // --- Painel de Botões de Ação ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnRemover = new JButton("Remover");
        btnFechar = new JButton("Fechar");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnFechar);

        // --- Adiciona tudo ao JFrame ---
        add(scrollTabela, BorderLayout.CENTER);
        add(painelDetalhes, BorderLayout.EAST);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- Action Listeners ---
        btnNovo.addActionListener(e -> acaoNovo());
        btnSalvar.addActionListener(e -> acaoSalvar());
        btnRemover.addActionListener(e -> acaoRemover());
        btnFechar.addActionListener(e -> dispose());
    }

    private void carregarProdutos() {
        modeloTabela.setRowCount(0); // Limpa a tabela
        try {
            List<Produto> produtos = serviceManager.getServiceProduto().listarPorIDLoja(lojaAssociada.getId());
            for (Produto p : produtos) {
                modeloTabela.addRow(new Object[]{p.getId(), p.getNome(), p.getPreco(), p.getEstoque()});
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecaoProdutoMudou(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && tabelaProdutos.getSelectedRow() != -1) {
            int linhaSelecionada = tabelaProdutos.getSelectedRow();
            String id = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
            String nome = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
            Double preco = (Double) modeloTabela.getValueAt(linhaSelecionada, 2);
            Integer estoque = (Integer) modeloTabela.getValueAt(linhaSelecionada, 3);

            lblIdProduto.setText(id);
            txtNome.setText(nome);
            txtPreco.setText(String.valueOf(preco));
            txtEstoque.setText(String.valueOf(estoque));
            habilitarCampos(true);
            btnRemover.setEnabled(true);
        } else {
            limparCampos();
            habilitarCampos(false);
            btnRemover.setEnabled(false);
        }
    }

    private void acaoNovo() {
        tabelaProdutos.clearSelection();
        limparCampos();
        habilitarCampos(true);
        btnRemover.setEnabled(false);
        lblIdProduto.setText("Novo Produto");
    }

    private void acaoSalvar() {
        try {
            String nome = txtNome.getText();
            double preco = Double.parseDouble(txtPreco.getText());
            int estoque = Integer.parseInt(txtEstoque.getText());

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome do produto não pode ser vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (preco <= 0) {
                JOptionPane.showMessageDialog(this, "O preço deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (estoque < 0) {
                JOptionPane.showMessageDialog(this, "O estoque não pode ser negativo.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = lblIdProduto.getText();
            if (id.equals("Novo Produto")) {
                // Adiciona um novo produto
                Produto novoProduto = new Produto(nome, preco, estoque);
                novoProduto.setIdLoja(lojaAssociada.getId());
                serviceManager.getServiceProduto().addProduto(novoProduto);
                JOptionPane.showMessageDialog(this, "Produto adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Atualiza um produto existente
                Produto produtoAtualizado = new Produto(nome, preco, estoque);
                produtoAtualizado.setIdLoja(lojaAssociada.getId());
                serviceManager.getServiceProduto().atualizarProduto(produtoAtualizado);
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            carregarProdutos();
            limparCampos();
            habilitarCampos(false);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço e estoque devem ser números válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoRemover() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este produto?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String id = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
                serviceManager.getServiceProduto().removerProdutoID(id);
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos();
                limparCampos();
                habilitarCampos(false);
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Erro ao remover o produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        lblIdProduto.setText("-");
        txtNome.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
    }

    private void habilitarCampos(boolean habilitar) {
        txtNome.setEnabled(habilitar);
        txtPreco.setEnabled(habilitar);
        txtEstoque.setEnabled(habilitar);
        btnSalvar.setEnabled(habilitar);
    }
}