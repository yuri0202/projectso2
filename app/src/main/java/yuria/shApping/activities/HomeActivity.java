package yuria.shApping.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import yuria.shApping.R;

public class HomeActivity extends MenuActivity {
    public Button ricpro, regpro;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initWidgets();
    }



    private void initWidgets() {
        // Create refs for all widgets
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
