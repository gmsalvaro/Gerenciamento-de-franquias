package Model;

public class Vendedor extends Usuario {
     public  Vendedor(String nome, String email, String senha, String cpf) {
         super(nome, email, senha, cpf, 3);
     }

    @Override
    public int getPermissao() {return 3;}

    public Vendedor(){} //contrutor vazio para o JSON
}
