package model;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("dono")
public class Dono extends Usuario {
    public Dono (){
        super();
    } //construtor vazio para o JSON

    public Dono(String nome, String email, String senha, String cpf) {
        super(nome, email, senha, cpf, 1);
    }

    @Override
    public int getPermissao() {return 1;}
}
