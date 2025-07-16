package Model;

public class Usuario {
    String nome;
    String password;
    String documento;
    String dataNascimento;
    TipoUsuario tipoUsuario;

    public Usuario(TipoUsuario tipoUsuario,String nome, String password, String documento, String dataNascimento) {
        this.nome = nome;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.documento = documento;
        this.dataNascimento = dataNascimento;
    }
    public String getNome() {
        return nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    //Validar senha e login;

}
