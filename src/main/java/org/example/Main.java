
import Dados.*;
import Model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import tela.InterfaceGerenciarUsuario;
import tela.InterfaceDono;
import tela.InterfaceGerenciarLojas;
import tela.Login;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String caminhoUsuario = "usuario.json";
        DadosUsuario dadosUsuario = new DadosUsuario(caminhoUsuario);
        Dono Dono = new Dono("alvaro", "alvaro@gmail.com", "teste", "12345678");
        dadosUsuario.adicionar(Dono);
        //SwingUtilities.invokeLater(() -> new Login(caminhoUsuario));

        List<Franquia> franquias = List.of(
                new Franquia("Franquia A", "Rua 1", "123"),
                new Franquia("Franquia B", "Rua 2", "123"),
                new Franquia("Franquia C", "Rua 3", "123")
        );
        List<Loja> lojas = List.of(
                new Loja("Franquia A", "Rua 1", "123"),
                new Loja("Franquia B", "Rua 2", "123"),
                new Loja("Franquia C", "Rua 3", "123")
        );

        //SwingUtilities.invokeLater(() -> new InterfaceGerenciarLojas(lojas));

        //SwingUtilities.invokeLater(() -> new InterfaceDono(franquias));
        SwingUtilities.invokeLater(() -> {
            List<Usuario> usuariosDeExemplo = new ArrayList<>();
            // Adicione alguns usuários de exemplo aqui
            usuariosDeExemplo.add(new Vendedor("Carlos Silva", "carlos@exemplo.com", "(31) 98765-4321", "Cliente VIP"));
            usuariosDeExemplo.add(new Vendedor("Ana Paula", "ana@exemplo.com", "(21) 91234-5678", "Novo Cadastro"));
            usuariosDeExemplo.add(new Vendedor("Pedro Costa", "pedro@exemplo.com", "(11) 99887-6655", "Suporte Técnico"));

            new InterfaceGerenciarUsuario(usuariosDeExemplo).setVisible(true);
        });

    }






}
