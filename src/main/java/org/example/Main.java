package org.example;

import Dados.*;
import Model.*;
import Service.ServiceLoja;
import Service.ServiceManager;
import Service.ServiceUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import exception.persistencia.PersistenciaException;
import exception.usuario.ValidacaoUsuarioException;
import tela.*;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String DATA_PATH = "data";

    public static void main(String[] args) {
        System.out.println("--- INICIANDO APLICAÇÃO ---");
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("step 1 -> Criando o diretório de dados...");
                new File(DATA_PATH).mkdirs();
                System.out.println("Diretório '" + DATA_PATH + "' verificado/criado.");

                System.out.println("step 2 -> Inicializando o ServiceManager...");
                ServiceManager serviceManager = new ServiceManager(DATA_PATH);
                System.out.println("ServiceManager inicializado com sucesso.");

                System.out.println("step 3 -> Executando seedInitialData...");
                seedInitialData(serviceManager);
                System.out.println("seedInitialData finalizado.");

                System.out.println("step 4 -> Criando a tela de Login...");
                GerenciaFluxoLogin fluxoLogin = new GerenciaFluxoLogin() {
                    @Override
                    public void sucessoLogin(Usuario usuarioLogado) {
                        switch (usuarioLogado.getPermissao()) {
                            case 1:
                                new InterfaceDono(serviceManager, (Dono) usuarioLogado);
                                break;
                            case 2:
                                new InterfaceGerente(serviceManager, (Gerente) usuarioLogado);
                                break;
                            case 3:
                                JOptionPane.showMessageDialog(null, "Login como VENDEDOR OK. Tela em construção.");
                                break;
                            default:
                                JOptionPane.showMessageDialog(null, "Permissão desconhecida.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                new Login(serviceManager, fluxoLogin);
                System.out.println("Tela de Login criada.");

            } catch (Exception e) {
                System.err.println("!!! ERRO CRÍTICO NA INICIALIZAÇÃO !!!");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ocorreu um erro crítico ao iniciar a aplicação:\n" + e.getMessage(), "Erro Fatal", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void seedInitialData(ServiceManager serviceManager) {
        System.out.println("  [Dentro de seedInitialData] Verificando se a base de usuários está vazia...");

        if (serviceManager.getServiceUsuario().getUsuarios().isEmpty()) {
            System.out.println("  [Dentro de seedInitialData] A base está vazia! Tentando criar usuários de teste...");
            try {
                Dono dono = new Dono("alvaro", "alvaro@gmail.com", "Senha@123", "11111111111");
                Gerente gerente = new Gerente("Pedro", "pedro@email.com", "Senha@123", "22222222222");
                Gerente gerente2 = new Gerente("Heitor", "heitor@email.com", "Senha@123", "77777777777");
                Vendedor vendedor = new Vendedor("Vendedor teste", "vendedor@email.com", "Senha@123", "33333333333");

                System.out.println("    Adicionando Dono...");
                serviceManager.getServiceUsuario().addUsuario(dono);
                System.out.println("    Dono adicionado.");

                System.out.println("    Adicionando Gerente...");
                serviceManager.getServiceUsuario().addUsuario(gerente);
                serviceManager.getServiceUsuario().addUsuario(gerente2);
                System.out.println("    Gerente adicionado.");

                System.out.println("    Adicionando Vendedor...");
                serviceManager.getServiceUsuario().addUsuario(vendedor);
                System.out.println("    Vendedor adicionado.");

                System.out.println("  [Dentro de seedInitialData] Usuários de teste criados com sucesso!");

            } catch (Exception e) {
                // Se um erro acontecer aqui, esta mensagem aparecerá no console
                System.err.println("!!! ERRO DENTRO DO seedInitialData AO TENTAR ADICIONAR USUÁRIOS !!!");
                e.printStackTrace();
            }
        } else {
            System.out.println("  [Dentro de seedInitialData] A base de dados já contém " + serviceManager.getServiceUsuario().getUsuarios().size() + " usuários. Nenhuma ação foi tomada.");
        }
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
