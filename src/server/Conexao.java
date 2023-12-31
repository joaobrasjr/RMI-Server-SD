package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Conexao {
    public Statement stm;
    public ResultSet rs;
    private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private String caminho;
    private String usuario;
    private String senha;
    private String database;
    private Connection con;

    public Connection Conexao(String caminho, String usuario, String senha, String dataBase) {
        this.caminho = caminho;
        this.usuario = usuario;
        this.senha = senha;
        this.database = dataBase;

        try {
            System.setProperty("jdbc.Drivers", driver); //permissão que define valor da propriedade de segurança
            con = DriverManager.getConnection(this.caminho + ";" + "user=" + this.usuario + ";" + "password=" + this.senha + ";" + "databaseName=" + this.database + ";" + "encrypt=false");
            System.out.println("Conectou");
            return con;
        } catch (Exception e) {
            System.out.println("Falha ao conectar no banco");
        }
        return con;
    }
}
