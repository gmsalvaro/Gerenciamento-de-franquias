package Service;

import repository.DadosLojas;
import model.Franquia;
import model.Gerente;
import model.Loja;
import model.Usuario;
import exception.ValidacaoException;
import exception.persistencia.LojaInvalidaException;
import exception.persistencia.LojaNaoAtualizadaException;
import exception.persistencia.LojaNaoRemovidaException;
import exception.persistencia.PersistenciaException;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceLoja {
    private final String FILE_LOJA;
    private final DadosLojas dadosLojas;
    private Map<String, Loja> lojasMap;

    public ServiceLoja(String FILE_LOJA) throws PersistenciaException {
        this.FILE_LOJA = FILE_LOJA;
        this.dadosLojas = new DadosLojas(FILE_LOJA);
    }

    public void adicionar(Loja loja, Franquia franquia, Gerente gerenteResponsavel) throws PersistenciaException {
        for (Loja l : dadosLojas.listarMap().values()) {
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
        loja.addUsuarioID(gerenteResponsavel.getId());
        dadosLojas.adicionar(loja);
    }

    public void auxRemoverLoja(String id) throws PersistenciaException {
        if (dadosLojas.listarMap().containsKey(id)) {
            dadosLojas.remover(id);
        } else {
            throw new LojaNaoRemovidaException("Loja não encontrada para remoção.");
        }
    }

    public void remover(Loja lojaParaRemover, ServiceManager serviceManager) throws PersistenciaException, ValidacaoException {
        if (lojaParaRemover == null) {
            return;
        }
        String idFranquia = lojaParaRemover.getFranquiaId();
        if (idFranquia != null) {
            Optional<Franquia> franquiaMae = Optional.ofNullable(serviceManager.getServiceFranquia().buscarPorId(idFranquia));
            if (franquiaMae.isPresent()) {
                franquiaMae.get().removeIDLoja(lojaParaRemover.getId());
                serviceManager.getServiceFranquia().atualizar(franquiaMae.orElse(null));
            }
        }
        List<String> idsUsuarios = new ArrayList<>(lojaParaRemover.getIdsUsuarios());
        for (String idUsuario : idsUsuarios) {
            Usuario usuario = serviceManager.getServiceUsuario().buscarPorId(idUsuario).orElse(null);
            if (usuario != null) {
                serviceManager.getServiceUsuario().removeUsuario(usuario);
            }
        }
        this.auxRemoverLoja(lojaParaRemover.getId());
    }

    public List<Loja> listarTodos() {
        return new ArrayList<>(dadosLojas.listarMap().values());
    }

    public List<Loja> listarPorFranquia(String id) {
        ArrayList<Loja> lojas = new ArrayList<>();
        for(Loja l : dadosLojas.listarMap().values()) {
            if (l.getFranquiaId().equalsIgnoreCase(id)) {
                lojas.add(l);
            }
        }
        return lojas;
    }


    public void atualizar(Loja lojaAtualizada) throws PersistenciaException {
        if (dadosLojas.listarMap().containsKey(lojaAtualizada.getId())) {
            dadosLojas.atualizar(lojaAtualizada);
        } else {
            throw new LojaNaoAtualizadaException("Loja não encontrada para atualização.");
        }
    }

    public boolean lojaTemGerente(Loja loja, ServiceManager serviceManager) {
        return auxLojaTemGerente(loja, serviceManager.getServiceUsuario());
    }

    private boolean auxLojaTemGerente(Loja loja, ServiceUsuario serviceUsuario) {
        if (loja.getIdsUsuarios() == null || loja.getIdsUsuarios().isEmpty()) {
            return false;
        }
        return loja.getIdsUsuarios().stream()
                .map(serviceUsuario::getUsuarioById)
                .filter(Objects::nonNull)
                .anyMatch(usuario -> usuario instanceof Gerente);
    }

    public String getNomeGerenteDaLoja(Loja loja, ServiceManager serviceManager) {
        if(loja.getIdsUsuarios() == null || loja.getIdsUsuarios().isEmpty() | !lojaTemGerente(loja, serviceManager))
            return null;
        else{
            for(String idUsuario : loja.getIdsUsuarios()) {
                Usuario usuario = serviceManager.getServiceUsuario().getUsuarioById(idUsuario);
                if(usuario instanceof Gerente)
                    return usuario.getNome();
            }
        }
        return null;
    }

    public Loja getLojaById(String idLoja) {
                return dadosLojas.listarMap().get(idLoja);
    }


    public void designarGerenteParaLoja(Gerente novoGerente, Loja novaLoja, ServiceUsuario serviceUsuario) throws PersistenciaException {
        Optional<Loja> lojaAntigaOpt = buscarLojaPorUsuario(novoGerente);
        if (lojaAntigaOpt.isPresent()) {
            Loja lojaAntiga = lojaAntigaOpt.get();
            if (lojaAntiga.getId().equals(novaLoja.getId())) {
                return;
            }
            System.out.println("Removendo gerente '" + novoGerente.getNome() + "' da loja antiga '" + lojaAntiga.getNome() + "'");
            lojaAntiga.getIdsUsuarios().remove(novoGerente.getId());
            this.atualizar(lojaAntiga);
        }
        if (novaLoja.getIdsUsuarios() != null) {
            novaLoja.getIdsUsuarios().removeIf(idUsuario -> {
                Optional<Usuario> uOpt = serviceUsuario.buscarPorId(idUsuario);
                return uOpt.isPresent() && uOpt.get() instanceof Gerente;
            });
        }
        System.out.println("Adicionando gerente '" + novoGerente.getNome() + "' à nova loja '" + novaLoja.getNome() + "'");
        novaLoja.addUsuarioID(novoGerente.getId());
        this.atualizar(novaLoja);

        novoGerente.setIloja(novaLoja.getId());
        serviceUsuario.atualizarUsuario(novoGerente);
    }

    public List<Loja> listarLojasSemGerente(ServiceManager serviceManager) {
        return listarTodos().stream()
                .filter(loja -> !lojaTemGerente(loja, serviceManager))
                .collect(Collectors.toList());
    }

    public Optional<Loja> buscarLojaPorUsuario(Usuario usuario) {
        if (usuario == null) {
            return Optional.empty();
        }
        return listarTodos().stream()
                .filter(loja -> loja.getIdsUsuarios() != null && loja.getIdsUsuarios().contains(usuario.getId()))
                .findFirst();
    }

}

