package com.distesala.android_concorsi_gazzetta.model;

import java.util.List;

/**
 * Created by Marco on 20/08/16.
 */
public class Concorso
{
    private int idConcorso;

    private int idGazzetta;
    private String emettitore;
    private String areaDiInteresse;
    private String titoloConcorso;
    private String codiceRedazionale;
    private int numeroArticoli;

    private List<String> articoliBando;

    public Concorso() {}

    public int getIdConcorso()
    {
        return idConcorso;
    }

    public int getIdGazzetta()
    {
        return idGazzetta;
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

    public int getNumeroArticoli()
    {
        return numeroArticoli;
    }

    public List<String> getArticoliBando()
    {
        return articoliBando;
    }
}
