package yuria.testmap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vividsolutions.jts.geom.Point;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuria.testmap.models.Registrazione;
import yuria.testmap.models.Utente;

import static java.util.Arrays.asList;

public class ModElimRegActivity extends MenuActivity {



    Button modBtn = null,elimBtn=null;
    String previousActivity = null;
    Registrazione regCurr = null;
    final Activity activity = this;
    List<Registrazione> regLista = null;
    TextView tipoTxt,nomeTxt,dataTxt,prezzoTxt,dettagliTxt,posTxt,utenteTxt;
    RestTemplate restTemplate = new RestTemplate();
    private List<MediaType> acceptableMediaTypes = asList(MediaType.APPLICATION_JSON);
    HttpHeaders headers = new HttpHeaders() {{
        setAccept(acceptableMediaTypes);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_elim_reg);
        Bundle bun = getIntent().getExtras();
        regCurr = (Registrazione) bun.get("reg");

        initWidgets();
        setRegInfo();


    }

    private void setRegInfo() {
        double latitude = regCurr.getPos().getX();
        double longitude = regCurr.getPos().getY();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


        utenteTxt.setText(regCurr.getUtente().getNome() +" "+regCurr.getUtente().getCognome());
        nomeTxt.setText(regCurr.getNome());
        tipoTxt.setText(regCurr.getTipo());
        dataTxt.setText(df.format(regCurr.getData()));

        prezzoTxt.setText(Float.toString(regCurr.getPrezzo())+" €");
        posTxt.setText(addresses.get(0).getAddressLine(0));
        dettagliTxt.setText(regCurr.getDettagli());
    }

    private void initWidgets() {

        modBtn = (Button) findViewById(R.id.modificaBtn);
        elimBtn = (Button) findViewById(R.id.EliminaBtn);
        modBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // start modifica activity
                Intent int1 = new Intent(ModElimRegActivity.this, ModificaRegistrazione.class);
                int1.putExtra("reg",regCurr);
                startActivity(int1);

            }
        });

        elimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ModElimRegActivity.this)
                        .setTitle("Conferma")
                        .setMessage("Sei sicuro di voler eliminare la registrazione?")
                        .setIcon(R.drawable.ic_warning_black_24dp)

                        .setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener()  {
                            public void onClick(DialogInterface dialog, int whichButton){
                                new ModElimRegActivity.validateHttpRequest(regCurr.getIdreg()).execute();

                            }
                        })
                        .setNegativeButton(android.R.string.no,null).show();
            }
        });

        tipoTxt = (TextView) findViewById(R.id.tipoTxt);
        nomeTxt = (TextView) findViewById(R.id.nomeTxt);
        dataTxt = (TextView) findViewById(R.id.dataTxt);
        prezzoTxt = (TextView) findViewById(R.id.prezzoTxt);
        dettagliTxt = (TextView) findViewById(R.id.dettagliTxt);
        posTxt = (TextView) findViewById(R.id.posTxt);
        utenteTxt = (TextView) findViewById(R.id.utenteTxt);
    }

    private class validateHttpRequest extends AsyncTask<Void, Void, Void> {
        int idreg;


        public validateHttpRequest(int idreg) {
            this.idreg = idreg;


        }

        @Override
        protected Void doInBackground(Void... params) {
            //sostituire 0 con l'id
            Registrazione reg = new Registrazione(idreg, null, null, null, null, null, null, 0, 0);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<Registrazione> entity = new HttpEntity<>(reg, headers);
            final String ret = restTemplate.postForObject("https://whispering-lake-91455.herokuapp.com/delete_reg", entity, String.class);


            new Thread() {
                public void run() {
                    ModElimRegActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (ret.equals("Registration successfully deleted")) {
                                CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_success), R.layout.custom_toast,  "Registrazione eliminata con successo");
                                startActivity(new Intent(ModElimRegActivity.this, RegistrazioneActivity.class));
                            }
                            else
                                CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_error), R.layout.custom_toast_error,  "Registrazione non eliminata");

                        }
                    });
                }
            }.start();
            return null;

        }
    }
}
