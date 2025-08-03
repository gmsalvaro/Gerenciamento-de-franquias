package tela;

import Model.Loja;
import Model.Produto;
import Service.ServiceManager;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class InterfaceGerenciarProdutos extends JFrame {
    private final Loja loja;
    private final ServiceManager serviceManager;
    private final DefaultListModel<Produto> listModel;
    private final JList<Produto> listaProdutos;

    // Campos de texto para o formulário de VISUALIZAÇÃO
    private final JTextField txtNome, txtPreco, txtEstoque;
    private final JTextArea txtDescricao;

    private final JButton btnAdicionar, btnEditar, btnRemover;

    public InterfaceGerenciarProdutos(Loja loja, ServiceManager serviceManager) {
        super("Gerenciar Produtos da Loja: " + loja.getNome());
        this.loja = loja;
        this.serviceManager = serviceManager;

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- PAINEL DA ESQUERDA (LISTA) ---
        listModel = new DefaultListModel<>();
        listaProdutos = new JList<>(listModel);
        listaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaProdutos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Produto) {
                    setText(((Produto) value).getNome());
                }
                return this;
            }
        });
        listaProdutos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                exibirDetalhes(listaProdutos.getSelectedValue());
            }
        });
        JScrollPane scrollLista = new JScrollPane(listaProdutos);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Produtos"));

        // --- PAINEL DA DIREITA (DETALHES E BOTÕES) ---
        JPanel painelDireito = new JPanel(new BorderLayout(10, 10));
        painelDireito.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Formulário (agora apenas para visualização)
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Detalhes do Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        txtNome = new JTextField(20);
        txtPreco = new JTextField(20);
        txtEstoque = new JTextField(20);
        txtDescricao = new JTextArea(4, 20);
        txtNome.setEditable(false);
        txtPreco.setEditable(false);
        txtEstoque.setEditable(false);
        txtDescricao.setEditable(false);

        gbc.weightx = 0.0; // Coluna dos labels não estica
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0; painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridy++; painelFormulario.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridy++; painelFormulario.add(new JLabel("Estoque:"), gbc);
        gbc.gridy++; gbc.anchor = GridBagConstraints.NORTHWEST; painelFormulario.add(new JLabel("Descrição:"), gbc);

        gbc.weightx = 1.0; // Coluna dos campos de texto estica
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painelFormulario.add(txtNome, gbc);
        gbc.gridy++; painelFormulario.add(txtPreco, gbc);
        gbc.gridy++; painelFormulario.add(txtEstoque, gbc);
        gbc.gridy++; painelFormulario.add(new JScrollPane(txtDescricao), gbc);



        // Botões de Ação
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("Adicionar Produto");
        btnEditar = new JButton("Editar Produto");
        btnRemover = new JButton("Remover Produto");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);

        painelDireito.add(painelFormulario, BorderLayout.CENTER);
        painelDireito.add(painelBotoes, BorderLayout.SOUTH);

        // Montagem final
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, painelDireito);
        splitPane.setDividerLocation(250);
        add(splitPane);

        // Ações dos botões
        btnAdicionar.addActionListener(e -> acaoAdicionarProduto());
        btnEditar.addActionListener(e -> acaoEditarProduto());
        btnRemover.addActionListener(e -> acaoRemoverProduto());

        carregarProdutos();
        exibirDetalhes(null);
        setVisible(true);
    }

    // --- MÉTODOS DE AÇÃO (CORRIGIDOS) ---

    private void acaoAdicionarProduto() {
        JTextField txtNomeForm = new JTextField();
        JTextField txtPrecoForm = new JTextField();
        JTextField txtEstoqueForm = new JTextField();
        JTextArea txtDescricaoForm = new JTextArea(3, 20);
        JPanel painelFormulario = criarPainelFormularioProduto(txtNomeForm, txtPrecoForm, txtEstoqueForm, txtDescricaoForm);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Adicionar Novo Produto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nome = txtNomeForm.getText().trim();
                if (nome.isEmpty()) throw new Exception("O nome do produto é obrigatório.");

                BigDecimal preco = new BigDecimal(txtPrecoForm.getText().trim().replace(",", "."));
                int estoque = Integer.parseInt(txtEstoqueForm.getText().trim());
                String descricao = txtDescricaoForm.getText().trim();

                Produto novoProduto = new Produto(nome, preco, estoque, descricao);
                serviceManager.getServiceProduto().addProduto(novoProduto, this.loja, serviceManager.getServiceLoja());

                JOptionPane.showMessageDialog(this, "Produto '" + nome + "' adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro de formato: Preço e Estoque devem ser números válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoEditarProduto() {
        Produto produtoSelecionado = listaProdutos.getSelectedValue();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na lista para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField txtNomeForm = new JTextField(produtoSelecionado.getNome());
        JTextField txtPrecoForm = new JTextField(produtoSelecionado.getPreco().toPlainString());
        JTextField txtEstoqueForm = new JTextField(String.valueOf(produtoSelecionado.getEstoque()));
        JTextArea txtDescricaoForm = new JTextArea(produtoSelecionado.getDescricao(), 3, 20);
        JPanel painelFormulario = criarPainelFormularioProduto(txtNomeForm, txtPrecoForm, txtEstoqueForm, txtDescricaoForm);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Editar Produto", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nome = txtNomeForm.getText().trim();
                if (nome.isEmpty()) throw new Exception("O nome do produto é obrigatório.");

                produtoSelecionado.setNome(nome);
                produtoSelecionado.setPreco(new BigDecimal(txtPrecoForm.getText().trim().replace(",", ".")));
                produtoSelecionado.setEstoque(Integer.parseInt(txtEstoqueForm.getText().trim()));
                produtoSelecionado.setDescricao(txtDescricaoForm.getText().trim());

                serviceManager.getServiceProduto().atualizarProduto(produtoSelecionado);
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * NOVO MÉTODO AUXILIAR REUTILIZÁVEL
     * Cria e retorna o painel do formulário para ser usado nos diálogos de Adicionar e Editar.
     */
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
        listModel.clear();
        try {
            List<Produto> produtos = serviceManager.getServiceProduto().listarPorIDLoja(loja.getId());
            produtos.forEach(listModel::addElement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirDetalhes(Produto produto) {
        if (produto != null) {
            // Preenche o formulário
            txtNome.setText(produto.getNome());
            txtPreco.setText("R$ " + produto.getPreco().toPlainString());
            txtEstoque.setText(String.valueOf(produto.getEstoque()));
            txtDescricao.setText(produto.getDescricao());
            // Habilita os botões de ação para o item selecionado
            btnEditar.setEnabled(true);
            btnRemover.setEnabled(true);
        } else {
            // Limpa o formulário
            txtNome.setText("");
            txtPreco.setText("");
            txtEstoque.setText("");
            txtDescricao.setText("");
            // Desabilita os botões de ação
            btnEditar.setEnabled(false);
            btnRemover.setEnabled(false);
            listaProdutos.clearSelection();
        }
    }

    private void acaoRemoverProduto() {
        Produto produtoSelecionado = listaProdutos.getSelectedValue();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum produto selecionado para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover o produto '" + produtoSelecionado.getNome() + "'?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                serviceManager.getServiceProduto().removerProduto(produtoSelecionado, this.loja, serviceManager.getServiceLoja());
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos(); // Recarrega a lista para refletir a remoção
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}