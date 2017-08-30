package yuria.testmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RicProdActivity extends MenuActivity {
    Button cercaBtn, homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ric_prod);

        cercaBtn = (Button) findViewById(R.id.cercaBtn);
        homeBtn = (Button) findViewById(R.id.homeBtn);

        cercaBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                startActivity(new Intent(RicProdActivity.this, MapsActivity.class));

            }
        });


        homeBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                startActivity(new Intent(RicProdActivity.this, HomeActivity.class));

            }
        });
    }
}
