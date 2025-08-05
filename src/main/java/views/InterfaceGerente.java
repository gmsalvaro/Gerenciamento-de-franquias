package views;

import model.*;
import Service.CriaVendedor;
import Service.ServiceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;

public class InterfaceGerente extends PainelPrincipal {
     private final ServiceManager serviceManager;
     private final Gerente gerenteLogado;
     private final Loja lojaDoGerente;
     private JRadioButton radioPorVolume, radioPorValor;
     private JPanel painelListaVendedores;
     private final GerenciaFluxoLogin fluxoLogin;

     public InterfaceGerente(ServiceManager serviceManager, Gerente gerenteLogado, GerenciaFluxoLogin fluxoLogin) {
          super("Painel do Gerente - " + gerenteLogado.getNome());
          this.serviceManager = serviceManager;
          this.gerenteLogado = gerenteLogado;
          this.lojaDoGerente = serviceManager.getServiceLoja().buscarLojaPorUsuario(gerenteLogado).orElse(null);
          this.fluxoLogin = fluxoLogin;

          if (lojaDoGerente == null) {
               JOptionPane.showMessageDialog(null, "ERRO: Você não está designado a nenhuma loja.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
               dispose();
               return;
          }

          contruirSidebar();
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          mostrarBoasVindas();
          setVisible(true);
     }

     @Override
     public void contruirSidebar() {
          sidebar.setBackground(new Color(240, 240, 240));
          JLabel lblTituloSidebar = new JLabel(lojaDoGerente.getNome());
          lblTituloSidebar.setFont(new Font("Segoe UI", Font.BOLD, 18));
          lblTituloSidebar.setAlignmentX(Component.CENTER_ALIGNMENT);
          sidebar.add(lblTituloSidebar);
          sidebar.add(Box.createVerticalStrut(30));

          JButton btnGerenciarVendedores = criarBotaoMenu("Gerenciar Vendedores");
          JButton btnGerenciarProdutos = criarBotaoMenu("Gerenciar Produtos");
          JButton btnVerPedidos = criarBotaoMenu("Ver Pedidos");
          JButton btnRelatorios = criarBotaoMenu("Relatórios da Loja");

          sidebar.add(btnGerenciarVendedores);
          sidebar.add(btnGerenciarProdutos);
          sidebar.add(btnVerPedidos);
          sidebar.add(btnRelatorios);
          sidebar.add(Box.createVerticalGlue());
          JButton btnSair = criarBotaoMenu("Sair");
          sidebar.add(btnSair);

          btnGerenciarVendedores.addActionListener(e -> mostrarGerenciarVendedores());
          btnGerenciarProdutos.addActionListener(e -> new InterfaceGerenciarProdutos(lojaDoGerente, serviceManager));
          btnVerPedidos.addActionListener(e -> new InterfaceGerenciarPedidos(lojaDoGerente, serviceManager));
          btnSair.addActionListener(e -> fluxoLogin.fazerLogout());
          btnRelatorios.addActionListener(e -> new InterfaceRelatorioLoja(lojaDoGerente, serviceManager));
     }

     private void mostrarBoasVindas() {
          configurarPainelConteudo("Bem-vindo, " + gerenteLogado.getNome());
     }

     private void mostrarGerenciarVendedores() {
          configurarPainelConteudo("Vendedores da Loja");
          painelConteudo.setLayout(new BorderLayout(10, 10));

          JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER));
          painelControles.add(new JLabel("Ordenar por:"));

          radioPorVolume = new JRadioButton("Volume de Vendas");
          radioPorValor = new JRadioButton("Valor Total");

          ButtonGroup grupoRadios = new ButtonGroup();
          grupoRadios.add(radioPorVolume);
          grupoRadios.add(radioPorValor);

          painelControles.add(radioPorVolume);
          painelControles.add(radioPorValor);

          ActionListener listenerRadios = e -> atualizarListaVendedores();
          radioPorVolume.addActionListener(listenerRadios);
          radioPorValor.addActionListener(listenerRadios);

          radioPorVolume.setSelected(true);
          painelConteudo.add(painelControles, BorderLayout.NORTH);

          painelListaVendedores = new JPanel();
          painelListaVendedores.setLayout(new BoxLayout(painelListaVendedores, BoxLayout.Y_AXIS));
          painelListaVendedores.setBackground(Color.WHITE);
          JScrollPane scrollPane = new JScrollPane(painelListaVendedores);
          scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
          painelConteudo.add(scrollPane, BorderLayout.CENTER);

          JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
          JButton btnAdicionar = new JButton("Adicionar Vendedor");
          JButton btnRemover = new JButton("Remover Vendedor");
          painelBotoesAcao.add(btnAdicionar);
          painelBotoesAcao.add(btnRemover);
          painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH);

          btnAdicionar.addActionListener(e -> acaoAdicionarVendedor());
          btnRemover.addActionListener(e -> acaoRemoverVendedor());

          atualizarListaVendedores();
     }

     private void atualizarListaVendedores() {
          painelListaVendedores.removeAll();

          List<PerformanceVendedor> performance = serviceManager.getServiceRelatorio().gerarRankingVendedoresPorLoja(this.lojaDoGerente);

          if (radioPorVolume.isSelected()) {
               performance.sort(Comparator.comparingInt(PerformanceVendedor::getNumeroDeVendas).reversed());
          } else {
               performance.sort(Comparator.comparing(PerformanceVendedor::getValorTotalVendas).reversed());
          }

          if (performance.isEmpty()) {
               painelListaVendedores.add(new JLabel("Nenhum vendedor cadastrado nesta loja."));
          } else {
               for (PerformanceVendedor p : performance) {
                    painelListaVendedores.add(criarCardVendedor(p));
                    painelListaVendedores.add(Box.createRigidArea(new Dimension(0, 10)));
               }
          }
          painelListaVendedores.revalidate();
          painelListaVendedores.repaint();
     }

     private JPanel criarCardVendedor(PerformanceVendedor performance) {
          Vendedor vendedor = performance.getVendedor();

          JPanel card = new JPanel(new BorderLayout(10, 10));
          card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
          card.setBackground(new Color(245, 245, 245));

          JPanel painelInfo = new JPanel();
          painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
          painelInfo.setOpaque(false);
          painelInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

          JLabel lblNome = new JLabel(vendedor.getNome());
          lblNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
          painelInfo.add(lblNome);

          JLabel lblEmail = new JLabel(vendedor.getEmail());
          lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
          painelInfo.add(lblEmail);

          painelInfo.add(Box.createVerticalStrut(5));

          painelInfo.add(new JLabel(String.format("Volume de Vendas: %d pedidos", performance.getNumeroDeVendas())));
          painelInfo.add(new JLabel(String.format("Valor Total Vendido: R$ %.2f", performance.getValorTotalVendas())));

          card.add(painelInfo, BorderLayout.CENTER);

          JPanel painelBotao = new JPanel(new GridBagLayout());
          painelBotao.setOpaque(false);
          JButton btnEditar = new JButton("Editar");
          btnEditar.addActionListener(e -> acaoEditarVendedor(vendedor));
          painelBotao.add(btnEditar);
          card.add(painelBotao, BorderLayout.EAST);

          return card;
     }

     private void acaoAdicionarVendedor() {
          Vendedor novoVendedor = CriaVendedor.criarNovoVendedor(this, serviceManager);

          if (novoVendedor != null) {
               try {
                    this.lojaDoGerente.addUsuarioID(novoVendedor.getId());
                    serviceManager.getServiceLoja().atualizar(this.lojaDoGerente);

                    JOptionPane.showMessageDialog(this, "Vendedor '" + novoVendedor.getNome() + "' adicionado e vinculado a esta loja com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    mostrarGerenciarVendedores();

               } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao vincular vendedor à loja: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
               }
          }
     }

     private void acaoEditarVendedor(Vendedor vendedorParaEditar) {
          if (vendedorParaEditar == null) {
               return;
          }

          JTextField txtNome = new JTextField(vendedorParaEditar.getNome());
          JTextField txtEmail = new JTextField(vendedorParaEditar.getEmail());
          JTextField txtCpf = new JTextField(vendedorParaEditar.getCpf());
          JPasswordField txtSenha = new JPasswordField();
          txtSenha.putClientProperty("JComponent.outline", "warning");

          JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
          painelFormulario.add(new JLabel("Nome:"));
          painelFormulario.add(txtNome);
          painelFormulario.add(new JLabel("Email:"));
          painelFormulario.add(txtEmail);
          painelFormulario.add(new JLabel("CPF: (não editável)"));
          txtCpf.setEditable(false);
          painelFormulario.add(txtCpf);
          painelFormulario.add(new JLabel("Nova Senha (deixe em branco para manter a atual):"));
          painelFormulario.add(txtSenha);

          int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Editar Vendedor",
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

          if (resultado == JOptionPane.OK_OPTION) {
               try {
                    String nome = txtNome.getText().trim();
                    String email = txtEmail.getText().trim();
                    String novaSenha = new String(txtSenha.getPassword());

                    if (nome.isEmpty() || email.isEmpty()) {
                         throw new Exception("Nome e Email são obrigatórios.");
                    }

                    vendedorParaEditar.setNome(nome);
                    vendedorParaEditar.setEmail(email);

                    if (!novaSenha.isEmpty()) {
                         vendedorParaEditar.setSenha(novaSenha);
                    }

                    serviceManager.getServiceUsuario().atualizarUsuario(vendedorParaEditar);

                    JOptionPane.showMessageDialog(this, "Vendedor atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    mostrarGerenciarVendedores();

               } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar vendedor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
               }
          }
     }

     private void acaoRemoverVendedor() {

          List<Vendedor> vendedores = serviceManager.getServiceUsuario().getVendedoresPorLoja(lojaDoGerente);
          if (vendedores.isEmpty()) {
               JOptionPane.showMessageDialog(this, "Não há vendedores para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
               return;
          }

          JComboBox<Vendedor> comboBox = new JComboBox<>(vendedores.toArray(new Vendedor[0]));
          comboBox.setRenderer(new DefaultListCellRenderer() {
               @Override
               public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Vendedor) {
                         setText(((Vendedor) value).getNome());
                    }
                    return this;
               }
          });

          int resultado = JOptionPane.showConfirmDialog(this, comboBox, "Selecione o Vendedor para Remover", JOptionPane.OK_CANCEL_OPTION);

          if (resultado == JOptionPane.OK_OPTION) {
               Vendedor vendedorSelecionado = (Vendedor) comboBox.getSelectedItem();
               if (vendedorSelecionado == null) return;

               int confirmacaoFinal = JOptionPane.showConfirmDialog(
                       this,
                       "Tem certeza que deseja remover o vendedor '" + vendedorSelecionado.getNome() + "'?",
                       "Confirmar Remoção",
                       JOptionPane.YES_NO_OPTION,
                       JOptionPane.WARNING_MESSAGE
               );

               if (confirmacaoFinal == JOptionPane.YES_OPTION) {
                    try {
                         this.lojaDoGerente.removeUsuario(vendedorSelecionado.getId());
                         serviceManager.getServiceLoja().atualizar(this.lojaDoGerente);

                         serviceManager.getServiceUsuario().removeUsuario(vendedorSelecionado);

                         JOptionPane.showMessageDialog(this, "Vendedor removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                         mostrarGerenciarVendedores();

                    } catch (Exception ex) {
                         JOptionPane.showMessageDialog(this, "Erro ao remover vendedor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
               }
          }
     }
}