package Model;

public class Gerente extends Usuario{
    private String idloja;

    public Gerente() {
        super(); //contrsutor vazio pra leitura do JSON
    }

    public Gerente(String nome, String email, String senha, String cpf) {

        super(nome, email, senha, cpf, 2);
    }

    public String getIdloja() {return idloja;}
    public void setIloja(String idDaloja) { this.idloja = idDaloja;}

    @Override
    public int getPermissao() {return 2;}
}
