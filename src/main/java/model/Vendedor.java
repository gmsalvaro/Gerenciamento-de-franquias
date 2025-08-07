//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package model;
import com.fasterxml.jackson.annotation.JsonTypeName;

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
