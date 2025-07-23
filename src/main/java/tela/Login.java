package tela;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.example.Main;

public class Login {

    public Login() {

        JFrame janela = new JFrame();
        janela.setTitle("Tela inicial!");

        // metodo para parar a execução ao fechar a janela
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setVisible(true);
        janela.setLayout(null);

        // tamanho da janela(Relativo a minha resolução 1920x1080
        janela.setBounds(750, 250, 700, 700);


        //Label's
        JLabel usuario = new JLabel("Usuario:");
        usuario.setBounds(100,50,100,30);
        janela.add(usuario);

        JLabel senha = new JLabel("senha: ");
        senha.setBounds(100,150,100,30);
        janela.add(senha);

        JLabel cadas = new JLabel("Nao possui Cadastro?");
        cadas.setBounds(300, 200, 180, 30);
        janela.add(cadas);


        //Tanto o JtextFIeld quanto JPassword irao ser a parte da entrada/inserção dos dados seja usuario/CPF e senha

        //Text
        JTextField insereUser = new JTextField();
        insereUser.setBounds(100, 80, 150, 30);
        janela.add(insereUser);

        //JPassword
        JPasswordField inserePass = new JPasswordField();
        inserePass.setBounds(100,180,150,30);
        janela.add(inserePass);

        //Cria Botao Login
        JButton Login = new JButton("Entrar");
        janela.add(Login);
        Login.setBounds(100,250,150,30);
        Login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = insereUser.getText();
                String password = new String(inserePass.getPassword());
                System.out.println("Entrando com o usuario: " + user);
                System.out.println("Senha: " + password);


                // Redirecionar utilizando o JSON (ainda nao entendi como e nem tenho arquivo)
                if(user.equals("150.521.376-21") && password.equals("ADMINadmin@123")){
                    JOptionPane.showMessageDialog(null, "Bem vindo!");
                    new InterfaceDono();
                    janela.dispose();
                } else if(user.equals("111.333.666-77") && password.equals("gerente#45")){
                    new InterfaceGerente();
                    janela.dispose();
                } else if(user.equals("222.444.555-88") && password.equals("vendeDOR*%")){
                    new InterfaceVendedor();
                    janela.dispose();
                } else
                    JOptionPane.showMessageDialog(janela, "Usuario ou senha incorretos");

                insereUser.setText("");
                inserePass.setText("");

            }
        });

        //botao Cadastro
        JButton Cadastro = new JButton("Cadastro");
        janela.add(Cadastro);
        Cadastro.setBounds(300,250,150,30);
        Cadastro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Tentando Fazer Cadastro!");
                //Adicionar implementação para redirecionar para a interface de cadastro?
                //Nova Classe?? ou apenas a funcionalidade aqui?
                //Dependendo do caso pode ser ruim para a legebilidade do codigo ou para a memoria do programa!
            }
        });


    }

}
