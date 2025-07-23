package tela;

import javax.swing.*;

public class InterfaceVendedor {



    public InterfaceVendedor()
    {
        JFrame janelVendedor = new JFrame();
        janelVendedor.setTitle("Interface vendedores");
        janelVendedor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelVendedor.setVisible(true);
        janelVendedor.setLayout(null);

        janelVendedor.setBounds(850, 250, 1280, 720);


        //Label's
        JLabel userGerente = new JLabel("Usuario: " + "222.444.555-88");
        userGerente.setBounds(100,50,200,50);
        janelVendedor.add(userGerente);

        JLabel senhaGerente = new JLabel("senha: " + "********");
        senhaGerente.setBounds(300,50,200,50);
        janelVendedor.add(senhaGerente);


    }



}
