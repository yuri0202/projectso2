package yuria.testmap.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Point;

import java.io.Serializable;
import java.util.Date;

import yuria.testmap.serializers.PointToJsonSerializer;

/**
 * Created by yuria on 30/08/2017.
 */

public class Ricerca implements Serializable {
    private String tipo;
    private Date dataDa;
    private Date dataA;
    private Float prezzoDa;
    private Float prezzoA;
    private Float distanza;
    @JsonSerialize(using = PointToJsonSerializer.class)
    private Point pos;
    private int idutente;

    public Ricerca (){}

    public Ricerca(String tipo, Date dataDa, Date dataA, Float prezzoDa, Float prezzoA, Float distanza, Point pos, int idutente) {
        this.tipo = tipo;
        this.dataDa = dataDa;
        this.dataA = dataA;
        this.prezzoDa = prezzoDa;
        this.prezzoA = prezzoA;
        this.distanza = distanza;
        this.pos = pos;
        this.idutente=idutente;
    }

    public int getIdutente() {
        return idutente;
    }

    public void setIdutente(int idutente) {
        this.idutente = idutente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getDataDa() {
        return dataDa;
    }

    public void setDataDa(Date dataDa) {
        this.dataDa = dataDa;
    }

    public Date getDataA() {
        return dataA;
    }

    public void setDataA(Date dataA) {
        this.dataA = dataA;
    }

    public Float getPrezzoDa() {
        return prezzoDa;
    }

    public void setPrezzoDa(Float prezzoDa) {
        this.prezzoDa = prezzoDa;
    }

    public Float getPrezzoA() {
        return prezzoA;
    }

    public void setPrezzoA(Float prezzoA) {
        this.prezzoA = prezzoA;
    }

    public Float getDistanza() {
        return distanza;
    }

    public void setDistanza(Float distanza) {
        this.distanza = distanza;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }
}
