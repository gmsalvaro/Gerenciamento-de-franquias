package Service;
import Dados.*;
import Model.*;
import exception.ValidacaoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceUsuario {
    String FILE_FRANQUIA;
    DadosFranquias dadosFranquias;
    Map<String, Franquia> franquiasMap;


    public ServiceUsuario(String FILE_FRANQUIA) {
        this.FILE_FRANQUIA = FILE_FRANQUIA;
        dadosFranquias = new DadosFranquias(FILE_FRANQUIA);
        franquiasMap = dadosFranquias.listarMap();
    }

    public void addFranquia(Franquia franquia) throws ValidacaoException {
        for(Map.Entry<String, Franquia> entry : franquiasMap.entrySet()) {
            if( entry.getValue().getEndereco().equals(franquia.getEndereco()) ||
                    entry.getValue().getNome().equals(franquia.getNome())) {
                    throw new ValidacaoException("ERRO");
            }
        }
            dadosFranquias.adicionar(franquia);
            franquiasMap = dadosFranquias.listarMap();
        }

   public void removeFranquia(Franquia franquia) throws ValidacaoException {
        if(franquiasMap.containsKey(franquia.getId())){
            dadosFranquias.remover(franquia.getId());
        } else
            throw new ValidacaoException("Franquia invalida");
   }

   public List<Franquia> listarFranquias() {
       return new ArrayList<>(franquiasMap.values());
   }

   public Franquia buscarPorId(String id) {
        return franquiasMap.get(id);
   }

   public void atualizar(Franquia franquia) throws ValidacaoException {
        if (franquiasMap.containsKey(franquia.getId())) {
            dadosFranquias.atualizar(franquia);
            franquiasMap = dadosFranquias.listarMap();
        } else
            throw new ValidacaoException("erro");
   }

}
