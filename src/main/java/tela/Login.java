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
    private final GerenciaFluxoLogin gerenciaFluxoLogin;

    private final JTextField insereEmail;
    private final JPasswordField insereSenha;

    public Login(ServiceManager serviceManager, GerenciaFluxoLogin gerenciaFluxoLogin) {
        this.gerenciaFluxoLogin = gerenciaFluxoLogin;
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
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(botaoLogin, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        botaoLogin.addActionListener(this::verificarLogin);
        getRootPane().setDefaultButton(botaoLogin);
        pack();
        setVisible(true);
    }

    // O método verificarLogin agora está correto e não precisa de alterações
    private void verificarLogin(ActionEvent e) {
        String email = insereEmail.getText().trim();
        String senha = new String(insereSenha.getPassword());

        try {
            Usuario usuarioEncontrado = this.serviceManager.getServiceUsuario().autenticarUsuario(email, senha);

            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuarioEncontrado.getNome() + "!");

            if (this.gerenciaFluxoLogin != null) {
                this.gerenciaFluxoLogin.sucessoLogin(usuarioEncontrado);
            }

            this.dispose();

        } catch (UsuarioInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
            insereSenha.setText("");
        }
    }
}

//        //Label's
//        JLabel usuario = new JLabel("Usuario:");
//        usuario.setBounds(100,50,100,30);
//        janela.add(usuario);
//
//        JLabel senha = new JLabel("senha: ");
//        senha.setBounds(100,150,100,30);
//        janela.add(senha);
//
//        JLabel cadas = new JLabel("Nao possui Cadastro?");
//        cadas.setBounds(300, 200, 180, 30);
//        janela.add(cadas);
//
//
//        //Tanto o JtextFIeld quanto JPassword irao ser a parte da entrada/inserção dos dados seja usuario/CPF e senha
////
//        //Text
//        JTextField insereEmail = new JTextField();
//        insereEmail.setBounds(100, 80, 150, 30);
//        janela.add(insereEmail);
//
//        //JPassword
//        JPasswordField inserePass = new JPasswordField();
//        inserePass.setBounds(100,180,150,30);
//        janela.add(inserePass);
//
//        //Cria Botao Login
//        JButton Login = new JButton("Entrar");
//        janela.add(Login);
//        Login.setBounds(100,250,150,30);
//        Login.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String email = insereEmail.getText();
//                String password = new String(inserePass.getPassword());
//                System.out.println("Entrando com o usuario: " + email);
//                System.out.println("Senha: " + password);
//                List<Usuario> usuarioList = dadosUsuario.listarTodas();
//                for (Usuario usuario : usuarioList) {
//                    if (usuario.getEmail().equals(email) && usuario.getSenha().equals(password)){
//                        switch (usuario.getPermissao()) {
//                            case 1:
//                                JOptionPane.showMessageDialog(null, "Bem vindo!");
//                                //new InterfaceDono();
//                                janela.dispose();
//                                break;
//                            case 2:
//                                //new InterfaceGerente();
//                                janela.dispose();
//                                break;
//                            case 3:
//                                //new InterfaceVendedor();
//                                janela.dispose();
//                                break;
//                        }
//                    } else { // Utilizar Excessoes
//                        JOptionPane.showMessageDialog(janela, "Usuario ou senha incorretos");
//                        insereEmail.setText("");
//                        inserePass.setText("");
//                    }
//                }
//
//
//            }
//        });
//
//        //botao Cadastro
//        JButton Cadastro = new JButton("Cadastro");
//        janela.add(Cadastro);
//        Cadastro.setBounds(300,250,150,30);
//        Cadastro.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Tentando Fazer Cadastro!");
//                //Adicionar implementação para redirecionar para a interface de cadastro?
//                //Nova Classe?? ou apenas a funcionalidade aqui?
//                //Dependendo do caso pode ser ruim para a legebilidade do codigo ou para a memoria do programa!
//            }
//        });
//
//
//    }


