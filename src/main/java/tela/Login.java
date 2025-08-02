package tela;

import Dados.DadosUsuario;
import Model.Gerente;
import Model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Login extends JFrame {
    private DadosUsuario dadosUsuario;
    private GerenciaFluxoLogin gerenciaFluxoLogin;

    private JTextField insereEmail;
    private JPasswordField insereSenha;

    public Login(String caminhoUsuario, GerenciaFluxoLogin gerenciaFluxoLogin) {
        this.dadosUsuario = new DadosUsuario(caminhoUsuario);
        this.gerenciaFluxoLogin = gerenciaFluxoLogin;

        setTitle("Tela de Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(450, 550));
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);


        JLabel titleLabel = new JLabel("Bem-vindo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        mainPanel.add(titleLabel, BorderLayout.NORTH);


        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel emailLabel = new JLabel("Email") ;
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(emailLabel, gbc);

        insereEmail = new JTextField("", 20);
        insereEmail.putClientProperty("JComponent.roundRect", true);
        insereEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(insereEmail, gbc);


        JLabel senha = new JLabel("Senha:");
        senha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(senha, gbc);


        insereSenha = new JPasswordField();
        insereSenha.putClientProperty("JComponent.roundRect", true);
        insereSenha.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(insereSenha, gbc);

        JButton botaoLogin = new JButton("Entrar");
        botaoLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botaoLogin.setBackground(new Color(60, 110, 200)); // Um tom de azul
        botaoLogin.setForeground(Color.WHITE); // Texto branco
        botaoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Muda o cursor para a "mãozinha"
        botaoLogin.putClientProperty("JButton.buttonType", "roundRect"); // Borda arredondada (FlatLaf)
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 8, 8, 8); // Mais espaço acima do botão
        formPanel.add(botaoLogin, gbc);

        // Adiciona o painel do formulário ao centro do painel principal
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- Ações e Exibição ---
        botaoLogin.addActionListener(this::verificarLogin);

        // Faz com que o botão "Entrar" seja clicado ao pressionar Enter
        getRootPane().setDefaultButton(botaoLogin);

        pack(); // Ajusta o tamanho da janela ao conteúdo
        setVisible(true);
    }


    private void verificarLogin(ActionEvent e) {
        String email = insereEmail.getText();
        String senha = new  String(insereSenha.getPassword());

        Usuario usuarioEncontrado = null ;
        List<Usuario> listaUsuarios = dadosUsuario.listarTodas() ;

        for (Usuario usuario : listaUsuarios) {
            if (email.equals(usuario.getEmail()) && senha.equals(usuario.getSenha())) {
                usuarioEncontrado = usuario;
                break;
            }
        }

        if (usuarioEncontrado != null) {
            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuarioEncontrado.getNome() + "!");
            this.gerenciaFluxoLogin.sucessoLogin(usuarioEncontrado);

            this.dispose();
        }
        else{
            JOptionPane.showMessageDialog(this,"Usuario ou senhas incorretas!");
            insereEmail.setText("");
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


