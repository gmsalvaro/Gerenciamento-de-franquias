package tela;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import Model.*;
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

        // Painel para a lista de cards de gerentes
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

        // Painel inferior com os botões de ação
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAdicionar = new JButton("Adicionar Gerente");
        JButton btnRemover = new JButton("Remover Gerente");
        JButton btnRebaixar = new JButton("Rebaixar para Vendedor");

        painelBotoesAcao.add(btnAdicionar);
        painelBotoesAcao.add(btnRemover);
        painelBotoesAcao.add(btnRebaixar);

        painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH);

        // Adiciona as ações aos botões
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

        // Exibe Nome e Email (sem alterações)
        JLabel lblNome = new JLabel(gerente.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblNome);

        JLabel lblEmail = new JLabel(gerente.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblEmail);

        // --- INÍCIO DA CORREÇÃO ---

        // 1. Busca a franquia do gerente, sabendo que o resultado pode ser nulo.
        Optional<Franquia> franquiaDoGerente = serviceManager.getServiceFranquia().getFranquiaDoGerente(gerente, serviceManager.getServiceLoja());

        String textoExibicao;
        // 2. VERIFICA SE O RESULTADO É NULO antes de usá-lo.
        if (franquiaDoGerente.isPresent()) {
            // Se a franquia foi encontrada, exibe o nome dela.
            textoExibicao = "Franquia: " + franquiaDoGerente.get().getNome();
        } else {
            // Se for nulo, o gerente está disponível e não associado a uma franquia.
            textoExibicao = "Status: Disponível (sem loja/franquia)";
        }

        JLabel lblFranquia = new JLabel(textoExibicao);
        lblFranquia.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFranquia.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelInfo.add(lblFranquia);

        // --- FIM DA CORREÇÃO ---

        card.add(painelInfo, BorderLayout.CENTER);
        return card;
    }

    private void acaoAdicionarGerente() {
        Gerente novoGerente = CriaGerente.criarNovoGerente(this, serviceManager);

        // Se um gerente foi realmente criado (usuário não cancelou), atualiza a tela
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

        // Configura o renderer para exibir o nome do gerente
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

        // --- INÍCIO DA LÓGICA IMPLEMENTADA ---
        if (resultado == JOptionPane.OK_OPTION) {
            // 1. Pega o gerente selecionado
            Gerente gerenteSelecionado = (Gerente) comboBox.getSelectedItem();
            if (gerenteSelecionado == null) {
                return; // Nenhuma seleção foi feita
            }

            // 2. Pede uma confirmação final para segurança
            int confirmacaoFinal = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja remover o gerente '" + gerenteSelecionado.getNome() + "'?\nEsta ação é irreversível.",
                    "Confirmar Remoção",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacaoFinal == JOptionPane.YES_OPTION) {
                // 3. Tenta remover o usuário através do serviço
                try {
                    serviceManager.getServiceUsuario().removeUsuario(gerenteSelecionado);
                    JOptionPane.showMessageDialog(this, "Gerente removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // 4. Atualiza a tela para refletir a remoção
                    mostrarGerentes();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao remover gerente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        // --- FIM DA LÓGICA IMPLEMENTADA ---
    }

    private void acaoRebaixarGerente(List<Gerente> gerentes) {
        if (gerentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há gerentes para rebaixar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JComboBox<Gerente> comboBox = new JComboBox<>(gerentes.toArray(new Gerente[0]));

        // --- AQUI ESTÁ A CORREÇÃO ---
        // Configura o renderer para exibir o nome do gerente em vez do "código"
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Gerente) {
                    // Se o objeto na lista for um Gerente, define o texto do item para o nome dele
                    setText(((Gerente) value).getNome());
                }
                return this;
            }
        });
        // --- FIM DA CORREÇÃO ---

        int resultado = JOptionPane.showConfirmDialog(this, comboBox, "Selecione o Gerente para Rebaixar", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            Gerente gerenteSelecionado = (Gerente) comboBox.getSelectedItem();
            if (gerenteSelecionado == null) return; // Garante que algo foi selecionado

            try {
                serviceManager.getServiceUsuario().rebaixarGerenteParaVendedor(gerenteSelecionado);
                JOptionPane.showMessageDialog(this, "Gerente rebaixado para Vendedor com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mostrarGerentes(); // Atualiza a tela
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao rebaixar gerente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void mostrarFranquias() {
        // 1. Prepara o painel de conteúdo
        configurarPainelConteudo("Gerenciar Franquias"); // Limpa o painel e adiciona o título
        painelConteudo.setLayout(new BorderLayout(10, 10)); // Define o layout principal

        // 2. Cria o painel que conterá a lista de cards de franquias
        JPanel painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS)); // Um card embaixo do outro
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 3. Busca os dados e cria um card para cada franquia
        List<Franquia> listaDeFranquias = serviceManager.getServiceFranquia().listarTodos();
        if (listaDeFranquias.isEmpty()) {
            painelListaCards.add(new JLabel("Nenhuma franquia cadastrada."));
        } else {
            for (Franquia franquia : listaDeFranquias) {
                painelListaCards.add(criarCardFranquia(franquia));
                painelListaCards.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento entre os cards
            }
        }

        // 4. Coloca a lista de cards dentro de uma barra de rolagem
        JScrollPane scrollPane = new JScrollPane(painelListaCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        painelConteudo.add(scrollPane, BorderLayout.CENTER); // Adiciona a área de rolagem no centro

        // 5. Cria o painel inferior para os botões de ação
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Alinha botões à direita
        JButton btnAdicionar = new JButton("Adicionar Franquia");

        painelBotoesAcao.add(btnAdicionar);

        painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH); // Adiciona o painel de botões na parte de baixo

        // Ações dos botões de Adicionar e Remover
        btnAdicionar.addActionListener(e -> acaoAdicionarFranquia());


        // Revalida e redesenha o painel para exibir as alterações
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    /**
     * Cria um JPanel estilizado (card) para representar uma única franquia.
     *
     * @param franquia A franquia a ser exibida no card.
     * @return um JPanel configurado como um card.
     */
    private JPanel criarCardFranquia(Franquia franquia) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBackground(new Color(245, 245, 245));

        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border bordaLinha = BorderFactory.createLineBorder(new Color(200, 200, 200));
        card.setBorder(BorderFactory.createCompoundBorder(bordaLinha, padding));

        // 1. Cria um painel para agrupar as informações da esquerda (nome e n° de lojas)
        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS)); // Layout vertical
        painelInfo.setOpaque(false); // Fundo transparente

        // 2. Adiciona o nome da franquia a este novo painel
        JLabel lblNome = new JLabel(franquia.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda
        painelInfo.add(lblNome);

        // 3. Adiciona o número de lojas a este novo painel, debaixo do nome
        String textoLojas = franquia.getIdLojas().size() + " loja(s) cadastrada(s)";
        JLabel labelLojas = new JLabel(textoLojas);
        labelLojas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLojas.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda
        painelInfo.add(labelLojas);

        // 4. Adiciona o painel de informações ao CENTRO do card principal
        card.add(painelInfo, BorderLayout.CENTER);

        // --- PAINEL DE BOTÕES DENTRO DO CARD (ATUALIZADO) ---
        JPanel painelBotoesCard = new JPanel();

        // A MUDANÇA PRINCIPAL: Usamos GridLayout para criar uma coluna única de botões.
        // GridLayout(0, 1) -> Linhas flexíveis, 1 coluna.
        // (0, 5) -> 0 de espaço horizontal, 5 de espaço vertical entre os botões.
        painelBotoesCard.setLayout(new GridLayout(0, 1, 0, 5));
        painelBotoesCard.setOpaque(false);

        // Cria os botões
        JButton btnGerenciar = new JButton("Gerenciar");
        btnGerenciar.addActionListener(e -> new InterfaceGerenciarLojas(serviceManager, franquia));

        JButton btnEditar = new JButton("Editar Franquia");
        btnEditar.addActionListener(e -> acaoEditarFranquia(franquia));

        JButton btnRemover = new JButton("Remover Franquia");
        btnRemover.addActionListener(e -> acaoRemoverFranquia(franquia));

        // Adiciona os botões ao painel. O GridLayout cuidará do tamanho.
        painelBotoesCard.add(btnGerenciar);
        painelBotoesCard.add(btnEditar);
        painelBotoesCard.add(btnRemover);

        // Não precisamos mais de 'setAlignmentX' nem 'Box.createVerticalStrut'

        card.add(painelBotoesCard, BorderLayout.EAST);

        return card;
    }

    private void acaoEditarFranquia(Franquia franquiaParaEditar) {
        // Cria os campos de texto e já preenche com os dados atuais da franquia
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
            // Pega os novos dados e atualiza o objeto
            franquiaParaEditar.setNome(txtNome.getText().trim());
            franquiaParaEditar.setEndereco(txtEndereco.getText().trim());
            franquiaParaEditar.setTelefone(txtTelefone.getText().trim());

            try {
                // Validação de duplicidade (reutilizando a lógica que já tínhamos)
                if (serviceManager.getServiceFranquia().existeDuplicata(franquiaParaEditar)) {
                    JOptionPane.showMessageDialog(this, "Já existe uma franquia com este nome ou endereço.", "Erro de Duplicidade", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Salva a atualização
                serviceManager.getServiceFranquia().atualizar(franquiaParaEditar);
                JOptionPane.showMessageDialog(this, "Franquia atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Atualiza a tela para refletir a mudança
                mostrarFranquias();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao editar franquia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoAdicionarFranquia() {
        // Cria os campos de texto para o formulário
        JTextField txtNome = new JTextField();
        JTextField txtEndereco = new JTextField();
        JTextField txtTelefone = new JTextField();

        // Cria um painel para organizar os labels e os campos
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

            // Validação simples para garantir que os campos não estão vazios
            if (nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe a execução se a validação falhar
            }

            try {
                // Cria a nova franquia e a adiciona através do serviço
                Franquia novaFranquia = new Franquia(nome, endereco, telefone);
                serviceManager.getServiceFranquia().adicionar(novaFranquia);

                JOptionPane.showMessageDialog(this, "Franquia '" + nome + "' adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // ATUALIZA A TELA para exibir a nova franquia
                mostrarFranquias();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar franquia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void acaoRemoverFranquia(Franquia franquiaParaRemover) {
        if (franquiaParaRemover == null) return;

        // Pede uma confirmação final, alertando sobre a gravidade da ação
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
                // Chama o serviço para remover a franquia em cascata
                serviceManager.getServiceFranquia().remover(franquiaParaRemover, serviceManager);
                JOptionPane.showMessageDialog(this, "Franquia removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Atualiza a tela para refletir a remoção
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

        // --- PAINEL DE CONTROLES (NORTE) ---
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

        radioPorValor.setSelected(true); // Ordenar por valor como padrão
        painelConteudo.add(painelControles, BorderLayout.NORTH);

        // --- PAINEL DE CONTEÚDO (CENTRO) ---
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

        // --- ORDENAÇÃO ATUALIZADA ---
        if (radioPorVolume.isSelected()) {
            // Ordena pelo número de vendas (inteiro), do maior para o menor
            performance.sort(Comparator.comparingInt(PerformanceVendedor::getNumeroDeVendas).reversed());
        } else {
            // Ordena pelo valor total (BigDecimal), do maior para o menor
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
        // ... (estilização do card)
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









//        super("Painel do Dono");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(800, 600);
//        setLocationRelativeTo(null);
//
//        // Cria sidebar (esquerda)
//        sidebar = new JPanel();
//        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
//        sidebar.setBackground(new Color(230, 230, 250));
//        sidebar.setPreferredSize(new Dimension(200, getHeight()));
//
//        // Adiciona botões ou menus na sidebar
//        sidebar.add(new JLabel("Menu"));
//        sidebar.add(Box.createVerticalStrut(10));
//        JButton btnFranquias = new JButton("Ver Franquias");
//        sidebar.add(btnFranquias);
//        JButton btnSair = new JButton("Sair");
//        sidebar.add(btnSair);
//
//        // Painel de conteúdo (direita)
//        painelConteudo = new JPanel();
//        painelConteudo.setLayout(new BorderLayout());
//        //Pegar a lista de Franquias no Service -> Arquivo
//        List<Franquia> franquias = serviceFranquia.listarFranquias();
//
//        // Quando clicar em "Ver Franquias"
//        btnFranquias.addActionListener(e -> mostrarFranquias(franquias));
//        btnSair.addActionListener(e -> System.exit(0)); //Implementar para voltar para o login
//
//        // Usa JSplitPane para dividir a tela
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, painelConteudo);
//        splitPane.setDividerLocation(200); // Largura da sidebar
//        splitPane.setEnabled(false); // Impede redimensionamento
//
//        add(splitPane);
//        setVisible(true);