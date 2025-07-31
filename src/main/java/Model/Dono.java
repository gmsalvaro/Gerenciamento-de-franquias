package Model;

public class Dono extends Usuario {
    public Dono(String nome, String email, String senha, String cpf) {
        super(nome, email, senha, cpf, 1);
    }

    @Override
    public int getPermissao() {return 1;}
}
