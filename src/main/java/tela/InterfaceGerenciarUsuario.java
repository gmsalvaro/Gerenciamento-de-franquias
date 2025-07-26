package tela;

import Model.Usuario;
import Model.Vendedor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList; // Ainda necessário se você criar uma lista no main para teste
import java.util.List;

public class InterfaceGerenciarUsuario extends JFrame {

    private JList<Usuario> listaUsuarios;
    private DefaultListModel<Usuario> listModel;

    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JTextField txtDescricao;

    private JButton btnAdicionar;
    private JButton btnRemover;
    private JButton btnEditar;

    // private UserService userService;


    public InterfaceGerenciarUsuario(List<Usuario> usuariosIniciais) {
        super("Gerenciar Usuários");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);


        listModel = new DefaultListModel<>();
        listaUsuarios = new JList<>(listModel);
        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listaUsuarios);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Usuários"));

        if (usuariosIniciais != null) {
            for (Usuario u : usuariosIniciais) {
                listModel.addElement(u);
            }
        }



        listaUsuarios.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Usuario selecionado = listaUsuarios.getSelectedValue();
                    if (selecionado != null) {
                        exibirDetalhesUsuario(selecionado);
                        habilitarCamposFormulario(true);
                        btnEditar.setEnabled(true);
                        btnRemover.setEnabled(true);
                        btnAdicionar.setText("Adicionar"); // Garante que o texto volte ao normal
                        // Remove e readiciona o listener para evitar múltiplos listeners em btnAdicionar
                        for (java.awt.event.ActionListener al : btnAdicionar.getActionListeners()) {
                            btnAdicionar.removeActionListener(al);
                        }
                        btnAdicionar.addActionListener(event -> prepararParaAdicionarNovoUsuario());

                    } else {
                        limparCampos();
                        habilitarCamposFormulario(false);
                        btnEditar.setEnabled(false);
                        btnRemover.setEnabled(false);
                        btnAdicionar.setEnabled(true);
                        btnAdicionar.setText("Adicionar");
                    }
                }
            }
        });

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Detalhes do Usuário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        painelFormulario.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        painelFormulario.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        txtTelefone = new JTextField(20);
        painelFormulario.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        txtDescricao = new JTextField(20);
        painelFormulario.add(txtDescricao, gbc);

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

        btnAdicionar.addActionListener(e -> prepararParaAdicionarNovoUsuario());
        btnRemover.addActionListener(e -> acaoRemoverUsuario());
        btnEditar.addActionListener(e -> acaoEditarUsuario());

        limparCampos();
        habilitarCamposFormulario(false);
        btnRemover.setEnabled(false);
        btnEditar.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }

    public void recarregarListaUsuarios(List<Usuario> novosUsuarios) {
        listModel.clear();
        if (novosUsuarios != null) {
            for (Usuario u : novosUsuarios) {
                listModel.addElement(u);
            }
        }
        // Opcional: limpar campos e desabilitar botões após recarregar
        limparCampos();
        habilitarCamposFormulario(false);
        btnRemover.setEnabled(false);
        btnEditar.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }

    private void exibirDetalhesUsuario(Usuario usuario) {
        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
        //txtTelefone.setText(usuario.getTelefone());
        //txtDescricao.setText(usuario.getDescricao());
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        txtDescricao.setText("");
    }

    private void habilitarCamposFormulario(boolean habilitar) {
        txtNome.setEnabled(habilitar);
        txtEmail.setEnabled(habilitar);
        txtTelefone.setEnabled(habilitar);
        txtDescricao.setEnabled(habilitar);
    }

    private void prepararParaAdicionarNovoUsuario() {
        limparCampos();
        habilitarCamposFormulario(true);
        listaUsuarios.clearSelection();

        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnAdicionar.setText("Salvar Novo");

        for (java.awt.event.ActionListener al : btnAdicionar.getActionListeners()) {
            btnAdicionar.removeActionListener(al);
        }
        btnAdicionar.addActionListener(e -> acaoSalvarNovoUsuario());
    }

    private void acaoSalvarNovoUsuario() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Email são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario novoUsuario = new Vendedor(nome, email, telefone, descricao);

        try {
            // userService.adicionarUsuario(novoUsuario); // Chame seu serviço AQUI
            JOptionPane.showMessageDialog(this, "Usuário '" + nome + "' adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // AVISO: A recarga da lista precisará vir do seu Service!
            // Exemplo: recarregarListaUsuarios(userService.listarTodosUsuarios());
            // Por enquanto, apenas para demonstrar a atualização visual, se você não tem o service:
            listModel.addElement(novoUsuario); // Adiciona diretamente se não tem service real
            // A linha acima só funciona se o objeto 'novoUsuario' for exatamente o que o listModel precisa.

            limparCampos();
            habilitarCamposFormulario(false);
            btnAdicionar.setText("Adicionar");
            for (java.awt.event.ActionListener al : btnAdicionar.getActionListeners()) {
                btnAdicionar.removeActionListener(al);
            }
            btnAdicionar.addActionListener(e -> prepararParaAdicionarNovoUsuario());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
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
                // userService.removerUsuario(selecionado); // Chame seu serviço AQUI
                JOptionPane.showMessageDialog(this, "Usuário '" + selecionado.getNome() + "' removido com sucesso.", "Removido", JOptionPane.INFORMATION_MESSAGE);

                // AVISO: A recarga da lista precisará vir do seu Service!
                // Exemplo: recarregarListaUsuarios(userService.listarTodosUsuarios());
                listModel.removeElement(selecionado); // Remove diretamente se não tem service real

                limparCampos();
                habilitarCamposFormulario(false);
                btnRemover.setEnabled(false);
                btnEditar.setEnabled(false);
                btnAdicionar.setEnabled(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Email são obrigatórios para edição.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selecionado.setNome(nome);
        selecionado.setEmail(email);
        //selecionado.setTelefone(telefone);
        //selecionado.setDescricao(descricao);

        try {
            // userService.atualizarUsuario(selecionado); // Chame seu serviço AQUI
            JOptionPane.showMessageDialog(this, "Usuário '" + selecionado.getNome() + "' atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Força a JList a atualizar a exibição do item
            int selectedIndex = listModel.indexOf(selecionado);
            if (selectedIndex != -1) {
                listModel.setElementAt(selecionado, selectedIndex);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
