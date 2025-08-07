package model;
import com.fasterxml.jackson.annotation.JsonTypeName; // <-- ADICIONE ESTE IMPORT

//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

@JsonTypeName("gerente")
public class Gerente extends Usuario{
    private String idloja;

    public Gerente() {
        super();
    } //contrutor vazio para o JSON

    public Gerente(String nome, String email, String senha, String cpf) {
        super(nome, email, senha, cpf, 2);
    }

    public String getIdloja() {return idloja;}
    public void setIloja(String idDaloja) { this.idloja = idDaloja;}

    @Override
    public int getPermissao() {return 2;}
}
