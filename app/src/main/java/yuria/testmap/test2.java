package yuria.testmap;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuria.testmap.R;

import static yuria.testmap.R.id.a;

public class test2 extends AppCompatActivity {
    EditText text1,text2;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        text1 = (EditText) findViewById(R.id.text1);
        text2 = (EditText) findViewById(R.id.text2);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               translate();

            }
        });
    }

    private void translate() {
        /*
        if(!text1.getText().toString().equals("")){
            List<Address> add = null;

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                add = geocoder.getFromLocationName(text1.getText().toString(),1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(add.size()!=0) {
                double lat = add.get(0).getLatitude();
                double lon = add.get(0).getLongitude();
                text2.setText(lat + " "+lon);
            }else{
                text2.setText("Indirizzo NOT FOUND");
            }

        }*/
        //Date nowDate = Calendar.getInstance().getTime();
        Date nowDate = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        text2.setText(sdf.format(nowDate));
    }
}
