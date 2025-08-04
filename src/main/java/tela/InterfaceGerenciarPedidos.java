package tela;

import Model.Loja;
import Model.Pedido;
import Model.Produto;
import Model.StatusPedido;
import Service.ServiceManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        btnAtualizarStatus.addActionListener(e -> acaoAtualizarStatus());
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
                        .map(Model.Usuario::getNome)
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
        dialogo.setSize(600, 500);
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

        String[] colunasItens = {"ID", "Item", "Preço Unitário", "Quantidade"};
        DefaultTableModel modeloTabelaItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 1; // Apenas a quantidade é editável
            }
        };
        JTable tabelaItens = new JTable(modeloTabelaItens);

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
        dialogo.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAprovar = new JButton("Aprovar Edição");
        JButton btnRejeitar = new JButton("Rejeitar Edição");
        JButton btnCancelar = new JButton("Cancelar");

        painelBotoesAcao.add(btnAprovar);
        painelBotoesAcao.add(btnRejeitar);
        painelBotoesAcao.add(btnCancelar);
        dialogo.add(painelBotoesAcao, BorderLayout.SOUTH);

        btnAprovar.addActionListener(e -> {
            try {
                Map<String, Integer> novosProdutosNoPedido = new HashMap<>();
                BigDecimal novoPrecoTotal = BigDecimal.ZERO;

                for (int i = 0; i < modeloTabelaItens.getRowCount(); i++) {
                    String idProduto = modeloTabelaItens.getValueAt(i, 0).toString();
                    int novaQuantidade = Integer.parseInt(modeloTabelaItens.getValueAt(i, 3).toString());

                    novosProdutosNoPedido.put(idProduto, novaQuantidade);

                    BigDecimal precoUnitario = new BigDecimal(modeloTabelaItens.getValueAt(i, 2).toString());
                    novoPrecoTotal = novoPrecoTotal.add(precoUnitario.multiply(new BigDecimal(novaQuantidade)));
                }

                // 1. Atualiza o objeto com os novos dados
                pedidoParaEditar.setProdutosNoPedido(novosProdutosNoPedido);
                pedidoParaEditar.setPrecoTotal(novoPrecoTotal);
                pedidoParaEditar.setStatus(StatusPedido.PENDENTE);

                // 2. Persiste o objeto atualizado
                serviceManager.getServicePedido().atualizar(pedidoParaEditar);

                JOptionPane.showMessageDialog(this, "Pedido " + pedidoParaEditar.getId() + " editado e aprovado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidos();
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valores de preço ou quantidade inválidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
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

    private void acaoAprovarRejeitar(Pedido pedido, StatusPedido novoStatus) {
        try {
            serviceManager.getServicePedido().atualizarStatus(pedido, novoStatus);
            JOptionPane.showMessageDialog(this, "Status do pedido " + pedido.getId() + " atualizado para " + novoStatus, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarPedidos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoAtualizarStatus() {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idPedido = modeloTabela.getValueAt(linhaSelecionada, 0).toString();
        Pedido pedidoParaAtualizar = serviceManager.getServicePedido().getPedidoById(idPedido);

        if (pedidoParaAtualizar == null) {
            JOptionPane.showMessageDialog(this, "Pedido não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
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