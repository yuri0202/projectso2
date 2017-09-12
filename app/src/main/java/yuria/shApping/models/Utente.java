package yuria.shApping.models;

import java.io.Serializable;

/**
 * Created by yuria on 21/08/2017.
 */

public class Utente implements Serializable{


    private int idutente;
    private String username;
    private String password;
    private String nome;
    private String cognome;


    public Utente () {}

    public Utente(int idutente, String username, String password, String nome, String cognome) {
        this.idutente = idutente;
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
    }

    public int getIdutente() {
        return idutente;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setIdutente(int idutente) {
        this.idutente = idutente;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
