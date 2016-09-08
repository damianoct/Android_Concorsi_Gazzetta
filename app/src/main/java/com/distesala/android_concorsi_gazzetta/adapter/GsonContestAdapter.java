package com.distesala.android_concorsi_gazzetta.adapter;

import android.util.Log;

import com.distesala.android_concorsi_gazzetta.model.Concorso;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by damiano on 31/08/16.
 */

public class GsonContestAdapter implements JsonDeserializer<Concorso>
{
    @Override
    public Concorso deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {

        final JsonObject jsonObject = json.getAsJsonObject();

        final String areaDiInteresse = jsonObject.get("areaDiInteresse").getAsString();
        final String codiceRedazionale = jsonObject.get("codiceRedazionale").getAsString();
        final String emettitore = jsonObject.get("emettitore").getAsString();
        final int numeroArticoli = jsonObject.get("numeroArticoli").getAsInt();
        final String titoloConcorso = jsonObject.get("titoloConcorso").getAsString();

        String tipologia = null;
        String scadenza = null; //no scadenza

        String type = jsonObject.get("tipologia").getAsString();

        try
        {
            String dateString = type.substring(type.indexOf(".") + 1, type.indexOf(")"));
            tipologia =  type.substring(0, type.indexOf(" "));

            DateFormat formatter = new SimpleDateFormat(" dd MMMM yyyy", Locale.ITALY);
            Date d = formatter.parse(dateString);
            DateFormat dfInsert = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
            scadenza = dfInsert.format(d);
        }
        catch(IndexOutOfBoundsException e) //è un concorso senza scadenza
        {
            tipologia = type;
        }
        catch(ParseException e)
        {
            System.out.println("Errore parsing data.");
        }

        final Concorso c = new Concorso();
        c.setAreaDiInteresse(areaDiInteresse);
        c.setCodiceRedazionale(codiceRedazionale);
        c.setEmettitore(emettitore);
        c.setNumeroArticoli(numeroArticoli);
        c.setTitoloConcorso(titoloConcorso);
        c.setTipologia(tipologia);
        c.setScadenza(scadenza);

        return c;
    }
}
