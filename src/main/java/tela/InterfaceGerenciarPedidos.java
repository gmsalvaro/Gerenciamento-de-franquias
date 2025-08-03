package tela;

import Model.Loja;
import Model.Pedido;
import Model.StatusPedido;
import Service.ServiceManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class InterfaceGerenciarPedidos extends JFrame {
    private final Loja loja;
    private final ServiceManager serviceManager;
    private final DefaultTableModel modeloTabela;
    private final JTable tabelaPedidos;

    public InterfaceGerenciarPedidos(Loja loja, ServiceManager serviceManager) {
        super("Gerenciar Pedidos da Loja: " + loja.getNome());
        this.loja = loja;
        this.serviceManager = serviceManager;

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Tabela de Pedidos (Painel Central) ---
        String[] colunas = {"ID Pedido", "Data", "Vendedor", "Valor Total (R$)", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPedidos = new JTable(modeloTabela);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);

        // --- Painel de Botões (Inferior) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAtualizarStatus = new JButton("Atualizar Status do Pedido");
        painelBotoes.add(btnAtualizarStatus);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ação do botão
        btnAtualizarStatus.addActionListener(e -> acaoAtualizarStatus());

        carregarPedidos();
        setVisible(true);
    }

    private void carregarPedidos() {
        modeloTabela.setRowCount(0);
        try {
            List<Pedido> pedidos = serviceManager.getServicePedido().listarPorIDLoja(loja.getId());
            SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Pedido p : pedidos) {
                // Busca o nome do vendedor para exibir na tabela
                String nomeVendedor = serviceManager.getServiceUsuario()
                        .buscarPorId(p.getIdVendedor())
                        .map(Model.Usuario::getNome)
                        .orElse("Não encontrado");

                modeloTabela.addRow(new Object[]{
                        p.getId(),
                        formatadorData.format(p.getDataPedido()),
                        nomeVendedor,
                        p.getPrecoTotal().toPlainString(),
                        p.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoAtualizarStatus() {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idPedido = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        Pedido pedidoParaAtualizar = serviceManager.getServicePedido().getPedidoById(idPedido);

        if (pedidoParaAtualizar == null) {
            JOptionPane.showMessageDialog(this, "Pedido não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Oferece as opções de status em um JComboBox
        JComboBox<StatusPedido> comboBoxStatus = new JComboBox<>(StatusPedido.values());
        comboBoxStatus.setSelectedItem(pedidoParaAtualizar.getStatus());

        int resultado = JOptionPane.showConfirmDialog(this, comboBoxStatus, "Selecione o novo status",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            StatusPedido novoStatus = (StatusPedido)comboBoxStatus.getSelectedItem();
            try {
                serviceManager.getServicePedido().atualizarStatusPedido(pedidoParaAtualizar, novoStatus);
                JOptionPane.showMessageDialog(this, "Status do pedido atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidos(); // Atualiza a tabela
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}