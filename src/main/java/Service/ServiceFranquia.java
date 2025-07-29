package Service;

import Dados.DadosFranquias;
import Model.Franquia;
import exception.ValidacaoException;
import exception.persistencia.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceFranquia {
    String FILE_FRANQUIA;
    DadosFranquias dadosFranquias;
    Map<String, Franquia> franquiasMap;


    public ServiceFranquia(String FILE_FRANQUIA) throws PersistenciaException {
        this.FILE_FRANQUIA = FILE_FRANQUIA;
        try{
            dadosFranquias = new DadosFranquias(FILE_FRANQUIA);
            franquiasMap = dadosFranquias.listarMap();

        }catch(ErroCarregarArquivosException e){
            throw new ErroCarregarArquivosException("ERRO: não foi possível inicializar o serviço de franquias!"+e.getMessage());
        }
    }

    public void addFranquia(Franquia franquia) throws PersistenciaException {
        for(Map.Entry<String, Franquia> entry : franquiasMap.entrySet()) {
            if( entry.getValue().getEndereco().equals(franquia.getEndereco()) ||  //Verifica se tem franquias com mesmo nome ou endereço
                    entry.getValue().getNome().equals(franquia.getNome())) {
                throw new LojaInvalidaException("ERRO: já existe uma loja com esse nome ou endereço!"); // Excessao
            }
        }
        dadosFranquias.adicionar(franquia);
        franquiasMap = dadosFranquias.listarMap();
    }

    public void removeFranquia(Franquia franquia) throws PersistenciaException {
        if(franquiasMap.containsKey(franquia.getId())){
            dadosFranquias.remover(franquia.getId());
        } else
            throw new LojaNaoRemovidaException("Franquia invalida");
    }

    public List<Franquia> listarFranquias() {
        return new ArrayList<>(franquiasMap.values());
    }

    public Franquia buscarPorId(String id) {
        return franquiasMap.get(id);
    }



    public void atualizar(Franquia franquia) throws PersistenciaException {
        if (franquiasMap.containsKey(franquia.getId())) {
            dadosFranquias.atualizar(franquia);
            franquiasMap = dadosFranquias.listarMap();
        } else
            throw new LojaNaoAtualizadaException("ERRO: não foi possível atualizar a loja!");
    }
}
