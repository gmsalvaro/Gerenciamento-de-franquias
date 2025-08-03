package Dados;
import Model.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import exception.persistencia.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DadosProdutos {
    private final String LOJAS_FILE;
    private final ObjectMapper mapper;
    private Map<String, Produto> produtoMap;

    public DadosProdutos(String filePath) throws PersistenciaException{
        this.LOJAS_FILE = filePath;
        produtoMap = new ConcurrentHashMap<>();
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
                throw new ArquivoNaoCriadoException("Erro ao criar arquivo de lojas: " + e.getMessage());
            }
            produtoMap = new ConcurrentHashMap<>();
            return;
        }
        try {
            List<Produto> lista = mapper.readValue(file, new TypeReference<List<Produto>>() {});
            produtoMap = new ConcurrentHashMap<>();
            lista.forEach(produto -> produtoMap.put(produto.getId(), produto));
        } catch (IOException e) {
            produtoMap = new ConcurrentHashMap<>();
            throw new LojaNaoCarregadaException("Erro ao carregar lojas: " + e.getMessage());
        }
    }

    private void salvar() throws PersistenciaException {
        try {
            mapper.writeValue(new File(LOJAS_FILE), new ArrayList<>(produtoMap.values()));
        } catch (IOException e) {
            throw new ErroSalvarLojaException("Erro ao salvar lojas: " + e.getMessage());
        }
    }

    public List<Produto> listarTodas() {
        return new ArrayList<>(produtoMap.values());
    }

    public Optional<Produto> buscarPorId(String id) {
        return Optional.ofNullable(produtoMap.get(id));
    }

    public void adicionar(Produto produto) throws PersistenciaException{
        produtoMap.put(produto.getId(), produto);
        salvar();
    }

    public void atualizar(Produto lojaAtualizada) throws PersistenciaException{
        if (produtoMap.containsKey(lojaAtualizada.getId())) {
            produtoMap.put(lojaAtualizada.getId(), lojaAtualizada);
            salvar();
        } else {
            throw new LojaNaoAtualizadaException("Loja com ID " + lojaAtualizada.getId() + " não encontrada para atualização.");
        }
    }

    public void remover(String id) throws PersistenciaException{
        if (produtoMap.remove(id) != null) {
            salvar();
        } else {
            throw new LojaNaoRemovidaException("Loja com ID " + id + " não encontrada para remoção.");
        }
    }

    public Map<String, Produto> getProdutosMap() {
        return produtoMap;
    }
}