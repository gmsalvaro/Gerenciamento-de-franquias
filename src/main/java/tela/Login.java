package tela;

import Model.Usuario;
import Service.ServiceManager;
import exception.autenticacao.UsuarioInvalidoException;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


public class Login extends JFrame {

    private final ServiceManager serviceManager;

    private final JTextField insereEmail;
    private final JPasswordField insereSenha;

    public Login(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;

        setTitle("Tela de Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(450, 480));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Bem-vindo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(emailLabel, gbc);

        insereEmail = new JTextField("", 20);
        insereEmail.putClientProperty("JComponent.roundRect", true);
        insereEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(insereEmail, gbc);

        JLabel senha = new JLabel("Senha:");
        senha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(senha, gbc);

        insereSenha = new JPasswordField();
        insereSenha.putClientProperty("JComponent.roundRect", true);
        insereSenha.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(insereSenha, gbc);

        JButton botaoLogin = new JButton("Entrar");
        botaoLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botaoLogin.setBackground(new Color(60, 110, 200));
        botaoLogin.setForeground(Color.WHITE);
        botaoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoLogin.putClientProperty("JButton.buttonType", "roundRect");
        botaoLogin.addActionListener(this::verificarLogin);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 8, 8, 8);
        gbc.gridwidth = 2;
        formPanel.add(botaoLogin, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        getRootPane().setDefaultButton(botaoLogin);
        pack();
        setVisible(true);
    }

    private void verificarLogin(ActionEvent e) {
        String email = insereEmail.getText().trim();
        String senha = new String(insereSenha.getPassword());

        try {
            Usuario usuarioEncontrado = this.serviceManager.getServiceUsuario().autenticarUsuario(email, senha);

            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuarioEncontrado.getNome() + "!");

            switch (usuarioEncontrado.getPermissao()) {
                case 1:
                    new InterfaceDono(serviceManager, usuarioEncontrado).setVisible(true);
                    break;
                case 2:
                    new InterfaceGerente(serviceManager, usuarioEncontrado).setVisible(true);
                    break;
                case 3:
                    new InterfaceVendedor(serviceManager, usuarioEncontrado).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Permissão de usuário desconhecida.", "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
                    return; // Retorna sem fechar a janela de login
            }

            this.dispose();

        } catch (UsuarioInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
            insereSenha.setText("");
        }
    }


}