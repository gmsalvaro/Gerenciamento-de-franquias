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

public class InterfaceGerente extends PainelPrincipal {
    private final ServiceManager serviceManager;
    private final Usuario gerente;

    public InterfaceGerente(ServiceManager serviceManager, Usuario usuario) {
        super("Painel Gerente - " + usuario.getNome());
        this.serviceManager = serviceManager;

        // Valida se o usuário tem permissão de gerente
        if (usuario.getPermissao() == 2) {
            this.gerente = usuario;
        } else {
            throw new IllegalArgumentException("O usuário fornecido não possui permissão de gerente.");
        }

        setVisible(true);
        mostrarEstoqueDaLoja();
    }

    private void mostrarEstoqueDaLoja() {
        configurarPainelConteudo("Estoque da Loja");
        try {
            Loja loja = serviceManager.getServiceLoja().getLojaByIDUsuario(gerente.getId());
            List<Produto> produtos = serviceManager.getServiceProduto().listarPorIDLoja(loja.getId());

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
            Loja loja = serviceManager.getServiceLoja().getLojaByIDUsuario(gerente.getId());

            if (loja == null) {
                painelConteudo.add(new JLabel("Erro: Loja não encontrada."));
                return;
            }

            List<Usuario> usuarios = serviceManager.getServiceUsuario().getUsuariosPorLoja(loja);

            // Filtra apenas os usuários com permissão de vendedor (ex: permissao == 2)
            List<Usuario> vendedores = usuarios.stream()
                    .filter(u -> u.getPermissao() == 2)
                    .toList();

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
