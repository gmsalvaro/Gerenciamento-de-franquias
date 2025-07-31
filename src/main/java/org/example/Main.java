package org.example;

import Dados.*;
import Model.*;
import Service.ServiceLoja;
import Service.ServiceManager;
import Service.ServiceUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import exception.persistencia.PersistenciaException;
import tela.*;

import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws PersistenciaException {
        ServiceManager serviceManager = new ServiceManager("Arquivos");
        ServiceUsuario serviceUsuario = serviceManager.getServiceUsuario();
        ServiceLoja serviceLoja = serviceManager.getServiceLoja();
        List<Usuario> usuarios = serviceUsuario.getUsuarios();
        Loja loja = new Loja("lerdasdasdaadasdadsasdo", "leasdasdasdasdsasdsaro", "dasdassdasdasds");
        Produto produto = new Produto("refri", 4);
        Map<String, Integer> produtosNoPedido = new HashMap<>();
        produtosNoPedido.put("refri", 4);
        Date dataPedido = new Date();
        Pedido pedido = new Pedido(loja.getId(), produtosNoPedido,dataPedido,"Não concluido");
        serviceManager.getServicePedido().addPedido(pedido);



//        Franquia franquia = new Franquia("lerdadasdasdasddddo", "leroasdaasdsassddddo", "leroddasdadasdlero");
//        Loja loja = new Loja("lerdasdasdaadasdadsasdo", "leasdasdasdasdsasdsaro", franquia.getId());
//        serviceManager.getServiceLoja().addLoja(loja, franquia);
//        for (Usuario usuario : usuarios) {
//            loja.addUsuarioID(usuario.getId());
//        }

        InterfaceGerenciarFranquia interfaceGerenciarFranquia = new InterfaceGerenciarFranquia(serviceManager);
        //InterfaceGerenciarUsuario interfaceGerenciarUsuario = new InterfaceGerenciarUsuario(loja, serviceManager, franquia);
        //interfaceGerenciarUsuario.setVisible(true);
        //InterfaceGerenciarLojas interfaceGerenciarLojas = new InterfaceGerenciarLojas(serviceManager, franquia);
        //interfaceGerenciarLojas.setVisible(true);


//        String caminhoUsuario = "usuario.json";
//        DadosUsuario dadosUsuario = new DadosUsuario(caminhoUsuario);
//        Dono Dono = new Dono("alvaro", "alvaro@gmail.com", "teste", "12345678");
//        Gerente gerente = new Gerente("Pedro","pedronalon@email.com","123","12345678");
//        dadosUsuario.adicionar(gerente);
//        dadosUsuario.adicionar(Dono);
//        SwingUtilities.invokeLater(() -> {
//            new Login(caminhoUsuario, new GerenciaFluxoLogin() {
//                @Override
//                public void sucessoLogin(Usuario usuarioLogado) {
//                    System.out.println("Login bem sucedido para" + usuarioLogado);
//
//                    switch (usuarioLogado.getPermissao()){
//                        case 1:
//                          abrirInterfaceDono();
//                        break;
//
//                        case 2:
//                           //abrirInterfaceGerente() ;
//                            break;
//
//                       case 3:
//                           break;
//
//                        default:
//                            //lançar exception
//                    }
//                }
//            });
//        });

    ;}

//    private static void abrirInterfaceDono(){
//        List<Franquia> franquiasDeExemplo = List.of(
//                new Franquia("Franquia A", "Rua 1", "123"),
//                new Franquia("Franquia B", "Rua 2", "123")
//        );
//
//        // A InterfaceDono é criada e exibida
//        new InterfaceDono(franquiasDeExemplo);
//    }

    private static void abrirInterfaceGerente(){

    }

}
//new Login(caminhoUsuario));
//
//List<Franquia> franquias = List.of(
//        new Franquia("Franquia A", "Rua 1", "123"),
//        new Franquia("Franquia B", "Rua 2", "123"),
//        new Franquia("Franquia C", "Rua 3", "123")
//);
//List<Loja> lojas = List.of(
//        new Loja("Franquia A", "Rua 1", "123"),
//        new Loja("Franquia B", "Rua 2", "123"),
//        new Loja("Franquia C", "Rua 3", "123")
//);
//
//        SwingUtilities.invokeLater(() -> new InterfaceDono(franquias));
//        SwingUtilities.invokeLater(() -> new InterfaceGerenciarLojas(lojas));
//        SwingUtilities.invokeLater(() -> {
//List<Usuario> usuariosDeExemplo = new ArrayList<>();
//// Adicione alguns usuários de exemplo aqui
//            usuariosDeExemplo.add(new Vendedor("Carlos Silva", "carlos@exemplo.com", "(31) 98765-4321", "Cliente VIP"));
//        usuariosDeExemplo.add(new Vendedor("Ana Paula", "ana@exemplo.com", "(21) 91234-5678", "Novo Cadastro"));
//        usuariosDeExemplo.add(new Vendedor("Pedro Costa", "pedro@exemplo.com", "(11) 99887-6655", "Suporte Técnico"));
//
//        new InterfaceGerenciarUsuario(usuariosDeExemplo).setVisible(true);
//        });