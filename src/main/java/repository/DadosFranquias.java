//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package repository;

import model.Franquia;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import exception.persistencia.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DadosFranquias implements IDados<Franquia, String> {
    private final String LOJAS_FILE;
    private final ObjectMapper mapper;
    private Map<String, Franquia> lojasMap;

    public DadosFranquias(String filePath) throws PersistenciaException{
        this.LOJAS_FILE = filePath;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        carregar();
    }
    //metodos para manipular os arquivos JSON corretamente
    private void carregar() throws PersistenciaException {
        File file = new File(LOJAS_FILE);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(LOJAS_FILE), "[]".getBytes());
            } catch (IOException e) {
                throw new ArquivoNaoCriadoException("Erro ao criar arquivo de lojas: " + e.getMessage());
            }
            lojasMap = new ConcurrentHashMap<>();
            return;
        }
        try {
            List<Franquia> lista = mapper.readValue(file, new TypeReference<List<Franquia>>() {});
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
    @Override
    public Map<String, Franquia> listarMap() {
        return new HashMap<>(lojasMap);
    }

    @Override
    public List<Franquia> listarTodas() {
        return new ArrayList<>(lojasMap.values());
    }

    @Override
    public Optional<Franquia> buscarPorId(String id) {
        return Optional.ofNullable(lojasMap.get(id));
    }

    @Override
    public void adicionar(Franquia loja) throws PersistenciaException{
        lojasMap.put(loja.getId(), loja);
        salvar();
    }
    @Override
    public void atualizar(Franquia lojaAtualizada) throws PersistenciaException{
        if (lojasMap.containsKey(lojaAtualizada.getId())) {
            lojasMap.put(lojaAtualizada.getId(), lojaAtualizada);
            salvar();
        } else {
            throw new LojaNaoAtualizadaException("Loja com ID " + lojaAtualizada.getId() + " não encontrada para atualização.");
        }
    }
    @Override
    public void remover(String id) throws PersistenciaException {
        if (lojasMap.remove(id) != null) {
            salvar();
        } else {
            throw new LojaNaoRemovidaException("Loja com ID " + id + " não encontrada para remoção.");
        }
    }



}
