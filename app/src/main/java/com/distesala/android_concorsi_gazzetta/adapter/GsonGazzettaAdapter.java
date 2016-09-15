package com.distesala.android_concorsi_gazzetta.adapter;

import com.distesala.android_concorsi_gazzetta.model.Concorso;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by damiano on 03/09/16.
 */
public class GsonGazzettaAdapter implements JsonDeserializer<Gazzetta>
{
    @Override
    public Gazzetta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {

        final JsonObject jsonObject = json.getAsJsonObject();

        final int idGazzetta = jsonObject.get("idGazzetta").getAsInt();
        final String numberOfPublication = jsonObject.get("numberOfPublication").getAsString();

        //retrieve date
        DateFormat formatter = new SimpleDateFormat("ddMMyy", Locale.ITALY);
        DateFormat dfInsert = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        final String dateString = jsonObject.get("dateOfPublication").getAsString();

        String dateOfPublication;

        try
        {
            dateOfPublication = dfInsert.format(formatter.parse(dateString));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            dateOfPublication = "1970-01-01"; // :-)
        }

        List<Concorso> contestsList = new LinkedList<>();

        final JsonArray concorsiJSONArray = jsonObject.get("concorsi").getAsJsonArray();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Concorso.class, new GsonContestAdapter());
        Gson gson = builder.create();

        for (JsonElement j: concorsiJSONArray)
        {
            Concorso c = gson.fromJson(j, Concorso.class);
            c.setGazzettaNumberOfPublication(numberOfPublication);
            c.setGazzettaDateOfPublication(dateOfPublication);
            contestsList.add(c);
        }

        final Gazzetta g = new Gazzetta();

        g.setIdGazzetta(idGazzetta);
        g.setNumberOfPublication(numberOfPublication);
        g.setDateOfPublication(dateOfPublication);
        g.setConcorsi(contestsList);

        return g;
    }
}
