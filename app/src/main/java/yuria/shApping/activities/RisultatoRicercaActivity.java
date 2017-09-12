package yuria.shApping.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import yuria.shApping.ListViewAdapters.RegAdapterCardRicerca;
import yuria.shApping.R;
import yuria.shApping.models.Registrazione;

public class RisultatoRicercaActivity extends MenuActivity {
    ListView itemList;
    Button homeBtn,indietroBtn;
    ArrayList<Registrazione> regLista=null;
    HashMap<Integer,Registrazione> regMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risultato_ricerca);
        initWidgets();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
    }
    private void initWidgets() {
        itemList = (ListView) findViewById(R.id.risRicercaList);
       // homeBtn = (Button) findViewById(R.id.homeRicBtn);
        indietroBtn = (Button) findViewById(R.id.indietroRicBtn);

        indietroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(RisultatoRicercaActivity.this,RicProdActivity.class));
                onBackPressed();


            }
        });


        Bundle bun = getIntent().getExtras();
        regLista = (ArrayList<Registrazione>) bun.get("reg");
        if(regLista.size()==0)
            populateListView(0);
        else
            populateListView(1);

    }

    private void populateListView(int code) {

        //code == 1, user has atleast 1 registration, 0 otherwise


        if(code==1) {
            ArrayList<Registrazione> reg = new ArrayList<>();
            int i = 0;
            HashMap<Integer, Registrazione> tempMap = new HashMap<>();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            for (Registrazione r : regLista) {
                reg.add(r);
                tempMap.put(i, r);
                i++;

            }
            regMap = tempMap;
            RegAdapterCardRicerca adapter = new RegAdapterCardRicerca(this,reg);

            itemList.setAdapter(adapter);

            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*
                    Intent int1 = new Intent(RegistrazioneActivity.this, DescAcquistoActivity.class);
                    int1.putExtra("activity", "registrazione");
                    int1.putExtra("reg", regMap.get(position));
                    startActivity(int1);*/
                }
            });
        }
        else{ //no registrations
           String [] myItems1= new String[1];
            myItems1[0] = "La ricerca non ha prodotto alcun risultato";
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.the_items, R.id.textViewItem, myItems1);
            itemList.setAdapter(adapter);

        }
    }
}
