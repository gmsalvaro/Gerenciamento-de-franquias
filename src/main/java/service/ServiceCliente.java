//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package service;

import exception.persistencia.PersistenciaException;
import exception.usuario.CPFInvalidoException;
import exception.usuario.ValidacaoUsuarioException;
import model.Cliente;
import repository.DadosCliente;

import java.util.List;
import java.util.Optional;

public class ServiceCliente {
    private final DadosCliente dadosCliente;
    private final ValidadorNome validadorNome;
    private final ValidadorCPF validadorCPF;

    public ServiceCliente(String filePath) throws PersistenciaException {
        this.dadosCliente = new DadosCliente(filePath);
        this.validadorNome = new ValidadorNome();
        this.validadorCPF = new ValidadorCPF();
    }

    public void adicionar(Cliente cliente) throws ValidacaoUsuarioException, PersistenciaException {
        validadorNome.validar(cliente.getNome());
        validadorCPF.validar(cliente.getCpf());

        if (buscarPorCpf(cliente.getCpf()).isPresent()) {
            throw new CPFInvalidoException("Já existe um cliente cadastrado com este CPF.");
        }
        dadosCliente.adicionar(cliente);
    }

    public List<Cliente> listarTodos() {
        return dadosCliente.listarTodas();
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return listarTodos().stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst();
    }
}