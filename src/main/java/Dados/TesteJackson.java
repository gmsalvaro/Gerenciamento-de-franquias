package Dados;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.ArrayList;
import java.util.List;

public class TesteJackson {

    // --- Suas classes de modelo, simplificadas para o teste ---

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Dono.class, name = "dono"),
            @JsonSubTypes.Type(value = Gerente.class, name = "gerente"),
            @JsonSubTypes.Type(value = Vendedor.class, name = "vendedor")
    })
    public static abstract class Usuario {
        private String nome;
        private int permissao;

        public Usuario() {}
        public Usuario(String nome, int permissao) {
            this.nome = nome;
            this.permissao = permissao;
        }
        public String getNome() { return nome; }
        public int getPermissao() { return permissao; }
    }

    @JsonTypeName("dono")
    public static class Dono extends Usuario {
        public Dono() { super(); }
        public Dono(String nome) { super(nome, 1); }
    }

    @JsonTypeName("gerente")
    public static class Gerente extends Usuario {
        public Gerente() { super(); }
        public Gerente(String nome) { super(nome, 2); }
    }

    @JsonTypeName("vendedor")
    public static class Vendedor extends Usuario {
        public Vendedor() { super(); }
        public Vendedor(String nome) { super(nome, 3); }
    }


    // --- O MÉTODO DE TESTE ---
    public static void main(String[] args) {
        System.out.println("--- INICIANDO TESTE DE SERIALIZAÇÃO JACKSON ---");

        // 1. Cria uma lista com diferentes tipos de Usuario
        List<Usuario> listaDeUsuarios = new ArrayList<>();
        listaDeUsuarios.add(new Dono("Álvaro"));
        listaDeUsuarios.add(new Gerente("Heitor"));
        System.out.println("Lista de objetos criada com sucesso.");

        // 2. Configura o ObjectMapper EXATAMENTE como no seu projeto
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerSubtypes(new NamedType(Dono.class, "dono"));
        mapper.registerSubtypes(new NamedType(Gerente.class, "gerente"));
        mapper.registerSubtypes(new NamedType(Vendedor.class, "vendedor"));
        System.out.println("ObjectMapper configurado.");

        // 3. Tenta converter a lista de objetos para uma String JSON
        try {
            System.out.println("\n--- RESULTADO DA SERIALIZAÇÃO ---");
            String jsonResult = mapper.writeValueAsString(listaDeUsuarios);

            // 4. Imprime o resultado no console
            System.out.println(jsonResult);

        } catch (JsonProcessingException e) {
            System.err.println("!!! OCORREU UM ERRO DURANTE A SERIALIZAÇÃO !!!");
            e.printStackTrace();
        }
    }
}