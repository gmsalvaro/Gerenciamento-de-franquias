package tela;

import Model.Franquia;
import Model.Loja;
import Model.Usuario;
import Model.Vendedor;
import Model.Gerente; // Importar Gerente
import Service.ServiceManager;
import exception.autenticacao.SenhaInvalidaException;
import exception.persistencia.PersistenciaException;
import exception.usuario.ValidacaoUsuarioException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class InterfaceGerenciarUsuario extends JFrame {

    private JList<Usuario> listaUsuarios;
    private DefaultListModel<Usuario> listModel;

    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JTextField txtPermissao;
    private JTextField txtCpf; // Novo campo para o CPF

    private JButton btnAdicionar;
    private JButton btnRemover;
    private JButton btnEditar;

    private Loja loja;
    private Franquia franquia;
    private ServiceManager serviceManager;

    public InterfaceGerenciarUsuario(Loja loja, ServiceManager serviceManager, Franquia franquia) {
        super("Gerenciar Usuários");
        this.loja = loja;
        this.serviceManager = serviceManager;
        this.franquia = franquia;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        listaUsuarios = new JList<>(listModel);
        listaUsuarios.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    setText(((Usuario) value).getNome());
                }
                return this;
            }
        });
        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listaUsuarios);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Usuários"));

        List<Usuario> usuariosIniciais = serviceManager.getServiceUsuario().getUsuariosPorLoja(loja);
        for (Usuario u : usuariosIniciais) {
            System.out.println(u.getNome());
            listModel.addElement(u);
        }

        listaUsuarios.addListSelectionListener((ListSelectionEvent listEvent) -> {
            if (!listEvent.getValueIsAdjusting()) {
                Usuario selecionado = listaUsuarios.getSelectedValue();
                if (selecionado != null) {
                    exibirDetalhesUsuario(selecionado);
                    habilitarCamposFormulario(true);
                    btnEditar.setEnabled(true);
                    btnRemover.setEnabled(true);
                    for (var al : btnAdicionar.getActionListeners()) {
                        btnAdicionar.removeActionListener(al);
                    }
                    btnAdicionar.setText("Adicionar");
                    btnAdicionar.addActionListener(actionEvent -> prepararParaAdicionarNovoUsuario());
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
                    btnAdicionar.addActionListener(actionEvent -> prepararParaAdicionarNovoUsuario());
                }
            }
        });

        // painel do formulário
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Detalhes do Usuário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        painelFormulario.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        painelFormulario.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        txtTelefone = new JTextField(20);
        painelFormulario.add(txtTelefone, gbc);

        // Novo campo para o CPF
        gbc.gridx = 0; gbc.gridy = 3;
        painelFormulario.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        painelFormulario.add(txtCpf, gbc);

        // Campo para exibir a permissão
        gbc.gridx = 0; gbc.gridy = 4; // Mude a linha do grid para acomodar o CPF
        painelFormulario.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1;
        txtPermissao = new JTextField(20);
        txtPermissao.setEditable(false);
        painelFormulario.add(txtPermissao, gbc);

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

        btnAdicionar.addActionListener(actionEvent -> prepararParaAdicionarNovoUsuario());
        btnRemover.addActionListener(actionEvent -> acaoRemoverUsuario());
        btnEditar.addActionListener(actionEvent -> acaoEditarUsuario());

        limparCampos();
        habilitarCamposFormulario(false);
        btnRemover.setEnabled(false);
        btnEditar.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }

    private void prepararParaAdicionarNovoUsuario() {
        limparCampos();
        habilitarCamposFormulario(false);

        JTextField dialogNome = new JTextField(20);
        JTextField dialogEmail = new JTextField(20);
        JTextField dialogTelefone = new JTextField(20);
        JTextField dialogCpf = new JTextField(20);
        String[] tiposUsuario = {"Vendedor", "Gerente", "Dono"};
        JComboBox<String> dialogTipo = new JComboBox<>(tiposUsuario);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nome:"));
        panel.add(dialogNome);
        panel.add(new JLabel("Email:"));
        panel.add(dialogEmail);
        panel.add(new JLabel("Telefone:"));
        panel.add(dialogTelefone);
        panel.add(new JLabel("CPF:"));
        panel.add(dialogCpf);
        panel.add(new JLabel("Tipo de Usuário:"));
        panel.add(dialogTipo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Novo Usuário",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nome = dialogNome.getText().trim();
            String email = dialogEmail.getText().trim();
            String telefone = dialogTelefone.getText().trim();
            String cpf = dialogCpf.getText().trim(); // Pega o valor do CPF
            String tipoSelecionado = (String) dialogTipo.getSelectedItem();

            if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome, Email e CPF são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario novoUsuario;
            if ("Dono".equals(tipoSelecionado)) {
                novoUsuario = new Gerente(nome, email, telefone, cpf);
            } else if ("Gerente".equals(tipoSelecionado)) {
                novoUsuario = new Gerente(nome, email, telefone, cpf);
            } else { // Vendedor
                novoUsuario = new Vendedor(nome, email, telefone, cpf);
            }

            try {
                serviceManager.getServiceUsuario().addUsuario(novoUsuario);
                loja.addUsuarioID(novoUsuario.getId());
                serviceManager.getServiceLoja().atualizarLoja(loja);

                JOptionPane.showMessageDialog(this, "Usuário '" + nome + "' adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listModel.addElement(novoUsuario);

            } catch (PersistenciaException | ValidacaoUsuarioException e) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar usuário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        btnAdicionar.setText("Adicionar");
        btnAdicionar.setEnabled(true);
        listaUsuarios.clearSelection();
        limparCampos();
        habilitarCamposFormulario(false);
        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);
    }

    private void acaoRemoverUsuario() {
        Usuario selecionado = listaUsuarios.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover o usuário '" + selecionado.getNome() + "'?", "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                serviceManager.getServiceUsuario().removeUsuario(selecionado);
                loja.removeUsuario(selecionado.getId());
                serviceManager.getServiceLoja().atualizarLoja(loja);

                JOptionPane.showMessageDialog(this, "Usuário removido com sucesso!", "Removido", JOptionPane.INFORMATION_MESSAGE);
                listModel.removeElement(selecionado);

                limparCampos();
                habilitarCamposFormulario(false);
                btnRemover.setEnabled(false);
                btnEditar.setEnabled(false);
                btnAdicionar.setEnabled(true);

            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Erro ao remover usuário: " + exception.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoEditarUsuario() {
        Usuario selecionado = listaUsuarios.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Email são obrigatórios para edição.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selecionado.setNome(nome);
        selecionado.setEmail(email);
        selecionado.setTelefone(telefone);

        try {
            serviceManager.getServiceUsuario().atualizarUsuario(selecionado);
            JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            listaUsuarios.repaint();
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirDetalhesUsuario(Usuario usuario) {
        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
        txtTelefone.setText(usuario.getTelefone());
        txtCpf.setText(usuario.getCpf()); // Exibe o CPF
        txtPermissao.setText(getPermissaoString(usuario.getPermissao()));
    }

    private String getPermissaoString(int permissao) {
        return switch (permissao) {
            case 1 -> "Dono";
            case 2 -> "Gerente";
            case 3 -> "Vendedor";
            default -> "Desconhecido";
        };
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        txtCpf.setText("");
        txtPermissao.setText("");
    }

    private void habilitarCamposFormulario(boolean habilitar) {
        txtNome.setEnabled(habilitar);
        txtEmail.setEnabled(habilitar);
        txtTelefone.setEnabled(habilitar);
        txtCpf.setEnabled(habilitar);
        txtPermissao.setEnabled(false);
    }
}