package yuria.testmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import yuria.testmap.models.Utente;

public class MenuActivity extends AppCompatActivity {
    public static Utente utente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        MenuItem menuHome = (MenuItem) findViewById(R.id.menuHome);
        MenuItem menuRegistrazioni = (MenuItem) findViewById(R.id.menuRegistrazioni);
        MenuItem menuLogout = (MenuItem) findViewById(R.id.menuLogout);
        

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.loggedmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuHome)
        {
            //Home Menu is clicked
            startActivity(new Intent(this,HomeActivity.class));
        }
        else if (id== R.id.menuLogout){
            //Logout menu is clicked

            CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_success), R.layout.custom_toast,  "Logout effettuato con successo");
            MenuActivity.utente=null;
            startActivity(new Intent(this,LoginActivity.class));
        }

        else if (id == R.id.menuRegistrazioni) {
            //Menu registrazioni is clicked
            startActivity(new Intent(this,RegistrazioneActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
