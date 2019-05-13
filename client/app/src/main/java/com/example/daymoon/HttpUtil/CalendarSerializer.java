package com.example.daymoon.HttpUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class CalendarSerializer implements JsonSerializer, JsonDeserializer {

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            GregorianCalendar gregorianCalendar = (GregorianCalendar) src;
            return new JsonPrimitive(format.format(gregorianCalendar.getTime()));
        }
    }

    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT,  JsonDeserializationContext context) throws JsonParseException {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            date = format.parse(json.getAsString());
        } catch (ParseException e) {
            date = null;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return gregorianCalendar;
    }

}