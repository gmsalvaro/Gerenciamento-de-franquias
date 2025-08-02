package Service;

import Dados.DadosLojas;
import Model.Franquia;
import Model.Loja;
import exception.persistencia.LojaInvalidaException;
import exception.persistencia.LojaNaoAtualizadaException;
import exception.persistencia.LojaNaoRemovidaException;
import exception.persistencia.PersistenciaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceLoja {
    private final String FILE_LOJA;
    private final DadosLojas dadosLojas;
    private Map<String, Loja> lojasMap;

    public ServiceLoja(String FILE_LOJA) throws PersistenciaException {
        this.FILE_LOJA = FILE_LOJA;
        this.dadosLojas = new DadosLojas(FILE_LOJA);
        this.lojasMap = dadosLojas.getLojasMap();
    }

    public void addLoja(Loja loja, Franquia franquia) throws PersistenciaException {
        for (Loja l : lojasMap.values()) { // verificar essa validação !
            if (l.getNome().equalsIgnoreCase(loja.getNome()) ||
                l.getEndereco().equalsIgnoreCase(loja.getEndereco())) {
                throw new LojaInvalidaException("Loja com nome ou endereço já existente.");
            }
        }
        if(franquia == null) {
            throw new LojaInvalidaException("Franquia invalida");
        }
        loja.setFranquiaId(franquia.getId());
        franquia.adicionarIdLoja(loja.getId());
        dadosLojas.adicionar(loja);
        lojasMap = dadosLojas.getLojasMap();
    }

    public void removerLoja(String id) throws PersistenciaException {
        if (lojasMap.containsKey(id)) {
            dadosLojas.remover(id);
            lojasMap = dadosLojas.getLojasMap();
        } else {
            throw new LojaNaoRemovidaException("Loja não encontrada para remoção.");
        }
    }

    public List<Loja> listarTodasAsLojas() {
        return new ArrayList<>(lojasMap.values());
    }

    public List<Loja> listarPorIDFranquia(String id) {
        ArrayList<Loja> lojas = new ArrayList<>();
        for(Loja l : lojasMap.values()) {
            if (l.getFranquiaId().equalsIgnoreCase(id)) {
                lojas.add(l);
            }
        }
        return lojas;
    }

    public Loja buscarPorId(String id) {
        return lojasMap.get(id);
    }

    public void atualizarLoja(Loja lojaAtualizada) throws PersistenciaException {
        if (lojasMap.containsKey(lojaAtualizada.getId())) {
            dadosLojas.atualizar(lojaAtualizada);
            lojasMap = dadosLojas.getLojasMap();
        } else {
            throw new LojaNaoAtualizadaException("Loja não encontrada para atualização.");
        }
    }

    public Loja getLojaById(String idLoja) throws PersistenciaException {
        return lojasMap.get(idLoja);
    }
}

