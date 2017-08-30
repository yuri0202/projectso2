
/*
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.springframework.expression.spel.ast.Operator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


import java.util.List;

import yuria.testmap.R;

import static java.util.Arrays.asList;

public class Login extends AppCompatActivity {
    private Operator loggedOperator;
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
        Button login = (Button) findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login.validateHttpRequest( ((EditText)findViewById(R.id.usernameInput)).getText().toString(), ((EditText)findViewById(R.id.passwordInput)).getText().toString()).execute();
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
            Operator operator=null;
            loggedOperator = new Operator(0, null, null, username, password, null);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<Operator> entity = new HttpEntity<>(loggedOperator, headers);
            try {
                operator = restTemplate.exchange("http://192.168.1.5:8080/pGisRestService/validateOperator", HttpMethod.POST, entity, Operator.class).getBody();
                final Operator finalOperator = operator;
                new Thread() {
                    public void run() {
                        Login.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalOperator != null && finalOperator.getPassword().equals(password)) {
                                    Toast.makeText(activity, "Logged in successfully", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("operator", finalOperator);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (finalOperator == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "Invalid username. Please try again", Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Invalid password. Please try again", Toast.LENGTH_LONG)
                                                .show();
                                    }
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
*/