package yuria.shApping.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import yuria.shApping.R;
import yuria.shApping.models.Utente;
import yuria.shApping.resources.CustomToast;

public class MenuActivity extends AppCompatActivity {
    public static Utente utente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
