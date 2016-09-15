package com.distesala.android_concorsi_gazzetta.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Marco on 20/08/16.
 */
public class Concorso
{
    private String gazzettaNumberOfPublication; //gazzetta di appartenenza
    private String emettitore;
    private String areaDiInteresse;
    private String titoloConcorso;
    private String tipologia;
    private String codiceRedazionale;
    private int numeroArticoli;
    private String scadenza;
    private int isFavorite;

    public Concorso() {}

    public String getGazzettaNumberOfPublication()
    {
        return gazzettaNumberOfPublication;
    }

    public String getEmettitore()
    {
        return emettitore;
    }

    public String getAreaDiInteresse()
    {
        return areaDiInteresse;
    }

    public String getTitoloConcorso()
    {
        return titoloConcorso;
    }

    public String getCodiceRedazionale()
    {
        return codiceRedazionale;
    }

    public String getTipologia()
    {
        return tipologia;
    }

    public String getScadenza()
    {
        return scadenza;
    }

    public int getNumeroArticoli()
    {
        return numeroArticoli;
    }

    public int getIsFavorite()
    {
        return isFavorite;
    }

    public void setGazzettaNumberOfPublication(String gazzettaNumberOfPublication)
    {
        this.gazzettaNumberOfPublication = gazzettaNumberOfPublication;
    }

    public void setEmettitore(String emettitore)
    {
        this.emettitore = emettitore;
    }

    public void setAreaDiInteresse(String areaDiInteresse)
    {
        this.areaDiInteresse = areaDiInteresse;
    }

    public void setTitoloConcorso(String titoloConcorso)
    {
        this.titoloConcorso = titoloConcorso;
    }

    public void setTipologia(String tipologia)
    {
        this.tipologia = tipologia;
    }

    public void setCodiceRedazionale(String codiceRedazionale)
    {
        this.codiceRedazionale = codiceRedazionale;
    }

    public void setNumeroArticoli(int numeroArticoli)
    {
        this.numeroArticoli = numeroArticoli;
    }

    public void setScadenza(String scadenza)
    {
        this.scadenza = scadenza;
    }

    public void setIsFavorite(int isFavorite)
    {
        this.isFavorite = isFavorite;
    }

    @Override
    public String toString()
    {
        return "Concorso{" +
                ", tipologia=" + tipologia +
                ", emettitore='" + emettitore + '\'' +
                ", areaDiInteresse='" + areaDiInteresse + '\'' +
                ", titoloConcorso='" + titoloConcorso + '\'' +
                ", codiceRedazionale='" + codiceRedazionale + '\'' +
                ", scadenza='" + scadenza + '\'' +
                ", numeroArticoli=" + numeroArticoli +
                '}';
    }
}