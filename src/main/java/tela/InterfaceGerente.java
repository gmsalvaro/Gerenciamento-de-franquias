package tela;

import javax.swing.*;

public class InterfaceGerente {



     public InterfaceGerente()
     {
         JFrame janelaGerente = new JFrame();
         janelaGerente.setTitle("Interface Gerente");
         janelaGerente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         janelaGerente.setVisible(true);
         janelaGerente.setLayout(null);

         janelaGerente.setBounds(850, 250, 1280, 720);


         //Label's
         JLabel userGerente = new JLabel("Usuario: " + "111.333.666-77");
         userGerente.setBounds(100,50,200,50);
         janelaGerente.add(userGerente);

         JLabel senhaGerente = new JLabel("senha: " + "********");
         senhaGerente.setBounds(300,50,200,50);
         janelaGerente.add(senhaGerente);


     }


}
