
/*
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.decio.pgis.model.Installation;
import com.example.decio.pgis.model.Operator;
import com.example.decio.pgis.model.Support;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    Button install,buttonSupportAssembly;
    TextView putLatitude,putLongitude;
    Location mLastLocation = null;
    GoogleApiClient mGoogleApiClient = null;
    double longitude = 0, latitude = 0;
    private Support validatedSupport=null;
    RestTemplate restTemplate = new RestTemplate();
    private List<MediaType> acceptableMediaTypes = asList(MediaType.APPLICATION_JSON);
    HttpHeaders headers = new HttpHeaders(){{setAccept(acceptableMediaTypes);}};
    final Activity activity = this;
    Bundle extras = getIntent().getExtras();
    Operator loggedOperator = (Operator) extras.get("operator");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Welcome "+ loggedOperator.getName()+" "+ loggedOperator.getSurname());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        install = (Button) findViewById(R.id.installButton);
        putLatitude = (TextView) findViewById(R.id.putLatitude);
        putLongitude = (TextView) findViewById(R.id.putLongitude);
        buttonSupportAssembly = (Button)findViewById(R.id.buttonSupportAssembly);


        if (checkPlayServices()) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = GoogleApiClientBuild();
                createLocationRequest();
            }
        }


        buttonSupportAssembly.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toSupportAssembly();
            }
        });

        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    validatedSupport = (Support) extras.get("support");
                    getLocation();
                    if (validatedSupport != null) {
                        new runApp().execute();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Support not scanned. Scan a support first", Toast.LENGTH_LONG)
                                .show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Support not scanned. Scan a support first", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    protected GoogleApiClient GoogleApiClientBuild() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    @Override
    protected void onStart() {
       // mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
      //  mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(MainActivity.class.getSimpleName(), "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);
    }

    /**
     * ottiene latitudine e longitudine della posizione corrente

    private void getLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException s) {
        }
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            putLatitude.setText(String.valueOf(latitude));
            putLongitude.setText(String.valueOf(longitude));


        } else {

            putLatitude.setText("(Couldn't get the location. Make sure location is enabled on your device)");
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }


    @Override
    public void onLocationChanged(Location location) {

    }



      Si occupa della gestione delle richieste http asincrone ai web services. In particolare invia un oggetto installazione al web service che provvederà a storicizzarlo nel database

    private class runApp extends AsyncTask<Void, Void, Void> {
        ResponseEntity response = null;
        HttpEntity<Installation> entity=null;


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Calendar calendar = Calendar.getInstance();
                Timestamp currentTimestamp = new Timestamp(calendar.getTime().getTime());
                final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                Installation installation = new Installation(0, new Operator(loggedOperator.getId(), null, null, null, null, null), validatedSupport, point, currentTimestamp, null, true);
                final String url = "http://100.102.6.249:8080/pGisRestService/saveInstallation";
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                entity = new HttpEntity<>(installation,headers);
                response = restTemplate.exchange(url, HttpMethod.POST, entity , Installation.class);
                new Thread() {
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(response.getStatusCode().value()== 200) {
                                    Toast.makeText(activity, "Installation successfully sent", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(activity, "Network error: please try again", Toast.LENGTH_LONG).show();
                                }
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

    /**
     *Passa dall'activity corrente all'activity SupportAssembly

    public void toSupportAssembly(){
        Intent intent = new Intent(MainActivity.this, SupportAssembly.class);
        intent.putExtra("operator", loggedOperator);
        startActivity(intent);
    }
}

*/