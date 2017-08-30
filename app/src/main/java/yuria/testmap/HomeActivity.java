package yuria.testmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import yuria.testmap.models.Utente;

public class HomeActivity extends MenuActivity {
    public Button ricpro, regpro;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initWidgets();
       // Toast.makeText(this, MenuActivity.utente.getUsername(), Toast.LENGTH_LONG).show();
    }



    private void initWidgets() {

        ricpro = (Button) findViewById(R.id.ricprodBtn);
        regpro = (Button) findViewById(R.id.regprodBtn);

        ricpro.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                startActivity(new Intent(HomeActivity.this, RicProdActivity.class));

            }
        });


        regpro.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                startActivity(new Intent(HomeActivity.this, RegProdActivity.class));

            }
        });
    }
}
