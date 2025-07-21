package tela;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {

    public Login(String[] args) {

        JFrame janela = new JFrame();

        // metodo para parar a execução ao fechar a janela
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setVisible(true);

        // tamanho da janela(Relativo a minha resolução 1920x1080
        janela.setBounds(750, 250, 500, 700);


        //Label
        JLabel usuario = new JLabel("Usuario:");
        usuario.setBounds(70,50,100,30);
        janela.add(usuario);

        JLabel senha = new JLabel("senha:");
        senha.setBounds(70,150,100,30);
        janela.add(senha);

        //Text
        JTextField inserirUser = new JTextField();
        inserirUser.setBounds(70, 80, 150, 30);
        janela.add(inserirUser);

        //JPassword
        JPasswordField inserirSenha = new JPasswordField();
        inserirSenha.setBounds(70,180,150,30);
        janela.add(inserirSenha);

        //Cria Botao Login
        JButton Login = new JButton("Entrar");
        janela.add(Login);
        Login.setBounds(70,250,200,30);
        Login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = inserirUser.getText();
                String password = new String(inserirSenha.getPassword());

                System.out.println("Entrando com o usuario: " + user);
                System.out.println("Senha: " + password);

                inserirUser.setText("");
                inserirSenha.setText("");
            }
        });

        janela.setLayout(null);
    }

}
