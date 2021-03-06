package yuria.shApping.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import yuria.shApping.ListViewAdapters.RegAdapterCardListaReg;
import yuria.shApping.R;
import yuria.shApping.models.Registrazione;

import static java.util.Arrays.asList;

public class RegistrazioneActivity extends MenuActivity {
    Button indietroBtn;
    ListView itemList;
    final Activity activity = this;
    List<Registrazione> regLista = null;
    RestTemplate restTemplate = new RestTemplate();
    final int idutentefinal = MenuActivity.utente.getIdutente();
    HashMap<Integer,Registrazione> regMap = null;
    private List<MediaType> acceptableMediaTypes = asList(MediaType.APPLICATION_JSON);
    HttpHeaders headers = new HttpHeaders() {{
        setAccept(acceptableMediaTypes);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);



        initWidgets();
        new RegistrazioneActivity.validateHttpRequest(idutentefinal).execute();




    }

    private void initWidgets() {
        indietroBtn = (Button) findViewById(R.id.indietroRicBtn);
        itemList = (ListView) findViewById(R.id.itemList);

        indietroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegistrazioneActivity.this, HomeActivity.class));
            }
        });




    }

    private void populateListView(int code) {

        //code == 1, user has atleast 1 registration, 0 otherwise
        if(code==1) {
            ArrayList<Registrazione> reg = new ArrayList<>();
            int i = 0;
            HashMap<Integer, Registrazione> tempMap = new HashMap<>();
            for (Registrazione r : regLista) {
                reg.add(r);
                tempMap.put(i, r);
                i++;

            }
            regMap = tempMap;
            RegAdapterCardListaReg adapter = new RegAdapterCardListaReg(this,reg);

            itemList.setAdapter(adapter);

        }
        else{ //no registrations
            String [] myItems1= new String[1];
            myItems1[0] = "Non hai effettuato nessuna registrazione";
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.the_items, R.id.textViewItem, myItems1);
            itemList.setAdapter(adapter);

        }
    }

    private class validateHttpRequest extends AsyncTask<Void, Void, Void> {
        Integer idutente;

        validateHttpRequest(Integer idutente) {
            this.idutente=idutente;

        }


        @Override
        protected Void doInBackground(Void... params) {

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<Integer> entity = new HttpEntity<Integer>(idutente, headers);
            try {
                List<Registrazione> regs = new ArrayList<>();
                regs= Arrays.asList(restTemplate.exchange("https://whispering-lake-91455.herokuapp.com/reg_by_user", HttpMethod.POST, entity, Registrazione[].class).getBody());
                regLista=regs;
                new Thread() {
                    public void run() {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(regLista.get(0).getNome()== null)
                                    populateListView(0);
                                 else
                                     populateListView(1);
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
