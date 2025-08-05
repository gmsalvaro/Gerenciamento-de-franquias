package model;
import com.fasterxml.jackson.annotation.JsonTypeName; // <-- ADICIONE ESTE IMPORT

@JsonTypeName("vendedor")
public class Vendedor extends Usuario {

    public Vendedor(){
        super();
    } //contrutor vazio para o JSON

    public  Vendedor(String nome, String email, String senha, String cpf) {
         super(nome, email, senha, cpf, 3);
     }

    @Override
    public int getPermissao() {return 3;}
}
