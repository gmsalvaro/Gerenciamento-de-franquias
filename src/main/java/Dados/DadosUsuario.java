package Dados;

import Model.Produto;
import Model.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DadosUsuario extends DadosGenerico<Usuario> {

    public DadosUsuario(String filePath) throws PersistenciaException {
        super(filePath);
        carregar();
    }


    @Override
    protected TypeReference<List<Usuario>> getTypeReference(){
        return new TypeReference<List<Usuario>>(){};
    }


    public Map<String, Usuario> getUsuariosMap()throws PersistenciaException {
        return getEntidadesMap();
    }


    public boolean existeCpf(String cpf) throws PersistenciaException{
        return getEntidadesMap().values().stream()
                .anyMatch(u -> u.getCpf().equals(cpf));
    }



}