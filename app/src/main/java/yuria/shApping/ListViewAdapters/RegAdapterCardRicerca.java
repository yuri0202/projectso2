package yuria.shApping.ListViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import yuria.shApping.activities.DescAcquistoActivity;
import yuria.shApping.activities.MapsActivity;
import yuria.shApping.R;
import yuria.shApping.models.Registrazione;

/**
 * Created by yuria on 02/09/2017.
 */

public class RegAdapterCardRicerca extends ArrayAdapter<Registrazione> {

    //Adapter for CardView into Ricerca

    public RegAdapterCardRicerca(Context context, ArrayList<Registrazione> regs)
    {
        super(context,0,regs);
    }

    View current = null;

    public View getView(int position, View convertView, ViewGroup parent){
        Registrazione reg =getItem(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_items,parent,false);
        }
        current=convertView;
        TextView tv1 = (TextView) convertView.findViewById(R.id.cardText21);
        TextView tv2 = (TextView) convertView.findViewById(R.id.cardText22);
        TextView tv3 = (TextView) convertView.findViewById(R.id.cardText23);
        tv1.setText("Nome: "+reg.getNome());
        tv2.setText("Tipo: "+reg.getTipo());
        tv3.setText("Prezzo: "+reg.getPrezzo());


        Button detailsBtn = (Button) convertView.findViewById(R.id.cardDettagli2Btn);
        Button mappaBtn = (Button) convertView.findViewById(R.id.cardMapBtn);
        detailsBtn.setTag(position);
        mappaBtn.setTag(position);
        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails(v);


            }
        });

        mappaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(v);
            }
        });
        return convertView;


    }

    private void showMap(View v) {
        int position = (Integer) v.getTag();
        Registrazione reg = getItem(position);
        Intent int1 = new Intent(current.getContext(), MapsActivity.class);
        int1.putExtra("reg",reg);
        current.getContext().startActivity(int1);
    }

    private void showDetails(View v) {
        int position = (Integer) v.getTag();
        Registrazione reg = getItem(position);
        Intent int1 = new Intent(current.getContext(), DescAcquistoActivity.class);
        int1.putExtra("activity","ricerca");
        int1.putExtra("reg",reg);
        current.getContext().startActivity(int1);
    }
}
