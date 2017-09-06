package yuria.testmap;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yuria.testmap.models.Registrazione;
import yuria.testmap.models.Ricerca;

import static java.util.Arrays.asList;

public class RicProdActivity extends MenuActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    Button cercaBtn, homeBtn, scegliDaBtn, scegliABtn;
    EditText dataDaTxt,dataATxt,prezzoDaTxt,prezzoATxt,distanzaTxt;
    Spinner tipoSpinner;


    final Activity activity = this;
    ArrayList<Registrazione> regLista = null;
    RestTemplate restTemplate = new RestTemplate();
    //final int idutentefinal = MenuActivity.utente.getIdutente();
    private List<MediaType> acceptableMediaTypes = asList(MediaType.APPLICATION_JSON);
    HttpHeaders headers = new HttpHeaders() {{
        setAccept(acceptableMediaTypes);
    }};

    Point pos;
    private Calendar startDate,endDate;
    private Calendar startCurrent = null, endCurrent = null;
    Location mLastLocation = null;
    GoogleApiClient mGoogleApiClient = null;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    double longitude = 0, latitude = 0;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = true;
    private static final String TAG = "debug";
    static final int DATE_DIALOG_ID = 0;
    private EditText activeDateDisplay;
    private Calendar activeDate;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


    private static int UPDATE_INTERVAL = 5000; // SEC
    private static int FATEST_INTERVAL = 3000; // SEC
    private static int DISPLACEMENT = 10; // METERS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ric_prod);
        initWidgets();


    }

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

    private void initWidgets() {
        dataDaTxt = (EditText) findViewById(R.id.dataDaTxt);
        dataATxt = (EditText) findViewById(R.id.dataATxt);
        dataDaTxt.setKeyListener(null);
        dataATxt.setKeyListener(null);
        prezzoDaTxt = (EditText) findViewById(R.id.prezzoDaTxt);
        prezzoATxt = (EditText) findViewById(R.id.prezzoATxt);
        distanzaTxt = (EditText) findViewById(R.id.distanzaTxt);
        tipoSpinner = (Spinner) findViewById(R.id.tipoSpinner);

        cercaBtn = (Button) findViewById(R.id.cercaBtn);
        homeBtn = (Button) findViewById(R.id.homeRicBtn);
        scegliDaBtn = (Button) findViewById(R.id.scegliDaBtn);
        scegliABtn=(Button) findViewById(R.id.scegliABtn);

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

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();


        scegliDaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DatePickerFragment frag = new DatePickerFragment(1);
                showDateDialog(dataDaTxt,startDate);

            }
        });

        scegliABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(dataATxt,endDate);
            }
        });




        cercaBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){

                // Prendi dati
                String tipo = null;
                Date dataDa = null;
                Date dataA = null;
                Float prezzoDa = null;
                Float prezzoA = null;
                Float distanza = null;
                if (!tipoSpinner.getSelectedItem().toString().equals("Tutti"))
                    tipo=tipoSpinner.getSelectedItem().toString();
                if (!dataDaTxt.getText().toString().equals(""))
                    dataDa = startCurrent.getTime();
                if (!dataATxt.getText().toString().equals(""))
                    dataA = endCurrent.getTime();
                if (!prezzoDaTxt.getText().toString().equals(""))
                    prezzoDa=Float.parseFloat(prezzoDaTxt.getText().toString());
                if (!prezzoATxt.getText().toString().equals(""))
                    prezzoA=Float.parseFloat(prezzoATxt.getText().toString());
                if (!distanzaTxt.getText().toString().equals(""))
                    distanza=Float.parseFloat(distanzaTxt.getText().toString());


                if (ActivityCompat.checkSelfPermission(RicProdActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(RicProdActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    /*
                    dataDaTxt.setText(Double.toString(latitude));
                    dataATxt.setText(Double.toString(longitude));*/
                    pos =  geometryFactory.createPoint(new Coordinate(latitude, longitude));
                }else{
                    Toast.makeText(RicProdActivity.this,"Attiva GPS",Toast.LENGTH_LONG).show();
                    return;
                }

                new RicProdActivity.validateHttpRequest(tipo,dataDa,dataA,prezzoDa,prezzoA,distanza,pos,MenuActivity.utente.getIdutente()).execute();


                //Request http validate blabla
                // costruttore, lancia, via
            }
        });


        homeBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                startActivity(new Intent(RicProdActivity.this, HomeActivity.class));

            }
        });
    }

    private void showDateDialog(EditText dateDisplay, Calendar date) {
        activeDateDisplay = dateDisplay;
        activeDate = date;
        showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            activeDate.set(Calendar.YEAR,year);
            activeDate.set(Calendar.MONTH,month);
            activeDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateDisplay(activeDateDisplay,activeDate);
            setCurrentDate(activeDateDisplay,activeDate);
            unregisterDateDisplay();

        }
    };

    private void setCurrentDate(EditText activeDateDisplay, Calendar activeDate) {
        if (activeDateDisplay == dataDaTxt)
            startCurrent=activeDate;
        else if (activeDateDisplay==dataATxt)
            endCurrent=activeDate;
    }

    private void unregisterDateDisplay() {
        activeDateDisplay = null;
        activeDate = null;
    }

    private void updateDisplay(EditText activeDateDisplay, Calendar activeDate) {

        activeDateDisplay.setText(sdf.format(activeDate.getTime()));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,dateSetListener,activeDate.get(Calendar.YEAR),activeDate.get(Calendar.MONTH),activeDate.get(Calendar.DAY_OF_MONTH));

        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch(id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR),activeDate.get(Calendar.MONTH),activeDate.get(Calendar.DAY_OF_MONTH));
                break;
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




    private class validateHttpRequest extends AsyncTask<Void, Void, Void> {
        //Metti dati della classe ricerca
        String tipo;
        Date dataDa;
        Date dataA;
        Float prezzoDa;
        Float prezzoA;
        Float distanza;
        Point pos;
        int idutente;

        public validateHttpRequest(String tipo, Date dataDa, Date dataA, Float prezzoDa, Float prezzoA, Float distanza, Point pos, int idutente) {
            this.tipo = tipo;
            this.dataDa = dataDa;
            this.dataA = dataA;
            this.prezzoDa = prezzoDa;
            this.prezzoA = prezzoA;
            this.distanza = distanza;
            this.pos = pos;
            this.idutente=idutente;
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Crea oggetto ricerca con i dati passati
            Ricerca ric = new Ricerca(tipo,dataDa,dataA,prezzoDa,prezzoA,distanza,pos,idutente);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<Ricerca> entity = new HttpEntity<>(ric, headers);
            try {


                List<Registrazione> regs=  Arrays.asList(restTemplate.exchange("https://whispering-lake-91455.herokuapp.com/search_reg", HttpMethod.POST, entity, Registrazione[].class).getBody());

                regLista= new ArrayList<Registrazione>(regs);


                new Thread() {
                    public void run() {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                //apri activity risultatoRicerca passando regLista.
                                if(regLista== null)
                                    System.out.println("NESSUNA REGISTRAZIONE TROVATA");
                                else
                                {
                                    System.out.println(regLista.size());

                                }
                                Intent int1 = new Intent(RicProdActivity.this, RisultatoRicerca.class);
                                int1.putExtra("reg", regLista);

                                startActivity(int1);


                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
