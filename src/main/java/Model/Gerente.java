package Model;

public class Gerente extends Usuario{

    public Gerente() {
    }

    public Gerente(String nome, String email, String senha, String cpf) {

        super(nome, email, senha, cpf, 2);
    }

}
