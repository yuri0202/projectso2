package yuria.testmap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import yuria.testmap.models.Registrazione;

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
                //populateListView();
                startActivity(new Intent(RegistrazioneActivity.this, HomeActivity.class));
            }
        });




    }

    private void populateListView(int code) {

       //code == 1, user has atleast 1 registration, 0 otherwise
        String[] myItems;
        if(code==1) {
            myItems = new String[regLista.size()];
            int i = 0;
            HashMap<Integer, Registrazione> tempMap = new HashMap<>();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            for (Registrazione r : regLista) {
                myItems[i] = r.getNome() + ", " + r.getTipo() + " - " + df.format(r.getData());
                tempMap.put(i, r);
                i++;

            }
            regMap = tempMap;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.the_items, R.id.textViewItem, myItems);
            itemList.setAdapter(adapter);

            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent int1 = new Intent(RegistrazioneActivity.this, DescAcquistoActivity.class);
                    int1.putExtra("activity", "registrazione");
                    int1.putExtra("reg", regMap.get(position));
                    startActivity(int1);
                }
            });
        }
        else{ //no registrations
            myItems= new String[1];
            myItems[0] = "Non hai effettuato nessuna registrazione";
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.the_items, R.id.textViewItem, myItems);
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

            //System.out.println(utenteLoggato.getUsername() + " "+ utenteLoggato.getPassword());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<Integer> entity = new HttpEntity<Integer>(idutente, headers);
            try {
                //final Utente finalUtente;
                List<Registrazione> regs = new ArrayList<>();

                regs= Arrays.asList(restTemplate.exchange("https://whispering-lake-91455.herokuapp.com/reg_by_user", HttpMethod.POST, entity, Registrazione[].class).getBody());
                regLista=regs;
                //final HttpStatus hs = response.getStatusCode();


                new Thread() {
                    public void run() {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //System.out.println("GRANDEZZA LISTA: "+regLista.size());
                                //System.out.println("nome: "+regLista.get(0).getNome());
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
