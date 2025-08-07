//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import exception.persistencia.*;
import model.Cliente;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DadosCliente implements IDados<Cliente, String> {
    private final String CLIENTE_FILE;
    private final ObjectMapper mapper;
    private Map<String, Cliente> clientesMap;

    public DadosCliente(String filePath) throws PersistenciaException {
        this.CLIENTE_FILE = filePath;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        carregar();
    }
//metodos para manipular os arquivos JSON corretamente
    private void carregar() throws PersistenciaException {
        File file = new File(CLIENTE_FILE);
        if (!file.exists() || file.length() == 0) {
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null)
                    file.getParentFile().mkdirs();

                Files.write(Paths.get(CLIENTE_FILE), "[]".getBytes());
            } catch (IOException e) {
                throw new ArquivoNaoCriadoException("Erro ao criar arquivo de clientes: " + e.getMessage());
            }
            clientesMap = new ConcurrentHashMap<>();
            return;
        }
        try {
            List<Cliente> lista = mapper.readValue(file, new TypeReference<List<Cliente>>() {});
            clientesMap = new ConcurrentHashMap<>();
            lista.forEach(cliente -> clientesMap.put(cliente.getId(), cliente));
        } catch (IOException e) {
            clientesMap = new ConcurrentHashMap<>();
            throw new LojaNaoCarregadaException("Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private void salvar() throws PersistenciaException {
        try {
            mapper.writeValue(new File(CLIENTE_FILE), new ArrayList<>(clientesMap.values()));
        } catch (IOException e) {
            throw new ErroSalvarLojaException("Erro ao salvar clientes: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Cliente> listarMap() { return clientesMap; }
    @Override
    public List<Cliente> listarTodas() { return new ArrayList<>(clientesMap.values()); }
    @Override
    public Optional<Cliente> buscarPorId(String id) { return Optional.ofNullable(clientesMap.get(id)); }
    @Override
    public void adicionar(Cliente cliente) throws PersistenciaException {
        clientesMap.put(cliente.getId(), cliente);
        salvar();
    }
    @Override
    public void atualizar(Cliente cliente) throws PersistenciaException {
        if (clientesMap.containsKey(cliente.getId())) {
            clientesMap.put(cliente.getId(), cliente);
            salvar();
        } else
            throw new LojaNaoAtualizadaException("Cliente com ID " + cliente.getId() + " não encontrado para atualização.");
    }
    @Override
    public void remover(String id) throws PersistenciaException {
        if (clientesMap.remove(id) != null) {
            salvar();
        } else
            throw new LojaNaoRemovidaException("Cliente com ID " + id + " não encontrado para remoção.");
    }
}