package tela;

import Model.Gerente;
import Model.Produto;
import Model.Usuario;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class InterfaceGerente extends PainelPrincipal {
    private final ServiceManager serviceManager;
    private final Gerente gerente;

    public InterfaceGerente(ServiceManager serviceManager, Gerente gerente) {
        super("Painel Gerente - " + gerente.getNome());
        this.serviceManager = serviceManager;
        this.gerente = gerente;

        setVisible(true);
        mostrarEstoqueDaLoja();
    }

    private void mostrarEstoqueDaLoja() {
        configurarPainelConteudo("Estoque da Loja");
        try {
            List<Produto> produtos = serviceManager.getServiceProduto().listarPorIDLoja(gerente.getIdloja());
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

    private void mostrarPedidosDaLoja() {
        configurarPainelConteudo("Pedidos da Loja");
        painelConteudo.add(new JLabel("Funcionalidade de pedidos em construção."));
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void mostrarVendedoresDaLoja() {
        configurarPainelConteudo("Vendedores da Loja");
        try {
            var loja = serviceManager.getServiceLoja().getLojaById(gerente.getIdloja());
            if (loja == null) {
                painelConteudo.add(new JLabel("Erro: Loja não encontrada."));
                return;
            }
            List<Usuario> vendedores = serviceManager.getServiceUsuario().getUsuariosPorLoja(loja);
            if (vendedores.isEmpty()) {
                painelConteudo.add(new JLabel("Nenhum vendedor encontrado para esta loja."));
            } else {
                JPanel painelLista = new JPanel();
                painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));

                for (Usuario v : vendedores) {
                    JPanel card = new JPanel(new BorderLayout(10, 10));
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            new EmptyBorder(10, 10, 10, 10)
                    ));
                    card.add(new JLabel(v.getNome()), BorderLayout.CENTER);
                    painelLista.add(card);
                    painelLista.add(Box.createVerticalStrut(5));
                }
                JScrollPane scrollPane = new JScrollPane(painelLista);
                painelConteudo.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar os vendedores: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            painelConteudo.add(new JLabel("Erro ao carregar os vendedores."));
        }
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    @Override
    public void contruirSidebar() {
        sidebar.add(new JLabel("Menu do Gerente"));
        sidebar.add(Box.createVerticalStrut(20));

        JButton botaoEstoque = criarBotaoMenu("Estoque");
        JButton botaoPedidos = criarBotaoMenu("Pedidos");
        JButton botaoVendedores = criarBotaoMenu("Vendedores");
        JButton botaoSair = criarBotaoMenu("Sair");

        sidebar.add(botaoEstoque);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(botaoPedidos);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(botaoVendedores);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(botaoSair);

        botaoEstoque.addActionListener(e -> mostrarEstoqueDaLoja());
        botaoPedidos.addActionListener(e -> mostrarPedidosDaLoja());
        botaoVendedores.addActionListener(e -> mostrarVendedoresDaLoja());
        botaoSair.addActionListener(e -> this.dispose());
    }
}