package yuria.shApping.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import yuria.shApping.R;
import yuria.shApping.models.Utente;
import yuria.shApping.resources.CustomToast;

import static java.util.Arrays.asList;

public class LoginActivity extends AppCompatActivity {

    private Utente utenteLoggato;
    Button loginBtn;
    final Activity activity = this;
    RestTemplate restTemplate = new RestTemplate();
    private List<MediaType> acceptableMediaTypes = asList(MediaType.APPLICATION_JSON);
    HttpHeaders headers = new HttpHeaders() {{
        setAccept(acceptableMediaTypes);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initiWidgets();

    }

    private void initiWidgets() {
        // Create refs for all widgets

        loginBtn = (Button) findViewById(R.id.okBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginActivity.validateHttpRequest( ((EditText)findViewById(R.id.usernameTxt)).getText().toString(),
                        ((EditText)findViewById(R.id.passwordTxt)).getText().toString()).execute();

            }
        });
    }



    private class validateHttpRequest extends AsyncTask<Void, Void, Void> {
        String username;
        String password;

        validateHttpRequest(String username, String password) {
            this.username=username;
            this.password=password;
        }


        @Override
        protected Void doInBackground(Void... params) {
            Utente utente=null;
            utenteLoggato = new Utente(0, username, password, null, null);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<Utente> entity = new HttpEntity<>(utenteLoggato, headers);
            try {

                final Utente finalUtente = restTemplate.exchange("https://whispering-lake-91455.herokuapp.com/auth_user", HttpMethod.POST, entity, Utente.class).getBody();

                new Thread() {
                    public void run() {
                        LoginActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (!finalUtente.getUsername().equals("") && !finalUtente.getPassword().equals("")){
                                    CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_success), R.layout.custom_toast,  "Login effettuato con successo");
                                    MenuActivity.utente = finalUtente;
                                    System.out.println(finalUtente.getUsername() + " " + finalUtente.getPassword() + " " + finalUtente.getNome() + " " + finalUtente.getCognome() + " " + finalUtente.getIdutente());
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                }
                                else if(finalUtente.getUsername().equals("")) {
                                    CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_error), R.layout.custom_toast_error,  "Username errato");
                                }
                                else {
                                    CustomToast.create_custom_toast(getApplicationContext(), getLayoutInflater(), (ViewGroup) findViewById(R.id.custom_toast_container_error), R.layout.custom_toast_error,  "Password errata");
                                }
                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }




}
