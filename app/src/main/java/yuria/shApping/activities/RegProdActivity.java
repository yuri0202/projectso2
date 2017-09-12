package yuria.shApping.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationServices;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuria.shApping.R;
import yuria.shApping.models.Registrazione;
import yuria.shApping.models.Utente;
import yuria.shApping.resources.CustomToast;

import static java.util.Arrays.asList;


public class RegProdActivity extends MenuActivity implements ConnectionCallbacks, OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    Button IndietroBtn, posBtn,registraBtn;
    EditText addressTxt,detailsTxt,nomeTxt,prezzoTxt;
    Spinner tipoSpinner;
    Location mLastLocation = null;
    GoogleApiClient mGoogleApiClient = null;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    double longitude = 0, latitude = 0;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = true;
    private static final String TAG = "debug";

    final Activity activity = this;
    RestTemplate restTemplate = new RestTemplate();
    private List<MediaType> acceptableMediaTypes = asList(MediaType.APPLICATION_JSON);
    HttpHeaders headers = new HttpHeaders() {{
        setAccept(acceptableMediaTypes);
    }};




    private static int UPDATE_INTERVAL = 5000; // SEC
    private static int FATEST_INTERVAL = 3000; // SEC
    private static int DISPLACEMENT = 10; // METERS


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices())
                        buildGoogleApiClient();
                    createLocationRequest();
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_prod);
        initWidget();


    }

    private void initWidget() {
        IndietroBtn = (Button) findViewById(R.id.indietroRicBtn);
        posBtn = (Button) findViewById(R.id.positionBtn);
        registraBtn = (Button) findViewById(R.id.RegistraBtn);
        detailsTxt = (EditText) findViewById(R.id.detailsTxt);
        addressTxt = (EditText) findViewById(R.id.addressTxt);
        prezzoTxt = (EditText) findViewById(R.id.prezzoTxt);
        nomeTxt = (EditText) findViewById(R.id.nomeTxt);
        tipoSpinner = (Spinner) findViewById(R.id.tipoSpinner);


        IndietroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegProdActivity.this, HomeActivity.class));
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Run-time request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLocation();
            }
        });

        registraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registraProdotto();
            }
        });
    }

    private void registraProdotto() {
        if(nomeTxt.getText().toString().equals("") ||  prezzoTxt.getText().toString().equals("") ||
                addressTxt.getText().toString().equals("")  )
            CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_error), R.layout.custom_toast_error,  "Inserire campi obbligatori");
        else{
            List<Address> add = null;
            String ind = null;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            Point newPoint=null;
            try {
                ind = addressTxt.getText().toString();
                add = geocoder.getFromLocationName(ind,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(add.size()==0) {
                CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_error), R.layout.custom_toast_error,  "Indirizzo non trovato, riprova");
            }else{
                double lat = add.get(0).getLatitude();
                double lon = add.get(0).getLongitude();
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                newPoint = geometryFactory.createPoint(new Coordinate(lat, lon));

               // Date nowDate = Calendar.getInstance().getTime();
                Date nowDate = new Date();
                String details = "";
                if (!detailsTxt.getText().toString().equals(""))
                    details=detailsTxt.getText().toString();
                float prezzo = Float.parseFloat(prezzoTxt.getText().toString());

                //Chiamare costruttore per cazzimmocca.
                new RegProdActivity.validateHttpRequest(nomeTxt.getText().toString(),
                                                        tipoSpinner.getSelectedItem().toString(),
                                                        newPoint,
                                                        nowDate,
                                                        details,
                                                        null,
                                                        MenuActivity.utente.getIdutente(),
                                                        prezzo).execute();

            }

        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        if(mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        //Fix first time run app if permission doesn't grant yet so can't get anything
        mGoogleApiClient.connect();


    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //displayLocation();
        if(mRequestingLocationUpdates)
            startLocationUpdates();
    }



    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //displayLocation();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
             latitude = mLastLocation.getLatitude();
             longitude = mLastLocation.getLongitude();
            //txtCoordinates.setText(latitude + " / " + longitude);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses  = null;
            try {
                addresses = geocoder.getFromLocation(latitude,longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }


            String address = addresses.get(0).getAddressLine(0);
            addressTxt.setText(address);
        } else
           addressTxt.setText("Couldn't get the location. Make sure location is enable on the device");

    }



    private class validateHttpRequest extends AsyncTask<Void, Void, Void> {
        String nome;
        String tipo;
        Point pos;
        Date data;
        String dettagli;
        Utente utente;
        int idutente;
        float prezzo;


        public validateHttpRequest( String nome, String tipo, Point pos, Date data, String dettagli, Utente utente, int idutente, float prezzo) {
            this.nome = nome;
            this.tipo = tipo;
            this.pos = pos;
            this.data = data;
            this.dettagli = dettagli;
            this.utente = utente;
            this.idutente = idutente;
            this.prezzo = prezzo;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Registrazione reg = new Registrazione(0,nome,tipo,pos,data,dettagli,utente,idutente,prezzo);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<Registrazione> entity = new HttpEntity<>(reg, headers);
            final String ret =  restTemplate.postForObject("https://whispering-lake-91455.herokuapp.com/save_reg",entity,String.class);


            new Thread() {
                public void run() {
                    RegProdActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(ret.equals("Registration successfully saved")) {
                                CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_success), R.layout.custom_toast,  "Registrazione effettuata con successo");
                                startActivity(new Intent(RegProdActivity.this, HomeActivity.class));
                            }
                            else
                                CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_error), R.layout.custom_toast_error,  "Non è stato possibile registrare il prodotto");



                        }
                    });
                }
            }.start();
            return null;

        }




    }

}
