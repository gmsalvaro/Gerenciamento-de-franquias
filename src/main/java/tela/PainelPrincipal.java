package tela;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;


public abstract class PainelPrincipal extends JFrame {
    public JPanel sidebar ;
    public JPanel painelConteudo ;

    public PainelPrincipal(String titulo) {
        super(titulo) ;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(15, 10, 15, 10));

        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, painelConteudo);
        splitPane.setDividerLocation(220);
        splitPane.setEnabled(false);

        add(splitPane);
    }

    public abstract void contruirSidebar();


    public JButton criarBotaoMenu(String texto){
        JButton botao = new JButton(texto);
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        botao.setIconTextGap(10);
        botao.setFocusPainted(false);
        return botao;
    }

    public void configurarPainelConteudo(String titulo){
        painelConteudo.removeAll();
        JLabel tituloLabel = new JLabel(titulo, SwingConstants.LEFT);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloLabel.setBorder(new EmptyBorder(0, 5, 15, 0));
        painelConteudo.add(tituloLabel, BorderLayout.NORTH);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    public void adicionarScrollAoConteudo(JScrollPane painelLista){
        JScrollPane scrollPane = new JScrollPane(painelLista);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        painelConteudo.add(scrollPane, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

}
