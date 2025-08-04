package org.example;

import Model.*;
import Service.ServiceManager;
import tela.*;
import javax.swing.*;
import java.io.File;

public class Main {

    private static final String DATA_PATH = "data";
    private static JFrame frameAtual;

    public static void main(String[] args) {
        // A aplicação agora é iniciada por este metodo
        iniciarAplicacao();
    }

    /**
     * Inicia a aplicação, configurando os serviços e mostrando a tela de login.
     * Este metodo será chamado tanto no início quanto no logout.
     */
    public static void iniciarAplicacao() {
        SwingUtilities.invokeLater(() -> {
            try {
                new File(DATA_PATH).mkdirs(); // Garante que a pasta de dados exista
                ServiceManager serviceManager = new ServiceManager("usuario.json", "lojas.json", "produtos.json", "pedidos.json", "franquia.json");
                seedInitialData(serviceManager);

                // Cria a implementação do callback de login e logout
                GerenciaFluxoLogin fluxoLogin = new GerenciaFluxoLogin() {
                    @Override
                    public void sucessoLogin(Usuario usuarioLogado) {
                        if (frameAtual != null) {
                            frameAtual.dispose();
                        }

                        // Abre a janela correta e GUARDA a referência dela
                        switch (usuarioLogado.getPermissao()) {
                            case 1 -> frameAtual = new InterfaceDono(serviceManager, usuarioLogado, this);
                            case 2 -> frameAtual = new InterfaceGerente(serviceManager, (Gerente) usuarioLogado, this);
                            case 3 -> frameAtual = new InterfaceVendedor(serviceManager, (Vendedor) usuarioLogado, this);
                            default -> JOptionPane.showMessageDialog(null, "Permissão desconhecida.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    @Override
                    public void fazerLogout() {
                        // Fecha a janela atual e reinicia o ciclo mostrando o login
                        if (frameAtual != null) {
                            frameAtual.dispose();
                        }
                        iniciarAplicacao();
                    }
                };

                // Mostra a primeira tela de Login
                new Login(serviceManager, fluxoLogin);

            } catch (Exception e) {
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
                serviceManager.getServiceUsuario().adicionar(dono);
                System.out.println("    Dono adicionado.");

                System.out.println("    Adicionando Gerente...");
                serviceManager.getServiceUsuario().adicionar(gerente);
                serviceManager.getServiceUsuario().adicionar(gerente2);
                System.out.println("    Gerente adicionado.");

                System.out.println("    Adicionando Vendedor...");
                serviceManager.getServiceUsuario().adicionar(vendedor);
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

