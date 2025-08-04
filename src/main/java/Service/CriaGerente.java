package Service;

import Model.Gerente;
import Model.Loja;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CriaGerente {


    public static Gerente criarNovoGerente(Frame parent, ServiceManager serviceManager) {
        // Chama o metodo privado que mostra o formulário e cria o gerente
        Gerente novoGerente = abrirFormularioCriacao(parent, serviceManager);

        // Se um gerente foi criado com sucesso (usuário não cancelou)
        if (novoGerente != null) {
            // Pergunta se o usuário deseja realizar a vinculação agora
            int vincularAgora = JOptionPane.showConfirmDialog(parent,
                    "Deseja vincular este novo gerente a uma loja agora?",
                    "Vincular Gerente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (vincularAgora == JOptionPane.YES_OPTION) {
                // Se sim, chama o método auxiliar que lida com a vinculação
                vincularGerenteALoja(parent, serviceManager, novoGerente);
            }
        }
        return novoGerente;
    }

    /**
     * VERSÃO 2 (SOBRECARGA): Fluxo específico para criar um gerente e já vinculá-lo
     * a uma loja de destino conhecida, sem fazer perguntas extras.
     * Ideal para ser usado no fluxo de "Adicionar Loja".
     *
     * @param parent           A janela pai (Frame) sobre a qual os diálogos aparecerão.
     * @param serviceManager   A instância do ServiceManager.
     * @param lojaParaVincular A loja específica à qual o novo gerente será vinculado.
     * @return O objeto Gerente recém-criado, ou null se a operação for cancelada.
     */
    public static Gerente criarNovoGerente(Frame parent, ServiceManager serviceManager, Loja lojaParaVincular) {
        // Chama o método privado que mostra o formulário e cria o gerente
        Gerente novoGerente = abrirFormularioCriacao(parent, serviceManager);

        // Se um gerente foi criado com sucesso
        if (novoGerente != null) {
            // Vincula diretamente à loja de destino, sem perguntar
            try {
                serviceManager.getServiceLoja().designarGerenteParaLoja(novoGerente, lojaParaVincular, serviceManager.getServiceUsuario());
                JOptionPane.showMessageDialog(parent,
                        "Gerente '" + novoGerente.getNome() + "' criado e vinculado à loja '" + lojaParaVincular.getNome() + "' com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent,
                        "Erro ao vincular gerente recém-criado: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return novoGerente;
    }

    /**
     * Método PRIVADO que contém a lógica do formulário de criação.
     * É reutilizado por ambos os métodos públicos acima.
     *
     * @return O Gerente criado ou null se o usuário cancelar.
     */
    private static Gerente abrirFormularioCriacao(Frame parent, ServiceManager serviceManager) {
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtCpf = new JTextField();
        JPasswordField txtSenha = new JPasswordField();

        JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
        painelFormulario.add(new JLabel("Nome do Novo Gerente:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Email:"));
        painelFormulario.add(txtEmail);
        painelFormulario.add(new JLabel("CPF:"));
        painelFormulario.add(txtCpf);
        painelFormulario.add(new JLabel("Senha:"));
        painelFormulario.add(txtSenha);

        int resultado = JOptionPane.showConfirmDialog(parent, painelFormulario, "Adicionar Novo Gerente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado == JOptionPane.OK_OPTION) {
            try {
                // Validação básica dos campos
                String nome = txtNome.getText().trim();
                String email = txtEmail.getText().trim();
                String cpf = txtCpf.getText().trim();
                String senha = new String(txtSenha.getPassword());
                if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
                    throw new Exception("Todos os campos são obrigatórios.");
                }

                Gerente novoGerente = new Gerente(nome, email, senha, cpf);
                serviceManager.getServiceUsuario().adicionar(novoGerente);
                return novoGerente;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Erro ao adicionar gerente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private static void vincularGerenteALoja(Frame parent, ServiceManager serviceManager, Gerente gerente) {
        List<Loja> lojasDisponiveis = serviceManager.getServiceLoja().listarLojasSemGerente(serviceManager);

        if (lojasDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Não há lojas disponíveis sem gerente no momento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Loja> comboBoxLojas = new JComboBox<>(lojasDisponiveis.toArray(new Loja[0]));
        comboBoxLojas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Loja) {
                    setText(((Loja) value).getNome());
                }
                return this;
            }
        });

        int resultadoVinculo = JOptionPane.showConfirmDialog(parent, comboBoxLojas, "Selecione a Loja", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultadoVinculo == JOptionPane.OK_OPTION) {
            Loja lojaSelecionada = (Loja) comboBoxLojas.getSelectedItem();
            try {
                serviceManager.getServiceLoja().designarGerenteParaLoja(gerente, lojaSelecionada, serviceManager.getServiceUsuario());
                JOptionPane.showMessageDialog(parent, "Gerente vinculado à loja '" + lojaSelecionada.getNome() + "' com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Erro ao vincular gerente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}