package views;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import model.*;
import Service.*;

public class InterfaceDono extends PainelPrincipal {
    private ServiceManager serviceManager;
    private JRadioButton radioPorVolume, radioPorValor;
    private JPanel painelListaRanking;
    private JScrollPane scrollPaneRanking;
    private final GerenciaFluxoLogin fluxoLogin;


    public InterfaceDono(ServiceManager serviceManager, Usuario usuario, GerenciaFluxoLogin fluxoLogin) {
        super("Painel do Dono - " + usuario.getNome());
        this.serviceManager = serviceManager;
        contruirSidebar();
        this.fluxoLogin = fluxoLogin;
        setVisible(true);

    }


    @Override
    public void contruirSidebar() {
        List<Franquia> f = new ArrayList<>();
        JLabel lblTitulo = new JLabel("Menu Principal");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTitulo);
        sidebar.add(Box.createVerticalStrut(20));

        JButton btnFranquias = criarBotaoMenu("Gerenciar Franquias");
        sidebar.add(btnFranquias);

        JButton btnGerentes = criarBotaoMenu("Administrar Gerentes");
        sidebar.add(btnGerentes);

        JButton btnRankingVendedores = criarBotaoMenu("Ranking de Vendedores");
        sidebar.add(btnRankingVendedores);

        sidebar.add(Box.createVerticalGlue());
        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);


        btnFranquias.addActionListener(e -> mostrarFranquias());
        btnSair.addActionListener(e -> fluxoLogin.fazerLogout());


        btnGerentes.addActionListener(e -> mostrarGerentes());

        btnRankingVendedores.addActionListener(e -> mostrarRankingVendedores());

    }


    private void mostrarGerentes() {
        configurarPainelConteudo("Administrar Gerentes");
        painelConteudo.setLayout(new BorderLayout(10, 10));
        JPanel painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS));
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Gerente> gerentes = serviceManager.getServiceUsuario().listarGerentes();

        if (gerentes.isEmpty()) {
            painelListaCards.add(new JLabel("Nenhum gerente cadastrado."));
        } else {
            for (Gerente gerente : gerentes) {
                painelListaCards.add(criarCardGerente(gerente));
                painelListaCards.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(painelListaCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        painelConteudo.add(scrollPane, BorderLayout.CENTER);
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAdicionar = new JButton("Adicionar Gerente");
        JButton btnRemover = new JButton("Remover Gerente");
        JButton btnRebaixar = new JButton("Rebaixar para Vendedor");

        painelBotoesAcao.add(btnAdicionar);
        painelBotoesAcao.add(btnRemover);
        painelBotoesAcao.add(btnRebaixar);

        painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH);
        btnAdicionar.addActionListener(e -> acaoAdicionarGerente());
        btnRemover.addActionListener(e -> acaoRemoverGerente(gerentes));
        btnRebaixar.addActionListener(e -> acaoRebaixarGerente(gerentes));

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private JPanel criarCardGerente(Gerente gerente) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBackground(new Color(245, 245, 245));
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border bordaLinha = BorderFactory.createLineBorder(new Color(200, 200, 200));
        card.setBorder(BorderFactory.createCompoundBorder(bordaLinha, padding));

        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
        painelInfo.setOpaque(false);
        painelInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        JLabel lblNome = new JLabel(gerente.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblNome);

        JLabel lblEmail = new JLabel(gerente.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblEmail);

        Optional<Franquia> franquiaDoGerente = serviceManager.getServiceFranquia().getFranquiaDoGerente(gerente, serviceManager.getServiceLoja());

        String textoExibicao;
        if (franquiaDoGerente == null || franquiaDoGerente.isEmpty()) {
            textoExibicao = "Status: Disponível (sem loja/franquia)";
        } else {
            textoExibicao = "Franquia: " + franquiaDoGerente.get().getNome();
        }

        JLabel lblFranquia = new JLabel(textoExibicao);
        lblFranquia.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFranquia.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblFranquia);


        card.add(painelInfo, BorderLayout.CENTER);
        return card;
    }

    private void acaoAdicionarGerente() {
        Gerente novoGerente = CriaGerente.criarNovoGerente(this, serviceManager);

        if (novoGerente != null) {
            mostrarGerentes();
        }
    }


    private void acaoRemoverGerente(List<Gerente> gerentes) {
        if (gerentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há gerentes para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Gerente> comboBox = new JComboBox<>(gerentes.toArray(new Gerente[0]));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Gerente) {
                    setText(((Gerente) value).getNome());
                }
                return this;
            }
        });

        int resultado = JOptionPane.showConfirmDialog(this, comboBox, "Selecione o Gerente para Remover", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            Gerente gerenteSelecionado = (Gerente) comboBox.getSelectedItem();
            if (gerenteSelecionado == null) {
                return;
            }

            int confirmacaoFinal = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja remover o gerente '" + gerenteSelecionado.getNome() + "'?\nEsta ação é irreversível.",
                    "Confirmar Remoção",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacaoFinal == JOptionPane.YES_OPTION) {
                try {
                    serviceManager.getServiceUsuario().removeUsuario(gerenteSelecionado);
                    JOptionPane.showMessageDialog(this, "Gerente removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    mostrarGerentes();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao remover gerente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void acaoRebaixarGerente(List<Gerente> gerentes) {
        if (gerentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há gerentes para rebaixar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JComboBox<Gerente> comboBox = new JComboBox<>(gerentes.toArray(new Gerente[0]));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Gerente) {
                    setText(((Gerente) value).getNome());
                }
                return this;
            }
        });

        int resultado = JOptionPane.showConfirmDialog(this, comboBox, "Selecione o Gerente para Rebaixar", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            Gerente gerenteSelecionado = (Gerente) comboBox.getSelectedItem();
            if (gerenteSelecionado == null) return;

            try {
                serviceManager.getServiceUsuario().rebaixarGerenteParaVendedor(gerenteSelecionado);
                JOptionPane.showMessageDialog(this, "Gerente rebaixado para Vendedor com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarGerentes();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao rebaixar gerente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void mostrarFranquias() {
        configurarPainelConteudo("Gerenciar Franquias");
        painelConteudo.setLayout(new BorderLayout(10, 10));

        JPanel painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS));
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Franquia> listaDeFranquias = serviceManager.getServiceFranquia().listarTodos();
        if (listaDeFranquias.isEmpty()) {
            painelListaCards.add(new JLabel("Nenhuma franquia cadastrada."));
        } else {
            for (Franquia franquia : listaDeFranquias) {
                painelListaCards.add(criarCardFranquia(franquia));
                painelListaCards.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(painelListaCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        painelConteudo.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAdicionar = new JButton("Adicionar Franquia");

        painelBotoesAcao.add(btnAdicionar);

        painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> acaoAdicionarFranquia());


        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private JPanel criarCardFranquia(Franquia franquia) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBackground(new Color(245, 245, 245));

        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border bordaLinha = BorderFactory.createLineBorder(new Color(200, 200, 200));
        card.setBorder(BorderFactory.createCompoundBorder(bordaLinha, padding));

        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
        painelInfo.setOpaque(false);

        JLabel lblNome = new JLabel(franquia.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblNome);

        String textoLojas = franquia.getIdLojas().size() + " loja(s) cadastrada(s)";
        JLabel labelLojas = new JLabel(textoLojas);
        labelLojas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLojas.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(labelLojas);

        card.add(painelInfo, BorderLayout.CENTER);

        JPanel painelBotoesCard = new JPanel();

        painelBotoesCard.setLayout(new GridLayout(0, 1, 0, 5));
        painelBotoesCard.setOpaque(false);

        JButton btnGerenciar = new JButton("Gerenciar");
        btnGerenciar.addActionListener(e -> new InterfaceGerenciarLojas(serviceManager, franquia));

        JButton btnEditar = new JButton("Editar Franquia");
        btnEditar.addActionListener(e -> acaoEditarFranquia(franquia));

        JButton btnRemover = new JButton("Remover Franquia");
        btnRemover.addActionListener(e -> acaoRemoverFranquia(franquia));

        painelBotoesCard.add(btnGerenciar);
        painelBotoesCard.add(btnEditar);
        painelBotoesCard.add(btnRemover);

        card.add(painelBotoesCard, BorderLayout.EAST);

        return card;
    }

    private void acaoEditarFranquia(Franquia franquiaParaEditar) {
        JTextField txtNome = new JTextField(franquiaParaEditar.getNome());
        JTextField txtEndereco = new JTextField(franquiaParaEditar.getEndereco());
        JTextField txtTelefone = new JTextField(franquiaParaEditar.getTelefone());

        JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
        painelFormulario.add(new JLabel("Nome da Franquia:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Endereço:"));
        painelFormulario.add(txtEndereco);
        painelFormulario.add(new JLabel("Telefone:"));
        painelFormulario.add(txtTelefone);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Editar Franquia",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            franquiaParaEditar.setNome(txtNome.getText().trim());
            franquiaParaEditar.setEndereco(txtEndereco.getText().trim());
            franquiaParaEditar.setTelefone(txtTelefone.getText().trim());

            try {
                if (serviceManager.getServiceFranquia().existeDuplicata(franquiaParaEditar)) {
                    JOptionPane.showMessageDialog(this, "Já existe uma franquia com este nome ou endereço.", "Erro de Duplicidade", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                serviceManager.getServiceFranquia().atualizar(franquiaParaEditar);
                JOptionPane.showMessageDialog(this, "Franquia atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                mostrarFranquias();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao editar franquia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoAdicionarFranquia() {
        JTextField txtNome = new JTextField();
        JTextField txtEndereco = new JTextField();
        JTextField txtTelefone = new JTextField();

        JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
        painelFormulario.add(new JLabel("Nome da Franquia:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Endereço:"));
        painelFormulario.add(txtEndereco);
        painelFormulario.add(new JLabel("Telefone:"));
        painelFormulario.add(txtTelefone);

        int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Adicionar Nova Franquia",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String nome = txtNome.getText().trim();
            String endereco = txtEndereco.getText().trim();
            String telefone = txtTelefone.getText().trim();

            if (nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Franquia novaFranquia = new Franquia(nome, endereco, telefone);
                serviceManager.getServiceFranquia().adicionar(novaFranquia);

                JOptionPane.showMessageDialog(this, "Franquia '" + nome + "' adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                mostrarFranquias();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar franquia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void acaoRemoverFranquia(Franquia franquiaParaRemover) {
        if (franquiaParaRemover == null) return;

        int confirmacaoFinal = JOptionPane.showConfirmDialog(
                this,
                "<html>Tem certeza que deseja remover a franquia <b>'" + franquiaParaRemover.getNome() + "'</b>?<br>" +
                        "<font color='red'><b>ATENÇÃO:</b> Esta ação é irreversível e removerá todos os dados associados.</font></html>",
                "Confirmar Remoção Permanente",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacaoFinal == JOptionPane.YES_OPTION) {
            try {
                serviceManager.getServiceFranquia().remover(franquiaParaRemover, serviceManager);
                JOptionPane.showMessageDialog(this, "Franquia removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                mostrarFranquias();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover franquia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void mostrarRankingVendedores() {
        configurarPainelConteudo("Ranking de Vendedores");
        painelConteudo.setLayout(new BorderLayout(10, 10));

        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPorVolume = new JRadioButton("Ordenar por Volume de Vendas");
        radioPorValor = new JRadioButton("Ordenar por Valor Total");
        ButtonGroup grupoRadios = new ButtonGroup();
        grupoRadios.add(radioPorVolume);
        grupoRadios.add(radioPorValor);
        painelControles.add(new JLabel("Critério de Ordenação:"));
        painelControles.add(radioPorVolume);
        painelControles.add(radioPorValor);

        ActionListener listenerRadios = e -> atualizarListaRanking();
        radioPorVolume.addActionListener(listenerRadios);
        radioPorValor.addActionListener(listenerRadios);

        radioPorValor.setSelected(true);
        painelConteudo.add(painelControles, BorderLayout.NORTH);

        painelListaRanking = new JPanel();
        painelListaRanking.setLayout(new BoxLayout(painelListaRanking, BoxLayout.Y_AXIS));
        painelListaRanking.setBackground(Color.WHITE);
        scrollPaneRanking = new JScrollPane(painelListaRanking);
        scrollPaneRanking.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        painelConteudo.add(scrollPaneRanking, BorderLayout.CENTER);

        atualizarListaRanking();
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void atualizarListaRanking() {
        painelListaRanking.removeAll();

        List<PerformanceVendedor> performance = serviceManager.getServiceRelatorio().gerarRankingVendedores();

        if (radioPorVolume.isSelected()) {
            performance.sort(Comparator.comparingInt(PerformanceVendedor::getNumeroDeVendas).reversed());
        } else {
            performance.sort(Comparator.comparing(PerformanceVendedor::getValorTotalVendas).reversed());
        }

        if (performance.isEmpty()) {
            painelListaRanking.add(new JLabel("Nenhum vendedor com vendas encontradas."));
        } else {
            int rank = 1;
            for (PerformanceVendedor p : performance) {
                painelListaRanking.add(criarCardVendedor(p, rank++));
                painelListaRanking.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        painelListaRanking.revalidate();
        painelListaRanking.repaint();
    }

    private JPanel criarCardVendedor(PerformanceVendedor performance, int rank) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
        painelInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNome = new JLabel(String.format("%dº. %s", rank, performance.getVendedor().getNome()));
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painelInfo.add(lblNome);

        String nomeLoja = (performance.getLoja() != null) ? performance.getLoja().getNome() : "N/A";
        String nomeFranquia = (performance.getFranquia() != null) ? performance.getFranquia().getNome() : "N/A";

        painelInfo.add(new JLabel("Franquia: " + nomeFranquia + " | Loja: " + nomeLoja));
        painelInfo.add(Box.createVerticalStrut(5));
        painelInfo.add(new JLabel(String.format("Número de Vendas: %d pedidos", performance.getNumeroDeVendas())));

        painelInfo.add(new JLabel(String.format("Valor Total Vendido: R$ %.2f", performance.getValorTotalVendas())));

        card.add(painelInfo);
        return card;
    }

}