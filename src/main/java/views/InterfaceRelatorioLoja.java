//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package views;

import model.Loja;
import model.Pedido;
import model.Usuario;
import service.ServiceManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

public class InterfaceRelatorioLoja extends JFrame{
    private final Loja loja;
    private final ServiceManager serviceManager;

    public InterfaceRelatorioLoja(Loja loja, ServiceManager serviceManager) {
        super("Relatórios de Desempenho - Loja: " + loja.getNome());
        this.loja = loja;
        this.serviceManager = serviceManager;

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel painelMetricas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        BigDecimal faturamento = serviceManager.getServiceRelatorio().calcularFaturamentoLoja(loja);
        painelMetricas.add(new JLabel("Faturamento Total (Pedidos Concluídos):"));
        JLabel lblFaturamento = new JLabel(String.format("R$ %.2f", faturamento));
        lblFaturamento.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelMetricas.add(lblFaturamento);

        add(painelMetricas, BorderLayout.NORTH);

        String[] colunas = {"ID Pedido", "Data", "Vendedor", "Valor Total (R$)"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        JTable tabelaHistorico = new JTable(modeloTabela);
        add(new JScrollPane(tabelaHistorico), BorderLayout.CENTER);

        List<Pedido> historico = serviceManager.getServiceRelatorio().getHistoricoVendas(loja);
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Pedido p : historico) {
            String nomeVendedor = serviceManager.getServiceUsuario().buscarPorId(p.getIdVendedor())
                    .map(Usuario::getNome).orElse("N/A");
            modeloTabela.addRow(new Object[]{p.getId(), formatador.format(p.getDataPedido()), nomeVendedor, p.getPrecoTotal().toPlainString()});
        }

        setVisible(true);
    }
}