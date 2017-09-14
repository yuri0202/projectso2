package yuria.shApping.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import yuria.shApping.R;
import yuria.shApping.models.Registrazione;

public class DescAcquistoActivity extends MenuActivity {
    Button OkBtn = null;
    String previousActivity = null;
    Registrazione regCurr = null;
    TextView tipoTxt,nomeTxt,dataTxt,prezzoTxt,dettagliTxt,posTxt,utenteTxt;
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc_acquisto);
        //Get registration infos from intent
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



        //Show registration's infos
        utenteTxt.setText(regCurr.getUtente().getNome() +" "+regCurr.getUtente().getCognome());
        nomeTxt.setText(regCurr.getNome());
        tipoTxt.setText(regCurr.getTipo());
        dataTxt.setText(df.format(regCurr.getData()));
        prezzoTxt.setText(Float.toString(regCurr.getPrezzo())+" €");
        posTxt.setText(addresses.get(0).getAddressLine(0));
        dettagliTxt.setText(regCurr.getDettagli());
    }

    private void initWidgets() {
        // Create refs for all widgets

        OkBtn = (Button) findViewById(R.id.indietroRicBtn);
        OkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bun1 = getIntent().getExtras();
                String extra = bun1.getString("activity");
                if (extra.equals("ricerca"))
                    onBackPressed();
                else if(extra.equals("registrazione"))
                    onBackPressed();
                previousActivity=extra;

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
}
