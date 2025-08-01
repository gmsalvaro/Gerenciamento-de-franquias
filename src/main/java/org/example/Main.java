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
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {

            ServiceManager serviceManager = new ServiceManager("");


            seedInitialData(serviceManager);


            SwingUtilities.invokeLater(() -> {
                new Login(serviceManager, new GerenciaFluxoLogin() {
                    @Override
                    public void sucessoLogin(Usuario usuarioLogado) {
                        System.out.println("Login bem-sucedido para: " + usuarioLogado.getNome());

                        switch (usuarioLogado.getPermissao()) {
                            case 1:
                                new InterfaceDono(serviceManager, (Dono) usuarioLogado);
                                break;
                            case 2:
                                new InterfaceGerente(serviceManager, (Gerente) usuarioLogado);
                                break;
                        }
                    }
                });
            });

        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(null, "Erro crítico ao carregar os dados: " + e.getMessage(), "Erro de Sistema", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private static void seedInitialData(ServiceManager sm) throws PersistenciaException {

        Franquia franquia;
        if (sm.getServiceFranquia().listarFranquias().isEmpty()) {
            franquia = new Franquia("Franquia Principal", "Rua A, 1", "12345");
            sm.getServiceFranquia().addFranquia(franquia);
        } else {
            franquia = sm.getServiceFranquia().listarFranquias().get(0);
        }

        Loja loja;
        if (sm.getServiceLoja().listarTodasAsLojas().isEmpty()) {
            loja = new Loja("Loja Central", "Av. B, 2", franquia.getId());
            sm.getServiceLoja().addLoja(loja, franquia);
        } else {
            loja = sm.getServiceLoja().listarTodasAsLojas().get(0);
        }

        // --- Adiciona Dono ---
        // CORREÇÃO: Em vez de verificar antes, tentamos adicionar. Se já existir, a exceção é capturada.
        try {
            Dono dono = new Dono("alvaro", "alvaro@gmail.com", "teste", "12345678");
            sm.getServiceUsuario().addUsuario(dono);
            System.out.println("Usuário Dono 'alvaro' criado.");
        } catch (PersistenciaException e) {
            // Se o usuário já existe, o addUsuario lança uma exceção. A gente ignora e continua.
            System.out.println("Usuário Dono 'alvaro' já existe, pulando criação.");
        }


        try {
            Gerente gerente = new Gerente("Pedro" , "pedro@email.com","123","12345678");
            sm.getServiceUsuario().addUsuario(gerente);

            loja.addUsuarioID(gerente.getId());
            sm.getServiceLoja().atualizarLoja(loja); // Salva a associação na loja
            System.out.println("Usuário Gerente 'Pedro' criado e associado à Loja.");

        } catch (PersistenciaException e) {
            System.out.println("Usuário Gerente 'Pedro' já existe, pulando criação.");
        }
    }

    private static void abrirInterfaceDono(ServiceManager serviceManager, Dono donoLogado) {
        new InterfaceDono(serviceManager, donoLogado);
    }

    private static void abrirInterfaceGerente(ServiceManager serviceManager, Gerente gerenteLogado) {
        new InterfaceGerente(serviceManager, gerenteLogado);
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












//
//ServiceManager serviceManager = new ServiceManager("Arquivos");
//    ServiceUsuario serviceUsuario = serviceManager.getServiceUsuario();
//    ServiceLoja serviceLoja = serviceManager.getServiceLoja();
//    List<Usuario> usuarios = serviceUsuario.getUsuarios();
//    Franquia franquia = new Franquia("lerdadasdasdasddddo", "leroasdaasdsassddddo", "leroddasdadasdlero");
//    Loja loja = new Loja("lerdasdasdaadasdadsasdo", "leasdasdasdasdsasdsaro", franquia.getId());
//        serviceManager.getServiceLoja().addLoja(loja, franquia);
//        for (Usuario usuario : usuarios) {
//        loja.addUsuarioID(usuario.getId());
//    }
//
//    InterfaceGerenciarFranquia interfaceGerenciarFranquia = new InterfaceGerenciarFranquia(serviceManager);
//    //InterfaceGerenciarUsuario interfaceGerenciarUsuario = new InterfaceGerenciarUsuario(loja, serviceManager, franquia);
//    // interfaceGerenciarUsuario.setVisible(true);
//    InterfaceGerenciarLojas interfaceGerenciarLojas = new InterfaceGerenciarLojas(serviceManager, franquia);
//        interfaceGerenciarLojas.setVisible(true);
