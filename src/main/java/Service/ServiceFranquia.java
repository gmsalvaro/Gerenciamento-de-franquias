package Service;

import repository.DadosFranquias;
import model.*;
import exception.ValidacaoException;
import exception.persistencia.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class  ServiceFranquia {
    String FILE_FRANQUIA;
    DadosFranquias dadosFranquias;

    public ServiceFranquia(String FILE_FRANQUIA) throws PersistenciaException {
        this.dadosFranquias = new DadosFranquias(FILE_FRANQUIA);
    }

    public void adicionar(Franquia franquia) throws PersistenciaException {
        for(Franquia f : dadosFranquias.listarMap().values()) {
            if(f.getEndereco().equalsIgnoreCase(franquia.getEndereco()) ||
                    f.getNome().equalsIgnoreCase(franquia.getNome())) {
                throw new LojaInvalidaException("ERRO: já existe uma franquia com esse nome ou endereço!");
            }
        }
        dadosFranquias.adicionar(franquia);
    }

    public void remover(Franquia franquia, ServiceManager serviceManager) throws PersistenciaException, ValidacaoException {
        Optional<Franquia> franquiaAtualizada = Optional.ofNullable(buscarPorId(franquia.getId()));
        if (franquiaAtualizada.isEmpty()) {
            throw new PersistenciaException("Franquia '" + franquia.getNome() + "' não encontrada para remoção.");
        }
        List<String> idsLojas = new ArrayList<>(franquiaAtualizada.get().getIdLojas());
        for (String idLoja : idsLojas) {
            Loja lojaParaRemover = serviceManager.getServiceLoja().getLojaById(idLoja);
            if (lojaParaRemover != null) {
                serviceManager.getServiceLoja().remover(lojaParaRemover, serviceManager);
            }
        }
        dadosFranquias.remover(franquiaAtualizada.get().getId());
        System.out.println("Franquia '" + franquia.getNome() + "' e todos os seus dados associados foram removidos com sucesso!");
    }


    public List<Franquia> listarTodos() {
        return new ArrayList<>(dadosFranquias.listarMap().values());
    }

    public Franquia buscarPorId(String id) {
        return dadosFranquias.listarMap().get(id);
    }

    public void atualizar(Franquia franquia) throws PersistenciaException, ValidacaoException {
        if (existeDuplicata(franquia)) {
            throw new ValidacaoException("Já existe uma franquia com este nome ou endereço.");
        }
        dadosFranquias.atualizar(franquia);
    }

    public Optional<Franquia> getFranquiaDoGerente(Usuario gerente, ServiceLoja serviceloja) {
        if (!(gerente instanceof Gerente))
            return null;
        for (Loja loja : serviceloja.listarTodos()) {
            if (loja.getIdsUsuarios() != null && loja.getIdsUsuarios().contains(gerente.getId()))
                return Optional.ofNullable(this.buscarPorId(loja.getFranquiaId()));
        }
        return null;
    }

    public boolean existeDuplicata(Franquia franquiaParaVerificar) {
        for (Franquia existente : listarTodos()) {
            if (existente.getId().equals(franquiaParaVerificar.getId())) {
                continue;
            }
            if (existente.getNome().equalsIgnoreCase(franquiaParaVerificar.getNome()) ||
                    existente.getEndereco().equalsIgnoreCase(franquiaParaVerificar.getEndereco())) {
                return true;
            }
        }
        return false;
    }

}
