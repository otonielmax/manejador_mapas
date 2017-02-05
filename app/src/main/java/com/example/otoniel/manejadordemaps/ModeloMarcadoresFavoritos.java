package com.example.otoniel.manejadordemaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Otoniel on 04/02/2017.
 */

public class ModeloMarcadoresFavoritos extends SQLiteOpenHelper {
    // Datos de la BD
    protected static final String DB_NAME = "manejador_mapas.db";
    protected static final Integer DB_VERSION = 1;

    // Datos de la Tabla
    protected static final String TABLE = "marcadores";

    // Nombre de las Columnas
    protected static final String ID_MARCADOR = "id_marcador";
    protected static final String DESCRIPCION = "descripcion";
    protected static final String LATITUD = "latitud";
    protected static final String LONGITUD = "longitud";
    protected static final String NUEVO = "nuevo";

    protected String query = "";

    public ModeloMarcadoresFavoritos(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        crearTabla();
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        crearTabla();
        db.execSQL(query);
    }

    public void crearTabla() {
        query = "CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                ID_MARCADOR + " INTEGER, " +
                DESCRIPCION + " TEXT, " +
                LATITUD + " REAL, " +
                LONGITUD + " REAL, " +
                NUEVO + " BOOLEAN " +
                ")";
    }

    public Boolean hayDatos() {
        SQLiteDatabase consulta = this.getReadableDatabase();
        Cursor cursor = consulta.rawQuery("SELECT * FROM " + TABLE, null);

        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean existeMarcador(String nombre) {
        SQLiteDatabase consulta = this.getReadableDatabase();
        Cursor cursor = consulta.rawQuery("SELECT * FROM " + TABLE +
                " WHERE " + DESCRIPCION + " = '" + nombre + "'", null);

        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    public List<MarcadorFavorito> consultarTodos() {
        SQLiteDatabase consulta = this.getReadableDatabase();
        Cursor cursor = consulta.rawQuery("SELECT * FROM " + TABLE, null);

        List<MarcadorFavorito> marcadorFavoritos = new ArrayList<MarcadorFavorito>();

        if (cursor.moveToFirst()) {
            do {
                MarcadorFavorito nuevo = new MarcadorFavorito();

                nuevo.setId(cursor.getInt(1));
                nuevo.setDescripcion(cursor.getString(2));
                nuevo.setLatitud(cursor.getDouble(3));
                nuevo.setLongitud(cursor.getDouble(4));

                marcadorFavoritos.add(nuevo);
            }
            while (cursor.moveToNext());
        }

        return marcadorFavoritos;
    }

    public MarcadorFavorito consultarPorId(Integer id) {
        SQLiteDatabase consulta = this.getReadableDatabase();
        Cursor cursor = consulta.rawQuery("SELECT * FROM " + TABLE + " WHERE " + ID_MARCADOR + " = " + id, null);

        MarcadorFavorito nuevo = new MarcadorFavorito();

        if (cursor.moveToFirst()) {
            do {

                nuevo.setId(cursor.getInt(1));
                nuevo.setDescripcion(cursor.getString(2));
                nuevo.setLatitud(cursor.getDouble(3));
                nuevo.setLongitud(cursor.getDouble(4));

            }
            while (cursor.moveToNext());
        }

        return nuevo;
    }

    public void insertar(MarcadorFavorito marcadorFavorito, Boolean nuevo) {
        SQLiteDatabase insert = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        if (marcadorFavorito.getId() != null) {
            valores.put(ID_MARCADOR, marcadorFavorito.getId());
        }

        if (marcadorFavorito.getDescripcion() != null) {
            valores.put(DESCRIPCION, marcadorFavorito.getDescripcion());
        }

        if (marcadorFavorito.getLatitud() != null) {
            valores.put(LATITUD, marcadorFavorito.getLatitud());
        }

        if (marcadorFavorito.getLongitud() != null) {
            valores.put(LONGITUD, marcadorFavorito.getLongitud());
        }

        valores.put(NUEVO, nuevo);

        insert.insert(TABLE, null, valores);
        insert.close();
    }

    public Boolean delete(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean data = db.delete(TABLE, ID_MARCADOR + " = " + id, null) > 0;
        db.close();
        return data;
    }

    public Boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean data = db.delete(TABLE, null, null) > 0;
        db.close();
        return data;
    }

}
