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
