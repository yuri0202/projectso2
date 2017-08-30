package yuria.testmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import yuria.testmap.models.Utente;

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
            Toast.makeText(this,"Logout effettuato con successo",Toast.LENGTH_LONG).show();
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
