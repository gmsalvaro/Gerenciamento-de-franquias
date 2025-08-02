package tela;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import Model.Franquia;
import Model.Loja;
import Model.Usuario;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

public class InterfaceGerenciarLojas extends PainelPrincipal {
    private final ServiceManager serviceManager;
    private final Franquia franquia;
    private final Usuario usuario;

    public InterfaceGerenciarLojas(ServiceManager serviceManager, Franquia franquia, Usuario usuario) {
        super("Gerenciar Lojas - " + franquia.getNome());
        this.serviceManager = serviceManager;
        this.franquia = franquia;
        this.usuario = usuario;

        mostrarLojas();
    }

    @Override
    public void contruirSidebar() {
        sidebar.add(new JLabel("Gerenciar Lojas"));
        sidebar.add(Box.createVerticalStrut(20));

        JButton btnVoltar = criarBotaoMenu("Voltar");
        sidebar.add(btnVoltar);

        sidebar.add(Box.createVerticalGlue());

        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);

        btnVoltar.addActionListener(e -> {
            this.dispose();
            new InterfaceDono(serviceManager, usuario);
        });

        btnSair.addActionListener(e -> {
            this.dispose();
            new Login(serviceManager).setVisible(true);
        });
    }

    private void mostrarLojas() {
        configurarPainelConteudo("Lojas da Franquia");

        try {
            List<Loja> lojas = serviceManager.getServiceLoja().listarPorIDFranquia(franquia.getId());

            JPanel painelPrincipal = new JPanel(new BorderLayout());
            painelPrincipal.setBackground(new Color(240, 242, 245));

            JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            JButton btnAdicionarLoja = new JButton("Adicionar Loja");

            btnAdicionarLoja.addActionListener(e -> adicionarLoja());
            painelBotoesAcao.add(btnAdicionarLoja);
            painelPrincipal.add(painelBotoesAcao, BorderLayout.NORTH);

            if (lojas.isEmpty()) {
                JPanel painelVazio = new JPanel(new GridBagLayout());
                painelVazio.add(new JLabel("Nenhuma loja encontrada para esta franquia."));
                painelVazio.setBackground(Color.WHITE);
                painelPrincipal.add(painelVazio, BorderLayout.CENTER);
            } else {
                JPanel painelLista = new JPanel();
                painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
                painelLista.setBorder(new EmptyBorder(10, 10, 10, 10));

                for (Loja lojaAtual : lojas) {
                    painelLista.add(criarCardLoja(lojaAtual));
                    painelLista.add(Box.createVerticalStrut(15));
                }

                JScrollPane scrollPane = new JScrollPane(painelLista);
                painelPrincipal.add(scrollPane, BorderLayout.CENTER);
            }
            painelConteudo.add(painelPrincipal, BorderLayout.CENTER);
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lojas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            painelConteudo.add(new JLabel("Erro ao carregar as lojas."));
        }

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void adicionarLoja() {
        // Crie um JDialog para coletar informações da nova loja
        JDialog dialog = new JDialog(this, "Adicionar Nova Loja", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel painelInputs = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField nomeField = new JTextField();
        JTextField enderecoField = new JTextField();

        painelInputs.add(new JLabel("Nome da Loja:"));
        painelInputs.add(nomeField);
        painelInputs.add(new JLabel("Endereço:"));
        painelInputs.add(enderecoField);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            String nome = nomeField.getText();
            String endereco = enderecoField.getText();
            if (!nome.isEmpty() && !endereco.isEmpty()) {
                try {
                    // Crie um objeto Loja e adicione-o
                    Loja novaLoja = new Loja(nome, endereco, franquia.getId());
                    serviceManager.getServiceLoja().addLoja(novaLoja, franquia);
                    JOptionPane.showMessageDialog(this, "Loja adicionada com sucesso!");
                    dialog.dispose();
                    mostrarLojas(); // Atualiza a lista
                } catch (PersistenciaException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar a loja: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(painelInputs, BorderLayout.CENTER);
        dialog.add(btnSalvar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editarLoja(Loja loja) {
        // Lógica para abrir um JDialog de edição
        JDialog dialog = new JDialog(this, "Editar Loja", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel painelInputs = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField nomeField = new JTextField(loja.getNome());
        JTextField enderecoField = new JTextField(loja.getEndereco());

        painelInputs.add(new JLabel("Nome da Loja:"));
        painelInputs.add(nomeField);
        painelInputs.add(new JLabel("Endereço:"));
        painelInputs.add(enderecoField);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            String novoNome = nomeField.getText();
            String novoEndereco = enderecoField.getText();
            if (!novoNome.isEmpty() && !novoEndereco.isEmpty()) {
                try {
                    loja.setNome(novoNome);
                    loja.setEndereco(novoEndereco);
                    serviceManager.getServiceLoja().atualizarLoja(loja);
                    JOptionPane.showMessageDialog(this, "Loja editada com sucesso!");
                    dialog.dispose();
                    mostrarLojas();
                } catch (PersistenciaException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao editar a loja: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(painelInputs, BorderLayout.CENTER);
        dialog.add(btnSalvar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void removerLoja(Loja loja) {
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover a loja " + loja.getNome() + "?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                serviceManager.getServiceLoja().removerLoja(loja.getId());
                JOptionPane.showMessageDialog(this, "Loja removida com sucesso!");
                mostrarLojas(); // Atualiza a lista
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Erro ao remover a loja: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel criarCardLoja(Loja lojaAtual) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        card.setBackground(new Color(250, 250, 250));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nomeLoja = new JLabel("<html><b>Nome:</b> " + lojaAtual.getNome() + "</html>");
        nomeLoja.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel enderecoLoja = new JLabel("<html><b>Endereço:</b> " + lojaAtual.getEndereco() + "</html>");
        enderecoLoja.setFont(new Font("Arial", Font.PLAIN, 12));

        int numeroPedidos = (lojaAtual.getIdPedidos() != null) ? lojaAtual.getIdPedidos().size() : 0;
        JLabel labelNumeroPedidos = new JLabel("<html><b>Número de Pedidos:</b> " + numeroPedidos + "</html>");
        labelNumeroPedidos.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel labelArrecadacao = new JLabel("<html><b>Arrecadação Total:</b> (Função não implementada) </html>");
        labelArrecadacao.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(nomeLoja);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(enderecoLoja);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(labelNumeroPedidos);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(labelArrecadacao);
        infoPanel.add(Box.createVerticalGlue());

        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new BoxLayout(botoesPanel, BoxLayout.Y_AXIS));
        botoesPanel.setOpaque(false);
        botoesPanel.setBorder(new EmptyBorder(5, 5, 5, 10));

        JButton btnGerenciarUsuarios = new JButton("Gerenciar Usuários");
        btnGerenciarUsuarios.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGerenciarUsuarios.addActionListener(e -> {
            new InterfaceGerenciarUsuario(serviceManager, lojaAtual).setVisible(true);
        });

        JButton btnGerenciarProdutos = new JButton("Gerenciar Produtos");
        btnGerenciarProdutos.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGerenciarProdutos.addActionListener(e -> {
            try {
                new InterfaceGerenciarEstoque(serviceManager, lojaAtual).setVisible(true);
            } catch (PersistenciaException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir tela de estoque: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnGerenciarPedidos = new JButton("Gerenciar Pedidos");
        btnGerenciarPedidos.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGerenciarPedidos.addActionListener(e -> {
            new InterfaceGerenciarPedidos(serviceManager, lojaAtual).setVisible(true);
        });

        JButton btnEditar = new JButton("Editar");
        btnEditar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEditar.addActionListener(e -> editarLoja(lojaAtual));

        JButton btnRemover = new JButton("Remover");
        btnRemover.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRemover.addActionListener(e -> removerLoja(lojaAtual));

        botoesPanel.add(btnGerenciarUsuarios);
        botoesPanel.add(Box.createVerticalStrut(5));
        botoesPanel.add(btnGerenciarProdutos);
        botoesPanel.add(Box.createVerticalStrut(5));
        botoesPanel.add(btnGerenciarPedidos);
        botoesPanel.add(Box.createVerticalStrut(15));
        botoesPanel.add(btnEditar);
        botoesPanel.add(Box.createVerticalStrut(5));
        botoesPanel.add(btnRemover);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(botoesPanel, BorderLayout.EAST);

        return card;
    }
}