package yuria.shApping.models;



import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Point;

import java.io.Serializable;
import java.util.Date;

import yuria.shApping.serializers.JsonToPointDeserializer;
import yuria.shApping.serializers.PointToJsonSerializer;

/**
 * Created by yuria on 22/08/2017.
 */



public class Registrazione implements Serializable{
    //Represent a registration

    private int idreg;
    private String nome;
    private String tipo;
    @JsonDeserialize(using= JsonToPointDeserializer.class)
    @JsonSerialize (using = PointToJsonSerializer.class)
    private Point pos;
    //private String foto;
    private Date data;
    private float prezzo;
    private String dettagli;
    private Utente utente;



    private int idutente;

    public Registrazione() {}

    public Registrazione(int idreg, String nome, String tipo, Point pos, Date data, String dettagli, Utente utente, int idutente, float prezzo) {
        this.idreg = idreg;
        this.nome = nome;
        this.tipo = tipo;
        this.pos=pos;
        this.data = data;
        this.dettagli = dettagli;
        this.utente = utente;
        this.idutente=idutente;
        this.prezzo=prezzo;
    }

    public int getIdreg() {
        return idreg;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public Point getPos() {
        return pos;
    }

    public void setIdreg(int idreg) {
        this.idreg = idreg;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setDettagli(String dettagli) {
        this.dettagli = dettagli;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Date getData() {
        return data;

    }

    public String getDettagli() {
        return dettagli;
    }

    public Utente getUtente() {
        return utente;
    }

    public int getIdutente() {
        return idutente;
    }

    public void setIdutente(int idutente) {
        this.idutente = idutente;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }
}
