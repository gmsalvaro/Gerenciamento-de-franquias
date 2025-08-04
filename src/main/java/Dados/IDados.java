package Dados;

import exception.persistencia.PersistenciaException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IDados<T, ID>{
    void adicionar(T entidade) throws PersistenciaException;

    void atualizar(T entidadeAtualizada) throws PersistenciaException;

    void remover(ID id) throws PersistenciaException;

    Optional<T> buscarPorId(ID id);

    List<T> listarTodas();

    Map<ID, T> listarMap();

}
