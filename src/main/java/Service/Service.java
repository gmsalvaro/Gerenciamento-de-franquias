package Service;

import exception.persistencia.PersistenciaException;
import java.util.List;

public interface Service<T> {
    void adicionar(T objeto) throws PersistenciaException;

    void atualizar(T objeto) throws PersistenciaException;

    void remover(String id) throws PersistenciaException;

    T buscarPorId(String id) throws PersistenciaException;

    List<T> listarTodos() throws PersistenciaException;
}
