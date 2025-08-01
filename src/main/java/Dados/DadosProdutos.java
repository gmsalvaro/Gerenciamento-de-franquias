package Dados;
import Model.Franquia;
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

public class DadosProdutos extends DadosGenerico<Produto>{


    public DadosProdutos(String filePath) throws PersistenciaException{
        super(filePath);
        carregar();
    }

    @Override
    protected TypeReference<List<Produto>> getTypeReference(){
        return new TypeReference<List<Produto>>(){};
    }

    public Map<String, Produto> getProdutosMap() throws PersistenciaException {
        return getEntidadesMap();
    }
}