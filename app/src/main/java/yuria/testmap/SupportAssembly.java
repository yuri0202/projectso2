/*
package com.example.decio.pgis;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.decio.pgis.model.Device;
import com.example.decio.pgis.model.Operator;
import com.example.decio.pgis.model.RoadSign;
import com.example.decio.pgis.model.Support;
import com.example.decio.pgis.model.Assembly;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


import static java.util.Arrays.asList;

public class SupportAssembly extends AppCompatActivity {
    private static final int SUPPORT=1, DEVICE=2, SIGN=3;

    TextView supportId, scannedDevices, scannedSigns;

    Support toAssembly=null;
    List<Device> devicesScanned = new LinkedList<>();
    List<RoadSign> roadSignsScanned = new LinkedList<>();
    RestTemplate restTemplate = new RestTemplate();
    private List<MediaType> acceptableMediaTypes = asList(MediaType.APPLICATION_JSON);
    HttpHeaders headers = new HttpHeaders() {{
        setAccept(acceptableMediaTypes);
    }};
    Integer scanResult = null;
    final Activity activity = this;
    Bundle extras = getIntent().getExtras();
    Operator loggedOperator = (Operator) extras.get("operator");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_assembly);
        final Button scanSupport = (Button) findViewById(R.id.scanSupport);
        Button scanDevice = (Button) findViewById(R.id.scanDevice);
        Button scanRoadSign = (Button) findViewById(R.id.scanSign);
        Button assembly = (Button) findViewById(R.id.assemblySupport);

        scanSupport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                scan(activity,SUPPORT);
                scanSupport.setEnabled(false);

            }
        });

        scanDevice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                scan(activity,DEVICE);
            }
        });

        scanRoadSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            scan(activity,SIGN);
            }
        });

        assembly.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(toAssembly!=null && (devicesScanned.size()>0 || roadSignsScanned.size()>0)) {
                    assembly(Math.max(devicesScanned.size(), roadSignsScanned.size()));
                }
                else if(toAssembly==null){
                    Toast.makeText(activity, "Support not scanned", Toast.LENGTH_LONG).show();
                }
                else if (devicesScanned.size()<1 && roadSignsScanned.size()<1){
                    Toast.makeText(activity, "Not enough devices and road signs scanned to assembly a support", Toast.LENGTH_LONG).show();
                }
            }
        });

}


    public void scan(Activity activity,int REQUEST_CODE){
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setRequestCode(REQUEST_CODE);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                scanResult = Integer.parseInt(result.getContents());
                new SupportAssembly.HttpRequest(scanResult,requestCode).execute();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    class HttpRequest extends AsyncTask<Object, Object, Void> {
            Integer REQUEST_CODE;
            Support support;
            Device device;
            RoadSign sign;

            HttpRequest(Integer id, Integer REQUEST_CODE) {
                this.REQUEST_CODE=REQUEST_CODE;
                switch(REQUEST_CODE){
                    case SUPPORT: support = new Support();
                                  support.setId(id);
                        break;
                    case DEVICE: device= new Device();
                                 device.setId(id);
                        break;
                    case SIGN: sign= new RoadSign();
                                sign.setId(id);
                        break;
                }
            }

            @Override
            protected Void doInBackground(Object... params) {
                try {
                    HttpEntity<?> entity;
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    switch (REQUEST_CODE) {

                        case SUPPORT:  entity = new HttpEntity<>(this.support, headers);
                                       toAssembly = restTemplate.exchange("http://100.102.6.249:8080/pGisRestService/validateSupport", HttpMethod.POST, entity, Support.class).getBody();
                            break;

                        case DEVICE:   entity = new HttpEntity<>(this.device, headers);
                                       device= restTemplate.exchange("http://100.102.6.249:8080/pGisRestService/validateDevice", HttpMethod.POST, entity, Device.class).getBody();
                                       if(!devicesScanned.contains(device)) {
                                           devicesScanned.add(device);
                                       }
                                       else{
                                           new Thread()
                                           {
                                               public void run()
                                               {
                                                   SupportAssembly.this.runOnUiThread(new Runnable()
                                                   {
                                                       @Override
                                                       public void run()
                                                       {
                                                           Toast.makeText(activity, "Device already scanned. Try again", Toast.LENGTH_LONG).show();
                                                       }
                                                   });
                                               }
                                           }.start();
                                       }
                            break;

                        case SIGN:     entity = new HttpEntity<>(this.sign,headers);
                                       sign= restTemplate.exchange("http://100.102.6.249:8080/pGisRestService/validateRoadSign", HttpMethod.POST, entity, RoadSign.class).getBody();
                                       if(!roadSignsScanned.contains(sign)) {
                                           roadSignsScanned.add(sign);
                                       }else{
                                           new Thread()
                                           {
                                               public void run()
                                               {
                                                   SupportAssembly.this.runOnUiThread(new Runnable()
                                                   {
                                                       @Override
                                                       public void run()
                                                       {
                                                           Toast.makeText(activity, "Device already scanned. Try again", Toast.LENGTH_LONG).show();
                                                       }
                                                   });
                                               }
                                           }.start();
                                       }
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void v) {
                switch (REQUEST_CODE){
                    case SUPPORT: supportId = (TextView) findViewById(R.id.id_support);
                        supportId.setText(Integer.toString(support.getId()));
                        break;
                    case DEVICE: scannedDevices = (TextView) findViewById(R.id.scannedDevices);
                                 scannedDevices.setText(Integer.toString(devicesScanned.size()));
                        break;
                    case SIGN: scannedSigns = (TextView) findViewById(R.id.scannedSigns);
                               scannedSigns.setText(Integer.toString(roadSignsScanned.size()));
                }

            }
        }


    class AssemblyHttpRequest extends AsyncTask<Object, Void, ResponseEntity>{
    List<Assembly> assemblages;
    HttpEntity<List<Assembly>> entity;
    ResponseEntity response;

    AssemblyHttpRequest(List<Assembly> assemblages){
        this.assemblages=assemblages;
    }

    @Override
    protected ResponseEntity doInBackground(Object... params){
        entity= new HttpEntity<>(assemblages,headers);
        try {
            response = restTemplate.exchange("http://100.102.6.249:8080/pGisRestService/saveAssembly", HttpMethod.POST, entity, assemblages.getClass());
            new Thread() {
                public void run() {
                    SupportAssembly.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(response.getStatusCode().value()==200) {
                                Toast.makeText(activity, "Assembly successfully sent", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SupportAssembly.this, MainActivity.class);
                                intent.putExtra("support", toAssembly);
                                startActivity(intent);
                            }else{
                                Toast.makeText(activity, "Network error: please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }.start();

        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }
}


    public void assembly(int max){
        List<Assembly> assemblagesList=new LinkedList<>();


        Calendar calendar = Calendar.getInstance();
        java.sql.Timestamp now = new java.sql.Timestamp(calendar.getTime().getTime());
        for(int i=0; i<max;i++) {
            assemblagesList.add(new Assembly(0, toAssembly, null, null, new Operator(loggedOperator.getId(), null, null, null, null, null), now));
        }
        for(int y=0; y<devicesScanned.size();y++){
            assemblagesList.get(y).setDevice(devicesScanned.get(y));
        }

        for(int z=0; z<roadSignsScanned.size();z++ ){
            assemblagesList.get(z).setRoadSign(roadSignsScanned.get(z));
        }

        new SupportAssembly.AssemblyHttpRequest(assemblagesList).execute();
    }

}

*/
