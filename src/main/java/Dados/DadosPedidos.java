package Dados;

import Model.Pedido;
import Model.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import exception.persistencia.ErroCarregarArquivosException;
import exception.persistencia.ErroSalvarLojaException;
import exception.persistencia.LojaNaoCarregadaException;
import exception.persistencia.PersistenciaException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap; // Bom para uso multi-thread se aplicável

public class DadosPedidos {
    private final String LOJAS_FILE;
    private final ObjectMapper mapper;
    private Map<String, Pedido> lojasMap;

    public DadosPedidos(String filePath) throws PersistenciaException {
        this.LOJAS_FILE = filePath;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        carregar();
    }

    private void carregar() throws PersistenciaException {
        File file = new File(LOJAS_FILE);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(LOJAS_FILE), "[]".getBytes());
            } catch (IOException e) {
                throw new ErroCarregarArquivosException("Erro ao criar arquivo de lojas: " + e.getMessage());
            }
            lojasMap = new ConcurrentHashMap<>();
            return;
        }
        try {
            List<Pedido> lista = mapper.readValue(file, new TypeReference<List<Pedido>>() {});
            lojasMap = new ConcurrentHashMap<>();
            lista.forEach(loja -> lojasMap.put(loja.getId(), loja));
        } catch (IOException e) {
            lojasMap = new ConcurrentHashMap<>();
            throw new LojaNaoCarregadaException("Erro ao carregar lojas: " + e.getMessage());
        }
    }

    private void salvar() throws PersistenciaException {
        try {
            mapper.writeValue(new File(LOJAS_FILE), new ArrayList<>(lojasMap.values()));
        } catch (IOException e) {
            throw new ErroSalvarLojaException("Erro ao salvar lojas: " + e.getMessage());
        }
    }

    public Map<String, Pedido> getPedidosMap() {
        return lojasMap;
    }

    public List<Pedido> listarTodas() {
        return new ArrayList<>(lojasMap.values());
    }

    public Optional<Pedido> buscarPorId(String id) {
        return Optional.ofNullable(lojasMap.get(id));
    }

    public void adicionar(Pedido loja) throws PersistenciaException{
        lojasMap.put(loja.getId(), loja);
        salvar();
    }

    public void atualizar(Pedido lojaAtualizada) throws PersistenciaException{
        if (lojasMap.containsKey(lojaAtualizada.getId())) {
            lojasMap.put(lojaAtualizada.getId(), lojaAtualizada);
            salvar();
        } else {
            System.err.println("Loja com ID " + lojaAtualizada.getId() + " não encontrada para atualização.");
        }
    }

    public void remover(String id) throws PersistenciaException{
        if (lojasMap.remove(id) != null) {
            salvar();
        } else {
            System.err.println("Loja com ID " + id + " não encontrada para remoção.");
        }
    }
}