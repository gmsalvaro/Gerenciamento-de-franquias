package tela;

import Model.Franquia;
import Model.Loja;
import Model.Usuario;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class InterfaceDono extends PainelPrincipal {
    private final ServiceManager serviceManager;
    private final Usuario usuario;

    public InterfaceDono(ServiceManager serviceManager, Usuario usuario) {
        super("Painel do Dono - " + usuario.getNome());
        this.serviceManager = serviceManager;
        this.usuario = usuario;
        setVisible(true);
        mostrarFranquias();
    }

    @Override
    public void contruirSidebar() {
        sidebar.add(new JLabel("Menu Principal"));
        sidebar.add(Box.createVerticalStrut(20));

        // Botão para voltar à visualização das franquias (lista principal)
        JButton btnVisaoGeral = criarBotaoMenu("Visão Geral");
        sidebar.add(btnVisaoGeral);

        sidebar.add(Box.createVerticalGlue());

        JButton btnSair = criarBotaoMenu("Sair");
        sidebar.add(btnSair);

        btnVisaoGeral.addActionListener(e -> mostrarFranquias());

        btnSair.addActionListener(e -> {
            this.dispose();
            new Login(serviceManager).setVisible(true);
        });
    }

    private void mostrarFranquias() {
        configurarPainelConteudo("Visão Geral de Franquias");
        painelConteudo.removeAll();

        try {
            List<Franquia> franquias = serviceManager.getServiceFranquia().listarFranquias();

            if (franquias.isEmpty()) {
                painelConteudo.add(new JLabel("Nenhuma franquia encontrada."));
            } else {
                JPanel painelLista = new JPanel();
                painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
                painelLista.setBorder(new EmptyBorder(10, 10, 10, 10));

                for (Franquia f : franquias) {
                    painelLista.add(criarCardFranquia(f));
                    painelLista.add(Box.createVerticalStrut(10));
                }

                JScrollPane scrollPane = new JScrollPane(painelLista);
                painelConteudo.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar franquias: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            painelConteudo.add(new JLabel("Erro ao carregar as franquias."));
        }

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private JPanel criarCardFranquia(Franquia f) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setBackground(new Color(240, 248, 255));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(new Color(240, 248, 255));

        JLabel nomeFranquia = new JLabel("Franquia: " + f.getNome());
        nomeFranquia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoPanel.add(nomeFranquia);

        // Lógica para contar lojas e usuários
        int numLojas = f.getIdLojas().size();
        int numUsuarios = 0;
        for (Loja loja : serviceManager.getServiceLoja().listarTodasAsLojas()) {
            numUsuarios += loja.getIdsUsuarios().size();
        }

        JLabel labelLojas = new JLabel("Número de Lojas: " + numLojas);
        labelLojas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoPanel.add(labelLojas);

        JLabel labelUsuarios = new JLabel("Total de Usuários: " + numUsuarios);
        labelUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoPanel.add(labelUsuarios);

        card.add(infoPanel, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botoesPanel.setBackground(new Color(240, 248, 255));

        JButton btnGerenciarLojas = new JButton("Gerenciar Lojas");
        botoesPanel.add(btnGerenciarLojas);

        card.add(botoesPanel, BorderLayout.SOUTH);

        // O botão agora abre uma nova interface para gerenciar as lojas da franquia
        btnGerenciarLojas.addActionListener(e -> {
            new InterfaceGerenciarLojas(serviceManager, f , usuario).setVisible(true);
        });

        return card;
    }
}