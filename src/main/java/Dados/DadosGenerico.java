package Dados;

import Model.Entidade;
import Model.Franquia;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import exception.persistencia.ArquivoNaoCriadoException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class DadosGenerico <T extends Entidade> {
    private final String FILE_PATH;
    private final ObjectMapper mapper;
    private Map<String, T> map;

    public DadosGenerico(String filePath) throws PersistenciaException{
        this.FILE_PATH = filePath;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        carregar();
    }

    protected abstract TypeReference<List<T>> getTypeReference();

    protected void carregar() throws PersistenciaException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            this.map = new ConcurrentHashMap<>();
            salvar(); // Garante que o arquivo seja criado com "[]"
            return;
        }
        try {
            List<T> lista = mapper.readValue(file, getTypeReference());
            // Usando stream para popular o mapa de forma mais concisa
            this.map = lista.stream().collect(
                    Collectors.toConcurrentMap(Entidade::getId, item -> item)
            );
        } catch (MismatchedInputException e){
            throw new PersistenciaException("Erro no formato do arquivo!");
        }
        catch (IOException e) {
            this.map = new ConcurrentHashMap<>();
            throw new PersistenciaException("Erro ao carregar dados do arquivo " + FILE_PATH + ": " + e.getMessage());
        }
    }



    private void salvar() throws PersistenciaException {
        try {
            List<T> lista = map != null ? new ArrayList<>(map.values()) : new ArrayList<>();
            mapper.writeValue(new File(FILE_PATH), lista);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar dados no arquivo " + FILE_PATH + ": " + e.getMessage());
        }
    }

    public void adicionar(T entidade) throws PersistenciaException {
        map.put(entidade.getId(), entidade);
        salvar();
    }

    public void atualizar(T entidade) throws PersistenciaException {
        if(!map.containsKey(entidade.getId()))
            throw new PersistenciaException("Entidade com o ID"+ entidade.getId()+"nao encontrada!");

        salvar();
    }

    public void remover(String entidade) throws PersistenciaException {
        T removido = map.remove(entidade);
        if(removido == null)
            throw new PersistenciaException("Entidade com o ID"+ entidade+"nao encontrada!");

        salvar();
    }

    public Optional<T> get(T entidade) throws PersistenciaException {
        if(map.isEmpty())
            throw new PersistenciaException("Lista vazia!");

        if(!map.containsKey(entidade.getId()))
            throw new PersistenciaException("Objeto nao encontrado!");

        return Optional.ofNullable(map.get(entidade.getId()));
    }

    public List<T> getAll() throws PersistenciaException{
        if(map.isEmpty())
            throw new PersistenciaException("Lista Vazia!");

        return new ArrayList<>(map.values());
    }

    public Map<String, T> getEntidadesMap() throws PersistenciaException {

        if(map == null || map.isEmpty())
            map = new ConcurrentHashMap<>();

        return map;
    }
}
