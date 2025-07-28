package Dados;

import Model.Franquia;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DadosFranquias {
    private final String LOJAS_FILE;
    private final ObjectMapper mapper;
    private Map<String, Franquia> lojasMap;

    public DadosFranquias(String filePath) {
        this.LOJAS_FILE = filePath;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        carregar();
    }

    private void carregar() {
        File file = new File(LOJAS_FILE);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(LOJAS_FILE), "[]".getBytes());
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo de lojas: " + e.getMessage());
            }
            lojasMap = new ConcurrentHashMap<>();
            return;
        }
        try {
            List<Franquia> lista = mapper.readValue(file, new TypeReference<List<Franquia>>() {});
            lojasMap = new ConcurrentHashMap<>();
            lista.forEach(loja -> lojasMap.put(loja.getId(), loja));
        } catch (IOException e) {
            System.err.println("Erro ao carregar lojas: " + e.getMessage());
            lojasMap = new ConcurrentHashMap<>();
        }
    }

    private void salvar() {
        try {
            mapper.writeValue(new File(LOJAS_FILE), new ArrayList<>(lojasMap.values()));
        } catch (IOException e) {
            System.err.println("Erro ao salvar lojas: " + e.getMessage());
        }
    }

    public Map<String, Franquia> listarMap() {
        return new HashMap<>(lojasMap);
    }


    public void adicionar(Franquia loja) {
        lojasMap.put(loja.getId(), loja);
        salvar();
    }

    public void atualizar(Franquia lojaAtualizada) {
        if (lojasMap.containsKey(lojaAtualizada.getId())) {
            lojasMap.put(lojaAtualizada.getId(), lojaAtualizada);
            salvar();
        } else {
            System.err.println("Loja com ID " + lojaAtualizada.getId() + " não encontrada para atualização.");
        }
    }

    public void remover(String id) {
        if (lojasMap.remove(id) != null) {
            salvar();
        } else {
            System.err.println("Loja com ID " + id + " não encontrada para remoção.");
        }
    }
}
