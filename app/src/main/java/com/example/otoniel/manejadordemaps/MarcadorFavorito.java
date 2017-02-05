package com.example.otoniel.manejadordemaps;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * Created by Otoniel on 04/02/2017.
 */

public class MarcadorFavorito {

    protected Integer id;
    protected String descripcion;
    protected Double latitud;
    protected Double longitud;

    public MarcadorFavorito() {

    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public Integer getId() {
        return id;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public LatLng generarCoordenadas() {
        return new LatLng(getLatitud(), getLongitud());
    }
}
