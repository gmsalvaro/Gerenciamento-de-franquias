package Dados;

import Model.Franquia;
import com.fasterxml.jackson.core.type.TypeReference;
import exception.persistencia.*;
import java.util.*;


public class DadosFranquias extends DadosGenerico<Franquia> {


    public DadosFranquias(String filePath) throws PersistenciaException{
        super(filePath);
        carregar();
    }

    @Override
    protected TypeReference<List<Franquia>> getTypeReference() {
        return new TypeReference<List<Franquia>>() {};
    }

    public Map<String, Franquia> listarMap() throws PersistenciaException {
        return getEntidadesMap();
    }



}
