package com.distesala.android_concorsi_gazzetta.model;

import java.util.List;

/**
 * Created by Marco on 20/08/16.
 */
public class Gazzetta
{
    private int idGazzetta;
    private String numberOfPublication;
    private String dateOfPublication;

    private List<Concorso> concorsi;

    public Gazzetta() {}

    public int getIdGazzetta()
    {
        return idGazzetta;
    }

    public String getNumberOfPublication()
    {
        return numberOfPublication;
    }

    public String getDateOfPublication()
    {
        return dateOfPublication;
    }

    public List<Concorso> getConcorsi()
    {
        return concorsi;
    }
}
