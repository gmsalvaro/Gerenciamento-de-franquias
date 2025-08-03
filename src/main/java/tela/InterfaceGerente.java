package tela;

import Model.*;
import Service.CriaVendedor;
import Service.ServiceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class InterfaceGerente extends PainelPrincipal {
     private final ServiceManager serviceManager;
     private final Gerente gerenteLogado;
     private final Loja lojaDoGerente;
     private JRadioButton radioPorVolume, radioPorValor;
     private JPanel painelListaVendedores;

     public InterfaceGerente(ServiceManager serviceManager, Gerente gerenteLogado) {
          super("Painel do Gerente - " + gerenteLogado.getNome());
          this.serviceManager = serviceManager;
          this.gerenteLogado = gerenteLogado;
          this.lojaDoGerente = serviceManager.getServiceLoja().buscarLojaPorUsuario(gerenteLogado).orElse(null);

          if (lojaDoGerente == null) {
               JOptionPane.showMessageDialog(null, "ERRO: Você não está designado a nenhuma loja.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
               dispose();
               return;
          }

          contruirSidebar();
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          mostrarBoasVindas(); // Mostra uma tela inicial
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

          // --- BOTÕES CORRIGIDOS E COMPLETOS ---
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

          // Ações dos botões
          btnGerenciarVendedores.addActionListener(e -> mostrarGerenciarVendedores());
          btnGerenciarProdutos.addActionListener(e -> new InterfaceGerenciarProdutos(lojaDoGerente, serviceManager));
          // Adicionar ações para Ver Pedidos e Relatórios
          btnSair.addActionListener(e -> this.dispose());
     }

     private void mostrarBoasVindas() {
          configurarPainelConteudo("Bem-vindo, " + gerenteLogado.getNome());
     }

     // --- NOVA FUNCIONALIDADE: GERENCIAR VENDEDORES ---

     private void mostrarGerenciarVendedores() {
          configurarPainelConteudo("Vendedores da Loja");
          painelConteudo.setLayout(new BorderLayout(10, 10));

          // --- PAINEL DE CONTROLES (NORTE) PARA ORDENAÇÃO ---
          JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER));
          painelControles.add(new JLabel("Ordenar por:"));

          radioPorVolume = new JRadioButton("Volume de Vendas");
          radioPorValor = new JRadioButton("Valor Total");

          ButtonGroup grupoRadios = new ButtonGroup();
          grupoRadios.add(radioPorVolume);
          grupoRadios.add(radioPorValor);

          painelControles.add(radioPorVolume);
          painelControles.add(radioPorValor);

          // Ação para atualizar a lista ao mudar a seleção
          ActionListener listenerRadios = e -> atualizarListaVendedores();
          radioPorVolume.addActionListener(listenerRadios);
          radioPorValor.addActionListener(listenerRadios);

          radioPorVolume.setSelected(true); // Ordenar por volume como padrão, conforme requisito
          painelConteudo.add(painelControles, BorderLayout.NORTH);

          // --- PAINEL DE LISTA (CENTRO) ---
          painelListaVendedores = new JPanel();
          painelListaVendedores.setLayout(new BoxLayout(painelListaVendedores, BoxLayout.Y_AXIS));
          painelListaVendedores.setBackground(Color.WHITE);
          JScrollPane scrollPane = new JScrollPane(painelListaVendedores);
          scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
          painelConteudo.add(scrollPane, BorderLayout.CENTER);

          // --- PAINEL DE BOTÕES (SUL) ---
          JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
          JButton btnAdicionar = new JButton("Adicionar Vendedor");
          JButton btnRemover = new JButton("Remover Vendedor");
          painelBotoesAcao.add(btnAdicionar);
          painelBotoesAcao.add(btnRemover);
          painelConteudo.add(painelBotoesAcao, BorderLayout.SOUTH);

          // Conecta as ações aos botões
          btnAdicionar.addActionListener(e -> acaoAdicionarVendedor());
          btnRemover.addActionListener(e -> acaoRemoverVendedor()); // Ajustado para não precisar de parâmetro

          // Chama o método para preencher a lista pela primeira vez
          atualizarListaVendedores();
     }

     private void atualizarListaVendedores() {
          painelListaVendedores.removeAll();

          // Usa o metodo de serviço que gera o ranking para a LOJA ESPECÍFICA do gerente
          List<PerformanceVendedor> performance = serviceManager.getServiceRelatorio().gerarRankingVendedoresPorLoja(this.lojaDoGerente);

          // Ordena a lista com base na seleção do radio button
          if (radioPorVolume.isSelected()) {
               performance.sort(Comparator.comparingInt(PerformanceVendedor::getNumeroDeVendas).reversed());
          } else {
               performance.sort(Comparator.comparing(PerformanceVendedor::getValorTotalVendas).reversed());
          }

          if (performance.isEmpty()) {
               painelListaVendedores.add(new JLabel("Nenhum vendedor cadastrado nesta loja."));
          } else {
               for (PerformanceVendedor p : performance) {
                    // Passa o objeto de performance para o método de criar o card
                    painelListaVendedores.add(criarCardVendedor(p));
                    painelListaVendedores.add(Box.createRigidArea(new Dimension(0, 10)));
               }
          }
          painelListaVendedores.revalidate();
          painelListaVendedores.repaint();
     }



     private JPanel criarCardVendedor(PerformanceVendedor performance) {
          Vendedor vendedor = performance.getVendedor(); // Pega o objeto Vendedor

          JPanel card = new JPanel(new BorderLayout(10, 10));
          card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
          card.setBackground(new Color(245, 245, 245));
          // ... (estilização do card)

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

          // Adiciona as métricas de desempenho ao card
          painelInfo.add(new JLabel(String.format("Volume de Vendas: %d pedidos", performance.getNumeroDeVendas())));
          painelInfo.add(new JLabel(String.format("Valor Total Vendido: R$ %.2f", performance.getValorTotalVendas())));

          card.add(painelInfo, BorderLayout.CENTER);

          // Botão de Editar dentro do card
          JPanel painelBotao = new JPanel(new GridBagLayout());
          painelBotao.setOpaque(false);
          JButton btnEditar = new JButton("Editar");
          btnEditar.addActionListener(e -> acaoEditarVendedor(vendedor));
          painelBotao.add(btnEditar);
          card.add(painelBotao, BorderLayout.EAST);

          return card;
     }

     private void acaoAdicionarVendedor() {
          // 1. Chama o helper para criar o objeto Vendedor
          Vendedor novoVendedor = CriaVendedor.criarNovoVendedor(this, serviceManager);

          // 2. Se um vendedor foi criado, o vincula à loja do gerente
          if (novoVendedor != null) {
               try {
                    // Vincula o novo vendedor à loja gerenciada
                    this.lojaDoGerente.addUsuarioID(novoVendedor.getId());
                    serviceManager.getServiceLoja().atualizarLoja(this.lojaDoGerente);

                    JOptionPane.showMessageDialog(this, "Vendedor '" + novoVendedor.getNome() + "' adicionado e vinculado a esta loja com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // 3. Atualiza a tela para exibir o novo vendedor
                    mostrarGerenciarVendedores();

               } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao vincular vendedor à loja: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
               }
          }
     }

     private void acaoEditarVendedor(Vendedor vendedorParaEditar) {
          if (vendedorParaEditar == null) {
               // Isso não deve acontecer se chamado a partir do botão do card, mas é uma boa verificação.
               return;
          }

          // --- 1. Cria o formulário e pré-preenche com os dados atuais do vendedor ---
          JTextField txtNome = new JTextField(vendedorParaEditar.getNome());
          JTextField txtEmail = new JTextField(vendedorParaEditar.getEmail());
          JTextField txtCpf = new JTextField(vendedorParaEditar.getCpf());
          // Por segurança, a senha pode ser deixada em branco para ser redefinida, ou não ser editável aqui.
          // Vamos permitir a redefinição.
          JPasswordField txtSenha = new JPasswordField();
          txtSenha.putClientProperty("JComponent.outline", "warning"); // Dica visual

          JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
          painelFormulario.add(new JLabel("Nome:"));
          painelFormulario.add(txtNome);
          painelFormulario.add(new JLabel("Email:"));
          painelFormulario.add(txtEmail);
          painelFormulario.add(new JLabel("CPF: (não editável)"));
          txtCpf.setEditable(false); // CPF geralmente não deve ser alterado
          painelFormulario.add(txtCpf);
          painelFormulario.add(new JLabel("Nova Senha (deixe em branco para manter a atual):"));
          painelFormulario.add(txtSenha);

          // --- 2. Exibe o formulário em um diálogo ---
          int resultado = JOptionPane.showConfirmDialog(this, painelFormulario, "Editar Vendedor",
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

          if (resultado == JOptionPane.OK_OPTION) {
               try {
                    // --- 3. Coleta os novos dados e atualiza o objeto ---
                    String nome = txtNome.getText().trim();
                    String email = txtEmail.getText().trim();
                    String novaSenha = new String(txtSenha.getPassword());

                    if (nome.isEmpty() || email.isEmpty()) {
                         throw new Exception("Nome e Email são obrigatórios.");
                    }

                    // Atualiza o objeto vendedor com as novas informações
                    vendedorParaEditar.setNome(nome);
                    vendedorParaEditar.setEmail(email);

                    // Só altera a senha se uma nova foi digitada
                    if (!novaSenha.isEmpty()) {
                         vendedorParaEditar.setSenha(novaSenha);
                    }

                    // --- 4. Chama o serviço para salvar as alterações ---
                    // O ServiceUsuario já tem o método 'atualizarUsuario' que serve para qualquer tipo de usuário
                    serviceManager.getServiceUsuario().atualizarUsuario(vendedorParaEditar);

                    JOptionPane.showMessageDialog(this, "Vendedor atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // --- 5. Atualiza a tela ---
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
                         // 1. Desvincula o vendedor da loja
                         this.lojaDoGerente.removeUsuario(vendedorSelecionado.getId());
                         serviceManager.getServiceLoja().atualizarLoja(this.lojaDoGerente);

                         // 2. Remove o usuário do sistema
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