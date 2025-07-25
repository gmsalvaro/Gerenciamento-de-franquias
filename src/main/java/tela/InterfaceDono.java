package tela;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceDono {


    public InterfaceDono(){
        JFrame janelaDono = new JFrame();
        janelaDono.setTitle("Interface CEO");
        janelaDono.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaDono.setVisible(true);
        janelaDono.setLayout(null);

        janelaDono.setBounds(850, 250, 1280, 720);


        // Aprendendo o Menu --->
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem openItem = new JMenuItem("Abrir");
        JMenuItem saveItem = new JMenuItem("Salvar");
        JMenuItem exitItem = new JMenuItem("Sair");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        janelaDono.setJMenuBar(menuBar);

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        // Final do Menu <---

        //Label's
        JLabel userDono = new JLabel("Usuario: " + "150.521.376-21");
        userDono.setBounds(100,50,200,50);
        janelaDono.add(userDono);

        JLabel senhaDono = new JLabel("senha: " + "********");
        senhaDono.setBounds(300,50,200,50);
        janelaDono.add(senhaDono);


    }






    //Obs: Nao utilizar Type no momento do JSon

}
