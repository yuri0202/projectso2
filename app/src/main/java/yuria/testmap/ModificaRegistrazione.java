package yuria.testmap;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuria.testmap.models.Registrazione;
import yuria.testmap.models.Utente;

import static java.util.Arrays.asList;

public class ModificaRegistrazione extends MenuActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    Button IndietroBtn, posBtn,modBtn;
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
    String indirizzo;
    Registrazione regCurr = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

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
        setContentView(R.layout.activity_modifica_registrazione);
        Bundle bun = getIntent().getExtras();
        regCurr = (Registrazione) bun.get("reg");
        initWidget();


    }

    private void initWidget() {
        IndietroBtn = (Button) findViewById(R.id.indietroRicBtn);
        posBtn = (Button) findViewById(R.id.positionBtn);
        modBtn = (Button) findViewById(R.id.RegistraBtn);
        detailsTxt = (EditText) findViewById(R.id.detailsTxt);
        addressTxt = (EditText) findViewById(R.id.addressTxt);
        prezzoTxt = (EditText) findViewById(R.id.prezzoTxt);
        nomeTxt = (EditText) findViewById(R.id.nomeTxt);
        tipoSpinner = (Spinner) findViewById(R.id.tipoSpinner);

        detailsTxt.setText(regCurr.getDettagli());
        nomeTxt.setText(regCurr.getNome());
        prezzoTxt.setText(Float.toString(regCurr.getPrezzo()));
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(regCurr.getPos().getX(),regCurr.getPos().getY(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        indirizzo = address +" "+city;
        addressTxt.setText(indirizzo);
        tipoSpinner.setSelection(getSpinnerIndexByValue(tipoSpinner,regCurr.getTipo()));



        IndietroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ModificaRegistrazione.this, HomeActivity.class));
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

        modBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registraProdotto();
            }
        });
    }

    private int getSpinnerIndexByValue(Spinner spinner, String value) {
        int index = 0;
        for (int i =0; i<spinner.getCount();i++){
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                index = i;
                break;
            }
        }
        return index;
    }
    private void registraProdotto() {



        List<Address> add = null;
        String ind = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Point newPoint=null;

        if(nomeTxt.getText().toString().equals("") ||  prezzoTxt.getText().toString().equals("") ||
                addressTxt.getText().toString().equals("")  )
            Toast.makeText(this,"Inserire campi obbligatori",Toast.LENGTH_LONG).show();
        else{


            try {
                ind = addressTxt.getText().toString();
                add = geocoder.getFromLocationName(ind,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(add.size()==0) {
                Toast.makeText(this,"Indirizzo non trovato, riprova!",Toast.LENGTH_LONG).show();
            }else{
                double lat = add.get(0).getLatitude();
                double lon = add.get(0).getLongitude();

                newPoint = geometryFactory.createPoint(new Coordinate(lat, lon));

                // Date nowDate = Calendar.getInstance().getTime();
                Date nowDate = new Date();
                String details = "";
                if (!detailsTxt.getText().toString().equals(""))
                    details=detailsTxt.getText().toString();
                float prezzo = Float.parseFloat(prezzoTxt.getText().toString());
                if(nomeTxt.getText().toString().equals(regCurr.getNome()) && tipoSpinner.getSelectedItem().toString().equals(regCurr.getTipo())
                    && prezzoTxt.getText().toString().equals(Float.toString(regCurr.getPrezzo())) && indirizzo.equals(addressTxt.getText().toString())
                        && details.equals(regCurr.getDettagli()))
                {
                    Toast.makeText(this,"E' necessario modificare almeno un campo",Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println(indirizzo + " " + addressTxt.getText().toString());

                    new ModificaRegistrazione.validateHttpRequest(regCurr.getIdreg(),
                            nomeTxt.getText().toString(),
                            tipoSpinner.getSelectedItem().toString(),
                            newPoint,
                            regCurr.getData(),
                            details,
                            null,
                            MenuActivity.utente.getIdutente(),
                            prezzo).execute();
                            
                }

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
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryName();
            addressTxt.setText(address +" "+city);
        } else
            addressTxt.setText("Couldn't get the location. Make sure location is enable on the device");

    }



    private class validateHttpRequest extends AsyncTask<Void, Void, Void> {
        int idreg;
        String nome;
        String tipo;
        Point pos;
        Date data;
        String dettagli;
        Utente utente;
        int idutente;
        float prezzo;


        public validateHttpRequest( int idreg, String nome, String tipo, Point pos, Date data, String dettagli, Utente utente, int idutente, float prezzo) {
            this.idreg=idreg;
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
            //sostituire 0 con l'id
            Registrazione reg = new Registrazione(idreg,nome,tipo,pos,data,dettagli,utente,idutente,prezzo);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<Registrazione> entity = new HttpEntity<>(reg, headers);
            final String ret =  restTemplate.postForObject("https://whispering-lake-91455.herokuapp.com/",entity,String.class);


            new Thread() {
                public void run() {
                    ModificaRegistrazione.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(ret.equals("Registration successfully saved"))
                                Toast.makeText(activity,"Modifiche effettuate con successo!",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(activity,"Errore - Modifiche non effettuate",Toast.LENGTH_LONG).show();



                        }
                    });
                }
            }.start();
            return null;

        }




    }
}
