package com.example.otoniel.manejadordemaps;

import android.*;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    protected LocationManager locationManager;
    protected AlertDialog alertDialog;
    private EditText editText;
    private LinearLayout linearLayout;

    ModeloMarcadoresFavoritos modeloMarcadoresFavoritos;

    protected static final int PERMISOS_UBICACION = 3;
    private static final String SHOWCASE_ID = "custom example";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        inicializarBD();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        editText = (EditText) findViewById(R.id.activity_maps_localizacion);
        linearLayout = (LinearLayout) findViewById(R.id.activity_maps_buscador);

        chequearGPS();

        //chequearPermisos();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        Posicionamiento posicionamiento = new Posicionamiento(this);
        Location miUbicacion = posicionamiento.getLocation();

        LatLng coordenadas = new LatLng(miUbicacion.getLatitude(), miUbicacion.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Mi ubicación")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getApplicationContext(), "Esta es tu posición actual", Toast.LENGTH_LONG).show();
            }
        });

        traerMarcadoresFavoritos();

        sequenciaTutorial();

    }

    @Override
    public void onResume() {
        super.onResume();
        //chequearGPS();
    }

    public void cargarMapa() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void inicializarBD() {
        modeloMarcadoresFavoritos = new ModeloMarcadoresFavoritos(this);
    }

    public void traerMarcadoresFavoritos() {
        if (modeloMarcadoresFavoritos.hayDatos()) {
            List<MarcadorFavorito> list = modeloMarcadoresFavoritos.consultarTodos();

            for (int i = 0; i < list.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .position(list.get(i).generarCoordenadas())
                        .title(list.get(i).getDescripcion())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.favorito)));
            }
        }

    }

    public void onSearch(View view) {
        final EditText localidad = (EditText) findViewById(R.id.activity_maps_localizacion);
        List<Address> addresses = null;
        if (localidad.getText().length() > 0) {
            Geocoder geocoder = new Geocoder(this);
            try {
                  addresses = geocoder.getFromLocationName(localidad.getText().toString(),1);
            }
            catch (IOException exc) {
                exc.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + exc.toString(), Toast.LENGTH_LONG).show();
            }

            final Address address = addresses.get(0);
            LatLng coordenadas = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(coordenadas).title(localidad.getText().toString()));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(coordenadas));

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Toast.makeText(getApplicationContext(),
                            "Si deseas agregar un marcador nuevo manualmente, manten presionada la " +
                            "pantalla o si deseas agregar uno existente a la lista de favoritos pulse sobre el.",
                            Toast.LENGTH_LONG).show();
                }
            });

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.addMarker(new MarkerOptions()
                            .title("Default")
                            .position(latLng));
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    dialogFavoritos(marker.getPosition(), marker.getTitle());

                    return false;
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Debes ingresar alguna palabra o dirección para poder buscar.", Toast.LENGTH_LONG).show();
        }
    }

    public void dialogFavoritos(final LatLng latLng, final String loc) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea agregar esta ubicacion a favoritos?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (modeloMarcadoresFavoritos.existeMarcador(loc)) {
                            Toast.makeText(getApplicationContext(), "El marcador ya se encuentra en la lista de favoritos", Toast.LENGTH_LONG).show();
                        }
                        else {
                            MarcadorFavorito marcadorFavorito = new MarcadorFavorito();

                            marcadorFavorito.setLatitud(latLng.latitude);
                            marcadorFavorito.setLongitud(latLng.longitude);
                            marcadorFavorito.setDescripcion(loc);

                            modeloMarcadoresFavoritos.insertar(marcadorFavorito, true);
                        }

                        dialog.dismiss();

                        return ;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        alertDialog = builder.create();
        alertDialog.show();

    }

    public void chequearPermisos() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                mMap.setMyLocationEnabled(true);
                //cargarMapa();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISOS_UBICACION);
            }
        }
        else {
            cargarMapa();
        }
    }

    public void chequearGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            solicitarGPS();
        }
        else {
            chequearPermisos();
            //cargarMapa();
        }
    }

    private void sequenciaTutorial() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(editText,
                "Ingresa el nombre de una localidad en este recuadro", "CONTINUAR");

        sequence.addSequenceItem(linearLayout,
                "Luego presiona este boton y podras buscar el lugar que desees", "CERRAR");

        sequence.start();
    }

    public void solicitarGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El gps se encuentra deshabilitado para el correcto funcionamiento de esta aplicación ¿Desea habilitarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int request, String permiso[], int[] grantResult) {
        switch (request) {
            case PERMISOS_UBICACION: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    cargarMapa();
                }
                else {

                }

                return;
            }
        }
    }
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISOS_UBICACION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission. &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }
    */
}
