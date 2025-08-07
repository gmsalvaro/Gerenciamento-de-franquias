package model;

//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName("cliente")
public class Cliente {
    private String id;
    private String nome;
    private String cpf;

    public Cliente() {
        this.id = UUID.randomUUID().toString();
    }

    public Cliente(String nome, String cpf) {
        this();
        this.nome = nome;
        this.cpf = cpf;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    @Override
    public String toString() {
        return getNome() + " (CPF: " + getCpf() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}