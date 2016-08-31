package com.distesala.android_concorsi_gazzetta.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Marco on 20/08/16.
 */
public class Concorso
{
    private String emettitore;
    private String areaDiInteresse;
    private String titoloConcorso;
    private String tipologia;
    private String codiceRedazionale;
    private int numeroArticoli;
    private long scadenza; //date in UNIX date format.

    public Concorso() {}

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

    public long getScadenza()
    {
        return scadenza;
    }

    public int getNumeroArticoli()
    {
        return numeroArticoli;
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

    public void setScadenza(long scadenza)
    {
        this.scadenza = scadenza;
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
