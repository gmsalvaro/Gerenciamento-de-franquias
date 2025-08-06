package views;

import exception.pedido.PedidoNaoEncontradoException;
import model.Loja;
import model.Pedido;
import model.Produto;
import model.StatusPedido;
import service.ServiceManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelEvent;

public class InterfaceGerenciarPedidos extends JFrame {
    private final Loja loja;
    private final ServiceManager serviceManager;
    private final DefaultTableModel modeloTabela;
    private final JTable tabelaPedidos;
    private final JButton btnVerJustificativaEAcoes;

    public InterfaceGerenciarPedidos(Loja loja, ServiceManager serviceManager) {
        super("Gerenciar Pedidos da Loja: " + loja.getNome());
        this.loja = loja;
        this.serviceManager = serviceManager;

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID Pedido", "Data", "Vendedor", "Valor Total (R$)", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPedidos = new JTable(modeloTabela);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAtualizarStatus = new JButton("Atualizar Status do Pedido");
        btnVerJustificativaEAcoes = new JButton("Ver Justificativa e Ações");

        btnVerJustificativaEAcoes.setEnabled(false);

        painelBotoes.add(btnAtualizarStatus);
        painelBotoes.add(btnVerJustificativaEAcoes);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAtualizarStatus.addActionListener(e -> {
            try {
                acaoAtualizarStatus();
            } catch (PedidoNaoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Pedido Não Encontrado", JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnVerJustificativaEAcoes.addActionListener(e -> verJustificativaEAcoes());
        tabelaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                verificarStatusParaHabilitarBotao();
            }
        });

        carregarPedidos();
        setVisible(true);
    }

    private void verificarStatusParaHabilitarBotao() {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada != -1) {
            String status = modeloTabela.getValueAt(linhaSelecionada, 4).toString();
            boolean isAnalise = status.equals(StatusPedido.ANALISE_ALTERACAO.toString()) || status.equals(StatusPedido.ANALISE_CANCELAMENTO.toString());
            btnVerJustificativaEAcoes.setEnabled(isAnalise);
        } else {
            btnVerJustificativaEAcoes.setEnabled(false);
        }
    }

    public void carregarPedidos() {
        modeloTabela.setRowCount(0);
        try {
            List<Pedido> pedidos = serviceManager.getServicePedido().listarPorIDLoja(loja.getId());
            SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Pedido p : pedidos) {
                String nomeVendedor = serviceManager.getServiceUsuario()
                        .buscarPorId(p.getIdVendedor())
                        .map(model.Usuario::getNome)
                        .orElse("Não encontrado");

                modeloTabela.addRow(new Object[]{
                        p.getId(),
                        formatadorData.format(p.getDataPedido()),
                        nomeVendedor,
                        p.getPrecoTotal().toPlainString(),
                        p.getStatus().toString()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verJustificativaEAcoes() {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada == -1) return;

        String idPedidoStr = modeloTabela.getValueAt(linhaSelecionada, 0).toString();
        Pedido pedidoParaGerenciar = serviceManager.getServicePedido().getPedidoById(idPedidoStr);

        if (pedidoParaGerenciar == null) {
            JOptionPane.showMessageDialog(this, "Pedido não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pedidoParaGerenciar.getStatus() == StatusPedido.ANALISE_ALTERACAO) {
            abrirJanelaEdicaoPedido(pedidoParaGerenciar);
        } else {
            abrirJanelaAprovacaoCancelamento(pedidoParaGerenciar);
        }
    }

    private void abrirJanelaAprovacaoCancelamento(Pedido pedidoParaGerenciar) {
        JDialog dialogo = new JDialog(this, "Ação para o Pedido " + pedidoParaGerenciar.getId(), true);
        dialogo.setSize(400, 300);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setLocationRelativeTo(this);
        dialogo.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblJustificativa = new JLabel("Justificativa da Solicitação de Cancelamento:");
        JTextArea areaJustificativa = new JTextArea(pedidoParaGerenciar.getJustificativa());
        areaJustificativa.setEditable(false);
        areaJustificativa.setLineWrap(true);
        areaJustificativa.setWrapStyleWord(true);
        JScrollPane scrollJustificativa = new JScrollPane(areaJustificativa);

        dialogo.add(lblJustificativa, BorderLayout.NORTH);
        dialogo.add(scrollJustificativa, BorderLayout.CENTER);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAprovar = new JButton("Aprovar Cancelamento");
        JButton btnRejeitar = new JButton("Rejeitar Cancelamento");
        JButton btnCancelar = new JButton("Cancelar");

        painelBotoesAcao.add(btnAprovar);
        painelBotoesAcao.add(btnRejeitar);
        painelBotoesAcao.add(btnCancelar);

        dialogo.add(painelBotoesAcao, BorderLayout.SOUTH);

        btnAprovar.addActionListener(e -> {
            acaoAprovarRejeitar(pedidoParaGerenciar, StatusPedido.CANCELADO);
            dialogo.dispose();
        });
        btnRejeitar.addActionListener(e -> {
            acaoAprovarRejeitar(pedidoParaGerenciar, StatusPedido.NEGADO);
            dialogo.dispose();
        });
        btnCancelar.addActionListener(e -> dialogo.dispose());

        dialogo.setVisible(true);
    }

    private void abrirJanelaEdicaoPedido(Pedido pedidoParaEditar) {
        JDialog dialogo = new JDialog(this, "Editar Pedido " + pedidoParaEditar.getId(), true);
        dialogo.setSize(800, 600);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setLocationRelativeTo(this);
        dialogo.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelJustificativa = new JPanel(new BorderLayout());
        painelJustificativa.setBorder(BorderFactory.createTitledBorder("Justificativa do Vendedor"));
        JTextArea areaJustificativa = new JTextArea(pedidoParaEditar.getJustificativa());
        areaJustificativa.setEditable(false);
        areaJustificativa.setLineWrap(true);
        areaJustificativa.setWrapStyleWord(true);
        painelJustificativa.add(new JScrollPane(areaJustificativa), BorderLayout.CENTER);
        dialogo.add(painelJustificativa, BorderLayout.NORTH);

        String[] colunasItens = {"ID", "Item", "Preço Unitário (R$)", "Quantidade"};
        DefaultTableModel modeloTabelaItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3;
            }
        };
        JTable tabelaItens = new JTable(modeloTabelaItens);

        JLabel lblValorTotal = new JLabel("Valor Total: R$ " + pedidoParaEditar.getPrecoTotal().toPlainString());
        lblValorTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));

        Map<String, Integer> quantidadesOriginais = new HashMap<>(pedidoParaEditar.getProdutosNoPedido());

        modeloTabelaItens.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE) {
                recalcularValorTotal(modeloTabelaItens, lblValorTotal);
            }
        });

        if (pedidoParaEditar.getProdutosNoPedido() != null) {
            for (Map.Entry<String, Integer> entry : pedidoParaEditar.getProdutosNoPedido().entrySet()) {
                String idProduto = entry.getKey();
                int quantidade = entry.getValue();
                Produto produto = serviceManager.getServiceProduto().getProduto(idProduto);
                if (produto != null) {
                    modeloTabelaItens.addRow(new Object[]{
                            produto.getId(),
                            produto.getNome(),
                            produto.getPreco(),
                            quantidade
                    });
                }
            }
        }

        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        JPanel painelBotoesItens = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdicionarProduto = new JButton("Adicionar Produto");
        JButton btnRemoverProduto = new JButton("Remover Produto");

        btnRemoverProduto.setEnabled(false);

        painelBotoesItens.add(btnAdicionarProduto);
        painelBotoesItens.add(btnRemoverProduto);

        painelItens.add(painelBotoesItens, BorderLayout.NORTH);
        dialogo.add(painelItens, BorderLayout.CENTER);

        tabelaItens.getSelectionModel().addListSelectionListener(e -> {
            btnRemoverProduto.setEnabled(tabelaItens.getSelectedRow() != -1);
        });

        btnAdicionarProduto.addActionListener(e -> abrirJanelaAdicionarProduto(modeloTabelaItens));

        btnRemoverProduto.addActionListener(e -> {
            int linhaSelecionada = tabelaItens.getSelectedRow();
            if (linhaSelecionada != -1) {
                modeloTabelaItens.removeRow(linhaSelecionada);
                recalcularValorTotal(modeloTabelaItens, lblValorTotal);
            }
        });

        JPanel painelBotoesAcao = new JPanel(new BorderLayout());
        painelBotoesAcao.add(lblValorTotal, BorderLayout.WEST);

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAprovar = new JButton("Aprovar Edição");
        JButton btnRejeitar = new JButton("Rejeitar Edição");
        JButton btnCancelar = new JButton("Cancelar");

        painelAcoes.add(btnAprovar);
        painelAcoes.add(btnRejeitar);
        painelAcoes.add(btnCancelar);

        painelBotoesAcao.add(painelAcoes, BorderLayout.CENTER);

        dialogo.add(painelBotoesAcao, BorderLayout.SOUTH);

        btnAprovar.addActionListener(e -> {
            try {
                Map<String, Integer> novosProdutosNoPedido = new HashMap<>();
                BigDecimal novoPrecoTotal = BigDecimal.ZERO;

                for (int i = 0; i < modeloTabelaItens.getRowCount(); i++) {
                    String idProduto = modeloTabelaItens.getValueAt(i, 0).toString();
                    int novaQuantidade = Integer.parseInt(modeloTabelaItens.getValueAt(i, 3).toString());

                    if (novaQuantidade <= 0) {
                        JOptionPane.showMessageDialog(dialogo, "A quantidade de produtos deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int quantidadeOriginal = quantidadesOriginais.getOrDefault(idProduto, 0);
                    int diferenca = novaQuantidade - quantidadeOriginal;

                    if (diferenca > 0) {
                        Produto produto = serviceManager.getServiceProduto().getProduto(idProduto);
                        if (produto == null) {
                            JOptionPane.showMessageDialog(dialogo, "Produto " + idProduto + " não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (produto.getEstoque() < diferenca) {
                            JOptionPane.showMessageDialog(dialogo, "Estoque insuficiente para o produto " + produto.getNome() + ". Disponível: " + produto.getEstoque(), "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    novosProdutosNoPedido.put(idProduto, novaQuantidade);
                    BigDecimal precoUnitario = new BigDecimal(modeloTabelaItens.getValueAt(i, 2).toString());
                    novoPrecoTotal = novoPrecoTotal.add(precoUnitario.multiply(new BigDecimal(novaQuantidade)));
                }

                for (Map.Entry<String, Integer> novoItem : novosProdutosNoPedido.entrySet()) {
                    String idProduto = novoItem.getKey();
                    int novaQuantidade = novoItem.getValue();
                    int quantidadeOriginal = quantidadesOriginais.getOrDefault(idProduto, 0);

                    int diferenca = novaQuantidade - quantidadeOriginal;

                    if (diferenca != 0) {
                        serviceManager.getServiceProduto().getProduto(idProduto).setEstoque( diferenca);
                        serviceManager.getServiceProduto().atualizarProduto(serviceManager.getServiceProduto().getProduto(idProduto));
                    }
                }

                pedidoParaEditar.setProdutosNoPedido(novosProdutosNoPedido);
                pedidoParaEditar.setPrecoTotal(novoPrecoTotal);
                pedidoParaEditar.setStatus(StatusPedido.CONCLUIDO);

                serviceManager.getServicePedido().atualizar(pedidoParaEditar);

                JOptionPane.showMessageDialog(this, "Pedido " + pedidoParaEditar.getId() + " editado, estoque atualizado e aprovado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidos();
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Valores de preço ou quantidade inválidos. Por favor, use apenas números.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar edição: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRejeitar.addActionListener(e -> {
            acaoAprovarRejeitar(pedidoParaEditar, StatusPedido.NEGADO);
            dialogo.dispose();
        });

        btnCancelar.addActionListener(e -> dialogo.dispose());

        dialogo.setVisible(true);
    }

    private void recalcularValorTotal(DefaultTableModel modeloTabelaItens, JLabel lblValorTotal) {
        BigDecimal novoPrecoTotal = BigDecimal.ZERO;
        for (int i = 0; i < modeloTabelaItens.getRowCount(); i++) {
            try {
                BigDecimal precoUnitario = new BigDecimal(modeloTabelaItens.getValueAt(i, 2).toString());
                int novaQuantidade = Integer.parseInt(modeloTabelaItens.getValueAt(i, 3).toString());
                novoPrecoTotal = novoPrecoTotal.add(precoUnitario.multiply(new BigDecimal(novaQuantidade)));
            } catch (NumberFormatException ex) {
            }
        }
        lblValorTotal.setText("Valor Total: R$ " + novoPrecoTotal.toPlainString());
    }

    private void abrirJanelaAdicionarProduto(DefaultTableModel modeloTabelaItens) {
        JDialog dialogoAdicionar = new JDialog(this, "Adicionar Produto ao Pedido", true);
        dialogoAdicionar.setSize(600, 400);
        dialogoAdicionar.setLocationRelativeTo(this);
        dialogoAdicionar.setLayout(new BorderLayout(10, 10));

        String[] colunasProdutos = {"ID", "Nome", "Preço (R$)", "Estoque"};
        DefaultTableModel modeloTabelaProdutos = new DefaultTableModel(colunasProdutos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelaProdutos = new JTable(modeloTabelaProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        List<Produto> produtosLoja = serviceManager.getServiceProduto().listarPorLoja(loja.getId());
        for (Produto p : produtosLoja) {
            modeloTabelaProdutos.addRow(new Object[]{
                    p.getId(), p.getNome(), p.getPreco(), p.getEstoque()
            });
        }

        dialogoAdicionar.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        JPanel painelBotoesAdicionar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSelecionar = new JButton("Adicionar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSelecionar.setEnabled(false);

        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            btnSelecionar.setEnabled(tabelaProdutos.getSelectedRow() != -1);
        });

        painelBotoesAdicionar.add(btnSelecionar);
        painelBotoesAdicionar.add(btnCancelar);

        dialogoAdicionar.add(painelBotoesAdicionar, BorderLayout.SOUTH);

        btnSelecionar.addActionListener(e -> {
            int linhaSelecionada = tabelaProdutos.getSelectedRow();
            if (linhaSelecionada != -1) {
                String idProduto = modeloTabelaProdutos.getValueAt(linhaSelecionada, 0).toString();
                String nomeProduto = modeloTabelaProdutos.getValueAt(linhaSelecionada, 1).toString();
                BigDecimal preco = (BigDecimal) modeloTabelaProdutos.getValueAt(linhaSelecionada, 2);
                int estoque = (Integer) modeloTabelaProdutos.getValueAt(linhaSelecionada, 3);

                if (estoque > 0) {
                    modeloTabelaItens.addRow(new Object[]{idProduto, nomeProduto, preco, 1});
                    dialogoAdicionar.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogoAdicionar, "Produto sem estoque. Por favor, escolha outro.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnCancelar.addActionListener(e -> dialogoAdicionar.dispose());

        dialogoAdicionar.setVisible(true);
    }

    private void acaoAprovarRejeitar(Pedido pedido, StatusPedido novoStatus) {
        try {
            StatusPedido statusParaRejeicao = (novoStatus == StatusPedido.NEGADO) ? StatusPedido.CONCLUIDO : novoStatus;

            serviceManager.getServicePedido().atualizarStatus(pedido, statusParaRejeicao);
            JOptionPane.showMessageDialog(this, "Status do pedido " + pedido.getId() + " atualizado para " + novoStatus, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarPedidos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoAtualizarStatus() throws PedidoNaoEncontradoException {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idPedido = modeloTabela.getValueAt(linhaSelecionada, 0).toString();
        Pedido pedidoParaAtualizar = serviceManager.getServicePedido().getPedidoById(idPedido);

        if (pedidoParaAtualizar == null) {
            throw new PedidoNaoEncontradoException("Pedido não encontrado para atualização.");
        }

        JComboBox<StatusPedido> comboBoxStatus = new JComboBox<>(StatusPedido.values());
        comboBoxStatus.setSelectedItem(pedidoParaAtualizar.getStatus());

        int resultado = JOptionPane.showConfirmDialog(this, comboBoxStatus, "Selecione o novo status",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            StatusPedido novoStatus = (StatusPedido) comboBoxStatus.getSelectedItem();
            try {
                serviceManager.getServicePedido().atualizarStatus(pedidoParaAtualizar, novoStatus);
                JOptionPane.showMessageDialog(this, "Status do pedido atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}