package tela;

import Model.Loja;
import Model.Produto;
import Model.Usuario;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class InterfaceVendedor extends PainelPrincipal {
    private final ServiceManager serviceManager;
    private final Usuario usuario;

    public InterfaceVendedor(ServiceManager serviceManager, Usuario usuario) {
        super("Painel Vendedor - " + usuario.getNome());
        this.serviceManager = serviceManager;
        this.usuario = usuario;  // Mantemos como Usuario, sem cast

        setVisible(true);
        mostrarPainelVendas();
    }

    private void mostrarPainelVendas() {
        configurarPainelConteudo("Realizar Venda");

        try {
            Loja lojaDoVendedor = serviceManager.getServiceLoja().getLojaByIDUsuario(usuario.getId());

            if (lojaDoVendedor != null) {
                InterfaceGerenciarVendas telaVendas = new InterfaceGerenciarVendas(serviceManager, lojaDoVendedor);
                telaVendas.setVisible(true);
                painelConteudo.add(new JLabel("A interface de vendas foi aberta em uma nova janela."));
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível encontrar a loja do vendedor.", "Erro", JOptionPane.ERROR_MESSAGE);
                painelConteudo.add(new JLabel("Erro: Loja não encontrada."));
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de vendas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            painelConteudo.add(new JLabel("Erro ao carregar a tela de vendas."));
        }
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void mostrarEstoqueDaLoja() {
        configurarPainelConteudo("Estoque da Loja");

        try {
            Loja lojaDoVendedor = serviceManager.getServiceLoja().getLojaByIDUsuario(usuario.getId());

            if (lojaDoVendedor == null) {
                painelConteudo.add(new JLabel("Não foi possível encontrar a loja do vendedor."));
                painelConteudo.revalidate();
                painelConteudo.repaint();
                return;
            }

            List<Produto> produtos = serviceManager.getServiceProduto().listarPorIDLoja(lojaDoVendedor.getId());
            if (produtos.isEmpty()) {
                painelConteudo.add(new JLabel("Nenhum produto encontrado nesta loja."));
            } else {
                JPanel painelLista = new JPanel();
                painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));

                for (Produto p : produtos) {
                    JPanel card = new JPanel(new BorderLayout(10, 10));
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            new EmptyBorder(10, 10, 10, 10)));
                    card.add(new JLabel(p.getNome()), BorderLayout.CENTER);
                    card.add(new JLabel("Qtd: " + p.getEstoque()), BorderLayout.EAST);
                    painelLista.add(card);
                    painelLista.add(Box.createVerticalStrut(5));
                }
                JScrollPane scrollPane = new JScrollPane(painelLista);
                painelConteudo.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            painelConteudo.add(new JLabel("Erro ao carregar o estoque."));
        }
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    @Override
    public void contruirSidebar() {
        sidebar.add(new JLabel("Menu do Vendedor"));
        sidebar.add(Box.createVerticalStrut(20));

        JButton botaoVendas = criarBotaoMenu("Realizar Venda");
        JButton botaoEstoque = criarBotaoMenu("Estoque");
        JButton botaoSair = criarBotaoMenu("Sair");

        sidebar.add(botaoVendas);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(botaoEstoque);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(botaoSair);

        botaoVendas.addActionListener(e -> mostrarPainelVendas());
        botaoEstoque.addActionListener(e -> mostrarEstoqueDaLoja());
        botaoSair.addActionListener(e -> this.dispose());
    }
}
