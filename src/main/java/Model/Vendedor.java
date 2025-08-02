package Model;

public class Vendedor extends Usuario {
     public  Vendedor(String nome, String email, String senha,  String cpf) {
         super(nome, email, senha, cpf, 3);
     }

}
