package com.example.otoniel.manejadordemaps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Otoniel on 04/02/2017.
 */

public class Posicionamiento implements LocationListener {

    private Context context;
    LocationManager locationManager;
    String proveedor;
    private  boolean networkOn;

    public Posicionamiento(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        proveedor = LocationManager.NETWORK_PROVIDER;
        networkOn = locationManager.isProviderEnabled(proveedor);
        try {
            locationManager.requestLocationUpdates(proveedor, 1000, 1, this);
        }
        catch (SecurityException exc) {
            Log.v("ExcepcionUbicacion", exc.toString());
        }
        updateLocation();
    }
    /*
    public void solicitarPermisosGPSyNetwork() {

    }
    */

    private void updateLocation() {
        if (networkOn) {
            try {
                Location location = locationManager.getLastKnownLocation(proveedor);
            }
            catch (SecurityException exc) {
                Log.v("ExcepcionUbicacion", exc.toString());
            }

        }
    }

    public Location getLocation() {
        if (networkOn) {

            try {
                Location location = locationManager.getLastKnownLocation(proveedor);
                if (location != null) {
                    return location;
                }
            }
            catch (SecurityException exc) {
                Log.v("ExcepcionUbicacion", exc.toString());
            }

        }

        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
