package yuria.testmap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import yuria.testmap.models.Utente;

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
        //System.out.println("ENTRO");
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
            //System.out.println(utenteLoggato.getUsername() + " "+ utenteLoggato.getPassword());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<Utente> entity = new HttpEntity<>(utenteLoggato, headers);
            try {

                final Utente finalUtente = restTemplate.exchange("https://whispering-lake-91455.herokuapp.com/auth_user", HttpMethod.POST, entity, Utente.class).getBody();
                //final HttpStatus hs = response.getStatusCode();

                new Thread() {
                    public void run() {
                        LoginActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (!finalUtente.getUsername().equals("") && !finalUtente.getPassword().equals("")){
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container_login_success));

                                    TextView text = (TextView) layout.findViewById(R.id.text);
                                    text.setText("Login effettuato con successo");

                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                    MenuActivity.utente = finalUtente;
                                    System.out.println(finalUtente.getUsername() + " " + finalUtente.getPassword() + " " + finalUtente.getNome() + " " + finalUtente.getCognome() + " " + finalUtente.getIdutente());
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                }
                                else if(finalUtente.getUsername().equals("")) {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.custom_toast_error, (ViewGroup) findViewById(R.id.custom_toast_container_login_error));

                                    TextView text = (TextView) layout.findViewById(R.id.text);
                                    text.setText("Username errato");

                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                }
                                else {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.custom_toast_error, (ViewGroup) findViewById(R.id.custom_toast_container_login_error));

                                    TextView text = (TextView) layout.findViewById(R.id.text);
                                    text.setText("Password errata");

                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
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
