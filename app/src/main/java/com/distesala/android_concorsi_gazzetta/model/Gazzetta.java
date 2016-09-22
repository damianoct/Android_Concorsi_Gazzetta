package com.distesala.android_concorsi_gazzetta.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marco on 20/08/16.
 */
public class Gazzetta implements Serializable
{
    private int idGazzetta;
    private String numberOfPublication;
    private String dateOfPublication;

    private List<Concorso> concorsi;

    public static final String DATE_OF_PUBLICATION = "dateOfPublication";

    public Gazzetta() {}

    public int getIdGazzetta()
    {
        return idGazzetta;
    }

    public void setIdGazzetta(int idGazzetta)
    {
        this.idGazzetta = idGazzetta;
    }

    public String getNumberOfPublication()
    {
        return numberOfPublication;
    }

    public void setNumberOfPublication(String numberOfPublication)
    {
        this.numberOfPublication = numberOfPublication;
    }

    public String getDateOfPublication()
    {
        return dateOfPublication;
    }

    public void setDateOfPublication(String dateOfPublication)
    {
        this.dateOfPublication = dateOfPublication;
    }

    public List<Concorso> getConcorsi()
    {
        return concorsi;
    }

    public void setConcorsi(List<Concorso> concorsi)
    {
        this.concorsi = concorsi;
    }

    @Override
    public String toString()
    {
        return "Gazzetta{" +
                "idGazzetta=" + idGazzetta +
                ", numberOfPublication='" + numberOfPublication + '\'' +
                ", dateOfPublication='" + dateOfPublication + '\'' +
                ", concorsi=" + concorsi +
                '}';
    }
}
