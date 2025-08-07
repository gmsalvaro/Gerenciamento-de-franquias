//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package service;

import model.Vendedor;

import javax.swing.*;
import java.awt.*;

public class CriaVendedor {

    /**
     * Abre um diálogo para criar um novo Vendedor.
     * @param parent A janela pai sobre a qual o diálogo aparecerá.
     * @param serviceManager A instância do ServiceManager.
     * @return O Vendedor recém-criado, ou null se a operação for cancelada.
     */
    public static Vendedor criarNovoVendedor(Frame parent, ServiceManager serviceManager) {
        // Cria os campos do formulário
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtCpf = new JTextField();
        JPasswordField txtSenha = new JPasswordField();

        // Monta o painel do formulário
        JPanel painelFormulario = new JPanel(new GridLayout(0, 1, 5, 5));
        painelFormulario.add(new JLabel("Nome do Vendedor:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Email:"));
        painelFormulario.add(txtEmail);
        painelFormulario.add(new JLabel("CPF:"));
        painelFormulario.add(txtCpf);
        painelFormulario.add(new JLabel("Senha:"));
        painelFormulario.add(txtSenha);

        int resultado = JOptionPane.showConfirmDialog(parent, painelFormulario, "Adicionar Novo Vendedor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nome = txtNome.getText().trim();
                String email = txtEmail.getText().trim();
                String senha = new String(txtSenha.getPassword());
                String cpf = txtCpf.getText().trim();

                // Validação de campos
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || cpf.isEmpty()) {
                    throw new Exception("Todos os campos são obrigatórios.");
                }

                Vendedor novoVendedor = new Vendedor(nome, email, senha, cpf);
                serviceManager.getServiceUsuario().adicionar(novoVendedor);
                return novoVendedor;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Erro ao adicionar vendedor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
