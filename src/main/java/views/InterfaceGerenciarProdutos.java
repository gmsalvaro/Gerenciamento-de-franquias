package views;

import model.Loja;
import model.Produto;
import Service.ServiceManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class InterfaceGerenciarProdutos extends JFrame {
    private final Loja loja;
    private final ServiceManager serviceManager;
    private final DefaultTableModel modeloTabela;
    private final JTable tabelaProdutos;

    public InterfaceGerenciarProdutos(Loja loja, ServiceManager serviceManager) {
        super("Gerenciar Produtos da Loja: " + loja.getNome());
        this.loja = loja;
        this.serviceManager = serviceManager;

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID", "Nome", "Preço (R$)", "Estoque", "Descrição"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelaProdutos.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Produtos em Estoque"));
        add(scrollTabela, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAdicionar = new JButton("Adicionar Produto");
        JButton btnEditar = new JButton("Editar Selecionado");
        JButton btnRemover = new JButton("Remover Selecionado");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> acaoAdicionarProduto());
        btnEditar.addActionListener(e -> acaoEditarProduto());
        btnRemover.addActionListener(e -> acaoRemoverProduto());

        carregarProdutos();
        setVisible(true);
    }

    private void acaoAdicionarProduto() {
        JTextField txtNomeForm = new JTextField();
        JTextField txtPrecoForm = new JTextField();
        JTextField txtEstoqueForm = new JTextField();
        JTextArea txtDescricaoForm = new JTextArea(3, 20);
        JPanel painelFormulario = criarPainelFormularioProduto(txtNomeForm, txtPrecoForm, txtEstoqueForm, txtDescricaoForm);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Adicionar Novo Produto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nome = txtNomeForm.getText().trim();
                if (nome.isEmpty()) throw new Exception("O nome do produto é obrigatório.");

                BigDecimal preco = new BigDecimal(txtPrecoForm.getText().trim().replace(",", "."));
                int estoque = Integer.parseInt(txtEstoqueForm.getText().trim());
                String descricao = txtDescricaoForm.getText().trim();

                Produto novoProduto = new Produto(nome, preco, estoque, descricao);
                serviceManager.getServiceProduto().adicionar(novoProduto, this.loja, serviceManager.getServiceLoja());

                JOptionPane.showMessageDialog(this, "Produto '" + nome + "' adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoEditarProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idProduto = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        Produto produtoParaEditar = serviceManager.getServiceProduto().getProduto(idProduto);

        if (produtoParaEditar == null) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField txtNomeForm = new JTextField(produtoParaEditar.getNome());
        JTextField txtPrecoForm = new JTextField(produtoParaEditar.getPreco().toPlainString());
        JTextField txtEstoqueForm = new JTextField(String.valueOf(produtoParaEditar.getEstoque()));
        JTextArea txtDescricaoForm = new JTextArea(produtoParaEditar.getDescricao(), 3, 20);
        JPanel painelFormulario = criarPainelFormularioProduto(txtNomeForm, txtPrecoForm, txtEstoqueForm, txtDescricaoForm);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Editar Produto", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                produtoParaEditar.setNome(txtNomeForm.getText().trim());
                produtoParaEditar.setPreco(new BigDecimal(txtPrecoForm.getText().trim().replace(",", ".")));
                produtoParaEditar.setEstoque(Integer.parseInt(txtEstoqueForm.getText().trim()));
                produtoParaEditar.setDescricao(txtDescricaoForm.getText().trim());

                serviceManager.getServiceProduto().atualizarProduto(produtoParaEditar);
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel criarPainelFormularioProduto(JTextField txtNome, JTextField txtPreco, JTextField txtEstoque, JTextArea txtDescricao) {
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; painel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painel.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; painel.add(txtPreco, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painel.add(new JLabel("Estoque:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; painel.add(txtEstoque, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHWEST; painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; painel.add(scrollDescricao, gbc);

        return painel;
    }

    private void carregarProdutos() {
        modeloTabela.setRowCount(0);
        try {
            List<Produto> produtos = serviceManager.getServiceProduto().listarPorLoja(loja.getId());
            for (Produto p : produtos) {
                modeloTabela.addRow(new Object[]{
                        p.getId(),
                        p.getNome(),
                        p.getPreco().toPlainString(),
                        p.getEstoque(),
                        p.getDescricao()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void acaoRemoverProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idProduto = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        Produto produtoParaRemover = serviceManager.getServiceProduto().getProduto(idProduto);

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover o produto '" + produtoParaRemover.getNome() + "'?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                serviceManager.getServiceProduto().remover(produtoParaRemover, this.loja, serviceManager.getServiceLoja());
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}