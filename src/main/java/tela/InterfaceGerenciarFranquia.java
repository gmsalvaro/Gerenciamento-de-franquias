package tela;

import Model.Franquia;
import Model.Loja; // Mantenha este import se Loja for usada em outro lugar, mas não é para a remoção em cascata aqui.
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class InterfaceGerenciarFranquia extends JFrame {

    private JList<Franquia> listaFranquias;
    private DefaultListModel<Franquia> listModel;

    private JTextField txtNome;
    private JTextField txtEndereco;
    private JTextField txtTelefone;

    private JButton btnAdicionar;
    private JButton btnRemover;
    private JButton btnEditar;

    private ServiceManager serviceManager;

    public InterfaceGerenciarFranquia(ServiceManager serviceManager) {
        super("Gerenciar Franquias");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
        setSize(700, 500);
        setLocationRelativeTo(null); // Centraliza a janela

        this.serviceManager = serviceManager;

        listModel = new DefaultListModel<>();
        listaFranquias = new JList<>(listModel);
        // Define um CellRenderer personalizado para exibir apenas o nome da franquia
        listaFranquias.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Franquia) {
                    setText(((Franquia) value).getNome());
                }
                return this;
            }
        });
        listaFranquias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listaFranquias);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Franquias"));

        // Carrega as franquias iniciais
        carregarFranquias();

        listaFranquias.addListSelectionListener((ListSelectionEvent listEvent) -> {
            if (!listEvent.getValueIsAdjusting()) {
                Franquia selecionada = listaFranquias.getSelectedValue();
                if (selecionada != null) {
                    exibirDetalhesFranquia(selecionada);
                    habilitarCamposFormulario(true);
                    btnEditar.setEnabled(true);
                    btnRemover.setEnabled(true);
                    // Garante que o botão Adicionar volta à sua função original
                    for (var al : btnAdicionar.getActionListeners()) {
                        btnAdicionar.removeActionListener(al);
                    }
                    btnAdicionar.setText("Adicionar");
                    btnAdicionar.addActionListener(actionEvent -> prepararParaAdicionarNovaFranquia());
                } else {
                    limparCampos();
                    habilitarCamposFormulario(false);
                    btnEditar.setEnabled(false);
                    btnRemover.setEnabled(false);
                    btnAdicionar.setEnabled(true);
                    btnAdicionar.setText("Adicionar");
                    for (var al : btnAdicionar.getActionListeners()) {
                        btnAdicionar.removeActionListener(al);
                    }
                    btnAdicionar.addActionListener(actionEvent -> prepararParaAdicionarNovaFranquia());
                }
            }
        });

        // Painel do formulário de detalhes
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Detalhes da Franquia"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        painelFormulario.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1;
        txtEndereco = new JTextField(20);
        painelFormulario.add(txtEndereco, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        txtTelefone = new JTextField(20);
        painelFormulario.add(txtTelefone, gbc);

        // Painel de botões de ação
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("Adicionar");
        btnRemover = new JButton("Remover");
        btnEditar = new JButton("Editar");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnEditar);

        JPanel painelDireitoCompleto = new JPanel(new BorderLayout());
        painelDireitoCompleto.add(painelFormulario, BorderLayout.NORTH);
        painelDireitoCompleto.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        painelDireitoCompleto.add(painelBotoes, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, painelDireitoCompleto);
        splitPane.setDividerLocation(200);
        add(splitPane);

        // Adiciona os ActionListeners
        btnAdicionar.addActionListener(actionEvent -> prepararParaAdicionarNovaFranquia());
        btnRemover.addActionListener(actionEvent -> acaoRemoverFranquia());
        btnEditar.addActionListener(actionEvent -> acaoEditarFranquia());

        limparCampos();
        habilitarCamposFormulario(false);
        btnRemover.setEnabled(false);
        btnEditar.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }

    private void carregarFranquias() {
        listModel.clear();
        List<Franquia> franquias = serviceManager.getServiceFranquia().listarFranquias();
        for (Franquia f : franquias) {
            listModel.addElement(f);
        }
    }

    private void prepararParaAdicionarNovaFranquia() {
        limparCampos();
        habilitarCamposFormulario(false); // Desabilita os campos do painel principal

        JTextField dialogNome = new JTextField(20);
        JTextField dialogEndereco = new JTextField(20);
        JTextField dialogTelefone = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nome:"));
        panel.add(dialogNome);
        panel.add(new JLabel("Endereço:"));
        panel.add(dialogEndereco);
        panel.add(new JLabel("Telefone:"));
        panel.add(dialogTelefone);

        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Nova Franquia",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nome = dialogNome.getText().trim();
            String endereco = dialogEndereco.getText().trim();
            String telefone = dialogTelefone.getText().trim();

            if (nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Franquia novaFranquia = new Franquia(nome, endereco, telefone);

            try {
                serviceManager.getServiceFranquia().addFranquia(novaFranquia);
                JOptionPane.showMessageDialog(this, "Franquia '" + nome + "' adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listModel.addElement(novaFranquia);
            } catch (PersistenciaException persistenciaException) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar franquia: " + persistenciaException.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Reseta o estado dos botões e campos após a operação
        btnAdicionar.setText("Adicionar");
        btnAdicionar.setEnabled(true);
        listaFranquias.clearSelection();
        limparCampos();
        habilitarCamposFormulario(false);
        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);
    }

    private void acaoRemoverFranquia() {
        Franquia selecionada = listaFranquias.getSelectedValue();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma franquia para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Esta validação da lista de lojas já está contida na lógica de remoção em cascata do ServiceFranquia.
        // É melhor ter um único ponto de decisão e aviso.
        // A interface deve apenas informar sobre a irreversibilidade.
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Tem certeza que deseja remover a franquia <b>'" + selecionada.getNome() + "'</b>?<br>" +
                        "<b>ATENÇÃO:</b> Isso removerá permanentemente todas as lojas, usuários, produtos e pedidos associados a esta franquia. Esta ação não pode ser desfeita!</html>",
                "Confirmar Remoção Irreversível", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // CHAMA O MÉTODO DE REMOÇÃO EM CASCATA NO SERVICEFRANQUIA
                // O ServiceFranquia.removeFranquia agora recebe o ServiceManager como parâmetro
                serviceManager.getServiceFranquia().removeFranquia(selecionada, serviceManager);

                JOptionPane.showMessageDialog(this, "Franquia e todos os dados associados removidos com sucesso!", "Removido", JOptionPane.INFORMATION_MESSAGE);
                listModel.removeElement(selecionada); // Remove da lista visual após sucesso

                limparCampos();
                habilitarCamposFormulario(false);
                btnRemover.setEnabled(false);
                btnEditar.setEnabled(false);
                btnAdicionar.setEnabled(true);

            } catch (PersistenciaException exception) { // Use a exceção específica de persistência
                JOptionPane.showMessageDialog(this, "Erro ao remover franquia: " + exception.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) { // Captura outras exceções inesperadas
                JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao remover a franquia: " + exception.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoEditarFranquia() {
        Franquia selecionada = listaFranquias.getSelectedValue();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma franquia para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = txtNome.getText().trim();
        String endereco = txtEndereco.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios para edição.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selecionada.setNome(nome);
        selecionada.setEndereco(endereco);
        selecionada.setTelefone(telefone);

        try {
            // Chamando o método correto: atualizarFranquia, não apenas atualizar
            serviceManager.getServiceFranquia().atualizar(selecionada);
            JOptionPane.showMessageDialog(this, "Franquia atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            listaFranquias.repaint(); // Atualiza a exibição na lista
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar franquia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirDetalhesFranquia(Franquia franquia) {
        txtNome.setText(franquia.getNome());
        txtEndereco.setText(franquia.getEndereco());
        txtTelefone.setText(franquia.getTelefone());
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEndereco.setText("");
        txtTelefone.setText("");
    }

    private void habilitarCamposFormulario(boolean habilitar) {
        txtNome.setEnabled(habilitar);
        txtEndereco.setEnabled(habilitar);
        txtTelefone.setEnabled(habilitar);
    }
}