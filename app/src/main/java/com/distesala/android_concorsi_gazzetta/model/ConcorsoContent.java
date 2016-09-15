package com.distesala.android_concorsi_gazzetta.model;

import java.util.List;

/**
 * Created by Marco on 15/09/16.
 */
public class ConcorsoContent
{
    public int numeroArticoli;
    public List<String> articoliBando;

    public int getNumeroArticoli()
    {
        return numeroArticoli;
    }

    public void setNumeroArticoli(int numeroArticoli)
    {
        this.numeroArticoli = numeroArticoli;
    }

    public List<String> getArticoliBando()
    {
        return articoliBando;
    }

    public void setArticoliBando(List<String> articoliBando)
    {
        this.articoliBando = articoliBando;
    }
}
